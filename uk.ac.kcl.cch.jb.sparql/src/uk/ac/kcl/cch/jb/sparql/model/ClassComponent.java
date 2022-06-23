package uk.ac.kcl.cch.jb.sparql.model;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.draw2d.geometry.Rectangle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;

public class ClassComponent extends NamedComponent implements HasURI{

	public static final String NEW_CLASS = "NEW_CLASS";
	public static final String myType = "class";
	
	private ClassItem theClass;
	private String displayURI = null;
	// private String name = null;
	

	public static ClassComponent newClass(ClassItem theClass, QueryWhereClause clause) {
		return new ClassComponent(theClass, clause);
	}

	private ClassComponent(ClassItem theClass, QueryWhereClause clause) {
		super(clause);
		this.theClass = theClass;
		// name = createName();
		name = getMyQuery().getVarNameManager().proposeName(this);
	}
	
	//private String createName() {
	//	String dName = getDisplayURI();
	//	if(dName.matches("^[^:]*:[^:/]+$")) {
	//		String[]dNameParts = dName.split(":");
	//		return dNameParts[1].toLowerCase()+"_"+getID();
	//	}
	//	return "var_"+getID();
	//}

	public ClassComponent(JSONObject data, QueryWhereClause clause) throws JSONException, OWLOntologyCreationException {
		super(data, clause);
	    IRI theIRI = IRI.create(data.getString("uri"));
		OWLClass owlClass = new OWLClassImpl(theIRI);
		theClass = query.getOntologyData().find(owlClass);
		
		//if(data.has("name")) {
		//	name = data.getString("name");
		//	query.getVarNameManager().addName(this);
		//}
		//else name = query.getVarNameManager().proposeName(this);
		
		displayURI = getDisplayURI();
	}
	
	public JSONObject export() throws JSONException {
		JSONObject rslt = super.export();
		rslt.put("type", myType);
		rslt.put("uri", theClass.getOWLClass().getIRI().toString());
		// rslt.put("name", name);
		return rslt;
	}

	public ClassItem getMyClass() {return theClass;}
	public void setMyClass(ClassItem newClass) {
		ClassItem oldClass = theClass;
		theClass = newClass;
		displayURI = null;
		this.firePropertyChange(NEW_CLASS, oldClass, newClass);
	}
	/* public String getName() {return name;}
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		this.firePropertyChange(NEW_NAME, oldName, name);
	} */

	public String getDisplayURI() {
		if(displayURI == null) {
			IRI iri = theClass.getOWLClass().getIRI();
			OntologyData ont = OntologyData.getOntologyData(query.getOntologyURL());
			try {
				displayURI = ont.getIRIDisplay(iri);
			} catch (OWLOntologyCreationException e) {
				displayURI = iri.toQuotedString();
			}
		}
		return displayURI;
	}

	public String getFullURI() {
		return theClass.getOWLClass().getIRI().toString();
	}

	@Override
	public String getBasisForName() {
		return this.getDisplayURI();
	}

}
