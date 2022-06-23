package uk.ac.kcl.cch.jb.sparql.commands;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.SelectVarItem;

public class ItemOrderUpdateCommand extends DirtyCommand {

	private int newValue;
	private SelectVarItem varItem;
	private int oldValue;

	public ItemOrderUpdateCommand(SelectVarItem varItem, int newValue, QueryCreationEditor myEditor) {
		super(myEditor);
		this.setLabel("Change Order Setting");
		this.newValue = newValue;
		this.oldValue = varItem.getSortOrder();
		this.varItem = varItem;
	}
	
	public void execute() {
		varItem.setSortOrder(newValue);
		super.execute();
	}
	
	public void undo() {
		varItem.setSortOrder(oldValue);
		super.undo();
	}

}
