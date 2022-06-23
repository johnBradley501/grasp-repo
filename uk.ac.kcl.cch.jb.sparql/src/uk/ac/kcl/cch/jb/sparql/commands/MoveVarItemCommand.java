package uk.ac.kcl.cch.jb.sparql.commands;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.QuerySelectClause;
import uk.ac.kcl.cch.jb.sparql.model.SelectVarItem;

public class MoveVarItemCommand extends DirtyCommand {

	private SelectVarItem thisItem;
	private SelectVarItem afterItem;
	private SelectVarItem oldAfterItem;
	private QuerySelectClause selectClause;

	public MoveVarItemCommand(SelectVarItem thisItem, SelectVarItem afterItem, QueryCreationEditor myEditor) {
		super(myEditor);
		this.setLabel("Move select item: "+thisItem.getComponent().getName());
		this.thisItem = thisItem;
		this.afterItem = afterItem;
		this.selectClause = myEditor.getQuery().getSelectClause();
		this.oldAfterItem = selectClause.getItemBefore(thisItem);
	}
	
	public void execute() {
		selectClause.placeItem(thisItem, afterItem);
		super.execute();
	}
	
	public void undo() {
		selectClause.placeItem(thisItem,  oldAfterItem);
		super.undo();
	}

}
