package com.blueprintit.multiim.event;

import com.blueprintit.multiim.Service;
import com.blueprintit.multiim.ServiceProvider;
import com.blueprintit.multiim.IM;

public class IMEvent
{
	private IM source;
	private int id;
	private Service service;
	private ServiceProvider sp;

	public static final int SERVICEADDED = 1;
	public static final int SERVICEREMOVED = 2;
	public static final int SERVICEPROVIDERADDED = 3;
	public static final int SERVICEPROVIDERREMOVED = 4;

	public IMEvent(IM source, int id)
	{
		this.source=source;
		this.id=id;
	}

	public String toString()
	{
		return "IMEvent "+id+" from "+source;
	}

	public void setService(Service s)
	{
		if ((id==SERVICEADDED)||(id==SERVICEREMOVED))
		{
			service=s;
		}
	}

	public Service getService()
	{
		if ((id==SERVICEADDED)||(id==SERVICEREMOVED))
		{
			return service;
		}
		else
		{
			return null;
		}
	}

	public void setServiceProvider(ServiceProvider s)
	{
		if ((id==SERVICEPROVIDERADDED)||(id==SERVICEPROVIDERREMOVED))
		{
			sp=s;
		}
	}

	public ServiceProvider getServiceProvider()
	{
		if ((id==SERVICEPROVIDERADDED)||(id==SERVICEPROVIDERREMOVED))
		{
			return sp;
		}
		else
		{
			return null;
		}
	}

	public IM getSource()
	{
		return source;
	}

	public int getId()
	{
		return id;
	}
}

