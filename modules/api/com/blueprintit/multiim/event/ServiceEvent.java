package com.blueprintit.multiim.event;

import com.blueprintit.multiim.Service;
import com.blueprintit.multiim.Group;

public class ServiceEvent
{
	private Service source;
	private int id;
	private Group group;

	public static final int CONNECTED = 1;
	public static final int DISCONNECTED = 2;
	public static final int GROUPADDED = 3;
	public static final int GROUPREMOVED = 4;
	public static final int STATUSCHANGED = 4;

	public ServiceEvent(Service source, int id)
	{
		this.source=source;
		this.id=id;
	}

	public String toString()
	{
		return "ServiceEvent "+id+" from "+source;
	}

	public void setGroup(Group g)
	{
		if ((id==GROUPADDED)||(id==GROUPREMOVED))
		{
			group=g;
		}
	}

	public Group getGroup()
	{
		return group;
	}

	public Service getSource()
	{
		return source;
	}

	public int getId()
	{
		return id;
	}
}
