package com.ardublock;

import java.io.File;

import processing.app.Preferences;

public class Configuration
{
	private Context context;
	private File configFile;
	public Configuration(Context context)
	{
		this.context = context;
		configFile = loadConfigFile();
	}
	
	private File loadConfigFile()
	{
		if (context.isInArduino())
		{
			System.out.println("in arduino, sketchbook path: ");
			System.out.println(Preferences.get("sketchbook.path"));
		}
		else
		{
			System.out.println("not in arduino");
		}
		return null;
	}

}
