package com.blueprintit.multiim.yahoo;

import com.blueprintit.Module;
import com.blueprintit.multiim.IM;
import com.blueprintit.multiim.AbstractServiceProvider;
import com.blueprintit.multiim.Service;

public class Yahoo extends AbstractServiceProvider
{
	public Yahoo()
	{
	}

	public String getName()
	{
		return "Yahoo";
	}

	public String getUsernameText()
	{
		return "Yahoo ID:";
	}

	public Service getService(String username,String password)
	{
		YahooService service = new YahooService(this);
		service.setLoginDetails(username,password);
		return service;
	}
}
