package uk.ac.kcl.cch.jb.sparql.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.figures.WhereComponentFigure;
import uk.ac.kcl.cch.jb.sparql.model.ClassComponent;
import uk.ac.kcl.cch.jb.sparql.model.InstanceComponent;
import uk.ac.kcl.cch.jb.sparql.model.WhereClauseComponent;
import uk.ac.kcl.cch.jb.sparql.policies.WhereClauseComponentConnectionEditPolicy;
import uk.ac.kcl.cch.jb.sparql.policies.NamedComponentDirectEditPolicy;
import uk.ac.kcl.cch.jb.sparql.policies.WhereClauseComponentsEditPolicy;

public abstract class WhereClauseComponentPart extends AbstractGraphicalEditPart implements PropertyChangeListener {
	
	// directEdit code here and elsewhere is modelled after that in 
	// https://vainolo.com/2011/07/07/creating-a-gef-editor-%E2%80%93-part-7-moving-elements-and-direct-editing/

	protected QueryCreationEditor myEditor;
	private ConnectionAnchor anchor;

	public WhereClauseComponentPart(WhereClauseComponent model, QueryCreationEditor myEditor) {
		super();
		setModel(model);
		this.myEditor = myEditor;
	}
	
	public WhereClauseComponent getWhereClauseComponent() {
		return (WhereClauseComponent)getModel();
	}
	
	public WhereComponentFigure getWhereComponentFigure(){
		return (WhereComponentFigure)getFigure();
	}
	
	public void activate() {
		if(this.isActive()) return;
		getWhereClauseComponent().addPropertyChangeListener(this);
		super.activate();
	}
	
	public void deactivate() {
		if(!isActive())return;
		super.deactivate();
		getWhereClauseComponent().removePropertyChangeListener(this);
	}
	
	public void refreshVisuals() {
		mapToBounds();
	}
	
	protected void mapToBounds() {
		if(!getFigure().getBounds().equals(getWhereClauseComponent().getMyBounds())) {
			((GraphicalEditPart) getParent()).setLayoutConstraint(
					this,
					getFigure(),
					getWhereClauseComponent().getMyBounds());
		}
		
	}
	
	public void performRequest(Request req) {
		if(req.getType() == RequestConstants.REQ_DIRECT_EDIT ||
				req.getType() == RequestConstants.REQ_OPEN) {
			performDirectEditing();
		}
	}

	protected abstract void performDirectEditing();

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		String propName = arg0.getPropertyName();
		if(propName.equals(ClassComponent.NEW_BOUNDS)) {
			mapToBounds();
		} else if((propName.equals(ClassComponent.DOMAIN_PRED_CHANGE)) || 
				(propName.equals(ClassComponent.RANGE_PRED_CHANGE))) {
	          refreshTargetConnections();
	          refreshSourceConnections();
			
		}

	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new WhereClauseComponentsEditPolicy(myEditor));
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new WhereClauseComponentConnectionEditPolicy(myEditor));

	}

	protected List getModelSourceConnections(){
		return getWhereClauseComponent().getDomainPredicates();
	}


	protected List getModelTargetConnections(){
		return getWhereClauseComponent().getRangePredicates();
	}
	
	private ConnectionAnchor getConnectionAnchor() {
		if(anchor == null) anchor = new ChopboxAnchor(getFigure());
		return anchor;
	}

	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return getConnectionAnchor();
	}

	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return getConnectionAnchor();
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return getConnectionAnchor();
	}

	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return getConnectionAnchor();
	}

}
