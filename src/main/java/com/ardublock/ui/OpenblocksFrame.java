package com.ardublock.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.ardublock.Context;
import com.ardublock.core.Example;
import com.ardublock.core.ExampleReader;
import com.ardublock.core.Updater;
import com.ardublock.ui.listener.ActionListenerWithString;
import com.ardublock.ui.listener.ArdublockWorkspaceListener;
import com.ardublock.ui.listener.GenerateCodeButtonListener;
import com.ardublock.ui.listener.NewButtonListener;
import com.ardublock.ui.listener.OpenButtonListener;
import com.ardublock.ui.listener.OpenblocksFrameListener;
import com.ardublock.ui.listener.SaveAsButtonListener;
import com.ardublock.ui.listener.SaveButtonListener;

import edu.mit.blocks.controller.WorkspaceController;
import edu.mit.blocks.workspace.Workspace;
import processing.app.Preferences;


public class OpenblocksFrame extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2841155965906223806L;

	private Context context;
	private JFileChooser fileChooser;
	private FileFilter ffilter;
	private Updater updater;
	private AboutDialog aboutDialog;
	private JLabel serialLabel;
	private ResourceBundle uiMessageBundle;
	
	public void addListener(OpenblocksFrameListener ofl)
	{
		context.registerOpenblocksFrameListener(ofl);
	}
	
	public void refreshSerialPort()
	{
		if (context.isInArduino())
		{
			serialLabel.setText(Preferences.get("serial.port"));
		}
	}
	
	
	public String makeFrameTitle()
	{
		String title = Context.APP_NAME + " " + context.getSaveFileName();
		if (context.isWorkspaceChanged())
		{
			title = title + " *";
		}
		return title;
		
	}
	
	public OpenblocksFrame(Context context)
	{
		this.context = context;
		this.setTitle(makeFrameTitle());
		this.setSize(new Dimension(1024, 760));
		this.setLayout(new BorderLayout());
		//put the frame to the center of screen
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		uiMessageBundle = ResourceBundle.getBundle("com/ardublock/block/ardublock");
		
		fileChooser = new JFileChooser();
		ffilter = new FileNameExtensionFilter(uiMessageBundle.getString("ardublock.file.suffix"), "abp");
		fileChooser.setFileFilter(ffilter);
		fileChooser.addChoosableFileFilter(ffilter);
		
		updater = new Updater();
		aboutDialog = new AboutDialog(this, updater);
		
		initOpenBlocks();
		
		updater.startCheck("startup", this);
		
		
	}
	
	private void renderHelpMenu(JMenu helpMenu)
	{

		
		JMenuItem checkUpdateItem = new JMenuItem(uiMessageBundle.getString("ardublock.ui.checkupdate"));
		checkUpdateItem.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent arg0)
			{
				OpenblocksFrame.this.updater.startCheckSync("click-in-menu", OpenblocksFrame.this);
			}
			
		});
		helpMenu.add(checkUpdateItem);
		
		
		JMenuItem websiteItem = new JMenuItem(uiMessageBundle.getString("ardublock.ui.website"));
		websiteItem.addActionListener(new ActionListener () {
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
		helpMenu.add(websiteItem);
		
				
		JMenuItem aboutItem = new JMenuItem(uiMessageBundle.getString("ardublock.ui.about"));
		aboutItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				aboutDialog.setVisible(true);
			}
		});
		helpMenu.add(aboutItem);
	}
	
	private JMenu renderExapmleMenu()
	{
		JMenu exampleMenu = new JMenu(uiMessageBundle.getString("ardublock.ui.examples"));
		ExampleReader exampleReader = new ExampleReader();
		List<Example> exampleList = exampleReader.getExampleList();
		for (Example e : exampleList)
		{
			JMenu menu = new JMenu(e.getName());
			JMenuItem filenameItem = new JMenuItem(e.getFilename());
			menu.add(filenameItem);
			filenameItem.addActionListener(new ActionListenerWithString(e.getFilename()){

				public void actionPerformed(ActionEvent arg0)
				{
					openArdublockExample(this.getStr());
				}
				
			});
			
			if (e.getTutorialLink() != null && !e.getTutorialLink().isEmpty())
			{
				JMenuItem linkItem = new JMenuItem(uiMessageBundle.getString("ardublock.ui.examplelink"));
				linkItem.addActionListener(new ActionListenerWithString(e.getTutorialLink()) {

					public void actionPerformed(ActionEvent e) {
						Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
					    URL url;
					    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
					        try {
								url = new URL(this.getStr());
					            desktop.browse(url.toURI());
					        } catch (Exception e1) {
					            e1.printStackTrace();
					        }
					    }
						
					}
					
				});
				menu.add(linkItem);
			}
			
			exampleMenu.add(menu);
			
		}
		return exampleMenu;
	}
	
	private void initOpenBlocks()
	{
		
		/*
		WorkspaceController workspaceController = context.getWorkspaceController();
		JComponent workspaceComponent = workspaceController.getWorkspacePanel();
		*/
		
		final Workspace workspace = context.getWorkspace();
		
		// WTF I can't add worksapcelistener by workspace contrller
		workspace.addWorkspaceListener(new ArdublockWorkspaceListener(context, this));
		
		JPanel toolbar = new JPanel();
		JPanel buttons = new JPanel();
		
		final JLabel buttonTooltip = new JLabel();
		
		
		final JButton newButton = new JButton();
		newButton.setIcon(new ImageIcon(this.getClass().getResource("/com/ardublock/icons/new.png")));
		newButton.setContentAreaFilled(false); 
		newButton.setBackground(Color.LIGHT_GRAY);
		newButton.setPreferredSize( new Dimension(48, 48));
		newButton.setOpaque(false);
		newButton.setToolTipText(uiMessageBundle.getString("ardublock.ui.new"));
		newButton.addActionListener(new NewButtonListener(context, this));
		newButton.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent arg0) {}
			public void mouseEntered(MouseEvent arg0) {
				buttonTooltip.setText(uiMessageBundle.getString("ardublock.ui.new"));
				newButton.setContentAreaFilled(true); 
			}
			public void mouseExited(MouseEvent arg0) {
				buttonTooltip.setText("");
				newButton.setContentAreaFilled(false);
			}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {}
	
		});

		final JButton saveButton = new JButton();
		saveButton.setIcon(new ImageIcon(this.getClass().getResource("/com/ardublock/icons/save.png")));
		saveButton.setContentAreaFilled(false); 
		saveButton.setBackground(Color.LIGHT_GRAY);
		saveButton.setPreferredSize( new Dimension(48,48));
		saveButton.setOpaque(false);
		saveButton.setToolTipText(uiMessageBundle.getString("ardublock.ui.save"));
		saveButton.addActionListener(new SaveButtonListener(context, this));
		saveButton.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent arg0) {}
			public void mouseEntered(MouseEvent arg0) {
				saveButton.setContentAreaFilled(true); 
				buttonTooltip.setText(uiMessageBundle.getString("ardublock.ui.save"));
			}
			public void mouseExited(MouseEvent arg0) {
				saveButton.setContentAreaFilled(false);
				buttonTooltip.setText("");
			}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {}
	
		});

		final JButton saveAsButton = new JButton();
		saveAsButton.setIcon(new ImageIcon(this.getClass().getResource("/com/ardublock/icons/saveas.png")));
		saveAsButton.setContentAreaFilled(false); 
		saveAsButton.setBackground(Color.LIGHT_GRAY);
		saveAsButton.setPreferredSize( new Dimension(48,48));
		saveAsButton.setOpaque(false);
		saveAsButton.setToolTipText(uiMessageBundle.getString("ardublock.ui.saveAs"));
		saveAsButton.addActionListener(new SaveAsButtonListener(context, this));
		saveAsButton.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent arg0) {}
			public void mouseEntered(MouseEvent arg0) {
				saveAsButton.setContentAreaFilled(true); 
				buttonTooltip.setText(uiMessageBundle.getString("ardublock.ui.saveAs"));
			}
			public void mouseExited(MouseEvent arg0) {
				saveAsButton.setContentAreaFilled(false);
				buttonTooltip.setText("");
			}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {}
	
		});
		

		final JButton openButton = new JButton();
		openButton.setIcon(new ImageIcon(this.getClass().getResource("/com/ardublock/icons/open.png")));
		openButton.setContentAreaFilled(false); 
		openButton.setBackground(Color.LIGHT_GRAY);
		openButton.setPreferredSize( new Dimension(48,48));
		openButton.setOpaque(false);
		openButton.setToolTipText(uiMessageBundle.getString("ardublock.ui.load"));
		openButton.addActionListener(new OpenButtonListener(context, this));
		openButton.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent arg0) {}
			public void mouseEntered(MouseEvent arg0) {
				openButton.setContentAreaFilled(true); 
				buttonTooltip.setText(uiMessageBundle.getString("ardublock.ui.load"));
			}
			public void mouseExited(MouseEvent arg0) {
				openButton.setContentAreaFilled(false);
				buttonTooltip.setText("");
			}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {}
	
		});


		final JButton generateButton = new JButton();
		generateButton.setIcon(new ImageIcon(this.getClass().getResource("/com/ardublock/icons/download.png")));
		generateButton.setContentAreaFilled(false); 
		generateButton.setBackground(Color.LIGHT_GRAY);
		generateButton.setPreferredSize( new Dimension(48,48));
		generateButton.setOpaque(false);
		generateButton.setToolTipText(uiMessageBundle.getString("ardublock.ui.upload"));
		generateButton.addActionListener(new GenerateCodeButtonListener(context, this));
		generateButton.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent arg0) {}
			public void mouseEntered(MouseEvent arg0) {
				generateButton.setContentAreaFilled(true); 
				buttonTooltip.setText(uiMessageBundle.getString("ardublock.ui.upload"));
			}
			public void mouseExited(MouseEvent arg0) {
				generateButton.setContentAreaFilled(false);
				buttonTooltip.setText("");
			}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {}
	
		});

		final JButton serialMonitorButton = new JButton();
		serialMonitorButton.setIcon(new ImageIcon(this.getClass().getResource("/com/ardublock/icons/serial.png")));
		serialMonitorButton.setContentAreaFilled(false); 
		serialMonitorButton.setBackground(Color.LIGHT_GRAY);
		serialMonitorButton.setPreferredSize( new Dimension(48,48));
		serialMonitorButton.setOpaque(false);
		serialMonitorButton.setToolTipText(uiMessageBundle.getString("ardublock.ui.serialMonitor"));
		serialMonitorButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				context.getEditor().handleSerial();
			}
		});
		serialMonitorButton.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent arg0) {}
			public void mouseEntered(MouseEvent arg0) {
				serialMonitorButton.setContentAreaFilled(true);
				buttonTooltip.setText(uiMessageBundle.getString("ardublock.ui.serialMonitor"));
			}
			public void mouseExited(MouseEvent arg0) {
				serialMonitorButton.setContentAreaFilled(false);
				buttonTooltip.setText("");
			}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {}
	
		});
		
		
		
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.LINE_AXIS));
		serialLabel = new JLabel("N/A", SwingConstants.RIGHT);
		//label2.setHorizontalAlignment(SwingConstants.RIGHT);
		
		infoPanel.add(new JLabel(uiMessageBundle.getString("ardublock.ui.serial.prompt") +  ": "));
		infoPanel.add(serialLabel);
		infoPanel.setLayout(new FlowLayout());
		
		
		
		buttons.add(newButton);
		buttons.add(saveButton);
		//buttons.add(saveAsButton);
		buttons.add(openButton);
		buttons.add(Box.createGlue());
		buttons.add(generateButton);
		buttons.add(serialMonitorButton);
		buttons.add(buttonTooltip);
		
		toolbar.setLayout(new BorderLayout());
		
		toolbar.add(buttons, BorderLayout.WEST);
		//toolbar.add(Box.createGlue());
		toolbar.add(infoPanel, BorderLayout.EAST);
		
		JPanel bottomPanel = new JPanel();
		
		JLabel versionLabel = new JLabel("v " + uiMessageBundle.getString("ardublock.ui.version"));
		
		bottomPanel.add(versionLabel);

		
		this.add(toolbar, BorderLayout.NORTH);
		this.add(bottomPanel, BorderLayout.SOUTH);
		this.add(workspace, BorderLayout.CENTER);
		
		
		JMenuBar menuBar = new JMenuBar();
		//File menu
		JMenu fileMenu = new JMenu(uiMessageBundle.getString("ardublock.ui.file"));
		JMenuItem newItem = new JMenuItem(uiMessageBundle.getString("ardublock.ui.new"));
		newItem.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		newItem.addActionListener(new NewButtonListener(context, this));
		fileMenu.add(newItem);
		
		JMenuItem openItem = new JMenuItem(uiMessageBundle.getString("ardublock.ui.load"));
		openItem.addActionListener(new OpenButtonListener(context, this));
		openItem.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		fileMenu.add(openItem);
		
		fileMenu.addSeparator();
		
		JMenuItem saveItem = new JMenuItem(uiMessageBundle.getString("ardublock.ui.save"));
		saveItem.addActionListener(new SaveButtonListener(context, this));
		saveItem.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		fileMenu.add(saveItem);
		
		JMenuItem saveAsItem = new JMenuItem(uiMessageBundle.getString("ardublock.ui.saveAs"));
		saveAsItem.addActionListener(new SaveAsButtonListener(context, this));
		saveAsItem.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | java.awt.event.InputEvent.SHIFT_MASK ));
		fileMenu.add(saveAsItem);
		
		fileMenu.addSeparator();
		
		JMenuItem exportImageItem = new JMenuItem(uiMessageBundle.getString("ardublock.ui.saveImage"));
		exportImageItem.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				Dimension size = workspace.getRenderableBlockDimension();
				System.out.println("size: " + size);
				//BufferedImage bi = new BufferedImage(2560, 2560, BufferedImage.TYPE_INT_RGB);
				BufferedImage bi = new BufferedImage(size.width + 100, size.height+100, BufferedImage.TYPE_INT_RGB);
				Graphics2D g = (Graphics2D)bi.createGraphics();
				double theScaleFactor = (300d/72d);  
				
				workspace.getBlockCanvas().getPageAt(0).getJComponent().paint(g);
				g.scale(theScaleFactor,theScaleFactor);
				try{
					final JFileChooser fc = new JFileChooser();
					fc.setSelectedFile(new File("ardublock.png"));
					int returnVal = fc.showSaveDialog(workspace.getBlockCanvas().getJComponent());
			        if (returnVal == JFileChooser.APPROVE_OPTION) {
			            File file = fc.getSelectedFile();
						ImageIO.write(bi,"png",file);
			        }
				} catch (Exception e1) {
					
				} finally {
					g.dispose();
				}
			}
		});
		fileMenu.add(exportImageItem);
		
		fileMenu.addSeparator();
		
		JMenuItem closeItem = new JMenuItem(uiMessageBundle.getString("ardublock.ui.close"));
		closeItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				OpenblocksFrame.this.askToQuit();
			}
		});
		fileMenu.add(closeItem);
		
		JMenu arduinoMenu = new JMenu(uiMessageBundle.getString("ardublock.ui.arduino"));
		
		JMenuItem generateItem = new JMenuItem(uiMessageBundle.getString("ardublock.ui.upload"));
		generateItem.addActionListener(new GenerateCodeButtonListener(context, this));
		generateItem.setAccelerator(KeyStroke.getKeyStroke('U', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		arduinoMenu.add(generateItem);
		
		JMenuItem serialItem = new JMenuItem(uiMessageBundle.getString("ardublock.ui.serialMonitor"));
		serialItem.setAccelerator(KeyStroke.getKeyStroke('M', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		serialItem.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				context.getEditor().handleSerial();
			}
		});
		arduinoMenu.add(serialItem);
		
		
		//tutorial menu
		JMenu tutorialMenu = new JMenu(uiMessageBundle.getString("ardublock.ui.tutorial"));
		tutorialMenu.add(renderExapmleMenu());
		
		//Help menu
		JMenu helpMenu = new JMenu(uiMessageBundle.getString("ardublock.ui.help"));
		renderHelpMenu(helpMenu);
		
		menuBar.add(fileMenu);
		menuBar.add(arduinoMenu);
		menuBar.add(tutorialMenu);
		menuBar.add(helpMenu);
		
		this.setJMenuBar(menuBar);
		
		
		this.addWindowListener(new java.awt.event.WindowAdapter()
		{
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent)
		    {
		    	OpenblocksFrame.this.askToQuit();
		    }
		});

	}
	
	private void askToQuit()
	{
		if (context.isWorkspaceChanged())
		{
			if (JOptionPane.showConfirmDialog(OpenblocksFrame.this, 
		            uiMessageBundle.getString("ardublock.ui.close.comfirm"),
		            uiMessageBundle.getString("ardublock.ui.close"),
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
			{
				close();
			}
		}
		else
		{
			close();
		}
	}
	
	private void close()
	{
		if (context.isInArduino())
		{
			this.setVisible(false);
		}
		else
		{
			System.exit(0);
		}
	}
	
	public void doOpenArduBlockFile()
	{
		if (context.isWorkspaceChanged())
		{
			int optionValue = JOptionPane.showOptionDialog(this, uiMessageBundle.getString("message.content.open_unsaved"), uiMessageBundle.getString("message.title.question"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION);
			if (optionValue == JOptionPane.YES_OPTION)
			{
				doSaveArduBlockFile();
				this.loadFile();
			}
			else
			{
				if (optionValue == JOptionPane.NO_OPTION)
				{
					this.loadFile();
				}
			}
		}
		else
		{
			this.loadFile();
		}
		this.setTitle(makeFrameTitle());
	}
	
	public void openArdublockExample(String fileName)
	{
		if (context.isWorkspaceChanged())
		{
			int optionValue = JOptionPane.showOptionDialog(this, uiMessageBundle.getString("message.content.open_unsaved"), uiMessageBundle.getString("message.title.question"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION);
			if (optionValue == JOptionPane.YES_OPTION)
			{
				doSaveArduBlockFile();
				this.doOpenArdublockExample(fileName);
			}
			else
			{
				if (optionValue == JOptionPane.NO_OPTION)
				{
					this.doOpenArdublockExample(fileName);
				}
			}
		}
		else
		{
			this.doOpenArdublockExample(fileName);
		}
		this.setTitle(makeFrameTitle());
	}
	
	private void doOpenArdublockExample(String filename)
	{
		context.setWorkspaceChanged(false);
		this.setTitle(this.makeFrameTitle());
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		try
		{
			context.loadArdublockExample(filename);
		}
		catch (IOException e)
		{
			JOptionPane.showOptionDialog(this, uiMessageBundle.getString("message.file_not_found"), uiMessageBundle.getString("message.title.error"), JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, null, JOptionPane.OK_OPTION);
			e.printStackTrace();
		}
		finally
		{
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	
	
	
	private void loadFile()
	{
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			File savedFile = fileChooser.getSelectedFile();
			if (!savedFile.exists())
			{
				JOptionPane.showOptionDialog(this, uiMessageBundle.getString("message.file_not_found"), uiMessageBundle.getString("message.title.error"), JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, null, JOptionPane.OK_OPTION);
				return ;
			}
			
			try
			{
				this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				context.loadArduBlockFile(savedFile);
				context.setWorkspaceChanged(false);
			}
			catch (IOException e)
			{
				JOptionPane.showOptionDialog(this, uiMessageBundle.getString("message.file_not_found"), uiMessageBundle.getString("message.title.error"), JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, null, JOptionPane.OK_OPTION);
				e.printStackTrace();
			}
			finally
			{
				this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}
	}
	
	public void doSaveArduBlockFile()
	{
		if (!context.isWorkspaceChanged())
		{
			return ;
		}
		
		String saveString = getArduBlockString();
		
		if (context.getSaveFilePath() == null)
		{
			chooseFileAndSave(saveString);
		}
		else
		{
			File saveFile = new File(context.getSaveFilePath());
			writeFileAndUpdateFrame(saveString, saveFile);
		}
	}

	
	public void doSaveAsArduBlockFile()
	{
		if (context.isWorkspaceEmpty())
		{
			return ;
		}
		
		String saveString = getArduBlockString();
		
		chooseFileAndSave(saveString);
		
	}
	
	private void chooseFileAndSave(String ardublockString)
	{
		File saveFile = letUserChooseSaveFile();
		saveFile = checkFileSuffix(saveFile);
		if (saveFile == null)
		{
			return ;
		}
		
		if (saveFile.exists() && !askUserOverwriteExistedFile())
		{
			return ;
		}
		
		writeFileAndUpdateFrame(ardublockString, saveFile);
	}
	
	private String getArduBlockString()
	{
		WorkspaceController workspaceController = context.getWorkspaceController();
		return workspaceController.getSaveString();
	}
	
	private void writeFileAndUpdateFrame(String ardublockString, File saveFile) 
	{
		try
		{
			saveArduBlockToFile(ardublockString, saveFile);
			context.setWorkspaceChanged(false);
			this.setTitle(this.makeFrameTitle());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	private File letUserChooseSaveFile()
	{
		int chooseResult;
		chooseResult = fileChooser.showSaveDialog(this);
		if (chooseResult == JFileChooser.APPROVE_OPTION)
		{
			return fileChooser.getSelectedFile();
		}
		return null;
	}
	
	private boolean askUserOverwriteExistedFile()
	{
		int optionValue = JOptionPane.showOptionDialog(this, uiMessageBundle.getString("message.content.overwrite"), uiMessageBundle.getString("message.title.question"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION);
		return (optionValue == JOptionPane.YES_OPTION);
	}
	
	private void saveArduBlockToFile(String ardublockString, File saveFile) throws IOException
	{
		context.saveArduBlockFile(saveFile, ardublockString);
		context.setSaveFileName(saveFile.getName());
		context.setSaveFilePath(saveFile.getAbsolutePath());
	}
	
	public void doNewArduBlockFile()
	{
		if (context.isWorkspaceChanged())
		{
			int optionValue = JOptionPane.showOptionDialog(this, uiMessageBundle.getString("message.question.newfile_on_workspace_changed"), uiMessageBundle.getString("message.title.question"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION);
			if (optionValue != JOptionPane.YES_OPTION)
			{
				return ;
			}
		}
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		context.resetWorksapce();
		context.setWorkspaceChanged(false);
		this.setTitle(this.makeFrameTitle());
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	
	
	private File checkFileSuffix(File saveFile)
	{
		String filePath = saveFile.getAbsolutePath();
		if (filePath.endsWith(".abp"))
		{
			return saveFile;
		}
		else
		{
			return new File(filePath + ".abp");
		}
	}
}
