package com.blueprintit.multiim;

import javax.swing.Icon;
import java.util.Iterator;
import java.awt.Component;
import javax.swing.text.Document;
import javax.swing.text.Position;
import javax.swing.text.AttributeSet;
import com.blueprintit.multiim.event.MessageListener;
import com.blueprintit.multiim.event.BuddyListener;
import com.blueprintit.multiim.event.GroupListener;
import com.blueprintit.multiim.event.ServiceListener;

public interface Service
{
	public ServiceProvider getServiceProvider();

	public void initialise();

	public void documentInsert(Document doc, Component c, Position pos, String text, AttributeSet attrs);

	public void addMessageListener(MessageListener l);

	public void removeMessageListener(MessageListener l);

	public void addBuddyListener(BuddyListener l);

	public void removeBuddyListener(BuddyListener l);

	public void addGroupListener(GroupListener e);

	public void removeGroupListener(GroupListener e);

	public void addServiceListener(ServiceListener e);

	public void removeServiceListener(ServiceListener e);

	public String getName();

	public String getID();

	public String getUsername();

	public void connect();

	public void disconnect();

	public boolean isConnected();

	public boolean isAway();

	public boolean isInvisible();

	public void setAvailable();

	public String getStatusName(int status);

	public int getMaxStatus();

	public boolean isCustomStatus(int status);

	public void setAway(int status, String message);

	public String getStatusMessage();

	public Group getGroup(String id);

	public Group addGroup(String id);

	public void removeGroup(Group g);

	public Iterator getGroups();

	public Icon getIcon();
}
