package com.blueprintit.multiim.yahoo;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.*;
import java.util.Map;
import java.util.HashMap;
import java.awt.Component;
import java.awt.Image;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import com.blueprintit.multiim.IM;
import com.blueprintit.multiim.Group;
import com.blueprintit.multiim.Service;
import com.blueprintit.multiim.ServiceProvider;
import com.blueprintit.multiim.AbstractService;
import com.blueprintit.multiim.event.ServiceEvent;
import com.blueprintit.multiim.event.BuddyEvent;
import com.blueprintit.multiim.event.MessageEvent;
import com.blueprintit.multiim.event.GroupEvent;
import javax.swing.text.Document;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Position;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.Icon;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import javax.swing.ImageIcon;

public class YahooService extends AbstractService implements Runnable
{
	private String username;
	private String password;
	private boolean connected;
	private boolean connecting;
	private boolean disconnecting;
	private int session_id = 0;
	private List msgqueue;
	Map buddies;
	private int status;
	private String message;
	private Yahoo yahoo;
	private Map smileys = new HashMap();
	private StringBuffer smileyregex = new StringBuffer();

	public YahooService(Yahoo yahoo)
	{
		this.yahoo=yahoo;
		connected=false;
		connecting=false;
		disconnecting=false;
		msgqueue = new ArrayList();
		buddies = new HashMap();
		status=YahooMessage.YAHOO_STATUS_AVAILABLE;
		loadSmileys();
	}

	public void documentInsert(Document doc, final Component c, Position pos, String text, AttributeSet attrs)
	{
		try
		{
			Pattern regex = Pattern.compile(smileyregex.toString());
			Matcher matcher = regex.matcher(text);
			int start = 0;
			while (matcher.find())
			{
				doc.insertString(pos.getOffset(),text.substring(start,matcher.start()),attrs);
				URL smiley = (URL)smileys.get(matcher.group());
				if (smiley!=null)
				{
					MutableAttributeSet iconattrs = new SimpleAttributeSet(attrs);
					ImageIcon icon = new ImageIcon();
					icon.setImage(Toolkit.getDefaultToolkit().createImage(smiley));
					StyleConstants.setIcon(iconattrs,icon);
					doc.insertString(pos.getOffset(),matcher.group(),iconattrs);
				}
				start=matcher.end();
			}
			doc.insertString(pos.getOffset(),text.substring(start),attrs);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public Icon getSmiley(String text)
	{
		URL url = (URL)smileys.get(text);
		if (url!=null)
		{
			return new ImageIcon(url);
		}
		else
		{
			return null;
		}
	}

	private void addSmiley(String text, String filename)
	{
		URL smiley = getClass().getResource("smileys/"+filename);
		if (smiley!=null)
		{
			smileys.put(text,smiley);
			text=text.replaceAll("\\\\","\\\\\\\\");
			text=text.replaceAll("\\(","\\\\\\(");
			text=text.replaceAll("\\)","\\\\\\)");
			text=text.replaceAll("\\[","\\\\\\[");
			text=text.replaceAll("\\]","\\\\\\]");
			text=text.replaceAll("\\^","\\\\\\^");
			text=text.replaceAll("\\$","\\\\\\$");
			text=text.replaceAll("\\*","\\\\\\*");
			text=text.replaceAll("\\?","\\\\\\?");
			text=text.replaceAll("\\+","\\\\\\+");
			text=text.replaceAll("\\.","\\\\\\.");
			text=text.replaceAll("\\|","\\\\\\|");
			if (smileyregex.length()>0)
			{
				smileyregex.append("|");
			}
			smileyregex.append(text);
		}
	}

	private void loadSmileys()
	{
		addSmiley("o:-)","angel.gif");
		addSmiley("O:-)","angel.gif");
		addSmiley("0:-)","angel.gif");
		addSmiley("X-(","angry.gif");
		addSmiley("x-(","angry.gif");
		addSmiley("X(","angry.gif");
		addSmiley("x(","angry.gif");
		addSmiley(";;)","batting.gif");
		addSmiley(":D","bigsmile.gif");
		addSmiley(":-D","bigsmile.gif");
		addSmiley(":d","bigsmile.gif");
		addSmiley(":-d","bigsmile.gif");
		addSmiley(":\">","blush.gif");
		addSmiley("=;","bye.gif");
		addSmiley("=D>","clap.gif");
		addSmiley(":o)","clown.gif");
		addSmiley(":O)","clown.gif");
		addSmiley("<@:)","clown.gif");
		addSmiley(":0))","clown.gif");
		addSmiley(":((","cry.gif");
		addSmiley(":-((","cry.gif");
		addSmiley(">:)","devil.gif");
		addSmiley("#-o","doh.gif");
		addSmiley("#-O","doh.gif");
		addSmiley("=P~","drool.gif");
		addSmiley("/:)","eyebrow.gif");
		addSmiley("/:-)","eyebrow.gif");
		addSmiley("8-|","eyeroll.gif");
		addSmiley("8|","eyeroll.gif");
		addSmiley(":B","glasses.gif");
		addSmiley(":-B","glasses.gif");
		addSmiley(":*","kiss.gif");
		addSmiley(":-*","kiss.gif");
		addSmiley(":))","laughloud.gif");
		addSmiley(":-))","laughloud.gif");
		addSmiley(":x","love.gif");
		addSmiley(":-x","love.gif");
		addSmiley(":X","love.gif");
		addSmiley(":-X","love.gif");
		addSmiley(":>","mean.gif");
		addSmiley(":->","mean.gif");
		addSmiley(":|","neutral.gif");
		addSmiley(":-|","neutral.gif");
		addSmiley(":O","ooooh.gif");
		addSmiley(":-O","ooooh.gif");
		addSmiley(":-\\","question.gif");
		addSmiley(":-/","question.gif");
		addSmiley(":(","sad.gif");
		addSmiley(":-(","sad.gif");
		addSmiley(":-$","shhhh.gif");
		addSmiley(":$","shhhh.gif");
		addSmiley(":-&","sick.gif");
		addSmiley(":&","sick.gif");
		addSmiley("[-(","silent.gif");
		addSmiley("[(","silent.gif");
		addSmiley("8-}","silly.gif");
		addSmiley("8}","silly.gif");
		addSmiley("I-)","sleep.gif");
		addSmiley("|-)","sleep.gif");
		addSmiley("I-|","sleep.gif");
		addSmiley(":-)","smiley.gif");
		addSmiley(":)","smiley.gif");
		addSmiley("(:","smiley.gif");
		addSmiley("(-:","smiley.gif");
		addSmiley("B-)","sunglas.gif");
		addSmiley("(:|","tired.gif");
		addSmiley(":-?","think.gif");
		addSmiley(":?","think.gif");
		addSmiley(":-p","tongue.gif");
		addSmiley(":-P","tongue.gif");
		addSmiley(":p","tongue.gif");
		addSmiley(":P","tongue.gif");
		addSmiley(";-)","wink.gif");
		addSmiley(";)","wink.gif");
		addSmiley(":-s","worried.gif");
		addSmiley(":-S","worried.gif");
		addSmiley(":s","worried.gif");
		addSmiley(":S","worried.gif");
		addSmiley("=:)","alien.gif");
		addSmiley("=:-)","alien.gif");
		addSmiley(">-)","alien2.gif");
		addSmiley(">)","alien2.gif");
		addSmiley("b-(","beatup.gif");
		addSmiley("b(","beatup.gif");
		addSmiley("~:>","chicken.gif");
		addSmiley("~o)","coffee.gif");
		addSmiley("3:-0","cow.gif");
		addSmiley("3:-O","cow.gif");
		addSmiley("3:-o","cow.gif");
		addSmiley("<):)","cowboy.gif");
		addSmiley("\\:d/","dance.gif");
		addSmiley("\\:D/","dance.gif");
		addSmiley("@};-","flower.gif");
		addSmiley(":-L","frustrated.gif");
		addSmiley(":-l","frustrated.gif");
		addSmiley("8-X","ghost.gif");
		addSmiley(">:D<","huggs.gif");
		addSmiley("@-)","hypnotized.gif");
		addSmiley("*-:)","idea.gif");
		addSmiley(":^o","liar.gif");
		addSmiley(":(|)","monkey.gif");
		addSmiley("$-)","moneyeyes.gif");
		addSmiley("$)","moneyeyes.gif");
		addSmiley(":)>-","peace.gif");
		addSmiley(":@)","pig.gif");
		addSmiley("[-o<","pray.gif");
		addSmiley("(~~)","pumpkin.gif");
		addSmiley("[-X","shame.gif");
		addSmiley("**==","flag.gif");
		addSmiley("%%-","shamrock.gif");
		addSmiley(":-\"","whistling.gif");
		addSmiley("(%)","yinyang.gif");
		addSmiley("o->","hiro.gif");
		addSmiley("o=>","billy.gif");
		addSmiley("o-+","april.gif");
	}

	public ServiceProvider getServiceProvider()
	{
		return yahoo;
	}

	void spreadEvent(BuddyEvent e)
	{
		deliverEvent(e);
	}

	void spreadEvent(MessageEvent e)
	{
		deliverEvent(e);
	}

	void spreadEvent(GroupEvent e)
	{
		deliverEvent(e);
	}

	protected void deliverEvent(ServiceEvent e)
	{
		super.deliverEvent(e);
	}

	public Icon getIcon()
	{
		return new ImageIcon(getClass().getResource("status/service.gif"));
	}

	public String getID()
	{
		return username;
	}

	public String getName()
	{
		return username;
	}

	public String getUsername()
	{
		return username;
	}

	public Group addGroup(String id)
	{
		return null;
	}

	protected Group createGroup(String id)
	{
		Group group = new YahooGroup(this,id);
		return group;
	}

	public void setLoginDetails(String name, String password)
	{
		if ((!connected)&&(!connecting))
		{
			this.username=name;
			this.password=password;
			initialise();
			deliverEvent(new ServiceEvent(this,ServiceEvent.STATUSCHANGED));
		}
	}

	public boolean isConnected()
	{
		return connected;
	}

	public boolean isInvisible()
	{
		return false;
	}

	public boolean isAway()
	{
		return !(status==YahooMessage.YAHOO_STATUS_AVAILABLE);
	}

	public int getMaxStatus()
	{
		return YahooMessage.YAHOO_STATUS_STEPPEDOUT+2;
	}

	public boolean isCustomStatus(int status)
	{
		if (status>YahooMessage.YAHOO_STATUS_STEPPEDOUT)
		{
			return true;
		}
		return false;
	}

	public String getStatusName(int status)
	{
		if (status==(YahooMessage.YAHOO_STATUS_STEPPEDOUT+1))
		{
			return "Custom";
		}
		else if (status==(YahooMessage.YAHOO_STATUS_STEPPEDOUT+2))
		{
			return "Custom Busy";
		}
		else
		{
			return YahooMessage.messages[status];
		}
	}

	public void setAvailable()
	{
		setAway(YahooMessage.YAHOO_STATUS_AVAILABLE,message);
	}

	public void setAway(int status,String message)
	{
		String customtype = "1";
		if (status>YahooMessage.YAHOO_STATUS_STEPPEDOUT)
		{
			if (status==(YahooMessage.YAHOO_STATUS_STEPPEDOUT+1))
			{
				customtype="0";
			}
			status=YahooMessage.YAHOO_STATUS_CUSTOM;
		}
		YahooMessage ymessage;
		if (status==YahooMessage.YAHOO_STATUS_AVAILABLE)
		{
			ymessage = new YahooMessage(YahooMessage.YAHOO_SERVICE_ISBACK,status);
		}
		else
		{
			ymessage = new YahooMessage(YahooMessage.YAHOO_SERVICE_ISAWAY,status);
			if (status==YahooMessage.YAHOO_STATUS_CUSTOM)
			{
				ymessage.set(19,message);
				ymessage.set(47,customtype);
				this.message=message;
			}
		}
		ymessage.set(10,String.valueOf(status));
		this.status=status;
		queueMessage(ymessage);
		deliverEvent(new ServiceEvent(this,ServiceEvent.STATUSCHANGED));
	}

	public String getStatusMessage()
	{
		if (isConnected())
		{
			if (status>YahooMessage.YAHOO_STATUS_STEPPEDOUT)
			{
				return message;
			}
			else
			{
				return YahooMessage.messages[status];
			}
		}
		else
		{
			return "Offline";
		}
	}

	public void connect()
	{
		if ((!connected)&&(!connecting))
		{
			connecting=true;
			msgqueue.clear();
			YahooMessage auth = new YahooMessage(YahooMessage.YAHOO_SERVICE_AUTH,YahooMessage.YAHOO_STATUS_AVAILABLE);
			auth.set(1,username);
			queueMessage(auth);
			(new Thread(this)).start();
		}
	}

	public void disconnect()
	{
		disconnecting=true;
	}

	private void processBuddyList(YahooMessage message)
	{
		List original = new ArrayList();
		Iterator loop = getGroups();
		while (loop.hasNext())
		{
			original.add(loop.next());
		}
		String buddies = message.get(87)[0];
		String groups[]=buddies.split("\n");
		for (int count=0; count<groups.length; count++)
		{
			int split = groups[count].indexOf(":");
			String grpname = groups[count].substring(0,split);
			YahooGroup group = (YahooGroup)getGroup(grpname);
			if (group==null)
			{
				group=(YahooGroup)createGroup(grpname);
				addGroup(group);
			}
			else
			{
				original.remove(group);
			}
			group.processGroupList(groups[count].substring(split+1));
		}
		loop = original.iterator();
		while (loop.hasNext())
		{
			removeGroup((YahooGroup)loop.next());
		}
	}

	private void processStateChange(YahooMessage message)
	{
		String userid="";
		int state=0;
		String awaymessage="";
		for (int loop=0; loop<message.count(); loop++)
		{
			int key = message.getKey(loop);
			String value = message.getValue(loop);
			switch (key)
			{
				case 0:
				case 1:
				case 8:
				case 11:
				case 17:
				case 47:
				case 137:
				case 138:
					break;
				case 7:
					userid=value;
					break;
				case 10:
					state = Integer.parseInt(value);
					break;
				case 19:
					awaymessage=value;
					break;
				case 13:
					YahooBuddy buddy;
					synchronized(buddies)
					{
						buddy = (YahooBuddy)buddies.get(userid);
					}
					if (buddy!=null)
					{
						int newstatus=0;
						if (message.getService()==message.YAHOO_SERVICE_LOGOFF)
						{
							newstatus=-1;
						}
						else
						{
							newstatus=state;
						}
						buddy.setState(newstatus,awaymessage,message.getStatus()==0);
					}
					break;
				default:
					System.err.println("State change unknown: "+key+" => "+value);
			}
		}
		if (message.getStatus()==0)
		{
			connected=true;
			connecting=false;
			deliverEvent(new ServiceEvent(this,ServiceEvent.CONNECTED));
		}
		else if (message.getStatus()==-1)
		{
			connected=false;
		}
	}

	void queueMessage(YahooMessage message)
	{
		if (message!=null)
		{
			synchronized(msgqueue)
			{
				msgqueue.add(message);
			}
		}
	}

	public void handleMessage(YahooMessage message)
	{
		if (message.getService()==message.YAHOO_SERVICE_AUTH)
		{
			String challenge = message.get(94)[0];
			queueMessage(constructAuthResp(challenge));
		}
		else if (message.getService()==message.YAHOO_SERVICE_LIST)
		{
			processBuddyList(message);
		}
		else if ((message.getService()==message.YAHOO_SERVICE_LOGON)||
			(message.getService()==message.YAHOO_SERVICE_USERSTAT)||
			(message.getService()==message.YAHOO_SERVICE_LOGOFF)||
			(message.getService()==message.YAHOO_SERVICE_ISAWAY)||
			(message.getService()==message.YAHOO_SERVICE_ISBACK)||
			(message.getService()==message.YAHOO_SERVICE_GAMELOGON)||
			(message.getService()==message.YAHOO_SERVICE_GAMELOGOFF)||
			(message.getService()==message.YAHOO_SERVICE_IDACT)||
			(message.getService()==message.YAHOO_SERVICE_IDDEACT))
		{
			processStateChange(message);
		}
		else if (message.getService()==message.YAHOO_SERVICE_MESSAGE)
		{
			String[] id = message.get(4);
			if ((id!=null)&&(id.length==1))
			{
				YahooBuddy buddy;
				synchronized(buddies)
				{
					buddy = (YahooBuddy)buddies.get(id[0]);
				}
				buddy.messageReceived(message.get(14)[0]);
			}
		}
		else if (message.getService()==message.YAHOO_SERVICE_NOTIFY)
		{
			if (message.get(49)[0].equals("TYPING"))
			{
				String id = message.get(4)[0];
				YahooBuddy buddy;
				synchronized(buddies)
				{
					buddy = (YahooBuddy)buddies.get(id);
				}
				buddy.typingNotify(message.get(13)[0].equals("1"));
			}
			else
			{
				System.out.println(message);
			}
		}
		else
		{
			System.out.println(message);
		}
			System.out.println(message);
	}

	public void run()
	{
		try
		{
			session_id=0;
			ByteBuffer readbuffer = ByteBuffer.allocate(65535+20);
			ByteBuffer writebuffer = ByteBuffer.allocate(65535+20);
			SocketChannel channel = SocketChannel.open(new InetSocketAddress("scs.yahoo.com",5050));
			channel.configureBlocking(false);
			readbuffer.clear();
			readbuffer.limit(20);
			Selector select = Selector.open();
			channel.register(select,SelectionKey.OP_READ);
			try
			{
				while ((connecting)||(connected))
				{
					synchronized(msgqueue)
					{
						while (msgqueue.size()>0)
						{
							YahooMessage message = (YahooMessage)msgqueue.get(0);
							msgqueue.remove(0);
							writebuffer.put(message.encode(session_id));
							writebuffer.flip();
							while (writebuffer.remaining()>0)
							{
								channel.write(writebuffer);
							}
							writebuffer.clear();
						}
					}
					if (disconnecting)
					{
						channel.close();
						disconnecting=false;
						connected=false;
					}
					if (select.selectNow()>0)
					{
						Iterator loop = select.selectedKeys().iterator();
						while (loop.hasNext())
						{
							SelectionKey key = (SelectionKey)loop.next();
							loop.remove();
							assert key.isReadable();
							assert key.channel()==channel;
							int count = channel.read(readbuffer);
							if (count>0)
							{
								if ((readbuffer.position()>=10)&&((readbuffer.position()-count)<10))
								{
									readbuffer.limit(readbuffer.getShort(8)+20);
								}
								if (readbuffer.remaining()==0)
								{
									readbuffer.flip();
									byte[] messagedata = new byte[readbuffer.remaining()];
									readbuffer.get(messagedata);
									YahooMessage message = YahooMessage.decode(messagedata);
									session_id=message.getSession();
									handleMessage(message);
									readbuffer.clear();
									readbuffer.limit(20);
								}
							}
							else if (count==-1)
							{
								connecting=false;
								connected=false;
								disconnecting=false;
							}
						}
					}
					try
					{
						Thread.sleep(100);
					}
					catch (Exception e)
					{
					}
				}
			}
			catch (IOException e)
			{
				System.err.println("Exception in thread: "+e.getMessage());
				e.printStackTrace();
				connecting=false;
				connected=false;
			}
			synchronized(buddies)
			{
				Iterator loop = buddies.values().iterator();
				while (loop.hasNext())
				{
					((YahooBuddy)loop.next()).setState(-1,"",true);
				}
			}
			deliverEvent(new ServiceEvent(this,ServiceEvent.DISCONNECTED));
		}
		catch (IOException e)
		{
			System.err.println("Exception in thread: "+e.getMessage());
			e.printStackTrace();
			connecting=false;
			connected=false;
		}
	}

	private static String md5_salt_prefix = "$1$";

	private static String encryptPassword(String key, String salt)
	{
		try
		{
			MessageDigest ctx = MessageDigest.getInstance("MD5");
			MessageDigest alt_ctx = MessageDigest.getInstance("MD5");
			String buffer;
			byte[] alt_result;
			int salt_len;
			int key_len;
			int cnt;
			char cp;

			/* Find beginning of salt string.  The prefix should normally always
			   be present.  Just in case it is not, skip it.  */
			if (salt.startsWith(md5_salt_prefix))
			{
				salt=salt.substring(md5_salt_prefix.length());
			}

			if ((salt.indexOf("$")>=0)&&(salt.indexOf("$")<8))
			{
				salt_len=salt.indexOf("$");
			}
			else
			{
				salt_len=Math.min(8,salt.length());
			}
			key_len = key.length();

			/* Add the key string.  */
			ctx.update(key.getBytes());

			/* Because the SALT argument need not always have the salt prefix we
			   add it separately.  */
			ctx.update(md5_salt_prefix.getBytes());

			/* The last part is the salt string.  This must be at most 8
			   characters and it ends at the first `$' character (for
			   compatibility which existing solutions).  */
			ctx.update(salt.substring(0,salt_len).getBytes());

			/* Compute alternate MD5 sum with input KEY, SALT, and KEY.  The
			   final result will be added to the first context.  */

			/* Add key.  */
			alt_ctx.update(key.getBytes());

			/* Add salt.  */
			alt_ctx.update(salt.substring(0,salt_len).getBytes());

			/* Add key again.  */
			alt_ctx.update(key.getBytes());

			/* Now get result of this (16 bytes) and add it to the other
			   context.  */
			alt_result=alt_ctx.digest();
			alt_ctx.reset();

			/* Add for any character in the key one byte of the alternate sum.  */
			for (cnt = key_len; cnt > 16; cnt -= 16)
			{
				ctx.update(alt_result);
			}
			ctx.update(alt_result,0,cnt);

			/* The original implementation now does something weird: for every 1
			   bit in the key the first 0 is added to the buffer, for every 0
			   bit the first character of the key.  This does not seem to be
			   what was intended but we have to follow this to be compatible.  */
			for (cnt = key_len; cnt > 0; cnt >>= 1)
			{
				if ((cnt & 1)!=0)
				{
					ctx.update((byte)0);
				}
				else
				{
					ctx.update(key.getBytes()[0]);
				}
			}

			/* Create intermediate result.  */
			alt_result=ctx.digest();

			/* Now comes another weirdness.  In fear of password crackers here
			   comes a quite long loop which just processes the output of the
			   previous round again.  We cannot ignore this here.  */
			for (cnt = 0; cnt < 1000; ++cnt)
			{
				ctx.reset();

				/* Add key or last result.  */
				if ((cnt & 1) != 0)
				{
					ctx.update(key.getBytes());
				}
				else
				{
					ctx.update(alt_result);
				}

				/* Add salt for numbers not divisible by 3.  */
				if (cnt % 3 != 0)
				{
					ctx.update(salt.substring(0,salt_len).getBytes());
				}

				/* Add key for numbers not divisible by 7.  */
				if (cnt % 7 != 0)
				{
					ctx.update(key.getBytes());
				}

				/* Add key or last result.  */
				if ((cnt & 1) != 0)
				{
					ctx.update(alt_result);
				}
				else
				{
					ctx.update(key.getBytes());
				}

				/* Create intermediate result.  */
				alt_result=ctx.digest();
			}

			/* Now we can construct the result string.  It consists of three
			   parts.  */

			buffer=md5_salt_prefix+salt.substring(0,salt_len)+"$";

			buffer+=YahooMessage.b64_from_24bit(alt_result[0],alt_result[6],alt_result[12],4);
			buffer+=YahooMessage.b64_from_24bit(alt_result[1],alt_result[7],alt_result[13],4);
			buffer+=YahooMessage.b64_from_24bit(alt_result[2],alt_result[8],alt_result[14],4);
			buffer+=YahooMessage.b64_from_24bit(alt_result[3],alt_result[9],alt_result[15],4);
			buffer+=YahooMessage.b64_from_24bit(alt_result[4],alt_result[10],alt_result[5],4);
			buffer+=YahooMessage.b64_from_24bit((byte)0,(byte)0,alt_result[11],2);

			return buffer;
		}
		catch (NoSuchAlgorithmException e)
		{
			return null;
		}
	}

	private YahooMessage constructAuthResp(String seed)
	{
		try
		{
			MessageDigest ctx = MessageDigest.getInstance("MD5");
			YahooMessage authresp = new YahooMessage(YahooMessage.YAHOO_SERVICE_AUTHRESP,YahooMessage.YAHOO_STATUS_AVAILABLE);

			String crypt_result = "";
			String password_hash;
			String crypt_hash;
			String hash_string_p;
			String hash_string_c;

			char checksum;

			int sv;

			String result6;
			String result96;

			sv = (int)seed.charAt(15);
			sv = (sv % 8) % 5;

			ctx.update(password.getBytes());
			password_hash = YahooMessage.mac64Encode(ctx.digest());

			ctx.reset();
			crypt_result = encryptPassword(password, "$1$_2S43d5f$");
			ctx.update(crypt_result.getBytes());
			crypt_hash = YahooMessage.mac64Encode(ctx.digest());

			switch (sv) {
				case 0:
					checksum = seed.charAt(seed.charAt(7) % 16);
					hash_string_p=checksum+password_hash+username+seed;
					hash_string_c=checksum+crypt_hash+username+seed;
					break;
				case 1:
					checksum = seed.charAt(seed.charAt(9) % 16);
					hash_string_p=checksum+username+seed+password_hash;
					hash_string_c=checksum+username+seed+crypt_hash;
					break;
				case 2:
					checksum = seed.charAt(seed.charAt(15) % 16);
					hash_string_p=checksum+seed+password_hash+username;
					hash_string_c=checksum+seed+crypt_hash+username;
					break;
				case 3:
					checksum = seed.charAt(seed.charAt(1) % 16);
					hash_string_p=checksum+username+password_hash+seed;
					hash_string_c=checksum+username+crypt_hash+seed;
					break;
				default:
					checksum = seed.charAt(seed.charAt(3) % 16);
					hash_string_p=checksum+password_hash+seed+username;
					hash_string_c=checksum+crypt_hash+seed+username;
					break;
			}

			ctx.reset();
			ctx.update(hash_string_p.getBytes());
			result6=YahooMessage.mac64Encode(ctx.digest());

			ctx.reset();
			ctx.update(hash_string_c.getBytes());
			result96=YahooMessage.mac64Encode(ctx.digest());

			authresp.set(0,username);
			authresp.set(1,username);
			authresp.set(6,result6);
			authresp.set(96,result96);

			return authresp;
		}
		catch (NoSuchAlgorithmException e)
		{
			return null;
		}
	}
}
