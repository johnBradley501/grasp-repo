package uk.ac.kcl.cch.jb.sparql.commands;

import java.net.URI;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDatatype;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.figures.VariableComponentFigure;
import uk.ac.kcl.cch.jb.sparql.model.ClassComponent;
import uk.ac.kcl.cch.jb.sparql.model.DataPropertyItem;
import uk.ac.kcl.cch.jb.sparql.model.PropertyItem;
import uk.ac.kcl.cch.jb.sparql.model.QueryWhereClause;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery;
import uk.ac.kcl.cch.jb.sparql.model.VariableComponent;
import uk.ac.kcl.cch.jb.sparql.model.WhereClausePredicate;

public class AddNewVariableComponentCommand extends DirtyCommand {
	
	private static final int CONSTRAINT_X_OFFSET = 50;
	private static final int CONSTRAINT_Y_OFFSET = 20;
	private static IRI XsdAnyURI = null;

	private ClassComponent item;
	private PropertyItem theProperty;
	private QueryWhereClause myWhereClause;
	private VariableComponent myVarComponent;
	private WhereClausePredicate pred;

	public AddNewVariableComponentCommand(QueryCreationEditor myEditor, String myLabel, ClassComponent item, PropertyItem theProperty) {
		super(myEditor);
		this.setLabel(myLabel);
		this.item = item;
		this.theProperty = theProperty;
		
		SPARQLQuery query = myEditor.getQuery();
		this.myWhereClause = query.getWhereClause();
		IRI myType = null;
		if(theProperty instanceof DataPropertyItem) {
			DataPropertyItem dataProperty = (DataPropertyItem)theProperty;
			Set<OWLDatatype> types = dataProperty.getTypes();
			if(types.size() != 1)myType = IRI.create(DataPropertyItem.UNDEFINED_TYPE);
			else {
				Iterator<OWLDatatype> it = types.iterator();
				OWLDatatype odt = it.next();
				myType = odt.getIRI();
			}
		} else if(XsdAnyURI == null) {
			XsdAnyURI = IRI.create(DataPropertyItem.XSD_ANYURI);
			myType = XsdAnyURI;
		} else myType = XsdAnyURI;
		
		// this is ugly: a perhaps better fix would be to change VariableComponent constructor to take theProperty
		// and then reverse the two constructors. initializeRange would then no longer be needed.      jb
		this.pred = new WhereClausePredicate(theProperty, item, null);
		this.myVarComponent = new VariableComponent(myWhereClause, myType, this.pred);
		this.pred.initializeRange(myVarComponent);
		
		// Rectangle newBounds = new Rectangle(item.getMyBounds());
		// newBounds.height = VariableComponentFigure.DEFAULT_DIMENSION.height;
		// newBounds.y += CONSTRAINT_Y_OFFSET;
		// newBounds.x = newBounds.x+newBounds.width+CONSTRAINT_X_OFFSET;
		// myVarComponent.setMyBounds(newBounds);
		Dimension size = item.getMyBounds().getSize();
		size.height = VariableComponentFigure.DEFAULT_DIMENSION.height;
		myVarComponent.setMyBounds(myEditor.findBestPosition(item.getMyBounds(), size, CONSTRAINT_X_OFFSET));

	}
	
	public void execute() {
		myWhereClause.addComponent(myVarComponent);
		item.addDomainPredicate(pred);
		myVarComponent.addRangePredicate(pred);
		super.execute();
	}

	public void undo() {
		item.removeDomainPredicate(pred);
		myVarComponent.removeRangePredicate(pred);
		myWhereClause.removeComponent(myVarComponent);
		super.undo();
	}
}
