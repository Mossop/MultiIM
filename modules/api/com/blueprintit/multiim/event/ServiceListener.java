package com.blueprintit.multiim.event;

public interface ServiceListener
{
	public void serviceConnected(ServiceEvent e);

	public void serviceDisconnected(ServiceEvent e);

	public void serviceStatusChanged(ServiceEvent e);

	public void groupAdded(ServiceEvent e);

	public void groupRemoved(ServiceEvent e);
}
