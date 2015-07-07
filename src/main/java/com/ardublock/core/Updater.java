package com.ardublock.core;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

public class Updater
{
	public static void main(String args[]) throws Exception
	{
		ResourceBundle uiMessageBundle = ResourceBundle.getBundle("com/ardublock/block/ardublock");
		Updater u = new Updater();
		System.out.println(u.getOsInfo());
		System.out.println(u.getMachineId());
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
	
}
