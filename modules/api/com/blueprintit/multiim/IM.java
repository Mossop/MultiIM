package com.blueprintit.multiim;

import java.util.Iterator;
import com.blueprintit.multiim.event.MessageListener;
import com.blueprintit.multiim.event.BuddyListener;
import com.blueprintit.multiim.event.GroupListener;
import com.blueprintit.multiim.event.ServiceListener;
import com.blueprintit.multiim.event.IMListener;
import com.blueprintit.Module;

public interface IM extends Module
{
	public void addMessageListener(MessageListener l);

	public void removeMessageListener(MessageListener l);

	public void addBuddyListener(BuddyListener l);

	public void removeBuddyListener(BuddyListener l);

	public void addGroupListener(GroupListener e);

	public void removeGroupListener(GroupListener e);

	public void addServiceListener(ServiceListener e);

	public void removeServiceListener(ServiceListener e);

	public void addIMListener(IMListener e);

	public void removeIMListener(IMListener e);

	public void addService(ServiceProvider sp, String username, String password);

	public void removeService(Service s);

	public Iterator getServices();

	public void addServiceProvider(ServiceProvider s);

	public void removeServiceProvider(ServiceProvider s);

	public Iterator getServiceProviders();
}
