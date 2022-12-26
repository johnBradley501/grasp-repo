package uk.ac.kcl.cch.jb.sparql.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.swt.SWT;

import uk.ac.kcl.cch.jb.sparql.model.WhereClausePredicate;

public class WhereClausePredicateFigure extends PolylineConnection {
	
	Label label = null;
	private WhereClausePredicate myPredicate;

	public WhereClausePredicateFigure(WhereClausePredicate myPredicate) {
		setTargetDecoration(new PolygonDecoration()); // arrow at target endpoint
		setForegroundColor(ColorConstants.black);
		
		label = new Label(myPredicate.getProperty().getDisplayURI());
		add(label, new ConnectionLocator(this, ConnectionLocator.MIDDLE));
		this.myPredicate = myPredicate;
		setMyLineType();

	}
	
	public void setMyLineType() {
		if(myPredicate.isOptional()) {
			setLineStyle(SWT.LINE_DASH);
			setLineWidth(2);
			setLineDash(new float[] {10.0f});
		} else {
			setLineStyle(SWT.LINE_SOLID);
			setLineWidth(1);
			setLineDash(null);
		}
		
	}
}
