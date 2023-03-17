package uk.ac.kcl.cch.jb.sparql.commands;

import org.eclipse.draw2d.geometry.Dimension;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.figures.VariableComponentFigure;
import uk.ac.kcl.cch.jb.sparql.model.ClassComponent;
import uk.ac.kcl.cch.jb.sparql.model.DataPropertyItem;
import uk.ac.kcl.cch.jb.sparql.model.PropertyItem;
import uk.ac.kcl.cch.jb.sparql.model.QuerySelectClause;
import uk.ac.kcl.cch.jb.sparql.model.QueryWhereClause;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery;
import uk.ac.kcl.cch.jb.sparql.model.SelectVarItem;
import uk.ac.kcl.cch.jb.sparql.model.VariableComponent;
import uk.ac.kcl.cch.jb.sparql.model.WhereClausePredicate;
import uk.ac.manchester.cs.owl.owlapi.OWLAnnotationPropertyImpl;

public class AddRdfsLabelReferenceCommand extends DirtyCommand {
	private static final int CONSTRAINT_X_OFFSET = 50;

	private static PropertyItem rdfsLabelProperty = null;
	private static IRI xsd_string_iri = IRI.create(DataPropertyItem.XSD_STRING);

	private QueryCreationEditor myEditor;
	private ClassComponent item;
	private QueryWhereClause myWhereClause;
	private VariableComponent component = null;
	private WhereClausePredicate pred;

	private QuerySelectClause selectClause;
	private SelectVarItem newItem;

	public AddRdfsLabelReferenceCommand(QueryCreationEditor myEditor, String myLabel, ClassComponent item) {
		super(myEditor);
		this.setLabel(myLabel);
		this.myEditor = myEditor;
		this.item = item;
		SPARQLQuery query = myEditor.getQuery();
		this.myWhereClause = query.getWhereClause();
		if(rdfsLabelProperty == null) {
			IRI iri = IRI.create("http://www.w3.org/2000/01/rdf-schema#label");
			OWLAnnotationProperty prop = new OWLAnnotationPropertyImpl(iri);
			rdfsLabelProperty = query.getOntologyData().find(prop);
		}
		pred = new WhereClausePredicate(rdfsLabelProperty, item, null);
		component = new VariableComponent(myWhereClause, xsd_string_iri, pred);
		pred.initializeRange(component);

		Dimension size = item.getMyBounds().getSize();
		size.height = VariableComponentFigure.DEFAULT_DIMENSION.height;
		component.setMyBounds(myEditor.findBestPosition(item.getMyBounds(), size, CONSTRAINT_X_OFFSET));
		
		component.setName(item.getName()+"_label");

		this.selectClause = myEditor.getQuery().getSelectClause();
		newItem = new SelectVarItem(query, component);
	}
	
	public void execute() {
		myWhereClause.addComponent(component);
		item.addDomainPredicate(pred);
		component.addRangePredicate(pred);
		selectClause.appendItem(newItem);
		super.execute();
	}
	
	public void undo() {
		selectClause.deleteItem(newItem);
		item.removeDomainPredicate(pred);
		component.removeRangePredicate(pred);
		myWhereClause.removeComponent(component);
		super.undo();
	}

}
