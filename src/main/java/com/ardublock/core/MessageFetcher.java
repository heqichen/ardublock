package com.ardublock.core;

import javax.swing.JDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ardublock.Configuration;
import com.ardublock.Context;
import com.ardublock.ui.MessageDialog;

public class MessageFetcher
{
	private MessageDialog messageDialog;
	private Context context;
	private Configuration config;
	public void startFetchMessage(MessageDialog messageDialog, Context context)
	{
		this.messageDialog = messageDialog;
		this.context = context;
		config = context.getConfiguration();
		
		Thread t = new Thread(new Runnable()
		{
			public void run()
			{
				String messagesJson = HttpFetcher.get("http://ardublock.heqichen.cn/messages.txt");
				if (messagesJson != null)
				{
					handleMessageJson(messagesJson);
				}
			}
			
		});
		t.start();
	}
	
	private void handleMessageJson(String msgStr)
	{
		try
		{
			JSONObject json = new JSONObject(msgStr);
			JSONArray messageArray = (JSONArray) json.get("messages");
			System.out.println(messageArray.length());
			int i;
			for (i=0; i<messageArray.length(); ++i)
			{
				JSONObject message = (JSONObject) messageArray.get(i);
				showUnshowedMessage(message);
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	private void showUnshowedMessage(JSONObject message)
	{
		try
		{
			int showedMessageId = Integer.parseInt(config.getValue("message.showed.id", "0"));
			int id = message.getInt("id");
			String text = message.getString("message");
			boolean force = message.getBoolean("force");
			
			if (id > showedMessageId)
			{
				showedMessageId = id;
				config.setValue("message.showed.id", String.valueOf(showedMessageId));
				messageDialog.setMessage(text);
				messageDialog.setVisible(true);
			}
			
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		
	}
}

