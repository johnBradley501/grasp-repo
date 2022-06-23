package uk.ac.kcl.cch.jb.sparql.actions;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.Action;

import uk.ac.kcl.cch.jb.sparql.commands.AddDataConstraintCommand;
import uk.ac.kcl.cch.jb.sparql.commands.AddInstanceConstraintCommand;
import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.VariableComponent;

public class AddDataConstraintAction extends Action {

	private QueryCreationEditor myEditor;
	private VariableComponent variableComponent;
	private CommandStack commandStack;

	public AddDataConstraintAction(QueryCreationEditor myEditor, VariableComponent item, CommandStack commandStack) {
		super("Add New Constraint");
		this.myEditor = myEditor;
		this.variableComponent = item;
		this.commandStack = commandStack;
	}
	
	public void run() {
		commandStack.execute(new AddDataConstraintCommand(myEditor, variableComponent));

	}

}
