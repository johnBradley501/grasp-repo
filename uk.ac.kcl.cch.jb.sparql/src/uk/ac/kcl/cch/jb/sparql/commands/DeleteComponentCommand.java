package uk.ac.kcl.cch.jb.sparql.commands;

import java.util.ArrayList;
import java.util.List;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.QueryWhereClause;
import uk.ac.kcl.cch.jb.sparql.model.SelectVarItem;
import uk.ac.kcl.cch.jb.sparql.model.VariableComponent;
import uk.ac.kcl.cch.jb.sparql.model.WhereClauseComponent;
import uk.ac.kcl.cch.jb.sparql.model.ClassComponent;
import uk.ac.kcl.cch.jb.sparql.model.DataConstraint;
import uk.ac.kcl.cch.jb.sparql.model.QuerySelectClause;
import uk.ac.kcl.cch.jb.sparql.model.WhereClausePredicate;
import uk.ac.kcl.cch.jb.sparql.parts.WhereClauseComponentPart;

public class DeleteComponentCommand extends DirtyCommand {
	
	private QueryWhereClause myWhereClause = null;
	private WhereClauseComponent myComponent;
	private List<WhereClausePredicate> heldDomainPreds = null;
	private List<WhereClausePredicate> heldRangePreds = null;
	private QuerySelectClause mySelectClause;
	private SelectVarItem after = null;
	private SelectVarItem theItem = null;
	private List<DataConstraint>constraints = new ArrayList<DataConstraint>();

	public DeleteComponentCommand(WhereClauseComponentPart comp, QueryCreationEditor myEditor) {
		super(myEditor);
		myComponent = comp.getWhereClauseComponent();
		myWhereClause = myEditor.getQuery().getWhereClause();
		mySelectClause = myEditor.getQuery().getSelectClause();
	}
	
	public void execute() {
		heldDomainPreds = new ArrayList<WhereClausePredicate>(myComponent.getDomainPredicates());
		for(WhereClausePredicate pred: heldDomainPreds) handleRemoval(pred);
		heldRangePreds = new ArrayList<WhereClausePredicate>(myComponent.getRangePredicates());
		for(WhereClausePredicate pred: heldRangePreds) handleRemoval(pred);
		
		if(myComponent instanceof VariableComponent) removeDataConstraints((VariableComponent)myComponent);
		
		handleRemoveSelectItem();
		
		myWhereClause.removeComponent(myComponent);
		super.execute();
	}
	
	private void removeDataConstraints(VariableComponent myVarComp) {
		constraints = new ArrayList<DataConstraint>(myVarComp.getConstraints());
		for(DataConstraint constraint: constraints)myVarComp.deleteConstraint(constraint);
	}

	private void handleRemoval(WhereClausePredicate pred) {
		WhereClauseComponent domain = pred.getDomain();
		domain.removeDomainPredicate(pred);
		WhereClauseComponent range = pred.getRange();
		if(range != null)range.removeRangePredicate(pred);
	}
	
	private void handleRemoveSelectItem() {
		after = null;
		for(SelectVarItem item: mySelectClause.getVars()) {
			if(item.getComponent()==myComponent) {
				theItem = item;
				mySelectClause.deleteItem(item);
				return;
			}
			after = item;
		}
		
	}

	public void undo() {
		if(myComponent instanceof VariableComponent) reintroduceDataConstraints((VariableComponent)myComponent);
		myWhereClause.addComponent(myComponent);
		for(WhereClausePredicate pred: heldDomainPreds) handleReintroduction(pred);
		for(WhereClausePredicate pred: heldRangePreds) handleReintroduction(pred);
		
		mySelectClause.placeItem(theItem, after);
		super.undo();
	}
	
	private void reintroduceDataConstraints(VariableComponent myVarComp) {
		for(DataConstraint constraint: constraints)myVarComp.addConstraint(constraint);
		
	}

	private void handleReintroduction(WhereClausePredicate pred) {
		WhereClauseComponent domain = pred.getDomain();
		domain.addDomainPredicate(pred);
		WhereClauseComponent range = pred.getRange();
		range.addRangePredicate(pred);
	}
}
