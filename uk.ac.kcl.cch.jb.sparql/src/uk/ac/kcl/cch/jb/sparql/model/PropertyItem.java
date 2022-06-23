package uk.ac.kcl.cch.jb.sparql.model;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class PropertyItem {
   private IRI myIRI;
   private String displayURI = null;
   private Set<ClassItem> domains = new HashSet<ClassItem>();
   private OntologyData ontData;
   private boolean annotation = false;

public PropertyItem(IRI myIRI, OntologyData ontData) {
	   this.myIRI = myIRI;
	   this.ontData = ontData;
   }

public IRI getMyIRI() {
	return myIRI;
}

public OntologyData getMyOntologyData() {return ontData;}

public void addDomain(ClassItem item) {
	domains.add(item);
}


public Set<ClassItem> getDomains(){
	return domains;
}

public String getDisplayURI() {
	if(displayURI == null) {
		// IRI iri = null;
		try {
			displayURI = ontData.getIRIDisplay(myIRI);
		} catch (OWLOntologyCreationException e) {
			displayURI = myIRI.toQuotedString();
		}
	}
	return displayURI;
}

public boolean isAnnotation() {return annotation;}
public void setAnnotation(boolean val) {annotation = val; }

public String toString() {
	return makeString();
}

protected String makeString() {
	if(myIRI != null) return myIRI.toString()+","+super.hashCode();
	return "null property,"+super.hashCode();

}

}
