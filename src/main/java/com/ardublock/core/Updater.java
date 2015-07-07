package com.ardublock.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Updater
{
	
	private ResourceBundle uiMessageBundle;
	int internalVersion;
	String os;
	String machineId;
	String lang;
	String queryUrl;
	public Updater()
	{
		uiMessageBundle = ResourceBundle.getBundle("com/ardublock/block/ardublock");
		internalVersion = Integer.parseInt(uiMessageBundle.getString("ardublock.ui.version-internal"));
		os = getOsInfo();
		machineId = getMachineId();
		lang = getLocale();
		
		try {
			queryUrl = "http://ardublock.heqichen.cn/version.php?mid=";
			queryUrl += URLEncoder.encode(machineId, "UTF-8");
			queryUrl += "&v=";
			queryUrl += String.valueOf(internalVersion);
			queryUrl += "&os=";
			queryUrl += URLEncoder.encode(os, "UTF-8");
			queryUrl += "&lang=";
			queryUrl += URLEncoder.encode(lang, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void startCheck(final String action, final JFrame parentFrame)
	{
		Thread t = new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					String result = Updater.this.checkUpdate(action);
					if (result.equals("updated"))
					{
						//already new
					}
					else
					{
						Object[] options = {"go to download page", "No"};
						int userSel = JOptionPane.showOptionDialog(parentFrame, 
								"message", 
								"title", 
								JOptionPane.YES_NO_OPTION, 
								JOptionPane.INFORMATION_MESSAGE, 
								null, 
								options, 
								options[1]);
						System.out.println(userSel);
						
					}
				}
				catch (IOException e)
				{
					
				}
				
			}
			
		});
		t.start();
	}
	
	private String checkUpdate(String action) throws IOException
	{
		URL url = new URL(queryUrl + "&action=" + action);
		System.out.println(url.toString());
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", "ardublock");
		int responseCode = con.getResponseCode();
		
		BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null)
		{
			response.append(inputLine);
		}
		
		in.close();
		return response.toString();
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
