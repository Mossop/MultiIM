package com.blueprintit.multiim.event;

public interface BuddyListener
{
	public void buddyConnected(BuddyEvent e);

	public void buddyDisconnected(BuddyEvent e);

	public void buddyStatusChanged(BuddyEvent e);
}
