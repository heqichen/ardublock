package com.ardublock.core;

public class MessageFetcher
{
	public void startFetchMessage()
	{
		Thread t = new Thread(new Runnable()
		{
			public void run()
			{
				String messagesJson = HttpFetcher.get("ardublock.heqichen.cn/messages.txt");
			}
			
		});
	}
}
