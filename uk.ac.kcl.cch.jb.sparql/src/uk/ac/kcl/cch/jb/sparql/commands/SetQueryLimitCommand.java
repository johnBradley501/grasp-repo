package uk.ac.kcl.cch.jb.sparql.commands;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.ModifierComponent;
import uk.ac.kcl.cch.jb.sparql.model.SelectClauseModifier;

public class SetQueryLimitCommand extends DirtyCommand {

	private int newVal;
	private SelectClauseModifier myModifier;
	private int oldVal;

	public SetQueryLimitCommand(ModifierComponent myMC, int newVal, QueryCreationEditor myEditor) {
		super(myEditor);
		this.setLabel("Changing Query Limit to "+newVal);
		this.myModifier = myMC.getModifier();
		this.newVal = newVal;
		this.oldVal = myModifier.getLimit();
	}
	
	public void execute() {
		super.execute();
		myModifier.setLimit(newVal);
	}
	
	public void undo() {
		super.undo();
		myModifier.setLimit(oldVal);
	}

}
