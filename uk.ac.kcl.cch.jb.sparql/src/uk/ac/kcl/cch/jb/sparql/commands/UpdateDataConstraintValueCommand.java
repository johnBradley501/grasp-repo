package uk.ac.kcl.cch.jb.sparql.commands;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.DataConstraint;

public class UpdateDataConstraintValueCommand extends DirtyCommand {

	private DataConstraint mydc;
	private String newVal;
	private String oldVal;

	public UpdateDataConstraintValueCommand(DataConstraint mydc, String newVal, QueryCreationEditor myEditor) {
		super(myEditor);
		this.mydc = mydc;
		this.newVal = newVal;
		this.oldVal = mydc.getConstraintValue();
	}
	
	public void execute() {
		super.execute();
		mydc.setConstraintValue(newVal);
	}
	
	public void undo() {
		super.undo();
		mydc.setConstraintValue(oldVal);
	}

}
