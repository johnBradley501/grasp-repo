package uk.ac.kcl.cch.jb.sparql.parts;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.EditPolicy;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.figures.VariableComponentFigure;
import uk.ac.kcl.cch.jb.sparql.figures.WhereComponentFigure;
import uk.ac.kcl.cch.jb.sparql.model.ClassComponent;
import uk.ac.kcl.cch.jb.sparql.model.INamedComponent;
import uk.ac.kcl.cch.jb.sparql.policies.NamedComponentDirectEditPolicy;

public class ClassComponentPart extends WhereClauseComponentPart implements NamedComponentPart {
	
	public ClassComponentPart(ClassComponent model, QueryCreationEditor myEditor) {
		super(model, myEditor);
	}
	
	public ClassComponent getClassComponent() {
		return (ClassComponent)getModel();
	}
	
	public INamedComponent getNamedComponent() {
		return (INamedComponent)getModel();
	}

	@Override
	protected IFigure createFigure() {
		return new WhereComponentFigure(getWhereClauseComponent());
	}
	
	public VariableComponentFigure getVariableComponentFigure() {
		return (VariableComponentFigure)getFigure();
	}

	protected void performDirectEditing() {
		Label label = getWhereComponentFigure().getNameLabel();
		NamedComponentDirectEditManager manager =
				new NamedComponentDirectEditManager(this, new NamedComponentEditorLocator(label), label);
		manager.show();
	}
	
	public void propertyChange(PropertyChangeEvent arg0) {
		String propName = arg0.getPropertyName();
		if(propName.equals(INamedComponent.NEW_NAME)) 
			getWhereComponentFigure().setName(getClassComponent().getName());
		else if(propName.equals(ClassComponent.NEW_CLASS))
			// getWhereComponentFigure().setClass(getClassComponent().getMyClass());
			getWhereComponentFigure().redraw();
		else super.propertyChange(arg0);
	}
	
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new NamedComponentDirectEditPolicy(myEditor));
		super.createEditPolicies();
	}

	@Override
	public void handleHover(boolean isHovering) {
		getWhereComponentFigure().handleHover(isHovering);
		
	}


}
