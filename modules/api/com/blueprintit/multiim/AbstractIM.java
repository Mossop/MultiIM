package com.blueprintit.multiim;

import com.blueprintit.multiim.event.IMEventSource;
import com.blueprintit.multiim.event.IMEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class AbstractIM extends IMEventSource implements IM
{
	private List services = new ArrayList();
	private Map plugins = new HashMap();
	private List sps = new ArrayList();

	protected void addService(Service s)
	{
		synchronized(services)
		{
			services.add(s);
		}
		IMEvent e = new IMEvent(this,IMEvent.SERVICEADDED);
		e.setService(s);
		deliverEvent(e);
	}

	public void removeService(Service s)
	{
		synchronized(services)
		{
			services.remove(s);
		}
		IMEvent e = new IMEvent(this,IMEvent.SERVICEREMOVED);
		e.setService(s);
		deliverEvent(e);
	}

	public Iterator getServices()
	{
		synchronized(services)
		{
			return (new ArrayList(services)).iterator();
		}
	}

	public void addServiceProvider(ServiceProvider s)
	{
		synchronized(sps)
		{
			sps.add(s);
		}
		IMEvent e = new IMEvent(this,IMEvent.SERVICEPROVIDERADDED);
		e.setServiceProvider(s);
		deliverEvent(e);
	}

	public void removeServiceProvider(ServiceProvider s)
	{
		synchronized(sps)
		{
			sps.remove(s);
		}
		IMEvent e = new IMEvent(this,IMEvent.SERVICEPROVIDERREMOVED);
		e.setServiceProvider(s);
		deliverEvent(e);
	}

	public Iterator getServiceProviders()
	{
		synchronized(sps)
		{
			return (new ArrayList(sps)).iterator();
		}
	}
}
