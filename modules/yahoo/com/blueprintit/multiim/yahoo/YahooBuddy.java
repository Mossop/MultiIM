package com.blueprintit.multiim.yahoo;

import com.blueprintit.multiim.AbstractBuddy;
import com.blueprintit.multiim.Group;
import com.blueprintit.multiim.Buddy;
import com.blueprintit.multiim.event.BuddyEvent;
import com.blueprintit.multiim.event.MessageEvent;
import com.blueprintit.multiim.event.GroupEvent;
import java.awt.Color;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.swing.Icon;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.ImageIcon;

public class YahooBuddy extends AbstractBuddy
{
	private YahooGroup group;
	private String id;
	private boolean online=false;
	private int state = YahooMessage.YAHOO_STATUS_AVAILABLE;
	private String message;
	private boolean istyping = false;

	public YahooBuddy(YahooGroup group, String id)
	{
		this.id=id;
		this.group=group;
	}

	protected void deliverEvent(BuddyEvent e)
	{
		super.deliverEvent(e);
		group.spreadEvent(e);
	}

	protected void deliverEvent(MessageEvent e)
	{
		super.deliverEvent(e);
		group.spreadEvent(e);
	}

	public Icon getIcon()
	{
		String resource;
		if (isConnected())
		{
			if (isAway())
			{
				resource="status/away.gif";
			}
			else
			{
				resource="status/online.gif";
			}
		}
		else
		{
			resource = "status/offline.gif";
		}
		return new ImageIcon(getClass().getResource(resource));
	}

	public void sendMessage(String m)
	{
		YahooMessage message = new YahooMessage(YahooMessage.YAHOO_SERVICE_MESSAGE,YahooMessage.YAHOO_STATUS_AVAILABLE);
		message.set(0,getGroup().getService().getUsername());
		message.set(1,getGroup().getService().getUsername());
		message.set(5,getID());
		message.set(14,m);
		((YahooService)getGroup().getService()).queueMessage(message);
	}

	void typingNotify(boolean state)
	{
		if (state)
		{
			istyping=true;
			deliverEvent(new MessageEvent(this,MessageEvent.TYPINGSTARTED));
		}
		else
		{
			istyping=false;
			deliverEvent(new MessageEvent(this,MessageEvent.TYPINGFINISHED));
		}
	}

	void messageReceived(String message)
	{
		message=message.replaceAll("<(?:b|B)[^>]*>","\u001B[1m");
		message=message.replaceAll("<(?:i|I)[^>]*>","\u001B[2m");
		message=message.replaceAll("<(?:u|U)[^>]*>","\u001B[4m");
		message=message.replaceAll("</(?:b|B)[^>]*>","\u001B[x1m");
		message=message.replaceAll("</(?:i|I)[^>]*>","\u001B[x2m");
		message=message.replaceAll("</(?:u|U)[^>]*>","\u001B[x4m");
		Pattern pattern = Pattern.compile("<(\\S*)\\s*([^>]*)>|\u001B\\[([^m]*)m");
		Pattern vars = Pattern.compile("(\\S*)=(?:\"([^\"]*)\"|(\\S*))");
		MessageEvent e = new MessageEvent(this,MessageEvent.MESSAGERECEIVED);
		MutableAttributeSet attrs = new SimpleAttributeSet();
		StyleConstants.setFontFamily(attrs,"Arial");
		StyleConstants.setFontSize(attrs,13);
		Matcher matcher = pattern.matcher(message);
		int pos=0;
		while (matcher.find())
		{
			String left = message.substring(pos,matcher.start());
			if (left.length()>0)
			{
				e.addMessageBlock(left,attrs);
				attrs = new SimpleAttributeSet(attrs);
			}
			if (matcher.group(1)!=null)
			{
				String tagname = matcher.group(1);
				System.out.println("Tag "+tagname);
				Matcher varmatch = vars.matcher(matcher.group(2));
				while (varmatch.find())
				{
					String varname = varmatch.group(1);
					String varvalue = varmatch.group(2);
					if (varvalue==null)
					{
						varvalue=varmatch.group(3);
					}
					if (tagname.equalsIgnoreCase("font"))
					{
						if (varname.equalsIgnoreCase("face"))
						{
							StyleConstants.setFontFamily(attrs,varvalue);
						}
						else if (varname.equalsIgnoreCase("size"))
						{
							StyleConstants.setFontSize(attrs,Integer.parseInt(varvalue)+3);
						}
					}
					else
					{
						e.addMessageBlock(matcher.group(),attrs);
						attrs = new SimpleAttributeSet(attrs);
					}
				}
			}
			else if (matcher.group(3)!=null)
			{
				String code=matcher.group(3);
				if (code.startsWith("#"))
				{
					int red = Integer.parseInt(code.substring(1,3),16);
					int green = Integer.parseInt(code.substring(3,5),16);
					int blue = Integer.parseInt(code.substring(5,7),16);
					Color color = new Color(red,green,blue);
					StyleConstants.setForeground(attrs,color);
				}
				else if (code.startsWith("x"))
				{
					code=code.substring(1);
					if (code.equals("1"))
					{
						StyleConstants.setBold(attrs,false);
					}
					else if (code.equals("2"))
					{
						StyleConstants.setItalic(attrs,false);
					}
					else if (code.equals("4"))
					{
						StyleConstants.setUnderline(attrs,false);
					}
				}
				else
				{
					if (code.equals("1"))
					{
						StyleConstants.setBold(attrs,true);
					}
					else if (code.equals("2"))
					{
						StyleConstants.setItalic(attrs,true);
					}
					else if (code.equals("4"))
					{
						StyleConstants.setUnderline(attrs,true);
					}
				}
			}
			pos=matcher.end();
		}
		if (pos<message.length())
		{
			e.addMessageBlock(message.substring(pos),attrs);
		}
		deliverEvent(e);
		if (istyping)
		{
			typingNotify(false);
		}
	}

	void setState(int state, String message, boolean initial)
	{
		if (state<0)
		{
			if (online)
			{
				online=false;
				deliverEvent(new BuddyEvent(this,BuddyEvent.DISCONNECTED,!initial));
			}
		}
		else
		{
			if (!online)
			{
				online=true;
				this.state=state;
				this.message=message;
				deliverEvent(new BuddyEvent(this,BuddyEvent.CONNECTED,!initial));
			}
			else if ((this.state!=state)||(!this.message.equals(message)))
			{
				this.state=state;
				this.message=message;
				deliverEvent(new BuddyEvent(this,BuddyEvent.STATUSCHANGED,!initial));
			}
		}
	}

	public boolean isConnected()
	{
		return online;
	}

	public boolean isAway()
	{
		return (state>YahooMessage.YAHOO_STATUS_AVAILABLE);
	}

	public String getStatusMessage()
	{
		if (isConnected())
		{
			if (state>YahooMessage.YAHOO_STATUS_STEPPEDOUT)
			{
				return message;
			}
			else
			{
				return YahooMessage.messages[state];
			}
		}
		else
		{
			return "Offline";
		}
	}

	public String getID()
	{
		return id;
	}

	public String getName()
	{
		return id;
	}

	public Group getGroup()
	{
		return group;
	}
}
