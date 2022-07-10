package uk.ac.kcl.cch.jb.sparql.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.figures.SelectClauseFigure;
import uk.ac.kcl.cch.jb.sparql.model.QuerySelectClause;
import uk.ac.kcl.cch.jb.sparql.model.SelectAreaFront;
import uk.ac.kcl.cch.jb.sparql.policies.SelectClauseLayoutPolicy;

public class QuerySelectClausePart extends AbstractGraphicalEditPart implements PropertyChangeListener {

	private QueryCreationEditor myEditor;

	public QuerySelectClausePart(QuerySelectClause model, QueryCreationEditor myEditor) {
		super();
		setModel(model);
		this.myEditor = myEditor;
	}
	
	public QuerySelectClause getSelectClause() {
		return (QuerySelectClause)getModel();
	}
	
	public void activate() {
		if(!isActive()) {
			super.activate();
			getSelectClause().addPropertyChangeListener(this);
		}
	}
	
	public void deactivate() {
		if(isActive()) {
			super.deactivate();
			getSelectClause().removePropertyChangeListener(this);
		}
	}
	
	public QueryCreationEditor getMyEditor() {return myEditor;}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		refreshChildren();
	}

	@Override
	protected IFigure createFigure() {
		// Label myLabel = new Label();
		// myLabel.setText("The Select Clause Area");
		// myLabel.setBackgroundColor(ColorConstants.orange);
		// return myLabel;
		return new SelectClauseFigure();
	}
	
	public List getModelChildren() {
		List children = new ArrayList();
		children.add(new SelectAreaFront(getSelectClause()));
		children.addAll(getSelectClause().getVars());
		return children;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new SelectClauseLayoutPolicy(this));
	}

}
