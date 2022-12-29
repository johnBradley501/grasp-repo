package uk.ac.kcl.cch.jb.sparql.figures;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public interface VarNameFigure extends IFigure {

	public static Color COLOUR_PURPLE = new Color(Display.getCurrent(),112, 48, 160);

	public void setName(String newName);
	public String getName();
	
	public Label getNameLabel();
	
	public void handleHover(boolean isHovering);

}
