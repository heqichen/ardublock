package com.ardublock;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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
	
	public String getValue(String key, String defaultValue)
	{
		try
		{
			Properties props = new Properties();
			FileReader reader = new FileReader(configFile);
			// load the properties file:
			props.load(reader);
			String value = props.getProperty(key, defaultValue);
			reader.close();
			return value;
		}
		catch (IOException e)
		{
			return null;
		}
	}
	
	public void setValue(String key, String value)
	{
		try
		{
			Properties props = new Properties();
			FileWriter writer = new FileWriter(configFile);
			// load the properties file:
			props.setProperty(key, value);
			props.store(writer, null);
			writer.close();
		}
		catch (IOException e)
		{
		}
	}

}
