package uk.ac.kcl.cch.jb.sparql.actions;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.action.Action;
import org.eclipse.zest.layouts.InvalidLayoutConfiguration;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.LayoutRelationship;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.CompositeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalShift;

import uk.ac.kcl.cch.jb.sparql.commands.DoLayoutCommand;
import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.QueryWhereClause;
import uk.ac.kcl.cch.jb.sparql.model.WhereClauseComponent;
import uk.ac.kcl.cch.jb.sparql.model.WhereClausePredicate;
import uk.ac.kcl.cch.jb.sparql.parts.QueryWhereClausePart;

public class DoLayoutAction extends Action {

	private QueryCreationEditor myEditor;
	private LayoutAlgorithm theLayout;

	public DoLayoutAction(QueryCreationEditor myEditor, LayoutAlgorithm theLayout) {
		super();
		setText("Do Layout");
		this.myEditor = myEditor;
		this.theLayout = theLayout;
	}
	
	public void run() {
		myEditor.getCommandStack().execute(new DoLayoutCommand(myEditor, theLayout));
		// int style = LayoutStyles.NO_LAYOUT_NODE_RESIZING;
		//CompositeLayoutAlgorithm composite = new CompositeLayoutAlgorithm(style,
		//		new LayoutAlgorithm[] {
		//				theLayout, new HorizontalShift(style)
		//		});
		/*
		QueryWhereClause whereClause = myEditor.getQuery().getWhereClause();
		LayoutEntity[] entitiesToLayout = whereClause.getComponents().toArray(new WhereClauseComponent[0]);
		LayoutRelationship[] relationshipsToConsider = whereClause.getPredicates().toArray(new WhereClausePredicate[0]);
		QueryWhereClausePart qwc = myEditor.getQueryWhereClausePart();
		Rectangle r = qwc.getFigure().getBounds();
		try {
			theLayout.applyLayout(entitiesToLayout, relationshipsToConsider, 0.0, 0.0, r.preciseWidth(), r.preciseHeight(), false, false);
		} catch (InvalidLayoutConfiguration e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */
	}

}
