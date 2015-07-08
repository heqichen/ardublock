package com.ardublock.ui;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.ardublock.core.Updater;

public class MessageDialog extends JDialog
{
	private static final long serialVersionUID = -8291726696132421122L;
	
	private ResourceBundle uiMessageBundle;
	
	public MessageDialog(JFrame parentFrame)
	{
		
		super(parentFrame, "Message", true);
		
		uiMessageBundle = ResourceBundle.getBundle("com/ardublock/block/ardublock");
		this.setTitle(uiMessageBundle.getString("ardublock.ui.message"));
		
		JPanel p =new JPanel();
		p.setBorder(new EmptyBorder(20,20,20,20));
		p.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		JTextArea textArea = new JTextArea("dummy");
		//textArea.setFont(new Font("Serif", Font.ITALIC, 16));
		
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		
		JScrollPane areaScrollPane = new JScrollPane(textArea);
		areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane.setPreferredSize(new Dimension(360, 165));
		
		p.add(areaScrollPane);
		
		JButton closeButton = new JButton(uiMessageBundle.getString("ardublock.ui.close"));
		closeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				MessageDialog.this.setVisible(false);
			}
		});
		p.add(closeButton);
		
		this.add(p);
		setSize(400, 260);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
	}

}
