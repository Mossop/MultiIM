package com.blueprintit.multiim.event;

import com.blueprintit.multiim.Group;
import com.blueprintit.multiim.Buddy;

public class GroupEvent
{
	private Group source;
	private int id;
	private Buddy buddy;

	public static final int BUDDYADDED = 1;
	public static final int BUDDYREMOVED = 2;

	public GroupEvent(Group source, int id)
	{
		this.source=source;
		this.id=id;
	}

	public String toString()
	{
		return "GroupEvent "+id+" from "+source;
	}

	public void setBuddy(Buddy b)
	{
		if ((id==BUDDYADDED)||(id==BUDDYREMOVED))
		{
			buddy=b;
		}
	}

	public Buddy getBuddy()
	{
		return buddy;
	}

	public Group getSource()
	{
		return source;
	}

	public int getId()
	{
		return id;
	}
}
