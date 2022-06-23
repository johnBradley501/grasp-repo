package uk.ac.kcl.cch.jb.sparql.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.kcl.cch.jb.sparql.Activator;
import uk.ac.kcl.cch.jb.sparql.utils.QueryRunner;
import uk.ac.kcl.cch.jb.sparql.utils.VarNameManager;

public class SPARQLQuery extends PropertyChangeObject{
	
	public static final String FILE_EXTENSION = "sqf";
	//public static final String TITLE_CHANGED = "titleChanged";
	public static final String METADATA_CHANGED = "metadataChanged";

	public static SPARQLQuery load(InputStream contents) throws CoreException {
		InputStreamReader reader = new InputStreamReader(contents);
		SPARQLQuery newObject = null;
		try {
			JSONObject jsonData = new JSONObject(new JSONTokener(reader));
			newObject = new SPARQLQuery(jsonData);
			contents.close();
			
			JSONObject metadata = jsonData.getJSONObject("metadata");
			JSONObject queryData = jsonData.getJSONObject("query");
			newObject.myMetadata.title = metadata.getString("title");
			newObject.myMetadata.serverName = metadata.getString("serverName");
			newObject.myMetadata.endpoint = new URL(metadata.getString("endpoint"));
			//if(!metadata.has("browserEndpoint")) {
			//	if(newObject.myMetadata.serverName.toLowerCase().contains("poms"))metadata.put("browserEndpoint", "https://www.poms.ac.uk/rdf/repositories/poms/query?query=");
			//	else metadata.put("browserEndpoint", "https://romanrepublic.ac.uk/rdf/repositories/dprr/query?query=");
			//}
			newObject.myMetadata.browserEndpoint = new URL(metadata.getString("browserEndpoint"));
			newObject.myMetadata.ontologyURL = new URL(metadata.getString("ontology"));
			if(queryData.has("where")) {
				newObject.whereClause = new QueryWhereClause(newObject, queryData.getJSONObject("where"));
			}
			if(queryData.has("select")) {
				newObject.selectClause = new QuerySelectClause(newObject, queryData.getJSONObject("select"));
			}
		} catch (JSONException | IOException e) {
			throw new CoreException(new Status(Status.ERROR, Activator.PLUGIN_ID, "Failure reading file.", e));
		} catch (OWLOntologyCreationException e) {
			throw new CoreException(new Status(Status.ERROR, Activator.PLUGIN_ID, "Failure setting up ontology.", e));
		}
		return newObject;
	}
	
	public class QueryMetadata {
		public String title = null;
		public String serverName = null;
		public URL endpoint = null;
		public URL browserEndpoint = null;
		public URL ontologyURL = null;
		
		public QueryMetadata() {
			
		}
		
		public QueryMetadata(QueryMetadata model) {
			this.title = model.title;
			this.serverName = model.serverName;
			this.endpoint = model.endpoint;
			this.browserEndpoint = model.browserEndpoint;
			this.ontologyURL = model.ontologyURL;
		}
		
		public boolean sameAs(QueryMetadata model) {
			if(!this.title.equals(model.title))return false;
			if(!this.serverName.equals(model.serverName))return false;
			if(!this.endpoint.equals(model.endpoint))return false;
			if(!this.browserEndpoint.equals(model.browserEndpoint))return false;
			if(!this.ontologyURL.equals(model.ontologyURL))return false;
			return true;
		}
	}
	
	private JSONObject mycontents = null;
	private QueryMetadata myMetadata = new QueryMetadata();
	// private String title = null;
	// private String serverName = null;
	// private URL endpoint = null;
	// private URL browserEndpoint = null;
	// private URL ontologyURL = null;
	private OntologyData ontData = null;
	private int componentCounter = 0;
	private VarNameManager nameManager = new VarNameManager();
	
	private QuerySelectClause selectClause = null;
	private QueryWhereClause whereClause = null;
	
	private QueryRunner queryRunner = null;
	
	private SPARQLQuery(JSONObject obj) {
		mycontents = obj;
		selectClause = new QuerySelectClause(this);
		whereClause = new QueryWhereClause(this);
	}
	
	public void dispose() {
		if(queryRunner != null)queryRunner.dispose();
	}
	
	public QuerySelectClause getSelectClause() {return selectClause;}
	public QueryWhereClause getWhereClause() {return whereClause;}
	
	public JSONObject runQuery(String query) {
		if(queryRunner == null)queryRunner = new QueryRunner(this);
		return queryRunner.run(query);
	}

	public int getComponentCounter() {
		return componentCounter;
	}
	public void setComponentCounter(int numb) {
		componentCounter = numb;
	}
	
	public int incrementCounter() {
		componentCounter++;
		return componentCounter;
	}
	
	//public void setTitle(String title) {
	//	if(title.equals(myMetadata.title))return;
	//	String oldTitle = myMetadata.title;
	//	myMetadata.title = title;
	//	this.firePropertyChange(TITLE_CHANGED, oldTitle, myMetadata.title);
	//}
	
	public String getTitle() {return myMetadata.title; }
	
	public String getServerName() {return myMetadata.serverName;}
	
	public VarNameManager getVarNameManager() {return nameManager;}
	
	public URL getEndpoint() {return myMetadata.endpoint;}
	
	public URL getBrowserEndpoint() {return myMetadata.browserEndpoint;}
	
	public URL getOntologyURL() {return myMetadata.ontologyURL;}
	public OntologyData getOntologyData() {
		if(ontData != null)return ontData;
		ontData = OntologyData.getOntologyData(getOntologyURL());
		return ontData;
	}

	public InputStream buildContentStream() throws CoreException {
		JSONObject contents = new JSONObject();
		JSONObject metadata = new JSONObject();
		JSONObject query = new JSONObject();
		StringWriter w = new StringWriter();
		try {
			contents.put("metadata", metadata);
			contents.put("query", query); // deliberatively left empty
			
			metadata.put("title", myMetadata.title);
			metadata.put("serverName", myMetadata.serverName);
			metadata.put("endpoint", myMetadata.endpoint.toString());
			metadata.put("browserEndpoint", myMetadata.browserEndpoint.toString());
			metadata.put("ontology", myMetadata.ontologyURL.toString());
			
			JSONObject wdata = new JSONObject();
			getWhereClause().export(wdata);
			query.put("where", wdata);
			
			query.put("select", selectClause.export());
			contents.write(w);
		} catch (JSONException e) {
			throw new CoreException(new Status(Status.ERROR, Activator.PLUGIN_ID, "Failure saving data.", e));
		}
		return new ByteArrayInputStream(w.toString().getBytes());
	}
	
	public QueryMetadata getQueryMetadata() {return myMetadata;}
	
	public void setQueryMetadata(QueryMetadata model) {
		if(model.sameAs(myMetadata))return;
		QueryMetadata old = myMetadata;
		myMetadata = model;
		this.firePropertyChange(METADATA_CHANGED, old, myMetadata);
	}

}
