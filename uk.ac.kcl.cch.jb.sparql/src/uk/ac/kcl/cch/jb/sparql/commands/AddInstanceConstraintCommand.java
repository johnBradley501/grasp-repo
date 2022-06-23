package uk.ac.kcl.cch.jb.sparql.commands;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.draw2d.geometry.Rectangle;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.ClassItem;
import uk.ac.kcl.cch.jb.sparql.model.InstanceComponent;
import uk.ac.kcl.cch.jb.sparql.model.ObjectPropertyItem;
import uk.ac.kcl.cch.jb.sparql.model.OntologyData;
import uk.ac.kcl.cch.jb.sparql.model.QueryWhereClause;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery;
import uk.ac.kcl.cch.jb.sparql.model.ClassComponent;
import uk.ac.kcl.cch.jb.sparql.model.WhereClausePredicate;

public class AddInstanceConstraintCommand extends DirtyCommand {
	
	private static final int CONSTRAINT_X_OFFSET = 50;
	private static final int CONSTRAINT_Y_OFFSET = 20;

	private ClassComponent domain;
	private ObjectPropertyItem connectingProperty;
	private ClassItem rangeClass;
	private InstanceComponent myConstraint;
	private WhereClausePredicate pred;
	private QueryWhereClause myWhereClause;

	public AddInstanceConstraintCommand(QueryCreationEditor myEditor, String fullText, ClassComponent domain,
			ObjectPropertyItem connectingProperty, ClassItem rangeClass) {
		super(myEditor);
		this.setLabel("constraint "+fullText);
		this.domain = domain;
		this.connectingProperty = connectingProperty;
		this.rangeClass = rangeClass;
		SPARQLQuery query = myEditor.getQuery();
		this.myWhereClause = query.getWhereClause();
		try {
			this.myConstraint  = new InstanceComponent(new URI(OntologyData.OWLNothingClass.toString()), rangeClass, myWhereClause);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Rectangle newBounds = new Rectangle(domain.getMyBounds());
		newBounds.y += CONSTRAINT_Y_OFFSET;
		newBounds.x = newBounds.x+newBounds.width+CONSTRAINT_X_OFFSET;
		myConstraint.setMyBounds(newBounds);
		pred = new WhereClausePredicate(connectingProperty, domain, myConstraint);
	}
	
	public void execute() {
		myWhereClause.addComponent(myConstraint);
		domain.addDomainPredicate(pred);
		myConstraint.addRangePredicate(pred);
		super.execute();
	}
	
	public void undo() {
		super.undo();
		domain.removeDomainPredicate(pred);
		myConstraint.removeRangePredicate(pred);
		myWhereClause.removeComponent(myConstraint);
	}

}
