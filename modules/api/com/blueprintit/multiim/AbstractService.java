package com.blueprintit.multiim;

import com.blueprintit.multiim.event.ServiceEventSource;
import com.blueprintit.multiim.event.ServiceEvent;
import com.blueprintit.ModularApplication;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import javax.swing.Icon;
import java.util.prefs.Preferences;

public abstract class AbstractService extends ServiceEventSource implements Service
{
	private Map groups = new HashMap();

	public void initialise()
	{
		try
		{
			Preferences prefs = ModularApplication.getApplication().getModuleUserPreferences(getServiceProvider());
			prefs=prefs.node(getID()+"/buddycache");
			String[] groups = prefs.childrenNames();
			for (int loop=0; loop<groups.length; loop++)
			{
				Group group = createGroup(groups[loop]);
				addGroup(group);
				group.initialise();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	protected abstract Group createGroup(String id);

	public void saveSettings()
	{
	}

	public void destroy()
	{
		disconnect();
	}

	public Group getGroup(String id)
	{
		synchronized(groups)
		{
			return (Group)groups.get(id);
		}
	}

	protected void addGroup(Group g)
	{
		try
		{
			Preferences prefs = ModularApplication.getApplication().getModuleUserPreferences(getServiceProvider());
			prefs=prefs.node(getID()+"/buddycache/"+g.getID());
			prefs.flush();
		}
		catch (Exception e)
		{
		}
		synchronized(groups)
		{
			groups.put(g.getID(),g);
		}
		ServiceEvent e = new ServiceEvent(this,ServiceEvent.GROUPADDED);
		e.setGroup(g);
		deliverEvent(e);
	}

	public void removeGroup(Group g)
	{
		try
		{
			Preferences prefs = ModularApplication.getApplication().getModuleUserPreferences(getServiceProvider());
			prefs=prefs.node(getID()+"/buddycache/"+g.getID());
			prefs.removeNode();
			prefs.flush();
		}
		catch (Exception e)
		{
		}
		synchronized(groups)
		{
			groups.remove(g.getID());
		}
		ServiceEvent e = new ServiceEvent(this,ServiceEvent.GROUPREMOVED);
		e.setGroup(g);
		deliverEvent(e);
	}

	public Iterator getGroups()
	{
		synchronized(groups)
		{
			return (new ArrayList(groups.values())).iterator();
		}
	}

	public boolean equals(Object o)
	{
		if (o instanceof Service)
		{
			return ((Service)o).getID().equals(getID());
		}
		else
		{
			return false;
		}
	}
}
