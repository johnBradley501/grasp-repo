package uk.ac.kcl.cch.jb.sparql.actions;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.Action;

import uk.ac.kcl.cch.jb.sparql.commands.DeleteWhereClausePredicateCommand;
import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.WhereClausePredicate;

public class DeleteWhereClausePredicateAction extends Action {
	
	private DeleteWhereClausePredicateCommand theCommand;
	private CommandStack commandStack;

	public DeleteWhereClausePredicateAction(WhereClausePredicate predicate, QueryCreationEditor myEditor, CommandStack commandStack) {
		super();
		this.setText("Delete Predicate");
		this.theCommand = new DeleteWhereClausePredicateCommand(predicate, myEditor);
		this.commandStack = commandStack;
	}
	
	public void run() {
		commandStack.execute(theCommand);
	}

}
