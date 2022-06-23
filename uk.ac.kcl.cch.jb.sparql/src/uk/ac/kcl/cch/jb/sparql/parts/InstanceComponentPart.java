package uk.ac.kcl.cch.jb.sparql.parts;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.EditPolicy;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.figures.WhereComponentFigure;
import uk.ac.kcl.cch.jb.sparql.model.InstanceComponent;
import uk.ac.kcl.cch.jb.sparql.policies.NamedComponentDirectEditPolicy;
import uk.ac.kcl.cch.jb.sparql.policies.InstanceComponentDirectEditPolicy;

public class InstanceComponentPart extends WhereClauseComponentPart {
	
	public InstanceComponentPart(InstanceComponent model, QueryCreationEditor myEditor) {
		super(model, myEditor);
	}
	
	public InstanceComponent getInstanceComponent() {
		return (InstanceComponent)getModel();
	}


	@Override
	protected IFigure createFigure() {
		return new WhereComponentFigure(getWhereClauseComponent());
	}

	protected void performDirectEditing() {
		Label label = getWhereComponentFigure().getNameLabel();
		InstanceComponentDirectEditManager manager =
				new InstanceComponentDirectEditManager(this, new SimpleDropDownEditorLocator(label), myEditor.getQuery(), getInstanceComponent().getMyClass());
		manager.show();
	}
	
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new InstanceComponentDirectEditPolicy(myEditor));
		super.createEditPolicies();
	}
	
	public void propertyChange(PropertyChangeEvent arg0) {
		String propName = arg0.getPropertyName();
		if(propName.equals(InstanceComponent.INSTANCE_CHANGE))
			this.getWhereComponentFigure().redraw();
		else super.propertyChange(arg0);
	}
	

}
