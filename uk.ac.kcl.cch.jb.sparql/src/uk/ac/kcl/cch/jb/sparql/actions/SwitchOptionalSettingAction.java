package uk.ac.kcl.cch.jb.sparql.actions;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.Action;

import uk.ac.kcl.cch.jb.sparql.commands.SwitchOptionalSettingCommand;
import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.WhereClausePredicate;

public class SwitchOptionalSettingAction extends Action {

	private SwitchOptionalSettingCommand theCommand;
	private CommandStack commandStack;

	public SwitchOptionalSettingAction(WhereClausePredicate predicate, QueryCreationEditor myEditor, CommandStack commandStack) {
		super();
		this.theCommand = new SwitchOptionalSettingCommand(predicate, myEditor);
		this.setText(theCommand.getLabel());
		this.commandStack = commandStack;
	}
	
	public void run() {
		commandStack.execute(theCommand);
	}
}
