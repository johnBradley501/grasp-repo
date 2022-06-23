package uk.ac.kcl.cch.jb.sparql.parts;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editpolicies.OrderedLayoutEditPolicy;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.figures.VariableComponentFigure;
import uk.ac.kcl.cch.jb.sparql.model.INamedComponent;
import uk.ac.kcl.cch.jb.sparql.model.VariableComponent;
import uk.ac.kcl.cch.jb.sparql.policies.NamedComponentDirectEditPolicy;
import uk.ac.kcl.cch.jb.sparql.policies.VariableComponentLayoutEditPolicy;

public class VariableComponentPart extends WhereClauseComponentPart implements NamedComponentPart {
	
	public VariableComponentPart(VariableComponent model, QueryCreationEditor myEditor) {
		super(model, myEditor);
	}
	
	public VariableComponent getVariableComponent() {
		return (VariableComponent)getModel();
	}
	
	public INamedComponent getNamedComponent() {
		return (INamedComponent)getModel();
	}

	@Override
	protected IFigure createFigure() {
		return new VariableComponentFigure(getVariableComponent());
	}
	
	public VariableComponentFigure getVariableComponentFigure() {
		return (VariableComponentFigure)getFigure();
	}
	
	//public void activate() {
	//	if(this.isActive()) return;
	//	getVariableComponentFigure().setDepthForConstraints(getVariableComponent().getConstraints().size());
	//	super.activate();
	//}

	
	public IFigure getContentPane() {
		return getVariableComponentFigure().getConstraintArea();
	}
	
	public List getModelChildren() {
		return getVariableComponent().getConstraints();
	}

	@Override
	protected void performDirectEditing() {
		Label label = getVariableComponentFigure().getNameLabel();
		NamedComponentDirectEditManager manager =
				new NamedComponentDirectEditManager(this, new NamedComponentEditorLocator(label), label);
		manager.show();
	}
	
	public void propertyChange(PropertyChangeEvent arg) {
		String propName = arg.getPropertyName();
		if(propName.equals(INamedComponent.NEW_NAME))
			getVariableComponentFigure().setName(getNamedComponent().getName());
		else if(propName.equals(VariableComponent.CHANGE_CONSTRAINTS)) {
			// getVariableComponentFigure().setDepthForConstraints(getVariableComponent().getConstraints().size());
			refreshChildren();
			refreshVisuals();
		}
		else super.propertyChange(arg);
	}
	
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new NamedComponentDirectEditPolicy(myEditor));
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new VariableComponentLayoutEditPolicy(myEditor));
		super.createEditPolicies();
	}
}