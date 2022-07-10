package uk.ac.kcl.cch.jb.sparql.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.figures.SelectAreaFrontFigure;
import uk.ac.kcl.cch.jb.sparql.model.SelectAreaFront;

public class SelectAreaFrontPart extends AbstractGraphicalEditPart {

	private QueryCreationEditor myEditor;

	public SelectAreaFrontPart(SelectAreaFront model, QueryCreationEditor myEditor) {
		super();
		setModel(model);
		this.myEditor = myEditor;
	}

	@Override
	protected IFigure createFigure() {
		// TODO Auto-generated method stub
		return new SelectAreaFrontFigure();
	}

	@Override
	protected void createEditPolicies() {
		// TODO Auto-generated method stub

	}

}
