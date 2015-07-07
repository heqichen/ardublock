package com.ardublock.core;

import java.awt.Desktop;
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
import java.text.MessageFormat;
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
	
	private void showDownloadAvailabeDialog(JFrame parentFrame, String newVersionName)
	{
		Object[] options = 
		{
			uiMessageBundle.getString("ardublock.ui.gotodownloadpage"), 
			uiMessageBundle.getString("ardublock.ui.dontdownload")
		};
		int userSel = JOptionPane.showOptionDialog(parentFrame, 
				MessageFormat.format(uiMessageBundle.getString("ardublock.ui.update.available"), newVersionName),
				uiMessageBundle.getString("ardublock.ui.update.title"), 
				JOptionPane.YES_NO_OPTION, 
				JOptionPane.INFORMATION_MESSAGE, 
				null, 
				options, 
				options[1]);
		if (userSel == 0)
		{
			Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		    URL url;
		    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
		        try {
					url = new URL("http://ardublock.heqichen.cn/blog/download/");
		            desktop.browse(url.toURI());
		        } catch (Exception e1) {
		            e1.printStackTrace();
		        }
		    }
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
					if (!result.equals("updated") && !result.equals("error"))
					{
						showDownloadAvailabeDialog(parentFrame, result);	
					}
				}
				catch (IOException e)
				{
					
				}
				
			}
			
		});
		t.start();
	}
	
	public void startCheckSync(final String action, final JFrame parentFrame)
	{
		try
		{
			String result = Updater.this.checkUpdate(action);
			if (!result.equals("updated") && !result.equals("error"))
			{
				showDownloadAvailabeDialog(parentFrame, result);
			}
			else
			{
				if (result.equals("updated"))
				{
					JOptionPane.showMessageDialog(
							parentFrame, 
							uiMessageBundle.getString("ardublock.ui.update.updated"), 
							uiMessageBundle.getString("ardublock.ui.update.title"), 
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(
					parentFrame, 
					uiMessageBundle.getString("ardublock.ui.update.error"), 
					uiMessageBundle.getString("ardublock.ui.error"), 
					JOptionPane.ERROR_MESSAGE);
		}
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
		if (responseCode == 200)
		{
			return response.toString().trim();
		}
		else
		{
			return "error";
		}
		
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
