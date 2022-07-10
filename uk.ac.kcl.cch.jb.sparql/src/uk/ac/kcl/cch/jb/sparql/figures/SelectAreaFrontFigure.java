package uk.ac.kcl.cch.jb.sparql.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;

public class SelectAreaFrontFigure extends Figure {

	public SelectAreaFrontFigure() {
		super();
		this.setLayoutManager(new ToolbarLayout());
		this.setOpaque(true);
		this.setBackgroundColor(ColorConstants.lightGray);
		createLabel("VarName:");
		createLabel("Order:");
		createLabel("Aggregate:");
		createLabel("AggName:");
	}

	private void createLabel(String string) {
		Label label = new Label();
		label.setPreferredSize(new Dimension(60,16));
		label.setBorder(new LineBorder(1));
		label.setText(" "+string+" ");
		this.add(label);
		
	}
}
