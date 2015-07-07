package com.ardublock.core;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

public class Updater
{
	public static void main(String args[]) throws Exception
	{
		ResourceBundle uiMessageBundle = ResourceBundle.getBundle("com/ardublock/block/ardublock");
		
		int internalVersion = Integer.parseInt(uiMessageBundle.getString("ardublock.ui.version-internal"));
		
		
		Updater u = new Updater();
		System.out.println(u.getOsInfo());
		System.out.println(u.getMachineId());
		System.out.println(internalVersion);
		System.out.println(u.getLocale());
	}
	
	private String getOsInfo()
	{
		String os = System.getProperty ("os.name") + " " + System.getProperty ("os.version") + " " + System.getProperty ("os.arch");
		return os;
	}
	
	private String getMachineId()
	{
		String mid = "unknown";
		
		try
		{
			Enumeration<NetworkInterface> netifList = NetworkInterface.getNetworkInterfaces();
			while (netifList.hasMoreElements())
			{
				NetworkInterface nif = netifList.nextElement();
				byte[] mac = nif.getHardwareAddress();
				if (mac != null)
				{
					StringBuilder sb = new StringBuilder(16);
					for (int k = 0; k < mac.length; k++) {
						sb.append(String.format("%02x", mac[k]));
					}
					mid = sb.toString();
					return mid;
				}
			}
		}
		catch (SocketException e) 
		{
			e.printStackTrace();
		}
		
		return mid;
	}
	
	private String getLocale()
	{
		Locale locale = Locale.getDefault();
		return locale.toString();
	}
	
}
