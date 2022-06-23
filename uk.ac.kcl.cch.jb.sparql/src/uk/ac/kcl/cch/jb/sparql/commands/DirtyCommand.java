package uk.ac.kcl.cch.jb.sparql.commands;

import org.eclipse.gef.commands.Command;

public class DirtyCommand extends Command {
	
	private CanBeDirty dObject;
	boolean oldDirtyFlag = false;

	public DirtyCommand(CanBeDirty dObject) {
		super();
		this.dObject = dObject;
	}
	
	public DirtyCommand(CanBeDirty dObject, String label) {
		super(label);
		this.dObject = dObject;
	}
	
	public void execute() {
		oldDirtyFlag = dObject.isDirty();
		dObject.markDirty();
		if(!oldDirtyFlag)dObject.fireDirtyEvent();
	}
	
	public void undo() {
		boolean curDirtyFlag = dObject.isDirty();
		dObject.setDirtyFlag(oldDirtyFlag);
		if(oldDirtyFlag!=curDirtyFlag)dObject.fireDirtyEvent();
	}
	
	public void redo() {
		execute();
	}

}
