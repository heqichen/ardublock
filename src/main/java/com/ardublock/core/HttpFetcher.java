package com.ardublock.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpFetcher
{
	public static String get(String urlStr)
	{
		try
		{
			URL url = new URL(urlStr);
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
				return null;
			}
		}
		catch (IOException e)
		{
			
		}
		return null;
	}
}
