package com.blueprintit.multiim.yahoo;

import com.blueprintit.multiim.event.BuddyEvent;
import com.blueprintit.multiim.event.MessageEvent;
import com.blueprintit.multiim.event.GroupEvent;
import com.blueprintit.multiim.AbstractGroup;
import com.blueprintit.multiim.Service;
import com.blueprintit.multiim.Buddy;
import com.blueprintit.multiim.Group;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import javax.swing.Icon;

public class YahooGroup extends AbstractGroup
{
	private String name;
	private YahooService service;

	public YahooGroup(YahooService service, String name)
	{
		this.name=name;
		this.service=service;
	}

	void spreadEvent(BuddyEvent e)
	{
		deliverEvent(e);
		service.spreadEvent(e);
	}

	void spreadEvent(MessageEvent e)
	{
		deliverEvent(e);
		service.spreadEvent(e);
	}

	protected void deliverEvent(GroupEvent e)
	{
		super.deliverEvent(e);
		service.spreadEvent(e);
	}

	public void addBuddy(String id)
	{
		YahooMessage msg = new YahooMessage(YahooMessage.YAHOO_SERVICE_ADDBUDDY,YahooMessage.YAHOO_STATUS_AVAILABLE);
		msg.set(1,service.getUsername());
		msg.set(7,id);
		msg.set(65,getName());
		service.queueMessage(msg);
	}

	public void addBuddy(Buddy buddy)
	{
		super.addBuddy(buddy);
		service.buddies.put(buddy.getID(),buddy);
	}

	protected Buddy createBuddy(String id)
	{
		Buddy buddy = new YahooBuddy(this,id);
		return buddy;
	}

	public void removeBuddy(Buddy buddy)
	{
		super.removeBuddy(buddy);
		service.buddies.remove(buddy.getID());
	}

	public String getID()
	{
		return name;
	}

	public String getName()
	{
		return name;
	}

	public Icon getIcon()
	{
		return null;
	}

	public Service getService()
	{
		return service;
	}

	void processGroupList(String buddies)
	{
		List original = new ArrayList();
		Iterator loop = getBuddies();
		while (loop.hasNext())
		{
			original.add(loop.next());
		}
		String[] ids = buddies.split(",");
		for (int count=0; count<ids.length; count++)
		{
			Buddy buddy = getBuddy(ids[count]);
			if (buddy==null)
			{
				buddy=createBuddy(ids[count]);
				addBuddy(buddy);
			}
			else
			{
				original.remove(buddy);
			}
		}
		loop = original.iterator();
		while (loop.hasNext())
		{
			removeBuddy((Buddy)loop.next());
		}
	}

}
