package com.blueprintit.multiim.yahoo;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.io.StringWriter;
import java.io.PrintWriter;

public class YahooMessage
{
	private int service;
	private int status;
	private int session;
	private Map data;
	private List keylist;
	private List valuelist;

	public static final short YAHOO_SERVICE_LOGON            = 0x01;
	public static final short YAHOO_SERVICE_LOGOFF           = 0x02;
	public static final short YAHOO_SERVICE_ISAWAY           = 0x03;
	public static final short YAHOO_SERVICE_ISBACK           = 0x04;
	public static final short YAHOO_SERVICE_IDLE             = 0x05;
	public static final short YAHOO_SERVICE_MESSAGE          = 0x06;
	public static final short YAHOO_SERVICE_IDACT            = 0x07;
	public static final short YAHOO_SERVICE_IDDEACT          = 0x08;
	public static final short YAHOO_SERVICE_MAILSTAT         = 0x09;
	public static final short YAHOO_SERVICE_USERSTAT         = 0x0a;
	public static final short YAHOO_SERVICE_NEWMAIL          = 0x0b;
	public static final short YAHOO_SERVICE_CHATINVITE       = 0x0c;
	public static final short YAHOO_SERVICE_CALENDAR         = 0x0d;
	public static final short YAHOO_SERVICE_NEWPERSONALMAIL  = 0x0e;
	public static final short YAHOO_SERVICE_NEWCONTACT       = 0x0f;
	public static final short YAHOO_SERVICE_ADDIDENT         = 0x10;
	public static final short YAHOO_SERVICE_ADDIGNORE        = 0x11;
	public static final short YAHOO_SERVICE_PING             = 0x12;
	public static final short YAHOO_SERVICE_GROUPRENAME      = 0x13;
	public static final short YAHOO_SERVICE_SYSMESSAGE       = 0x14;
	public static final short YAHOO_SERVICE_PASSTHROUGH2     = 0x16;
	public static final short YAHOO_SERVICE_CONFINVITE       = 0x18;
	public static final short YAHOO_SERVICE_CONFLOGON        = 0x19;
	public static final short YAHOO_SERVICE_CONFDECLINE      = 0x1a;
	public static final short YAHOO_SERVICE_CONFLOGOFF       = 0x1b;
	public static final short YAHOO_SERVICE_CONFADDINVITE    = 0x1c;
	public static final short YAHOO_SERVICE_CONFMSG          = 0x1d;
	public static final short YAHOO_SERVICE_CHATLOGON        = 0x1e;
	public static final short YAHOO_SERVICE_CHATLOGOFF       = 0x1f;
	public static final short YAHOO_SERVICE_CHATMSG          = 0x20;
	public static final short YAHOO_SERVICE_GAMELOGON        = 0x28;
	public static final short YAHOO_SERVICE_GAMELOGOFF       = 0x29;
	public static final short YAHOO_SERVICE_GAMEMSG          = 0x2a;
	public static final short YAHOO_SERVICE_FILETRANSFER     = 0x46;
	public static final short YAHOO_SERVICE_VOICECHAT        = 0x4a;
	public static final short YAHOO_SERVICE_NOTIFY           = 0x4b;
	public static final short YAHOO_SERVICE_P2PFILEXFER      = 0x4d;
	public static final short YAHOO_SERVICE_PEERTOPEER       = 0x4f;
	public static final short YAHOO_SERVICE_AUTHRESP         = 0x54;
	public static final short YAHOO_SERVICE_LIST             = 0x55;
	public static final short YAHOO_SERVICE_AUTH             = 0x57;
	public static final short YAHOO_SERVICE_ADDBUDDY         = 0x83;
	public static final short YAHOO_SERVICE_REMBUDDY         = 0x84;
	public static final short YAHOO_SERVICE_IGNORECONTACT    = 0x85;
	public static final short YAHOO_SERVICE_REJECTCONTACT    = 0x86;

	public static final int   YAHOO_STATUS_AVAILABLE         = 0x00;
	public static final int   YAHOO_STATUS_BRB               = 0x01;
	public static final int   YAHOO_STATUS_BUSY              = 0x02;
	public static final int   YAHOO_STATUS_NOTATHOME         = 0x03;
	public static final int   YAHOO_STATUS_NOTATDESK         = 0x04;
	public static final int   YAHOO_STATUS_NOTINOFFICE       = 0x05;
	public static final int   YAHOO_STATUS_ONPHONE           = 0x06;
	public static final int   YAHOO_STATUS_ONVACATION        = 0x07;
	public static final int   YAHOO_STATUS_OUTTOLUNCH        = 0x08;
	public static final int   YAHOO_STATUS_STEPPEDOUT        = 0x09;
	public static final int   YAHOO_STATUS_INVISIBLE         = 0x0c;
	public static final int   YAHOO_STATUS_CUSTOM            = 0x63;
	public static final int   YAHOO_STATUS_IDLE              = 0x3e7;
	public static final int   YAHOO_STATUS_OFFLINE           = 0x5a55aa56;
	public static final int   YAHOO_STATUS_TYPING            = 0x16;

	private static String MAC64DIGITS = 	"ABCDEFGHIJKLMNOPQRSTUVWXYZ"+
				                                "abcdefghijklmnopqrstuvwxyz"+
				                                "0123456789._";

	private static String b64t = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";


	static String[] messages = {"Available",
															"Be Right Back",
															"Busy",
															"Not at Home",
															"Not at my Desk",
															"Not in the Office",
															"On the Phone",
															"On Vacation",
															"Out to Lunch",
															"Stepped Out"};

	public YahooMessage(int service, int status)
	{
		this.service=service;
		this.status=status;
		keylist = new ArrayList();
		valuelist = new ArrayList();
		data = new HashMap();
	}

	private YahooMessage(int service, int status, int session)
	{
		this(service,status);
		this.session=session;
	}

	private static int byteval(byte b)
	{
		if (b<0)
		{
			return (int)b+256;
		}
		else
		{
			return (int)b;
		}
	}

	public static String b64_from_24bit(byte B2, byte B1, byte B0, int n)
	{
		StringBuffer result = new StringBuffer();
		int w = ((byteval(B2)) << 16) | ((byteval(B1)) << 8) | (byteval(B0));
		while (n-- > 0)
		{
			result.append(b64t.charAt(w & 0x3f));
			w >>= 6;
		}
		return result.toString();
	}

	public static String mac64Encode(byte[] in)
	{
		StringBuffer out = new StringBuffer();
		int pos=0;
		for (; pos<(in.length-2); pos += 3)
		{
			out.append(MAC64DIGITS.charAt(byteval(in[pos+0]) >> 2));
			out.append(MAC64DIGITS.charAt(((byteval(in[pos+0])<<4) & 0x30) | (byteval(in[pos+1])>>4)));
			out.append(MAC64DIGITS.charAt(((byteval(in[pos+1])<<2) & 0x3c) | (byteval(in[pos+2])>>6)));
			out.append(MAC64DIGITS.charAt(byteval(in[pos+2]) & 0x3f));
		}
		if (pos < in.length)
		{
			int fragment;

			out.append(MAC64DIGITS.charAt(byteval(in[pos+0]) >> 2));
			fragment = (byteval(in[pos+0]) << 4) & 0x30;
			if (pos < (in.length-1))
			{
				fragment |= byteval(in[pos+1]) >> 4;
			}
			out.append(MAC64DIGITS.charAt(fragment));
			if ((in.length-pos)>2)
			{
				out.append(MAC64DIGITS.charAt((byteval(in[pos+1]) << 2) & 0x3c));
			}
			else
			{
				out.append('-');
			}
			out.append('-');
		}
		return out.toString();
	}

	public void set(int key, String value)
	{
		keylist.add(new Integer(key));
		valuelist.add(value);
		String[] old = (String[])data.get(new Integer(key));
		if (old==null)
		{
			String[] newdata = new String[1];
			newdata[0]=value;
			data.put(new Integer(key),newdata);
		}
		else
		{
			String[] newdata = new String[old.length+1];
			for (int loop=0; loop<old.length; loop++)
			{
				newdata[loop]=old[loop];
			}
			newdata[old.length]=value;
			data.put(new Integer(key),newdata);
		}
	}

	public int count()
	{
		return keylist.size();
	}

	public int getKey(int pos)
	{
		return ((Integer)keylist.get(pos)).intValue();
	}

	public String getValue(int pos)
	{
		return (String)valuelist.get(pos);
	}

	public String[] get(int key)
	{
		return (String[])data.get(new Integer(key));
	}

	public int getSession()
	{
		return session;
	}

	public int getService()
	{
		return service;
	}

	public int getStatus()
	{
		return status;
	}

	public String toString()
	{
		StringWriter strout = new StringWriter();
		PrintWriter out = new PrintWriter(strout);
		out.println("YahooMessage service: "+service);
		out.println("status: "+status);
		out.println("session: "+session);
		Iterator keyloop = keylist.iterator();
		Iterator valueloop = valuelist.iterator();
		while (keyloop.hasNext())
		{
			Integer key = (Integer)keyloop.next();
			String value = (String)valueloop.next();
			out.println(key+": "+value);
		}
		return strout.toString();
	}

	public static YahooMessage decode(byte[] databytes)
	{
		try
		{
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(databytes));
			int test = in.readInt();
			if (test==0x594D5347)
			{
				in.readInt();
				int length = in.readShort();
				int service = in.readShort();
				int status = in.readInt();
				int session = in.readInt();
				byte[] data = new byte[length];
				in.read(data);
				List tokens = new ArrayList();
				int loop=0;
				int start=0;
				while (loop<(data.length-1))
				{
					if ((data[loop]==-64)&&(data[loop+1]==-128))
					{
						String thisitem = new String(data,start,loop-start);
						tokens.add(thisitem);
						loop+=2;
						start=loop;
					}
					else
					{
						loop++;
					}
				}
				YahooMessage message = new YahooMessage(service,status,session);
				while (tokens.size()>=2)
				{
					int key = Integer.parseInt(tokens.get(0).toString());
					tokens.remove(0);
					String value = tokens.get(0).toString();
					tokens.remove(0);
					message.set(key,value);
				}
				return message;
			}
			else
			{
				return null;
			}
		}
		catch (IOException e)
		{
			System.err.println("Exception in thread: "+e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public byte[] encode(int session)
	{
		ByteArrayOutputStream byteout = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteout);
		try
		{
			Iterator loop = data.keySet().iterator();
			while (loop.hasNext())
			{
				Integer key = (Integer)loop.next();
				String[] value = (String[])data.get(key);
				for (int count=0; count<value.length; count++)
				{
					out.writeBytes(key.toString());
					out.writeShort(0xc080);
					if (value!=null)
					{
						out.writeBytes(value[count]);
					}
					out.writeShort(0xc080);
				}
			}
			out.flush();
			byte[] databytes = byteout.toByteArray();
			byteout.reset();
			out.writeBytes("YMSG");
			out.writeByte(9);
			out.writeByte(0);
			out.writeByte(0);
			out.writeByte(0);
			out.writeShort(databytes.length);
			out.writeShort(service);
			out.writeInt(status);
			out.writeInt(session);
			out.write(databytes);
			out.flush();
			return byteout.toByteArray();
		}
		catch (IOException e)
		{
			return null;
		}
	}
}
