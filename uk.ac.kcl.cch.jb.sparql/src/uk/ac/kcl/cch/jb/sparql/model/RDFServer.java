package uk.ac.kcl.cch.jb.sparql.model;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

public class RDFServer extends PropertyChangeObject {
	
	public static String NAME_CHANGED = "RDFServer.NameChanged";
	
	private String name = null;
	private URL sparqlEngine = null;
	private URL sparqlBrowser = null;
	private URL ontologyURL = null;
	private boolean preferred = false;
	
	public RDFServer() {
		name = "";
	}
	
	public RDFServer(String nme) {
		name = nme;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String nme) {
		if(name.equals(nme))return;
		String oldName = name;
		this.name = nme;
		firePropertyChange(RDFServer.NAME_CHANGED, oldName, this);
	}
	
	public String toString() {
		return name;
	}
	
	public void setSparqlEngine(URL engine) {
		sparqlEngine = engine;
	}
	
	public URL getSparqlEngine() {
		return sparqlEngine;
	}
	
	public void setSparqlBrowser(URL engine) {
		sparqlBrowser = engine;
	}
	
	public URL getSparqlBrowser() {
		return sparqlBrowser;
	}
	
	public void setOntologyURL(URL ontology) {
		ontologyURL = ontology;
	}

	public URL getOntologyURL() {
		return ontologyURL;
	}
	
	public boolean isPreferred() {return preferred;}
	public void setPreferred(boolean val) {preferred = val;}
	
	public JSONObject export() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("name", name);
		obj.put("endpoint", sparqlEngine.toString());
		obj.put("browser", sparqlBrowser.toString());
		obj.put("ontology",  ontologyURL.toString());
		obj.put("preferred", preferred);
		return obj;
	}

	public static RDFServer makeServer(JSONObject obj) {
		RDFServer newServer = null;
		try {
			String name = obj.getString("name");
			newServer = new RDFServer(name);
			URL endpoint = new URL(obj.getString("endpoint"));
			newServer.setSparqlEngine(endpoint);
			//if(!obj.has("browser")) {
			//	if(name.toLowerCase().contains("poms")) obj.put("browser", "https://www.poms.ac.uk/rdf/repositories/poms/query?query=");
			//	else obj.put("browser", "https://romanrepublic.ac.uk/rdf/repositories/dprr/query?query=");
			//}
			URL browser = new URL(obj.getString("browser"));
			newServer.setSparqlBrowser(browser);
			URL ontology = new URL(obj.getString("ontology"));
			newServer.setOntologyURL(ontology);
			if(obj.has("preferred"))newServer.setPreferred(obj.getBoolean("preferred"));
		} catch (JSONException e) {
			return null;
		} catch (MalformedURLException e) {
			return null;
		}
		
		return newServer;
	}


}
