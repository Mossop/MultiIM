package com.blueprintit.multiim;

import com.blueprintit.multiim.event.BuddyEventSource;

public abstract class AbstractBuddy extends BuddyEventSource implements Buddy
{
	public boolean equals(Object o)
	{
		if (o instanceof Buddy)
		{
			return ((Buddy)o).getID().equals(getID());
		}
		else
		{
			return false;
		}
	}
}
