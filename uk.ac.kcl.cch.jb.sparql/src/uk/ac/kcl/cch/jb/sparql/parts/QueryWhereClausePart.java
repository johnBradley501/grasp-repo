package uk.ac.kcl.cch.jb.sparql.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.figures.WhereClauseRootFigure;
import uk.ac.kcl.cch.jb.sparql.model.QueryWhereClause;
import uk.ac.kcl.cch.jb.sparql.policies.WhereComponentsXYLayoutPolicy;

public class QueryWhereClausePart extends AbstractGraphicalEditPart implements PropertyChangeListener {

	private QueryCreationEditor myEditor;
	
	public QueryWhereClausePart(QueryWhereClause model, QueryCreationEditor myEditor) {
		super();
		setModel(model);
		this.myEditor = myEditor;
		// getWhereClause().addPropertyChangeListener(this);
	}
	
	public QueryWhereClause getWhereClause() {
		return (QueryWhereClause)getModel();
	}


	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String property = evt.getPropertyName();
		if(property.equals(QueryWhereClause.COMPONENTS_CHANGED)) {
			refreshChildren();
		}

	}
	
	public void activate() {
		if(!isActive()) {
			super.activate();
			getWhereClause().addPropertyChangeListener(this);
		}
	}
	
	public void deactivate() {
		if(isActive()) {
			super.deactivate();
			getWhereClause().removePropertyChangeListener(this);
		
		}
	}

	@Override
	protected IFigure createFigure() {
		// TODO Auto-generated method stub
		return new WhereClauseRootFigure();
	}
	
	public WhereClauseRootFigure getWhereClauseRootFigure() {
		return (WhereClauseRootFigure)getFigure();
	}
	
	
	public IFigure getContentPane() {
		return getWhereClauseRootFigure().getContentsFigure();
	}


	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new WhereComponentsXYLayoutPolicy(myEditor));

	}
	
	public List getModelChildren() {
		return getWhereClause().getComponents();
	}


}
