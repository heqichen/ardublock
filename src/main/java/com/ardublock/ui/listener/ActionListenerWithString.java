package com.ardublock.ui.listener;

import java.awt.event.ActionListener;

public abstract class ActionListenerWithString implements ActionListener
{
	
	public ActionListenerWithString(String str)
	{
		this.str = str;
	}

	private String str;
	public String getStr()
	{
		return str;
	}
}
