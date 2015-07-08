package com.ardublock.ui;

import java.awt.Desktop;
import java.awt.Dimension;
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
import javax.swing.SwingConstants;

import com.ardublock.core.Updater;

public class AboutDialog extends JDialog
{

	/**
	 * 
	 */
	private ResourceBundle uiMessageBundle;
	private static final long serialVersionUID = -8860041467043037212L;
	
	public AboutDialog(JFrame parentFrame, final Updater updater)
	{
		
		
		super(parentFrame, "About Dialog", true);
		
		uiMessageBundle = ResourceBundle.getBundle("com/ardublock/block/ardublock");
		
		Box b = Box.createVerticalBox();
		
		b.add(Box.createGlue());
		
		JLabel title = new JLabel("<html><h1><center>ArduBlock</center></h1></html>");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setFont(new Font(title.getFont().getName(), Font.BOLD, 30));
		b.add(title);
		b.add(Box.createGlue());
		
		Box versionWrap = Box.createHorizontalBox();
		versionWrap.add(Box.createGlue());
	    JLabel version = new JLabel(uiMessageBundle.getString("ardublock.ui.version"));
	    version.setHorizontalAlignment(SwingConstants.RIGHT);
	    b.setSize(250, 30);
	    versionWrap.add(version);
	    b.add(versionWrap);
	    
	    JLabel description = new JLabel("<html>"+ uiMessageBundle.getString("ardublock.ui.description") +"</html>");
	    description.setPreferredSize(new Dimension(300, 200));
		b.add(description);
		b.add(Box.createGlue());
		
		b.add(new JLabel("http://ardublock.heqichen.cn/"));
		b.add(Box.createGlue());
		
		JPanel textPanel = new JPanel();
		textPanel.add(b);
		
		getContentPane().add(textPanel, "North");
		
		JPanel buttonPanel = new JPanel();
		
		
		JButton websiteButton = new JButton(uiMessageBundle.getString("ardublock.ui.website"));
		buttonPanel.add(websiteButton);
		websiteButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
			    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			    URL url;
			    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			        try {
						url = new URL("http://ardublock.heqichen.cn");
			            desktop.browse(url.toURI());
			        } catch (Exception e1) {
			            e1.printStackTrace();
			        }
			    }
			}
		});
		
		
		JButton updateButton = new JButton(uiMessageBundle.getString("ardublock.ui.checkupdate"));
		buttonPanel.add(updateButton);
		updateButton.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent arg0)
			{
				updater.startCheckSync("click-in-about", null);
			}
			
		});
		
		
		JButton closeButton = new JButton(uiMessageBundle.getString("ardublock.ui.close"));
		buttonPanel.add(closeButton);
		closeButton.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent arg0)
			{
				setVisible(false);
			}
			
		});

		getContentPane().add(buttonPanel, "South");
		
		setSize(400, 380);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocationRelativeTo(null);
		

	}
	
}
