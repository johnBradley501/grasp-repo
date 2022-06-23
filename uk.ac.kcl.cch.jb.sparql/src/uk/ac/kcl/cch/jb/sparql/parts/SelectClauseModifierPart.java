package uk.ac.kcl.cch.jb.sparql.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.ArrayList;

import org.eclipse.draw2d.IFigure;

import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.figures.SelectModifierFigure;
import uk.ac.kcl.cch.jb.sparql.model.ModifierComponent;
import uk.ac.kcl.cch.jb.sparql.model.SelectClauseModifier;

public class SelectClauseModifierPart  extends AbstractGraphicalEditPart {

	private QueryCreationEditor myEditor;

	public SelectClauseModifierPart(SelectClauseModifier model, QueryCreationEditor myEditor) {
		super();
		setModel(model);
		this.myEditor = myEditor;
	}
	
	public SelectClauseModifier getModifier() {
		return (SelectClauseModifier)getModel();
	}

	@Override
	protected IFigure createFigure() {
		return new SelectModifierFigure();
	}
	
	public List getModelChildren() {
		List rslt = new ArrayList();
		rslt.add(new ModifierComponent(ModifierComponent.DISTINCT_COMPONENT,getModifier()));
		rslt.add(new ModifierComponent(ModifierComponent.LIMIT_COMPONENT,getModifier()));
		rslt.add(new ModifierComponent(ModifierComponent.OFFSET_COMPONENT,getModifier()));
		rslt.add(new ModifierComponent(ModifierComponent.PADDING_COMPONENT,getModifier()));
		return rslt;
	}


	@Override
	protected void createEditPolicies() {
		// none needed    jb
		
	}

}
