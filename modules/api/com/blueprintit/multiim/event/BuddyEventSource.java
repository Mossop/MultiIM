package com.blueprintit.multiim.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public abstract class BuddyEventSource
{
	private List buddylisteners = new ArrayList();
	private List messagelisteners = new ArrayList();

	public void addBuddyListener(BuddyListener l)
	{
		synchronized(buddylisteners)
		{
			buddylisteners.add(l);
		}
	}

	public void removeBuddyListener(BuddyListener l)
	{
		synchronized(buddylisteners)
		{
			buddylisteners.remove(l);
		}
	}

	public void addMessageListener(MessageListener l)
	{
		synchronized(messagelisteners)
		{
			messagelisteners.add(l);
		}
	}

	public void removeMessageListener(MessageListener l)
	{
		synchronized(messagelisteners)
		{
			messagelisteners.remove(l);
		}
	}

	protected void deliverEvent(BuddyEvent e)
	{
		List newlist;
		synchronized(buddylisteners)
		{
			newlist = new ArrayList(buddylisteners);
		}
		Iterator loop = newlist.iterator();
		while (loop.hasNext())
		{
			BuddyListener l = (BuddyListener)loop.next();
			if (e.getId()==e.CONNECTED)
			{
				l.buddyConnected(e);
				l.buddyStatusChanged(e);
			}
			else if (e.getId()==e.DISCONNECTED)
			{
				l.buddyStatusChanged(e);
				l.buddyDisconnected(e);
			}
			else if (e.getId()==e.STATUSCHANGED)
			{
				l.buddyStatusChanged(e);
			}
		}
	}

	protected void deliverEvent(MessageEvent e)
	{
		List newlist;
		synchronized(messagelisteners)
		{
			newlist = new ArrayList(messagelisteners);
		}
		Iterator loop = newlist.iterator();
		while (loop.hasNext())
		{
			MessageListener l = (MessageListener)loop.next();
			if (e.getId()==e.MESSAGERECEIVED)
			{
				l.messageReceived(e);
			}
			else if (e.getId()==e.TYPINGSTARTED)
			{
				l.messageTypingStarted(e);
			}
			else if (e.getId()==e.TYPINGFINISHED)
			{
				l.messageTypingFinished(e);
			}
		}
	}
}
