package uk.ac.kcl.cch.jb.sparql.commands;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.QuerySelectClause;
import uk.ac.kcl.cch.jb.sparql.model.SelectVarItem;

public class DeleteSelectVarItemCommand extends DirtyCommand {

	private QuerySelectClause myContainer;
	private SelectVarItem item;
	private SelectVarItem before;

	public DeleteSelectVarItemCommand(SelectVarItem item, QueryCreationEditor myEditor) {
		super(myEditor);
		this.setLabel("delete select component");
		myContainer = myEditor.getQuery().getSelectClause();
		this.item = item;
		before = myContainer.getItemBefore(item);
	}
	
	public void execute() {
		super.execute();
		myContainer.deleteItem(item);
	}
	
	public void undo() {
		super.undo();
		myContainer.placeItem(item, before);
	}

}
