package com.blueprintit.multiim;

import com.blueprintit.multiim.event.GroupEventSource;
import com.blueprintit.multiim.event.GroupEvent;
import com.blueprintit.ModularApplication;
import java.util.prefs.Preferences;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import javax.swing.Icon;

public abstract class AbstractGroup extends GroupEventSource implements Group
{
	private Map buddies = new HashMap();

	public Buddy getBuddy(String id)
	{
		synchronized(buddies)
		{
			return (Buddy)buddies.get(id);
		}
	}

	public void initialise()
	{
		try
		{
			Preferences prefs = ModularApplication.getApplication().getModuleUserPreferences(getService().getServiceProvider());
			prefs=prefs.node(getService().getID()+"/buddycache");
			Preferences grpprefs = prefs.node(getID());
			String[] buddies = grpprefs.keys();
			for (int count=0; count<buddies.length; count++)
			{
				Buddy buddy = createBuddy(buddies[count]);
				addBuddy(buddy);
				//buddy.setName(grpprefs.get(buddies[count],buddies[count]));
			}
		}
		catch (Exception e)
		{
		}
	}

	protected abstract Buddy createBuddy(String id);

	protected void addBuddy(Buddy b)
	{
		try
		{
			Preferences prefs = ModularApplication.getApplication().getModuleUserPreferences(getService().getServiceProvider());
			prefs=prefs.node(getService().getID()+"/buddycache/"+getID());
			prefs.put(b.getID(),b.getName());
			prefs.flush();
		}
		catch (Exception e)
		{
		}
		synchronized(buddies)
		{
			buddies.put(b.getID(),b);
		}
		GroupEvent e = new GroupEvent(this,GroupEvent.BUDDYADDED);
		e.setBuddy(b);
		deliverEvent(e);
	}

	public void removeBuddy(Buddy b)
	{
		try
		{
			Preferences prefs = ModularApplication.getApplication().getModuleUserPreferences(getService().getServiceProvider());
			prefs=prefs.node(getService().getID()+"/buddycache/"+getID());
			prefs.remove(b.getID());
			prefs.flush();
		}
		catch (Exception e)
		{
		}
		synchronized(buddies)
		{
			buddies.remove(b.getID());
		}
		GroupEvent e = new GroupEvent(this,GroupEvent.BUDDYREMOVED);
		e.setBuddy(b);
		deliverEvent(e);
	}

	public Iterator getBuddies()
	{
		synchronized(buddies)
		{
			return (new ArrayList(buddies.values())).iterator();
		}
	}

	public boolean equals(Object o)
	{
		if (o instanceof Group)
		{
			return ((Group)o).getID().equals(getID());
		}
		else
		{
			return false;
		}
	}
}
