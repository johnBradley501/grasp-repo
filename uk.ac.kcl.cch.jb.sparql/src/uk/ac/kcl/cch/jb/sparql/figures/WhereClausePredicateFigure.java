package uk.ac.kcl.cch.jb.sparql.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;

import uk.ac.kcl.cch.jb.sparql.model.WhereClausePredicate;

public class WhereClausePredicateFigure extends PolylineConnection {
	
	Label label = null;

	public WhereClausePredicateFigure(WhereClausePredicate myPredicate) {
		setTargetDecoration(new PolygonDecoration()); // arrow at target endpoint
		setForegroundColor(ColorConstants.black);
		setLineWidth(1);
		
		label = new Label(myPredicate.getProperty().getDisplayURI());
		add(label, new ConnectionLocator(this, ConnectionLocator.MIDDLE));

	}
}
