package uk.ac.kcl.cch.jb.sparql.figures;

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Point;

import uk.ac.kcl.cch.jb.sparql.model.SelectVarItem;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;

public class SelectVarItemFigure extends Figure implements VarNameFigure {

	private Label nameLabel;
	private Label orderLabel;
	private Label aggregateLabel;
	private Label aggNameLabel;

	public SelectVarItemFigure(SelectVarItem myItem) {
		super();
		//GridLayout layout = new GridLayout(1, true);
		//layout.marginHeight = 0;
		//layout.marginWidth = 5;
		//layout.horizontalSpacing = 0;
		//layout.verticalSpacing = 0;
		//this.setLayoutManager(layout);
		this.setLayoutManager(new ToolbarLayout());
		this.setOpaque(true);
		this.setBackgroundColor(ColorConstants.white);
		
		this.nameLabel = makeComponent(myItem.getComponent().getName(),"Variable Name"); // tooltip names no longer needed  JB
		this.orderLabel = makeComponent(SelectVarItem.orderings[myItem.getSortOrder()], "Ordering");
		this.aggregateLabel = makeComponent(myItem.getAggTypeName(), "Aggregation");
		this.aggNameLabel = makeComponent("", "Agg Varname");
		this.setAggName(myItem.getAggVarName());
	}
	
	public void add(IFigure figure,java.lang.Object constraint,int index) {
		super.add(figure, new GridData(GridData.FILL_HORIZONTAL), index);

	}
	
	private Label makeComponent(String initValue, String tooltipText) {
		Figure compFig = new Figure();
		compFig.setOpaque(true);
		// if(tooltipText != null)compFig.setToolTip(new Label(tooltipText));
		compFig.setBorder(new LineBorder());
		GridLayout layout = new GridLayout(1, true);
		layout.marginHeight = 0;
		layout.marginWidth = 5;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		compFig.setLayoutManager(layout);
		
		Label myLabel = new Label();
		myLabel.setText(initValue);
		compFig.add(myLabel);
		this.add(compFig);
		return myLabel;
	}

	@Override
	public void setName(String newName) {
		nameLabel.setText(newName);
		
	}

	@Override
	public String getName() {
		return nameLabel.getText();
	}

	@Override
	public Label getNameLabel() {
		return nameLabel;
	}
	
	public Label getOrderLabel() {return orderLabel;}
	public Label getAggregateLabel() {return aggregateLabel;}
	public Label getAggNameLabel() {return aggNameLabel;}
	
	public void setAggName(String text) {
		if(text == null)aggNameLabel.setText("--");
		else aggNameLabel.setText(text);
	}
	
	public Label localPointedLabel(Point pos) {
		IFigure f = this.findFigureAt(pos);
		if(f == null)return null;
		if(f instanceof Label)return (Label)f;
		List children = f.getChildren();
		if(children.size() != 1)return null;
		IFigure child = (IFigure)children.get(0);
		if(child instanceof Label)return (Label)child;
		return null;
	}
	
	public int getSelectedType(Point pos) {
		Label l = localPointedLabel(pos);
		if(l == null)return -1;
		if(l == nameLabel)return SelectVarItem.VAR_COMPONENT;
		if(l == orderLabel)return SelectVarItem.ORDER_COMPONENT;
		if(l == aggregateLabel)return SelectVarItem.AGGREGATE_COMPONENT;
		if(l == aggNameLabel)return SelectVarItem.AGG_NAME_COMPONENT;
		return -1;
	}

	public void setOrderLabelText(String string) {
		getOrderLabel().setText(string);
		
	}
	
	public void handleHover(boolean isHovering) {
		IFigure nameParent = nameLabel.getParent();
		if(isHovering) {
			nameParent.setBackgroundColor(VarNameFigure.COLOUR_PURPLE);
			nameLabel.setForegroundColor(ColorConstants.white);
		} else {
			nameParent.setBackgroundColor(ColorConstants.white);
			nameLabel.setForegroundColor(ColorConstants.black);
		}
	}
}
