package uk.ac.kcl.cch.jb.sparql.commands;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.SelectVarItem;

public class UpdateAggNameCommand extends DirtyCommand {

	private SelectVarItem myItem;
	private String text;
	private String oldtext;

	public UpdateAggNameCommand(SelectVarItem myItem, String text, QueryCreationEditor myEditor) {
		super(myEditor);
		this.setLabel("update aggregate varname");
		this.myItem = myItem;
		this.text = text;
		this.oldtext = myItem.getAggVarName();
	}
	
	public void execute() {
		super.execute();
		myItem.setAggVarName(text);
	}
	
	public void undo() {
		super.undo();
		myItem.setAggVarName(oldtext);
	}

}
