package uk.ac.kcl.cch.jb.sparql.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorInput;

public class InputCreatingAction extends Action {
	
	private IEditorInput editorInput = null;
	
	public InputCreatingAction() {
		super();
		editorInput = null;
	}
	
	protected void setEditorInput(IEditorInput input) {
		editorInput = input;
	}
	
	public IEditorInput getEditorInput() {return editorInput;}

}
