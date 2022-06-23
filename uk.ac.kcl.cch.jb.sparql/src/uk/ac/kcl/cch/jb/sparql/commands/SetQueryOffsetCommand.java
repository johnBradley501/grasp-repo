package uk.ac.kcl.cch.jb.sparql.commands;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.ModifierComponent;
import uk.ac.kcl.cch.jb.sparql.model.SelectClauseModifier;

public class SetQueryOffsetCommand extends DirtyCommand {

	private SelectClauseModifier myModifier;
	private int newVal;
	private int oldVal;

	public SetQueryOffsetCommand(ModifierComponent myMC, int newVal, QueryCreationEditor myEditor) {
		super(myEditor);
		this.setLabel("Setting Query Offset to "+newVal);
		this.myModifier = myMC.getModifier();
		this.newVal = newVal;
		this.oldVal = myModifier.getLimit();
	}
	
	public void execute() {
		super.execute();
		myModifier.setOffset(newVal);
	}
	
	public void undo() {
		super.undo();
		myModifier.setOffset(oldVal);
	}

}
