package com.blueprintit.multiim.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class IMEventSource extends ServiceEventSource
{
	private List imlisteners = new ArrayList();

	public void addIMListener(IMListener l)
	{
		synchronized(imlisteners)
		{
			imlisteners.add(l);
		}
	}

	public void removeIMListener(IMListener l)
	{
		synchronized(imlisteners)
		{
			imlisteners.remove(l);
		}
	}

	protected void deliverEvent(IMEvent e)
	{
		List newlist;
		synchronized(imlisteners)
		{
			newlist = new ArrayList(imlisteners);
		}
		Iterator loop = newlist.iterator();
		while (loop.hasNext())
		{
			IMListener l = (IMListener)loop.next();
			if (e.getId()==e.SERVICEADDED)
			{
				l.serviceAdded(e);
			}
			else if (e.getId()==e.SERVICEREMOVED)
			{
				l.serviceRemoved(e);
			}
			else if (e.getId()==e.SERVICEPROVIDERADDED)
			{
				l.serviceProviderAdded(e);
			}
			else if (e.getId()==e.SERVICEPROVIDERREMOVED)
			{
				l.serviceProviderRemoved(e);
			}
		}
	}
}
