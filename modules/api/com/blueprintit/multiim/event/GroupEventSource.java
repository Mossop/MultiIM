package com.blueprintit.multiim.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class GroupEventSource extends BuddyEventSource
{
	private List grouplisteners = new ArrayList();

	public void addGroupListener(GroupListener l)
	{
		synchronized(grouplisteners)
		{
			grouplisteners.add(l);
		}
	}

	public void removeGroupListener(GroupListener l)
	{
		synchronized(grouplisteners)
		{
			grouplisteners.remove(l);
		}
	}

	protected void deliverEvent(GroupEvent e)
	{
		List newlist;
		synchronized(grouplisteners)
		{
			newlist = new ArrayList(grouplisteners);
		}
		Iterator loop = newlist.iterator();
		while (loop.hasNext())
		{
			GroupListener l = (GroupListener)loop.next();
			if (e.getId()==e.BUDDYADDED)
			{
				l.buddyAdded(e);
			}
			else if (e.getId()==e.BUDDYREMOVED)
			{
				l.buddyRemoved(e);
			}
		}
	}
}
