package com.blueprintit.multiim;

import javax.swing.Icon;
import java.util.Iterator;
import com.blueprintit.multiim.event.MessageListener;
import com.blueprintit.multiim.event.BuddyListener;
import com.blueprintit.multiim.event.GroupListener;

public interface Group
{
	public void initialise();

	public void addMessageListener(MessageListener l);

	public void removeMessageListener(MessageListener l);

	public void addBuddyListener(BuddyListener l);

	public void removeBuddyListener(BuddyListener l);

	public void addGroupListener(GroupListener e);

	public void removeGroupListener(GroupListener e);

	public String getName();

	public String getID();

	public Buddy getBuddy(String id);

	public void addBuddy(String id);

	public void removeBuddy(Buddy b);

	public Iterator getBuddies();

	public Service getService();

	public Icon getIcon();
}
