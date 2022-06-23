package uk.ac.kcl.cch.jb.sparql.commands;

import uk.ac.kcl.cch.jb.sparql.builder.ExpressionMaker;
import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.SelectVarItem;

public class UpdateAggregateTypeCommand extends DirtyCommand {

	private SelectVarItem myItem;
	private int chosenType;
	private int oldChosenType;
	private String suggestedVariableName = null;
	private String oldAggVariableName = null;
	private boolean setupAggVariableName = true;

	public UpdateAggregateTypeCommand(SelectVarItem myItem, int chosenOne, QueryCreationEditor myEditor) {
		super(myEditor);
		this.setLabel("set aggregation type");
		this.myItem = myItem;
		this.chosenType = chosenOne;
		this.oldChosenType = myItem.getAggType();
		this.oldAggVariableName = myItem.getAggVarName();
		developProposedName();
	}
	
	private void developProposedName() {
		if(chosenType < 0) { // selection is not an aggregation request
			suggestedVariableName = null;
		} else if(oldAggVariableName == null ) {
			suggestedVariableName = myItem.getComponent().getName()+"_"+ExpressionMaker.ExpressionNames[chosenType];
		} else setupAggVariableName = false;
		
	}

	public void execute() {
		super.execute();
		myItem.setAggType(chosenType);
		if(setupAggVariableName)myItem.setAggVarName(suggestedVariableName);
	}
	
	public void undo() {
		super.undo();
		myItem.setAggType(oldChosenType);
		if(setupAggVariableName)myItem.setAggVarName(oldAggVariableName);
	}

}
