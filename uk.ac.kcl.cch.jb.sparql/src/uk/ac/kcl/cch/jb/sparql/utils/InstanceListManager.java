package uk.ac.kcl.cch.jb.sparql.utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.kcl.cch.jb.sparql.model.ClassItem;
import uk.ac.kcl.cch.jb.sparql.model.InstanceElement;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery;

public class InstanceListManager {
	
	private static Map<String, InstanceListManager> managers = new HashMap<String, InstanceListManager>();
	
	public static InstanceListManager find(SPARQLQuery query, ClassItem classItem) {
		String key = query.getEndpoint().toString()+"\t"+classItem.getOWLClass().getIRI().toString();
		if(managers.containsKey(key))return managers.get(key);
		InstanceListManager newOne = new InstanceListManager(query, classItem);
		managers.put(key, newOne);
		return newOne;
	}
	
	public static List<String> getNames(List<InstanceElement> lst){
		List<String> rslt = new ArrayList<String>();
		for(InstanceElement item: lst)rslt.add(item.name);
		return rslt;
	}
	
	public static List<InstanceElement> filterListElements(List<InstanceElement> list, String query){
		List<InstanceElement> rslt = new ArrayList<InstanceElement>();
		String pat = "\\b"+query;
		Pattern pattern = Pattern.compile(pat, Pattern.CASE_INSENSITIVE);
		for(InstanceElement item:list) {
			if(pattern.matcher(item.name).find())rslt.add(item);
		}
		return rslt;
	}
	
	private String countQuery = "select (count(?itm) as ?numb)\n" + 
			"where {?itm a $$$}";
	
	private String instancesQuery = "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
			"select ?name ?uri\n" + 
			"where {\n" + 
			"   ?uri a $$$.\r\n" + 
			"   OPTIONAL {?uri rdfs:label ?name}\n" + 
			"$FILTER" + 
			"} order by ?name limit 200";
	
	private String filterOption = "FILTER regex(?name, \"\\\\b$$$\", \"i\")\n";

	private int numbItems = 0;
	private SPARQLQuery query;
	private ClassItem cls;
	private Map<String, List<InstanceElement>> q2l = new HashMap<String, List<InstanceElement>>();
	//private String pattrn;

	private InstanceListManager(SPARQLQuery query, ClassItem cls) {
		this.query = query;
		this.cls = cls;
		numbItems = getCount();
	}

	private int getCount() {
		JSONObject rslt = query.runQuery(insertClass(countQuery));
		try {
		if(!rslt.has("results")) handleError(rslt, "count");
		JSONArray bindings = rslt.getJSONObject("results").getJSONArray("bindings");
		return bindings.getJSONObject(0).getJSONObject("numb").getInt("value");
		} catch (JSONException e){
			throw new RuntimeException("JSON failure in SPARQL Query (count)", e);
		}
	}
	
	private void handleError(JSONObject rslt, String type) throws JSONException{
		int status = rslt.getInt("failed");
		String reason = rslt.getString("reason");
		throw new RuntimeException("SPARQL Query ("+type+") failed, status:"+status+", reason:"+reason);
	}
	
	private String insertClass(String queryTemplate) {
		return queryTemplate.replaceAll("\\$\\$\\$", cls.getOWLClass().getIRI().toQuotedString());
	}
	
	public List<InstanceElement> getInstances(String pattern){
		pattern = pattern.toLowerCase();
		if(q2l.containsKey(pattern))return q2l.get(pattern);
		List<InstanceElement> candidateBase = locateCandidateList(pattern);
		List<InstanceElement> rslt = null;
		if(candidateBase != null && candidateBase.size() < 200)rslt = filterListElements(candidateBase, pattern);
		else rslt = fetchInstances(pattern);
		q2l.put(pattern, rslt);
		return rslt;
	}
	
	private List<InstanceElement> locateCandidateList(String pattern){
		if(pattern.length() == 0)return null;
		String p2 = pattern.substring(0, pattern.length()-1);
		if(q2l.containsKey(p2))return q2l.get(p2);
		return locateCandidateList(p2);
	}
	
	private List<InstanceElement> fetchInstances(String pattern){
		String classedTemplate = insertClass(instancesQuery);
		if(pattern.equals(""))classedTemplate = classedTemplate.replaceAll("\\$FILTER", "");
		else {
			// String filterOpt = filterOption.replaceAll("\\$\\$\\$", pattern);
			String filterOpt = "FILTER regex(?name, \"\\\\\\\\b"+pattern+"\", \"i\")\n";
			classedTemplate = classedTemplate.replaceAll("\\$FILTER",filterOpt);
		}
		JSONObject rslt = query.runQuery(classedTemplate);
		try {
			if(!rslt.has("results"))handleError(rslt, "instances");
			return buildInstances(rslt);
		} catch (JSONException e){
			throw new RuntimeException("JSON failure in SPARQL Query (instances)", e);
		}
	}

	private List<InstanceElement> buildInstances(JSONObject data) throws JSONException {
		List<InstanceElement> rslt = new ArrayList<InstanceElement>();
		JSONArray bindings = data.getJSONObject("results").getJSONArray("bindings");
		for(Object itm: bindings) {
			JSONObject item = (JSONObject)itm;
			String uri = item.getJSONObject("uri").getString("value");
			String name = uri;
			if(item.has("name"))name = item.getJSONObject("name").getString("value");
			rslt.add(new InstanceElement(name, uri));
		}
		return rslt;
	}
}
