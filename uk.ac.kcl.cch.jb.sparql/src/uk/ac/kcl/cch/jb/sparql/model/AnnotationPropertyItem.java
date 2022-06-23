package uk.ac.kcl.cch.jb.sparql.model;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;

public class AnnotationPropertyItem extends DataPropertyItem {
	
	private OWLAnnotationProperty owlData;

	public AnnotationPropertyItem(OWLAnnotationProperty owlData, OntologyData ontData) {
		super(owlData.getIRI(), ontData);
		this.owlData = owlData;
		setAnnotation(true);
	}
	
	public OWLAnnotationProperty getProperty() {return owlData;}
	
	public String toString() {
		return "AnnotationProperty: "+super.makeString();
	}

}
