package com.ardublock.ui.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.ardublock.Context;
import com.ardublock.ui.OpenblocksFrame;

public class OpenButtonListener implements ActionListener
{
	private OpenblocksFrame parentFrame;
	private Context context;
	
	public OpenButtonListener(Context context, OpenblocksFrame frame)
	{
		this.context = context;
		this.parentFrame = frame;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		parentFrame.doOpenArduBlockFile();
	}

}
