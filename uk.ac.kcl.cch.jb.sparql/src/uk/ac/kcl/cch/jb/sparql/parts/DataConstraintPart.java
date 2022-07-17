package uk.ac.kcl.cch.jb.sparql.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.requests.LocationRequest;
import org.eclipse.gef.tools.DirectEditManager;

import uk.ac.kcl.cch.jb.sparql.builder.ExpressionMaker;
import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.figures.DataConstraintFigure;
import uk.ac.kcl.cch.jb.sparql.model.DataConstraint;
import uk.ac.kcl.cch.jb.sparql.model.SelectVarItem;
import uk.ac.kcl.cch.jb.sparql.model.VariableComponent;
import uk.ac.kcl.cch.jb.sparql.policies.DataConstraintComponentEditPolicy;
import uk.ac.kcl.cch.jb.sparql.policies.DataConstraintDirectEditPolicy;

public class DataConstraintPart extends AbstractGraphicalEditPart implements PropertyChangeListener{

	private QueryCreationEditor myEditor;
	private CommandStack commandStack;
	private DataConstraintDirectEditPolicy directEditPolicy;
	private String[] dropDownNames = null;
	private int[] dropDownValues = null;

	public DataConstraintPart(DataConstraint model, QueryCreationEditor myEditor) {
		super();
		setModel(model);
		this.myEditor = myEditor;
		this.commandStack = myEditor.getCommandStack();
		getDropDownLists();
	}
	
	private void getDropDownLists() {
		VariableComponent owner = getDataConstraint().getOwner();
		String frag = owner.getDataType().getFragment();
		dropDownNames = ExpressionMaker.instance.getExpressionNameList(frag);
		dropDownValues = ExpressionMaker.instance.getExpressionValList(frag);
	}
	
	public DataConstraint getDataConstraint() {
		return (DataConstraint)getModel();
	}
	
	public void activate() {
		if(!isActive()){
			super.activate();
			getDataConstraint().addPropertyChangeListener(this);
		}
	}

	public void deactivate() {
		if(isActive()) {
			super.deactivate();
			getDataConstraint().removePropertyChangeListener(this);
		}
	}


	@Override
	protected IFigure createFigure() {
		return new DataConstraintFigure(getDataConstraint());
	}

	protected DataConstraintFigure getDataConstraintFigure() {
		return (DataConstraintFigure)getFigure();
	}
	
	public void performRequest(Request req) {
		if(req.getType() == RequestConstants.REQ_DIRECT_EDIT ||
				req.getType() == RequestConstants.REQ_OPEN) {
			performDirectEditing((LocationRequest)req);
		}
	}
	
	private void performDirectEditing(LocationRequest req) {
		Point rellocation = new Point(req.getLocation());
		getFigure().getParent().translateToRelative(rellocation); // not sure I understand why this is needed   JB
		// int myType = getDataConstraintFigure().getSelectedType(req.getLocation());
		int myType = getDataConstraintFigure().getSelectedType(rellocation);
		directEditPolicy.setField(myType);
		if(myType == DataConstraintFigure.TYPE_FIELD) {
			Label label = getDataConstraintFigure().getTypeLabel();
			DirectEditManager man = new ValuedDropDownDirectEditManager(this, new SimpleDropDownEditorLocator(label),
					dropDownNames,dropDownValues, getDataConstraint().getConstraintType());
			man.show();
		} else if(myType == DataConstraintFigure.VAL_FIELD) {
			Label label = getDataConstraintFigure().getValueLabel();
			NamedComponentDirectEditManager manager =
					new NamedComponentDirectEditManager(this, new NamedComponentEditorLocator(label), label);
			manager.show();
			
		}
	}
	
	public int getChosenConstraintType(int item) {
		return dropDownValues[item];
	}

	@Override
	protected void createEditPolicies() {
		this.directEditPolicy = new DataConstraintDirectEditPolicy(myEditor);
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, directEditPolicy);
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new DataConstraintComponentEditPolicy(myEditor));

	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		String eventName = arg0.getPropertyName();
		if(eventName.equals(DataConstraint.TYPE_CHANGED))
			getDataConstraintFigure().setType(getDataConstraint().getConstraintType());
		else if(eventName.equals(DataConstraint.VALUE_CHANGED))
			getDataConstraintFigure().setValue(getDataConstraint().getConstraintValue());
		
	}

}
