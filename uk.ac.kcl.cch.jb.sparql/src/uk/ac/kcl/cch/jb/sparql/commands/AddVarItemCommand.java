package uk.ac.kcl.cch.jb.sparql.commands;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.INamedComponent;
import uk.ac.kcl.cch.jb.sparql.model.QuerySelectClause;
import uk.ac.kcl.cch.jb.sparql.model.QueryWhereClause;
import uk.ac.kcl.cch.jb.sparql.model.SelectVarItem;

public class AddVarItemCommand extends DirtyCommand {

	private INamedComponent theComponent;
	private SelectVarItem afterItem;
	private QuerySelectClause selectClause;
	private SelectVarItem newItem;

	public AddVarItemCommand(INamedComponent theComponent, SelectVarItem afterItem, QueryCreationEditor myEditor) {
		super(myEditor);
		this.setLabel("Add new variable into select clause: "+theComponent.getName());
		this.theComponent = theComponent;
		this.afterItem = afterItem;
		this.selectClause = myEditor.getQuery().getSelectClause();
		newItem = new SelectVarItem(myEditor.getQuery(), theComponent);
	}
	
	public void execute() {
		selectClause.placeItem(newItem,  afterItem);
		super.execute();
	}
	
	public void undo() {
		super.undo();
		selectClause.deleteItem(newItem);
	}

}
