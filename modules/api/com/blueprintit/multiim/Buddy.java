package com.blueprintit.multiim;

import javax.swing.Icon;
import com.blueprintit.multiim.event.MessageListener;
import com.blueprintit.multiim.event.BuddyListener;

public interface Buddy
{
	public void addMessageListener(MessageListener l);

	public void removeMessageListener(MessageListener l);

	public void addBuddyListener(BuddyListener l);

	public void removeBuddyListener(BuddyListener l);

	public String getName();

	public String getID();

	public Group getGroup();

	public boolean isConnected();

	public boolean isAway();

	public void sendMessage(String message);

	public String getStatusMessage();

	public Icon getIcon();
}
