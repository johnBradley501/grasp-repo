package uk.ac.kcl.cch.jb.sparql.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import uk.ac.kcl.cch.jb.sparql.commands.DeleteWhereClausePredicateCommand;
import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.figures.WhereClausePredicateFigure;
import uk.ac.kcl.cch.jb.sparql.model.WhereClausePredicate;

public class WhereClausePredicatePart extends AbstractConnectionEditPart implements PropertyChangeListener {

	private QueryCreationEditor myEditor;

	public WhereClausePredicatePart(WhereClausePredicate predicate, QueryCreationEditor myEditor) {
		super();
		setModel(predicate);
		this.myEditor = myEditor;
	}
	
	public WhereClausePredicate getWhereClausePredicate() {
		return (WhereClausePredicate)getModel();
	}
	
	protected IFigure createFigure() {
		return new WhereClausePredicateFigure(getWhereClausePredicate());
	}
	
	public WhereClausePredicateFigure getWhereClausePredicateFigure() {
		return (WhereClausePredicateFigure)getFigure();
	}

	
	public void activate() {
		if(this.isActive()) return;
		getWhereClausePredicate().addPropertyChangeListener(this);
		super.activate();
	}
	
	public void deactivate() {
		if(!isActive())return;
		super.deactivate();
		getWhereClausePredicate().removePropertyChangeListener(this);
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new ConnectionEndpointEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_ROLE, new ConnectionEditPolicy() {
			protected Command getDeleteCommand(GroupRequest request) {
				return new DeleteWhereClausePredicateCommand(getWhereClausePredicate(), myEditor);
			}
		});
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String pName = evt.getPropertyName();
		if(pName.equals(WhereClausePredicate.OPTIONAL_CHANGED)) getWhereClausePredicateFigure().setMyLineType();
		
	}

}
