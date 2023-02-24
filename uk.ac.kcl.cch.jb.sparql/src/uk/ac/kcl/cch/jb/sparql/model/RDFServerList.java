package uk.ac.kcl.cch.jb.sparql.model;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import uk.ac.kcl.cch.jb.sparql.Activator;

public class RDFServerList extends PropertyChangeObject {
	public static final String LIST_CHANGED = "RDFServerList.ListChanged";
	public static final String PREFERRED_CHANGED = "RDFServerList.PreferredChanged";
	private static RDFServerList theList = null;
	
	class SortByName implements Comparator<RDFServer> {
		public int compare(RDFServer a, RDFServer b) {
			return a.getName().compareTo(b.getName());		}
	}

	private SortByName sortByName = new SortByName();
	
	public static RDFServerList getList() {
		if(theList == null)theList = new RDFServerList();
		return theList;
	}

	private LinkedList<RDFServer> servers = null;
	
	private RDFServerList() {
		servers = new LinkedList<RDFServer>();
		load();
	}

	public void add(RDFServer theServer) {
		if(servers.contains(theServer))return;
		servers.add(theServer);
		Collections.sort(servers, sortByName);
		this.firePropertyChange(LIST_CHANGED, null, theServer);
	}
	
	public void delete(RDFServer theServer) {
		if(!servers.contains(theServer))return;
		servers.remove(theServer);
		this.firePropertyChange(LIST_CHANGED, theServer, null);
	}
	
	public LinkedList<RDFServer> getServers(){
		return servers;
	}

	public void sort() {
		Collections.sort(servers, sortByName);
		this.firePropertyChange(LIST_CHANGED, null, null);
	}
	
	public void setPreferred(RDFServer theOne) {
		if(theOne.isPreferred())return;
		RDFServer oldPreferred = getPreferred();
		for(RDFServer serv:servers)serv.setPreferred(false);
		theOne.setPreferred(true);
		this.firePropertyChange(PREFERRED_CHANGED, oldPreferred, theOne);
	}
	
	public RDFServer getPreferred() {
		if(servers == null || servers.size() == 0)return null;
		for(RDFServer serv:servers) {
			if(serv.isPreferred())return serv;
		}
		return servers.getFirst();
	}

	public void save() {
		File cachedata = new File(Activator.getDefault().getStateLocation().toString(), "servers.json");
		JSONArray jary = new JSONArray();
		Iterator<RDFServer> it = servers.iterator();
		while(it.hasNext()) {
			RDFServer server = it.next();
			try {
				jary.put(server.export());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			FileWriter theFile = new FileWriter(cachedata);
			jary.write(theFile);
			theFile.close();
		} catch (JSONException | IOException e) {
			   // TODO Auto-generated catch block
			   e.printStackTrace();
		}
	}
	
	private void load() {
		File cachedata = new File(Activator.getDefault().getStateLocation().toString(), "servers.json");
		Reader theFile = null;
		JSONArray jsonData = null;
		try {
			if(cachedata.exists()) {
				theFile = new FileReader(cachedata);
			} else {
				InputStream inputStream = getClass().getResourceAsStream("defaultServers.json");
				theFile = new InputStreamReader(inputStream);
			}
			// FileReader theFile = new FileReader(cachedata);
			jsonData = new JSONArray(new JSONTokener(theFile));
			theFile.close();
			Iterator it = jsonData.iterator();
			while(it.hasNext()) {
				Object obj = it.next();
				if(obj instanceof JSONObject) {
					RDFServer server = RDFServer.makeServer((JSONObject)obj);
					if(server != null)
						servers.add(server);
				}
			}
		} catch(JSONException | IOException ex) {
			   return;
		}
		if(!cachedata.exists())save();

	}
}