package uk.ac.kcl.cch.jb.sparql.dnd;

import org.eclipse.gef.requests.CreationFactory;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.kcl.cch.jb.sparql.model.ClassItem;
import uk.ac.kcl.cch.jb.sparql.model.OntologyData;
import uk.ac.kcl.cch.jb.sparql.model.QueryWhereClause;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery;
import uk.ac.kcl.cch.jb.sparql.model.ClassComponent;
import uk.ac.kcl.cch.jb.sparql.views.ClassesViewItem;

public class WhereClauseComponentFactory implements CreationFactory {
	
	private ClassComponent createdObject = null;
	// private SPARQLQuery query;
	private QueryWhereClause clause;
	
	public WhereClauseComponentFactory(QueryWhereClause clause) {
		this.clause = clause;
	}

	@Override
	public Object getNewObject() {
		return createdObject;
	}

	@Override
	public Object getObjectType() {
		// TODO Auto-generated method stub
		return createdObject.getClass();
	}
	
	public void setupObject(ClassesViewItem item) {
		ClassItem theClass = item.getMyClassItem();
		createdObject = ClassComponent.newClass(theClass, clause);
	}

}
