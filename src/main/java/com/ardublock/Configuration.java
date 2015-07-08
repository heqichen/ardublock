package com.ardublock;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import processing.app.Preferences;

public class Configuration
{
	private Context context;
	private File configFile;
	public Configuration(Context context)
	{
		this.context = context;
		File configPath = loadConfigFile();
		Properties props = new Properties();
		configFile = new File(configPath.getAbsolutePath(), "ardublock.properties");
		if (!configFile.isFile())
	    {
			try
			{
				configFile.createNewFile();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
	    }
		
	}
	
	private File loadConfigFile()
	{
		if (context.isInArduino())
		{
			return new File(Preferences.get("sketchbook.path"));
		}
		else
		{
			return new File(System.getProperty("user.home") + File.separator + "sketchbook");
		}
	}

}
