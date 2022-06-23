package uk.ac.kcl.cch.jb.sparql.commands;

import java.util.List;

import org.eclipse.gef.commands.Command;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.DataConstraint;
import uk.ac.kcl.cch.jb.sparql.model.VariableComponent;

public class DeleteDataConstraintCommand extends DirtyCommand {

	private DataConstraint dataConstraint;
	private VariableComponent varComponent;
	private int place;

	public DeleteDataConstraintCommand(DataConstraint dataConstraint, QueryCreationEditor myEditor) {
		super(myEditor);
		setLabel("Delete Constraint");
		this.dataConstraint = dataConstraint;
		this.varComponent = dataConstraint.getOwner();
	}
	
	public void execute() {
		super.execute();
		List<DataConstraint> constraints = varComponent.getConstraints();
		place = constraints.indexOf(dataConstraint);
		varComponent.deleteConstraint(dataConstraint);
	}
	
	public void undo() {
		super.undo();
		varComponent.insertConstraint(dataConstraint, place);
	}

}
