package com.blueprintit.multiim.event;

import com.blueprintit.multiim.event.MessageEvent;

public interface MessageListener
{
	public void messageReceived(MessageEvent e);

	public void messageTypingStarted(MessageEvent e);

	public void messageTypingFinished(MessageEvent e);
}
