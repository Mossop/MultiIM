package com.blueprintit.multiim.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class ServiceEventSource extends GroupEventSource
{
	private List servicelisteners = new ArrayList();

	public void addServiceListener(ServiceListener l)
	{
		synchronized(servicelisteners)
		{
			servicelisteners.add(l);
		}
	}

	public void removeServiceListener(ServiceListener l)
	{
		synchronized(servicelisteners)
		{
			servicelisteners.remove(l);
		}
	}

	protected void deliverEvent(ServiceEvent e)
	{
		List newlist;
		synchronized(servicelisteners)
		{
			newlist = new ArrayList(servicelisteners);
		}
		Iterator loop = newlist.iterator();
		while (loop.hasNext())
		{
			ServiceListener l = (ServiceListener)loop.next();
			if (e.getId()==e.CONNECTED)
			{
				l.serviceConnected(e);
				l.serviceStatusChanged(e);
			}
			else if (e.getId()==e.DISCONNECTED)
			{
				l.serviceStatusChanged(e);
				l.serviceDisconnected(e);
			}
			else if (e.getId()==e.GROUPADDED)
			{
				l.groupAdded(e);
			}
			else if (e.getId()==e.STATUSCHANGED)
			{
				l.serviceStatusChanged(e);
			}
		}
	}
}
