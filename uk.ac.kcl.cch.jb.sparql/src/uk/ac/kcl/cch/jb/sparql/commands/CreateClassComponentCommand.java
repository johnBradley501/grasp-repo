package uk.ac.kcl.cch.jb.sparql.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.draw2d.geometry.Rectangle;
import org.semanticweb.owlapi.model.IRI;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.ObjectPropertyItem;
import uk.ac.kcl.cch.jb.sparql.model.OntologyData;
import uk.ac.kcl.cch.jb.sparql.model.QueryWhereClause;
import uk.ac.kcl.cch.jb.sparql.model.WhereClauseComponent;
import uk.ac.kcl.cch.jb.sparql.model.ClassComponent;
import uk.ac.kcl.cch.jb.sparql.model.WhereClausePredicate;

public class CreateClassComponentCommand extends DirtyCommand {

	private ClassComponent newComponent;
	private Rectangle bounds;
	private QueryWhereClause whereClause;
	private OntologyData ontData;

	public CreateClassComponentCommand(ClassComponent newComponent, QueryCreationEditor myEditor, Rectangle bounds) {
		super(myEditor);
		this.setLabel("Add new Class");
		this.newComponent = newComponent;
		this.bounds = new Rectangle(bounds);
		this.whereClause = myEditor.getQuery().getWhereClause();
		this.ontData = myEditor.getQuery().getOntologyData();
	}
	
	public void execute() {
		newComponent.setMyBounds(bounds);
		whereClause.addComponent(newComponent);
		addDefaultConnections();
		super.execute();
	}
	
	private void addDefaultConnections() {
		addDomainConnections();
		addRangeConnections();
		
	}

	private void addDomainConnections() {
		for(WhereClauseComponent otherComponent: whereClause.getComponents()){
			if((otherComponent instanceof ClassComponent) && (otherComponent != newComponent)) {
				Set<ObjectPropertyItem> props = ontData.lookupFromDomainRange(newComponent.getMyClass(), ((ClassComponent)otherComponent).getMyClass());
				if((props != null) && (props.size() > 0)) {
					ObjectPropertyItem prop = (ObjectPropertyItem)props.toArray()[0];
					WhereClausePredicate pred = new WhereClausePredicate(prop, newComponent, otherComponent);
					if(!alreadyHasPredicate(prop, otherComponent.getRangePredicates())) {
						newComponent.addDomainPredicate(pred);
						otherComponent.addRangePredicate(pred);
					}
				}
			}
		}
	}

	private void addRangeConnections() {
		for(WhereClauseComponent otherComponent: whereClause.getComponents()){
			if((otherComponent instanceof ClassComponent) && (otherComponent != newComponent)) {
				Set<ObjectPropertyItem> props = ontData.lookupFromDomainRange(((ClassComponent)otherComponent).getMyClass(), newComponent.getMyClass());
				if((props != null) && (props.size() > 0)) {
					ObjectPropertyItem prop = (ObjectPropertyItem)props.toArray()[0];
					WhereClausePredicate pred = new WhereClausePredicate(prop, otherComponent, newComponent);
					if(!alreadyHasPredicate(prop, otherComponent.getDomainPredicates())) {
						newComponent.addRangePredicate(pred);
						otherComponent.addDomainPredicate(pred);
					}
				}
			}
		}
	}

	private boolean alreadyHasPredicate(ObjectPropertyItem prop, List<WhereClausePredicate> predicates) {
		IRI matchingIRI = prop.getMyIRI();
		for(WhereClausePredicate pred: predicates) {
			if(pred.getProperty().getMyIRI().equals(matchingIRI))return true;
		}
		return false;
	}

	public void undo() {
		whereClause.removeComponent(newComponent);
		removeDomainConnections();
		removedRangeConnections();
		super.undo();
	}

	private void removeDomainConnections() {
		List<WhereClausePredicate> preds = new ArrayList<WhereClausePredicate>(newComponent.getDomainPredicates());
		for(WhereClausePredicate pred: preds) {
			WhereClauseComponent otherComponent = pred.getRange();
			newComponent.removeDomainPredicate(pred);
			otherComponent.removeRangePredicate(pred);
		}
	}

	private void removedRangeConnections() {
		List<WhereClausePredicate> preds = new ArrayList<WhereClausePredicate>(newComponent.getRangePredicates());
		for(WhereClausePredicate pred: preds) {
			WhereClauseComponent otherComponent = pred.getDomain();
			newComponent.removeRangePredicate(pred);
			otherComponent.removeDomainPredicate(pred);
		}
	
	}

}
