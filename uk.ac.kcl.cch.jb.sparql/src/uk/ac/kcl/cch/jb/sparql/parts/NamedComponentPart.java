package uk.ac.kcl.cch.jb.sparql.parts;

import org.eclipse.gef.GraphicalEditPart;

import uk.ac.kcl.cch.jb.sparql.figures.VariableComponentFigure;
import uk.ac.kcl.cch.jb.sparql.model.INamedComponent;

public interface NamedComponentPart extends GraphicalEditPart{
	public INamedComponent getNamedComponent();
	
	public VariableComponentFigure getVariableComponentFigure();
}
