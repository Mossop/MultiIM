package com.blueprintit.multiim.event;

public interface IMListener
{
	public void serviceAdded(IMEvent e);

	public void serviceRemoved(IMEvent e);

	public void serviceProviderAdded(IMEvent e);

	public void serviceProviderRemoved(IMEvent e);
}
