package com.ardublock;

import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.ardublock.ui.ConsoleFrame;
import com.ardublock.ui.MessageDialog;
import com.ardublock.ui.OpenblocksFrame;

public class Main
{
	private OpenblocksFrame openblocksFrame;

	public static void main(String args[]) throws SAXException, IOException, ParserConfigurationException
	{
		Main me = new Main();
		me.startArdublock();
	}
	
	public void startArdublock() throws SAXException, IOException, ParserConfigurationException
	{
		startOpenblocksFrame();
		//startConsoleFrame();
	}
	
	private void startOpenblocksFrame() throws SAXException, IOException, ParserConfigurationException
	{
		Context context = new Context(false);
		
		openblocksFrame = new OpenblocksFrame(context);
		//openblocksFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		openblocksFrame.setVisible(true);
		
		MessageDialog d = new MessageDialog(openblocksFrame);
		d.show();
	}

	public void shutdown()
	{
		openblocksFrame.dispatchEvent(new WindowEvent(openblocksFrame, WindowEvent.WINDOW_CLOSING));
	}
	
	@SuppressWarnings("unused")
	private void startConsoleFrame()
	{
		ConsoleFrame consoleFrame = new ConsoleFrame();
		consoleFrame.setVisible(true);
	}
}
