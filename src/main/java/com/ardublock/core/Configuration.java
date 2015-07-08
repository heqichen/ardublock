package com.ardublock.core;

import java.io.File;

import processing.app.Base;

public class Configuration
{
	private Context context;
	private File configFile;
	public Configuration(Context contxt)
	{
		this.context = context;
		configFile = loadConfigFile();
	}
	
	private File loadConfigFile()
	{
		if (context.isInArduino())
		{
			
		}
		else
		{
			
		}
		return null;
	}
	
	public static void main(String args[])
	{
	}
	
	
}
