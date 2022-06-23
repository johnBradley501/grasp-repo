package uk.ac.kcl.cch.jb.sparql.commands;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.DataConstraint;

public class UpdateDataConstraintTypeCommand extends DirtyCommand {

	private DataConstraint mydc;
	private int newType;
	private int oldType;

	public UpdateDataConstraintTypeCommand(DataConstraint mydc, int newType, QueryCreationEditor myEditor) {
		super(myEditor);
		this.mydc = mydc;
		this.newType = newType;
		this.oldType = mydc.getConstraintType();
	}
	
	public void execute() {
		super.execute();
		mydc.setConstraintType(newType);
	}
	
	public void undo() {
		super.undo();
		mydc.setConstraintType(oldType);
	}

}
