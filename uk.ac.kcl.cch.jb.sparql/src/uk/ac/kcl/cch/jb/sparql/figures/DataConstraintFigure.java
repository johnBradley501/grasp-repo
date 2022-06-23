package uk.ac.kcl.cch.jb.sparql.figures;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

import uk.ac.kcl.cch.jb.sparql.builder.ExpressionMaker;
import uk.ac.kcl.cch.jb.sparql.model.DataConstraint;

public class DataConstraintFigure extends Figure {
	
	public static final int TYPE_FIELD = 0;
	public static final int VAL_FIELD = 1;
	
	private DataConstraint myDC;
	
	private Figure typeHolder;
	private Label typeLabel;
	private Figure valueHolder;
	private Label valueLabel;

	public DataConstraintFigure(DataConstraint myDC) {
		super();
		this.myDC = myDC;
		constructFigure();
	}

	private void constructFigure() {
		GridLayout myLayout = new GridLayout(2, false);
		myLayout.horizontalSpacing = 0;
		myLayout.marginHeight = 0;
		myLayout.marginWidth = 0;
		myLayout.verticalSpacing = 0;
		this.setLayoutManager(myLayout);
		
		typeHolder = new Figure();
		typeHolder.setBorder(new LineBorder(1));
		typeHolder.setPreferredSize(50, VariableComponentFigure.constraintHeight-1);
		typeHolder.setLayoutManager(new StackLayout());
		this.add(typeHolder);
		
		this.typeLabel = new Label();
		//typeLabel.setPreferredSize(50, VariableComponentFigure.constraintHeight);
		//typeLabel.setMinimumSize(new Dimension(50, VariableComponentFigure.constraintHeight));
		//typeLabel.setOpaque(true);
		//typeLabel.setBorder(new LineBorder(1));
		setType(myDC.getConstraintType());
		typeHolder.add(typeLabel);
		
		valueHolder = new Figure();
		valueHolder.setBorder(new LineBorder(1));
		valueHolder.setLayoutManager(new StackLayout());
		this.add(valueHolder, new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		
		
		this.valueLabel = new Label();
		//valueLabel.setBorder(new LineBorder(1));
		setValue(myDC.getConstraintValue());
		valueHolder.add(valueLabel);
		
		this.setOpaque(true);
		
	}
	
	public Label getTypeLabel() {return typeLabel;}
	
	public void setType(int type) {
		if(type < 0)typeLabel.setText("??");
		else typeLabel.setText(ExpressionMaker.ExpressionNames[type]);

	}
	
	public Label getValueLabel() {return valueLabel;}
	
	public void setValue(String val) {
		if(val == null)valueLabel.setText("??");
		else valueLabel.setText(val);
	}
	
	public int getSelectedType(Point pos) {
		IFigure f = this.findFigureAt(pos);
		if(f == null)return -1;
		if(f == typeLabel) return TYPE_FIELD;
		if(f == valueLabel) return VAL_FIELD;
		if(f == typeHolder) return TYPE_FIELD;
		if(f == valueHolder) return VAL_FIELD;
		return -1;
	}

}
