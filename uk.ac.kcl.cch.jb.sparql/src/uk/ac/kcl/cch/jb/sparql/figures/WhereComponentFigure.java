package uk.ac.kcl.cch.jb.sparql.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.widgets.Display;

import uk.ac.kcl.cch.jb.sparql.model.ClassComponent;
import uk.ac.kcl.cch.jb.sparql.model.InstanceComponent;
import uk.ac.kcl.cch.jb.sparql.model.WhereClauseComponent;

public class WhereComponentFigure extends RectangleFigure implements VarNameFigure { // Label { // Figure {
	
	public static Dimension DEFAULT_DIMENSION = new Dimension(200,36);
	private static int STATIC_HEIGHT = -1;
	
	private WhereClauseComponent myComponent;
	private Label myName = null;
	private Label uriComponent = null;
	private GridLayout layout = null;
	
	public WhereComponentFigure(WhereClauseComponent myComponent) {
		super();
		this.myComponent = myComponent;
		
		layout = new GridLayout(1, false);
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		this.setLayoutManager(layout);
		this.setBorder(new LineBorder());
		drawContents();
	}
	
	private void drawContents() {
		if(myComponent instanceof InstanceComponent)drawInstanceContents((InstanceComponent)myComponent);
		else drawClassContents((ClassComponent)myComponent);
		
	}

	private void drawClassContents(ClassComponent theComponent) {
		uriComponent = new Label();
		uriComponent.setOpaque(true);
		uriComponent.setBackgroundColor(ColorConstants.darkGreen);
		uriComponent.setForegroundColor(ColorConstants.white);
		uriComponent.setText(theComponent.getDisplayURI());
		uriComponent.setToolTip(new Label(theComponent.getMyClass().getOWLClass().getIRI().toQuotedString()));
		uriComponent.setFont(Display.getCurrent().getSystemFont());
		// 	protected Dimension calculateLabelSize(Dimension txtSize)   public Dimension getMinimumSize(int w, int h)
		this.add(uriComponent);
		myName = new Label();
		myName.setLabelAlignment(PositionConstants.CENTER);
		myName.setBorder(new LineBorder(VarNameFigure.COLOUR_PURPLE, 2));
		// myName.setBorder(new LineBorder(1));
		myName.setText(theComponent.getName());
		myName.setFont(Display.getCurrent().getSystemFont());
		layout.setConstraint(uriComponent, new GridData(GridData.FILL_HORIZONTAL));
		layout.setConstraint(myName, new GridData(GridData.FILL_HORIZONTAL));
		this.add(myName);
	}

	private void drawInstanceContents(InstanceComponent theComponent) {
		uriComponent = new Label();
		uriComponent.setOpaque(true);
		uriComponent.setBackgroundColor(ColorConstants.yellow);
		uriComponent.setForegroundColor(ColorConstants.black);
		uriComponent.setText(theComponent.getMyClass().getDisplayURI()); // theComponent.getDisplayURI()); 
		uriComponent.setToolTip(new Label(theComponent.getMyClass().getOWLClass().getIRI().toQuotedString()));
		this.add(uriComponent);
		myName = new Label();
		myName.setLabelAlignment(PositionConstants.CENTER);
		// myName.setBorder(new LineBorder(1));
		myName.setText(theComponent.getLabel());
		myName.setToolTip(new Label("<"+theComponent.getMyInstance().toString()+">"));
		layout.setConstraint(uriComponent, new GridData(GridData.FILL_HORIZONTAL));
		layout.setConstraint(myName, new GridData(GridData.FILL_HORIZONTAL));
		this.add(myName);
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
	
	public void redraw() {
		removeAll();
		drawContents();
		repaint();
	}
	
	public void setBounds(Rectangle rect) {
		if(STATIC_HEIGHT == -1) {
			STATIC_HEIGHT = uriComponent.getMinimumSize().height + myName.getMinimumSize().height + 2;
		}
		rect.height = STATIC_HEIGHT;
		super.setBounds(rect);
	}

}
