package com.blueprintit.multiim;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.util.Iterator;
import java.util.prefs.Preferences;
import com.blueprintit.ModuleInfo;
import com.blueprintit.multiim.event.IMListener;
import com.blueprintit.multiim.event.ServiceListener;
import com.blueprintit.multiim.event.GroupListener;
import com.blueprintit.multiim.event.BuddyListener;
import com.blueprintit.multiim.event.MessageListener;
import com.blueprintit.multiim.event.IMEvent;
import javax.swing.UIManager;
import com.blueprintit.multiim.event.ServiceEvent;
import com.blueprintit.multiim.event.GroupEvent;
import com.blueprintit.multiim.event.BuddyEvent;
import com.blueprintit.multiim.event.MessageEvent;
import com.blueprintit.ModularApplication;
import com.blueprintit.Application;

public class MultiIM extends AbstractIM implements IM,ServiceListener,GroupListener,BuddyListener,MessageListener
{
	public MultiIM()
	{
	}

	public void initialise()
	{
	}

	public void destroy()
	{
	}

	public void addServiceProvider(ServiceProvider sp)
	{
		super.addServiceProvider(sp);
		try
		{
			Application app = ModularApplication.getApplication();
			Preferences prefs = ModularApplication.getApplication().getModuleUserPreferences(this);
			prefs = prefs.node("services");
			if (prefs.nodeExists(app.getModuleInfo(sp).getID()))
			{
				Preferences sprefs = prefs.node(app.getModuleInfo(sp).getID());
				String users[] = sprefs.childrenNames();
				for (int loop=0; loop<users.length; loop++)
				{
					String password = sprefs.node(users[loop]).get("password","");
					Service plugin = sp.getService(users[loop],password);
					addService(plugin);
					plugin.addServiceListener(this);
					plugin.addGroupListener(this);
					plugin.addBuddyListener(this);
					plugin.addMessageListener(this);
				}
			}
		}
		catch (Exception e)
		{
		}
	}

	public void addService(ServiceProvider sp, String username, String password)
	{
		Service plugin = sp.getService(username,password);
		addService(plugin);
		try
		{
			Preferences prefs = ModularApplication.getApplication().getModuleUserPreferences(this);
			prefs = prefs.node("services").node(ModularApplication.getApplication().getModuleInfo(sp).getID());
			prefs = prefs.node(username);
			prefs.put("password",password);
			prefs.flush();
		}
		catch (Exception e)
		{
		}
		plugin.addServiceListener(this);
		plugin.addGroupListener(this);
		plugin.addBuddyListener(this);
		plugin.addMessageListener(this);
	}

	public void removeService(Service s)
	{
		s.removeServiceListener(this);
		s.removeGroupListener(this);
		s.removeBuddyListener(this);
		s.removeMessageListener(this);
		super.removeService(s);
		try
		{
			Preferences prefs = ModularApplication.getApplication().getModuleUserPreferences(this);
			prefs = prefs.node("services").node(ModularApplication.getApplication().getModuleInfo(s.getServiceProvider()).getID());
			prefs = prefs.node(s.getUsername());
			prefs.removeNode();
			prefs.flush();
		}
		catch (Exception e)
		{
		}
	}

	public void serviceConnected(ServiceEvent e)
	{
		deliverEvent(e);
	}

	public void serviceDisconnected(ServiceEvent e)
	{
		deliverEvent(e);
	}

	public void serviceStatusChanged(ServiceEvent e)
	{
		deliverEvent(e);
	}

	public void groupAdded(ServiceEvent e)
	{
		deliverEvent(e);
	}

	public void groupRemoved(ServiceEvent e)
	{
		deliverEvent(e);
	}

	public void buddyAdded(GroupEvent e)
	{
		deliverEvent(e);
	}

	public void buddyRemoved(GroupEvent e)
	{
		deliverEvent(e);
	}

	public void buddyConnected(BuddyEvent e)
	{
		deliverEvent(e);
	}

	public void buddyDisconnected(BuddyEvent e)
	{
		deliverEvent(e);
	}

	public void buddyStatusChanged(BuddyEvent e)
	{
		deliverEvent(e);
	}

	public void messageReceived(MessageEvent e)
	{
		deliverEvent(e);
	}

	public void messageTypingStarted(MessageEvent e)
	{
		deliverEvent(e);
	}

	public void messageTypingFinished(MessageEvent e)
	{
		deliverEvent(e);
	}
}
