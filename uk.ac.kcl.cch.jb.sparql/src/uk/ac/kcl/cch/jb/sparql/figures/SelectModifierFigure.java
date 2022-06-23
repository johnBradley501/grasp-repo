package uk.ac.kcl.cch.jb.sparql.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.gef.commands.CommandStack;

import uk.ac.kcl.cch.jb.sparql.model.SelectClauseModifier;

public class SelectModifierFigure extends Figure {

	public SelectModifierFigure() {
		super();
		this.setLayoutManager(new ToolbarLayout());
		this.setOpaque(true);
		this.setBackgroundColor(ColorConstants.lightGray);
	}

}
