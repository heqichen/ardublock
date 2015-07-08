package com.ardublock.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.ardublock.core.Context;
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
	
	private ResourceBundle uiMessageBundle;
	
	public void addListener(OpenblocksFrameListener ofl)
	{
		context.registerOpenblocksFrameListener(ofl);
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
	
	public OpenblocksFrame()
	{
		context = Context.getContext();
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
		workspace.addWorkspaceListener(new ArdublockWorkspaceListener(this));
		
		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout());
		JButton newButton = new JButton(uiMessageBundle.getString("ardublock.ui.new"));
		newButton.addActionListener(new NewButtonListener(this));
		JButton saveButton = new JButton(uiMessageBundle.getString("ardublock.ui.save"));
		saveButton.addActionListener(new SaveButtonListener(this));
		JButton saveAsButton = new JButton(uiMessageBundle.getString("ardublock.ui.saveAs"));
		saveAsButton.addActionListener(new SaveAsButtonListener(this));
		JButton openButton = new JButton(uiMessageBundle.getString("ardublock.ui.load"));
		openButton.addActionListener(new OpenButtonListener(this));
		JButton generateButton = new JButton(uiMessageBundle.getString("ardublock.ui.upload"));
		generateButton.addActionListener(new GenerateCodeButtonListener(this, context));
		JButton serialMonitorButton = new JButton(uiMessageBundle.getString("ardublock.ui.serialMonitor"));
		serialMonitorButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				context.getEditor().handleSerial();
			}
		});
		

		buttons.add(newButton);
		buttons.add(saveButton);
		buttons.add(saveAsButton);
		buttons.add(openButton);
		buttons.add(generateButton);
		buttons.add(serialMonitorButton);

		JPanel bottomPanel = new JPanel();
		
		JLabel versionLabel = new JLabel("v " + uiMessageBundle.getString("ardublock.ui.version"));
		
		bottomPanel.add(versionLabel);

		
		this.add(buttons, BorderLayout.NORTH);
		this.add(bottomPanel, BorderLayout.SOUTH);
		this.add(workspace, BorderLayout.CENTER);
		
		
		JMenuBar menuBar = new JMenuBar();
		//File menu
		JMenu fileMenu = new JMenu(uiMessageBundle.getString("ardublock.ui.file"));
		JMenuItem newItem = new JMenuItem(uiMessageBundle.getString("ardublock.ui.new"));
		newItem.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		newItem.addActionListener(new NewButtonListener(this));
		fileMenu.add(newItem);
		
		JMenuItem openItem = new JMenuItem(uiMessageBundle.getString("ardublock.ui.load"));
		openItem.addActionListener(new OpenButtonListener(this));
		openItem.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		fileMenu.add(openItem);
		
		fileMenu.addSeparator();
		
		JMenuItem saveItem = new JMenuItem(uiMessageBundle.getString("ardublock.ui.save"));
		saveItem.addActionListener(new SaveButtonListener(this));
		saveItem.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		fileMenu.add(saveItem);
		
		JMenuItem saveAsItem = new JMenuItem(uiMessageBundle.getString("ardublock.ui.saveAs"));
		saveAsItem.addActionListener(new SaveAsButtonListener(this));
		saveAsItem.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | java.awt.event.InputEvent.SHIFT_MASK ));
		fileMenu.add(saveAsItem);
		
		fileMenu.addSeparator();
		
		JMenuItem exportImageItem = new JMenuItem(uiMessageBundle.getString("ardublock.ui.saveImage"));
		exportImageItem.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				Dimension size = workspace.getCanvasSize();
				System.out.println("size: " + size);
				BufferedImage bi = new BufferedImage(2560, 2560, BufferedImage.TYPE_INT_RGB);
				Graphics2D g = (Graphics2D)bi.createGraphics();
				double theScaleFactor = (300d/72d);  
				g.scale(theScaleFactor,theScaleFactor);
				
				workspace.getBlockCanvas().getPageAt(0).getJComponent().paint(g);
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
		generateItem.addActionListener(new GenerateCodeButtonListener(this, context));
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
