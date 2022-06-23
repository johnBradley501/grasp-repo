package uk.ac.kcl.cch.jb.sparql.commands;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.WhereClausePredicate;

public class DeleteWhereClausePredicateCommand extends DirtyCommand {

	private WhereClausePredicate thePredicate;

	public DeleteWhereClausePredicateCommand(WhereClausePredicate whereClausePredicate, QueryCreationEditor myEditor) {
		super(myEditor);
		this.setLabel("delete Predicate");
		this.thePredicate = whereClausePredicate;
	}
	
	public void execute() {
		super.execute();
		thePredicate.getDomain().removeDomainPredicate(thePredicate);
		thePredicate.getRange().removeRangePredicate(thePredicate);
	}
	
	public void undo() {
		super.undo();
		thePredicate.getDomain().addDomainPredicate(thePredicate);
		thePredicate.getRange().addRangePredicate(thePredicate);
	}

}
