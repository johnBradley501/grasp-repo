package uk.ac.kcl.cch.jb.sparql.model;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;

public class DataPropertyItem extends PropertyItem {
	
	public static final String XSD_ANYURI = "http://www.w3.org/2001/XMLSchema#anyURI";
	public static final String UNDEFINED_TYPE = "http://sparql.jb.cch.kcl.ac.uk/undefinedType";

	private OWLDataProperty owlData;
	private Set<OWLDatatype> types = new HashSet<OWLDatatype>();

	public DataPropertyItem(OWLDataProperty owlData, OntologyData ontData) {
		//super(owlData.getIRI(), ontData);
		//this.owlData = owlData;
		this(owlData.getIRI(), ontData);
	}
	
	public DataPropertyItem(IRI myIRI, OntologyData ontData) {
		super(myIRI, ontData);
		this.owlData = owlData;
	}
	
	public void addDatatype(OWLDatatype type) {types.add(type);}
	
	public Set<OWLDatatype> getTypes(){return types;}
	
	
	public String toString() {
		return "DataProperty: "+super.toString();
	}

}
