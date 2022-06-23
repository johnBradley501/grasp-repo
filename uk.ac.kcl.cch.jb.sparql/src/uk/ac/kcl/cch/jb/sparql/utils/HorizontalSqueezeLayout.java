package uk.ac.kcl.cch.jb.sparql.utils;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;

import uk.ac.kcl.cch.jb.sparql.model.WhereClauseComponent;

public class HorizontalSqueezeLayout extends AbstractLayoutAlgorithm {
	
	// see example from Rubel, Wren, Claybert 2021, pp 167-173
	
	private boolean isThisNeeded = false;
	private int boundsMin = 0;
	private int boundsMax = 0;
	
	public HorizontalSqueezeLayout(int style) {
		super(style);
	}

	@Override
	public void setLayoutArea(double x, double y, double width, double height) {
		// not needed here

	}

	@Override
	protected boolean isValidConfiguration(boolean asynchronous, boolean continuous) {
		return true;
	}

	@Override
	protected void applyLayoutInternal(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider,
			double boundsX, double boundsY, double boundsWidth, double boundsHeight) {
		calculateSqueezeNumbers(entitiesToLayout, relationshipsToConsider, boundsX, boundsY, boundsWidth, boundsHeight);
		if(!isThisNeeded)return;
		double currentWidth = boundsMax - boundsMin;
		for (InternalNode intNode: entitiesToLayout) {
			InternalNode interimNode = (InternalNode)intNode.getLayoutEntity();
			WhereClauseComponent comp = (WhereClauseComponent)interimNode.getLayoutEntity();
			Rectangle newBounds = new Rectangle(comp.getMyBounds());
			newBounds.x = (int)((newBounds.x - boundsMin)/currentWidth*boundsWidth+boundsX);
			comp.setMyBounds(newBounds);
		}
	}

	private void calculateSqueezeNumbers(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider,
			double x, double y, double width, double height) {
		boundsMin = (int)x;
		boundsMax = boundsMin+(int)width;
		
		for(InternalNode intNode: entitiesToLayout) {
			InternalNode interimNode = (InternalNode)intNode.getLayoutEntity();
			WhereClauseComponent comp = (WhereClauseComponent)interimNode.getLayoutEntity();
			Rectangle myBounds = comp.getMyBounds();
			if(myBounds.x < boundsMin)boundsMin = myBounds.x;
			if(myBounds.x+myBounds.width > boundsMax)boundsMax = myBounds.x+myBounds.width;
		}
		isThisNeeded = (boundsMin != (int)x) || (boundsMax != boundsMin+(int)width);

	}

	protected void preLayoutAlgorithm(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider,
			double x, double y, double width, double height) {
			// nothing needed here JB
	}
	
	@Override
	protected void postLayoutAlgorithm(InternalNode[] entitiesToLayout,
			InternalRelationship[] relationshipsToConsider) {
		// nothing needed here  JB
	}

	@Override
	protected int getTotalNumberOfLayoutSteps() {
		return 0;
	}

	@Override
	protected int getCurrentLayoutStep() {
		return 0;
	}

}
