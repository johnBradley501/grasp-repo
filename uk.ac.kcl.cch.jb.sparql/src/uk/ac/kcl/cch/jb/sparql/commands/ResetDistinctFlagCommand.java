package uk.ac.kcl.cch.jb.sparql.commands;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.SelectClauseModifier;

public class ResetDistinctFlagCommand extends DirtyCommand {

	private SelectClauseModifier modifier;

	public ResetDistinctFlagCommand(SelectClauseModifier modifier, QueryCreationEditor myEditor) {
		super(myEditor);
		String toWhat = "Distinct";
		if(modifier.isDistinct()) toWhat = "Multiple";
		this.setLabel("Change Distinct setting to "+toWhat);
		this.modifier = modifier;
	}
	
	public void execute() {
		modifier.setDistinct(!modifier.isDistinct());
		super.execute();
	}
	
	public void undo() {
		modifier.setDistinct(!modifier.isDistinct());
		super.undo();
	}

}
