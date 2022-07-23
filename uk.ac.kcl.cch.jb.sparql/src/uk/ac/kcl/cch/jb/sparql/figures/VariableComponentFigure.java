package uk.ac.kcl.cch.jb.sparql.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import uk.ac.kcl.cch.jb.sparql.model.VariableComponent;

public class VariableComponentFigure extends RectangleFigure implements VarNameFigure{

	public static int nameHeight = -1;
	// public static int constraintHeight = 18;
	public static Dimension DEFAULT_DIMENSION = new Dimension(200,nameHeight);

	private VariableComponent myComponent;
	private GridLayout layout = null;
	private Label myName;
	private Figure constraintArea;
	
	public VariableComponentFigure(VariableComponent myComponent) {
		super();
		this.myComponent = myComponent;
		
		layout = new GridLayout(1, false);
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		this.setLayoutManager(layout);
		// this.setBorder(new LineBorder(VarNameFigure.COLOUR_PURPLE, 3));
		this.setPreferredSize(DEFAULT_DIMENSION);
		
		myName = new Label();
		myName.setLabelAlignment(PositionConstants.CENTER);
		myName.setBorder(new LineBorder(VarNameFigure.COLOUR_PURPLE, 3));
		myName.setText(myComponent.getName());
		myName.setFont(Display.getCurrent().getSystemFont());
		layout.setConstraint(myName, new GridData(GridData.FILL_HORIZONTAL));
		this.add(myName);
		if(nameHeight == -1)nameHeight = myName.getMinimumSize().height;
		
		constraintArea = new Figure();
		// constraintArea.setBackgroundColor(ColorConstants.cyan);
		layout.setConstraint(constraintArea,  new GridData(GridData.FILL_HORIZONTAL));
		ToolbarLayout constraintLayout = new ToolbarLayout(false);
		constraintLayout.setStretchMinorAxis(true);
		constraintArea.setLayoutManager(constraintLayout);
		constraintArea.setOpaque(true);
		this.add(constraintArea);
		this.setOpaque(true);
	}

	public void setName(String newName) {
		myName.setText(newName);
	}
	
	public String getName() {
		return myName.getText();
	}
	
	public Label getNameLabel() {
		return myName;
	}
	
	public Figure getConstraintArea() {return constraintArea;}
	
	//public void setDepthForConstraints(int numberConstraints) {
	//	IFigure parent = this.getParent();
	//	LayoutManager man = parent.getLayoutManager();
	//	Rectangle r = new Rectangle((Rectangle)man.getConstraint(this));
	//	// r.height = myName.getBounds().height+20*numberConstraints;
	//	r.height = nameHeight+constraintHeight*numberConstraints;
	//	man.setConstraint(this, r);
		
	//}

}
