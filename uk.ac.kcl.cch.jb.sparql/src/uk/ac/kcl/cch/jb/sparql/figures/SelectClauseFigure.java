package uk.ac.kcl.cch.jb.sparql.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;

public class SelectClauseFigure extends Figure {
	
	private ToolbarLayout myLayout = new ToolbarLayout(true);
	
	public SelectClauseFigure() {
		super();
		this.setLayoutManager(myLayout);
		this.setOpaque(true);
		this.setBorder(new LineBorder());
	}

}
