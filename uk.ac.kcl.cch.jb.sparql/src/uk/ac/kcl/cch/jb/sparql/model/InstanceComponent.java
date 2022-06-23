package uk.ac.kcl.cch.jb.sparql.model;

import java.net.URI;

import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;

public class InstanceComponent extends WhereClauseComponent implements HasURI {

	public static final String INSTANCE_CHANGE = "INSTANCE_CHANGE"; // signalled when status between instance/class changes, or when instance itself changes   JB
	public static final String myType = "instance";
	
	private URI instanceURI;
	private ClassItem theClass;
	private String label;
	private String displayURI = null;

	public InstanceComponent (URI instanceURI, ClassItem theClass, QueryWhereClause clause) {
		super(clause);
		this.instanceURI = instanceURI;
		this.theClass = theClass;
		label = "---";
	}
	
	public InstanceComponent(JSONObject data, QueryWhereClause clause) throws JSONException, OWLOntologyCreationException {
		super(data, clause);
		instanceURI = URI.create(data.getString("instance"));
		
	    IRI theIRI = IRI.create(data.getString("class"));
		OWLClass owlClass = new OWLClassImpl(theIRI);
		theClass = query.getOntologyData().find(owlClass);
		
		label = data.getString("label");
	}
	
	public JSONObject export() throws JSONException {
		JSONObject rslt = super.export();
		rslt.put("type", myType);
		
		rslt.put("instance",instanceURI.toString());
		rslt.put("class", theClass.getOWLClass().getIRI().toString());
		rslt.put("label", label);
		return rslt;
		
	}
	public URI getMyInstance() {return instanceURI;}
	public ClassItem getMyClass() { return theClass;}
	public String getLabel() {return label;}
	public String getName() {return label;} // might be needed for Zest use
	
	public void updateInstance(InstanceElement instance) {
		InstanceElement oldElement = new InstanceElement(label, instanceURI.toString());
		instanceURI = URI.create(instance.uri);
		label = instance.name;
		displayURI = null;
		this.firePropertyChange(INSTANCE_CHANGE, oldElement, this);
	}
	
	public String getDisplayURI() {
		if(displayURI != null)return displayURI;
		IRI iri = IRI.create(instanceURI.toString());
		OntologyData ont = OntologyData.getOntologyData(query.getOntologyURL());
		try {
			displayURI = ont.getIRIDisplay(iri);
		} catch (OWLOntologyCreationException e) {
			displayURI = iri.toQuotedString();
		}
		return displayURI;
	}
	
	public String getFullURI() {
		return instanceURI.toString();
	}
}
