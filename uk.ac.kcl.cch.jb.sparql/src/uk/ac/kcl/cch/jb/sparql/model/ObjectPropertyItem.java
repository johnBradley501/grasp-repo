package uk.ac.kcl.cch.jb.sparql.model;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLObjectProperty;

public class ObjectPropertyItem extends PropertyItem {
	
	private OWLObjectProperty owlData;
    private Set<ClassItem> ranges = new HashSet<ClassItem>();

	public ObjectPropertyItem(OWLObjectProperty owlData, OntologyData ontData) {
		super(owlData.getIRI(), ontData);
		this.owlData = owlData;
	}
	
	public void addRange(ClassItem item) {
		ranges.add(item);
	}
	
	public Set<ClassItem> getRanges(){
		return ranges;
	}
	
	public String toString() {
		return "ObjectProperty: "+super.toString();
	}

}
