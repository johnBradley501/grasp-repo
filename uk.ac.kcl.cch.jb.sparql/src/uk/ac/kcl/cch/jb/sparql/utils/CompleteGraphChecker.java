package uk.ac.kcl.cch.jb.sparql.utils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import uk.ac.kcl.cch.jb.sparql.model.ClassComponent;
import uk.ac.kcl.cch.jb.sparql.model.InstanceComponent;
import uk.ac.kcl.cch.jb.sparql.model.PropertyChangeObject;
import uk.ac.kcl.cch.jb.sparql.model.QueryWhereClause;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery;
import uk.ac.kcl.cch.jb.sparql.model.VariableComponent;
import uk.ac.kcl.cch.jb.sparql.model.WhereClauseComponent;
import uk.ac.kcl.cch.jb.sparql.model.WhereClausePredicate;

public class CompleteGraphChecker extends PropertyChangeObject implements PropertyChangeListener {
	
	public static final String CompletenessChange = "Comp_Change"; 
	
	private SPARQLQuery query;
	private Boolean complete = null;

	public CompleteGraphChecker(SPARQLQuery query) {
		this.query = query;
		query.getWhereClause().addPropertyChangeListener(this);
	}
	
	public void dispose() {
		query.getWhereClause().removePropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		String propName = arg0.getPropertyName();
		if((propName==QueryWhereClause.COMPONENTS_CHANGED) || (propName==QueryWhereClause.PREDICATES_CHANGED)) {
			boolean newrslt = checkForCompleteness();
			if((complete == null) || newrslt != complete.booleanValue()) {
				complete = new Boolean(newrslt);
				this.firePropertyChange(CompletenessChange, null, complete);
			}
		}

	}
	
	public boolean joinChecker(PropertyChangeListener listener) {
		if(complete == null)complete = new Boolean(checkForCompleteness());
		this.addPropertyChangeListener(listener);
		return complete.booleanValue();
	}

	private boolean checkForCompleteness() {
		int numbOfComponents = query.getWhereClause().getComponents().size();
		if(numbOfComponents == 0)return false;
		int numbThroughPath = checkPath();
		return numbOfComponents == numbThroughPath;
	}

	private int checkPath() {
		Set<WhereClauseComponent> pathComponents = new HashSet<WhereClauseComponent>();
		WhereClauseComponent first = query.getWhereClause().getComponents().get(0);
		if(first instanceof VariableComponent) {
			VariableComponent vc = (VariableComponent)first;
			first = vc.getMyProperty().getDomain();
		} else if(first instanceof InstanceComponent) {
			InstanceComponent ic = (InstanceComponent)first;
			first = ic.getDomainPredicates().get(0).getDomain();
		}
		
		processComponent(first, pathComponents);
		return pathComponents.size();
	}

	private void processComponent(WhereClauseComponent item, Set<WhereClauseComponent> pathComponents) {
		if(item == null)return;
		if(pathComponents.contains(item))return;
		pathComponents.add(item);
		if(item instanceof ClassComponent) {
			ClassComponent clsItem = (ClassComponent)item;
			for(WhereClausePredicate pred: clsItem.getRangePredicates()) {
				WhereClauseComponent predec = pred.getDomain();
				processComponent(predec, pathComponents);
			}
			for(WhereClausePredicate pred: clsItem.getDomainPredicates()) {
				WhereClauseComponent domComp = pred.getRange();
				processComponent(domComp, pathComponents);
			}

		}
	}

}
