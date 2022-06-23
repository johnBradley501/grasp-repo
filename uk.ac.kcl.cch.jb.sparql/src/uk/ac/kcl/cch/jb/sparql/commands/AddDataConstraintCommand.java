package uk.ac.kcl.cch.jb.sparql.commands;

import org.eclipse.gef.commands.Command;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.DataConstraint;
import uk.ac.kcl.cch.jb.sparql.model.VariableComponent;

public class AddDataConstraintCommand extends DirtyCommand {

	private VariableComponent variableComponent;
	private DataConstraint dataConstraint;

	public AddDataConstraintCommand(QueryCreationEditor myEditor, VariableComponent variableComponent) {
		super(myEditor);
		this.variableComponent = variableComponent;
		this.dataConstraint = new DataConstraint(variableComponent);
	}
	
	public void execute() {
		super.execute();
		variableComponent.addConstraint(dataConstraint);
	}
	
	public void undo() {
		super.undo();
		variableComponent.deleteConstraint(dataConstraint);
	}

}
