package com.blueprintit.multiim.event;

import com.blueprintit.multiim.Buddy;
import java.util.List;
import java.util.ArrayList;
import javax.swing.text.AttributeSet;

public class MessageEvent
{
	private Buddy source;
	private int id;
	private String message = "";
	private List text = new ArrayList();
	private List attrs = new ArrayList();

	public static final int MESSAGERECEIVED = 1;
	public static final int TYPINGSTARTED = 2;
	public static final int TYPINGFINISHED = 3;

	public MessageEvent(Buddy source, int id)
	{
		this.source=source;
		this.id=id;
	}

	public void addMessageBlock(String text, AttributeSet attrs)
	{
		message+=text;
		this.text.add(text);
		this.attrs.add(attrs);
	}

	public AttributeSet[] getMessageAttributes()
	{
		AttributeSet[] attrs = new AttributeSet[this.attrs.size()];
		this.attrs.toArray(attrs);
		return attrs;
	}

	public String[] getMessageText()
	{
		String[] text = new String[this.text.size()];
		this.text.toArray(text);
		return text;
	}

	public String getMessage()
	{
		return message;
	}

	public String toString()
	{
		return "MessageEvent "+id+" from "+source+": "+message;
	}

	public Buddy getSource()
	{
		return source;
	}

	public int getId()
	{
		return id;
	}
}
