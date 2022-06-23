package uk.ac.kcl.cch.jb.sparql.figures;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Panel;
import org.eclipse.gef.EditPart;

public class EditorRootFigure extends Panel implements IFigure {
	
	public EditorRootFigure() {
		super();
		this.setLayoutManager(new BorderLayout());
		// this.setBackgroundColor(ColorConstants.cyan);
	}

}
