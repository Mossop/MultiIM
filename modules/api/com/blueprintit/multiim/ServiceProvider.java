package com.blueprintit.multiim;

import com.blueprintit.Module;

public interface ServiceProvider extends Module
{
	public Service getService(String username, String password);

	public String getName();

	public String getUsernameText();

	public String getPasswordText();
}
