package com.blueprintit.multiim.event;

import com.blueprintit.multiim.Buddy;

public class BuddyEvent
{
	private Buddy source;
	private int id;
	private boolean real;

	public static final int CONNECTED = 1;
	public static final int DISCONNECTED = 2;
	public static final int STATUSCHANGED = 3;

	public BuddyEvent(Buddy source, int id)
	{
		this.source=source;
		this.id=id;
		real=true;
	}

	public BuddyEvent(Buddy source, int id, boolean real)
	{
		this(source,id);
		this.real=real;
	}

	public boolean isReal()
	{
		return real;
	}

	public String toString()
	{
		return "BuddyEvent "+id+" from "+source;
	}

	public Buddy getSource()
	{
		return source;
	}

	public int getId()
	{
		return id;
	}
}
