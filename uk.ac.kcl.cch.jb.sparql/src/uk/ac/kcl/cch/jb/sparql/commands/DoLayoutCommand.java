package uk.ac.kcl.cch.jb.sparql.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.zest.layouts.InvalidLayoutConfiguration;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.LayoutRelationship;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.CompositeLayoutAlgorithm;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.QueryWhereClause;
import uk.ac.kcl.cch.jb.sparql.model.WhereClauseComponent;
import uk.ac.kcl.cch.jb.sparql.model.WhereClausePredicate;
import uk.ac.kcl.cch.jb.sparql.parts.QueryWhereClausePart;
import uk.ac.kcl.cch.jb.sparql.utils.BoundsHolder;
import uk.ac.kcl.cch.jb.sparql.utils.HorizontalSqueezeLayout;

public class DoLayoutCommand extends DirtyCommand {
	
private QueryCreationEditor myEditor;
private BoundsHolder oldBoundsSet;
private BoundsHolder newBoundsSet = null;
private QueryWhereClause whereClause;
private LayoutAlgorithm theLayout;

public DoLayoutCommand(QueryCreationEditor myEditor, LayoutAlgorithm theLayout) {
	super(myEditor);
	this.setLabel("Do Layout");
	this.myEditor = myEditor;
	this.whereClause = myEditor.getQuery().getWhereClause();
	oldBoundsSet = new BoundsHolder(whereClause);
	this.theLayout = theLayout;
}

public void execute(){
	super.execute();
	if(newBoundsSet != null) newBoundsSet.setBounds();
	else {
		QueryWhereClause whereClause = myEditor.getQuery().getWhereClause();
		LayoutEntity[] entitiesToLayout = whereClause.getComponents().toArray(new WhereClauseComponent[0]);
		LayoutRelationship[] relationshipsToConsider = whereClause.getPredicates().toArray(new WhereClausePredicate[0]);
		QueryWhereClausePart qwc = myEditor.getQueryWhereClausePart();
		Rectangle r = qwc.getFigure().getBounds();
		int style = LayoutStyles.NO_LAYOUT_NODE_RESIZING;
		CompositeLayoutAlgorithm composite = new CompositeLayoutAlgorithm(style,
				new LayoutAlgorithm[] {
						theLayout, new HorizontalSqueezeLayout(style)
				});
		try {
			composite.applyLayout(entitiesToLayout, relationshipsToConsider, 0.0, 0.0, r.preciseWidth(), r.preciseHeight(), false, false);
		} catch (InvalidLayoutConfiguration e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		newBoundsSet = new BoundsHolder(whereClause);
	}
}

public void undo() {
	super.undo();
	oldBoundsSet.setBounds();
}

}
