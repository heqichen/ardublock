package com.ardublock.ui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.ardublock.core.Updater;

public class AboutDialog extends JDialog
{

	/**
	 * 
	 */
	private ResourceBundle uiMessageBundle;
	private static final long serialVersionUID = -8860041467043037212L;
	
	public AboutDialog(JFrame parentFrame, Updater updater)
	{
		
		
		super(parentFrame, "About Dialog", true);
		
		
		uiMessageBundle = ResourceBundle.getBundle("com/ardublock/block/ardublock");
		
		Box b = Box.createVerticalBox();
		b.add(Box.createGlue());
		
		JLabel title = new JLabel("<html><h1><center>ArduBlock</center></h1></html>");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setFont(new Font(title.getFont().getName(), Font.BOLD, 30));
		b.add(title);
		
	    JLabel version = new JLabel(uiMessageBundle.getString("ardublock.ui.version"));
	    version.setHorizontalAlignment(SwingConstants.RIGHT);
	    b.add(version);
		
		b.add(new JLabel("By Java source and support"));
		b.add(new JLabel("At www.java2s.com"));
		b.add(Box.createGlue());
		getContentPane().add(b, "Center");
		
		JPanel p2 = new JPanel();
		JButton ok = new JButton("Ok");
		p2.add(ok);
		getContentPane().add(p2, "South");
		
		ok.addActionListener(new ActionListener() {
		  public void actionPerformed(ActionEvent evt) {
		    setVisible(false);
		  }
		});
		
		setSize(250, 150);
	}
	
}
