package com.ardublock.ui.listener;

import com.ardublock.Context;
import com.ardublock.ui.OpenblocksFrame;

import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.workspace.WorkspaceEvent;
import edu.mit.blocks.workspace.WorkspaceListener;

public class ArdublockWorkspaceListener implements WorkspaceListener
{
	private Context context;
	private OpenblocksFrame frame;
	public ArdublockWorkspaceListener(Context context, OpenblocksFrame frame)
	{
		this.context = context;
		this.frame = frame;
	}
	
	public void workspaceEventOccurred(WorkspaceEvent event)
	{
		frame.refreshSerialPort();
		//System.out.println("Event: " + event.getEventType());
		if (!context.isWorkspaceChanged())
		{
			context.setWorkspaceChanged(true);
			context.setWorkspaceEmpty(false);
			String title = frame.makeFrameTitle();
			if (frame != null)
			{
				frame.setTitle(title);
			}
		}
		context.resetHightlightBlock();
		
		
		Iterable<Block> blocks = context.getWorkspace().getBlocks();
		for (Block block : blocks)
		{
			//System.out.println(block.getBlockID() + ": " + block.getBlockLabel());
		}
		//System.out.println("======================\n\n");
		
	}
}
