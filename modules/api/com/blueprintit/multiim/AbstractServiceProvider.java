package com.blueprintit.multiim;

import com.blueprintit.ModularApplication;

public abstract class AbstractServiceProvider implements ServiceProvider
{
	public String getUsernameText()
	{
		return "Username:";
	}

	public String getPasswordText()
	{
		return "Password:";
	}

	public String toString()
	{
		return getName();
	}

	public void initialise()
	{
		IM im = (IM)ModularApplication.getApplication().getModule(ModularApplication.getApplication().getModuleInfo("api"));
		im.addServiceProvider(this);
	}

	public void destroy()
	{
		IM im = (IM)ModularApplication.getApplication().getModule(ModularApplication.getApplication().getModuleInfo("api"));
		im.removeServiceProvider(this);
	}

}
