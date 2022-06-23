package uk.ac.kcl.cch.jb.sparql.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.LayoutGraph;
import org.eclipse.zest.layouts.LayoutRelationship;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class QueryWhereClause extends PropertyChangeObject implements LayoutGraph {
	
	public static final String COMPONENTS_CHANGED = "COMPONENTS.CHANGED";
	public static final String PREDICATES_CHANGED = "PREDICATES.CHANGED";

	private SPARQLQuery myQuery;
	
	private List<WhereClauseComponent> components = null;
	private Map<Integer, WhereClauseComponent> lookupData = new HashMap<Integer, WhereClauseComponent>();

	public QueryWhereClause(SPARQLQuery query) {
		this.myQuery = query;
		components = new ArrayList<WhereClauseComponent>();
	}
	
	public QueryWhereClause(SPARQLQuery query, JSONObject source) throws JSONException, OWLOntologyCreationException {
		this(query);
		JSONArray jcs = source.getJSONArray("components");
		Iterator it = jcs.iterator();
		while(it.hasNext()) {
			JSONObject obj = (JSONObject)it.next();
			WhereClauseComponent component = WhereClauseComponent.loadComponent(obj,this);
			components.add(component);
			lookupData.put(component.getID(), component);
		}
		if(!source.has("objectProperties")) return;
		JSONArray jps = source.getJSONArray("objectProperties");
		it = jps.iterator();
		while(it.hasNext()) {
			JSONObject obj = (JSONObject)it.next();
			WhereClausePredicate pred = new WhereClausePredicate(obj, this);
		}
	}
	
	public SPARQLQuery getMyQuery() {return myQuery;}
	
	public OntologyData getOntologyData() {
		return myQuery.getOntologyData();
	}

	
	public void export(JSONObject holder) throws JSONException {
		JSONArray comps = new JSONArray();
		holder.put("components", comps);
		for (WhereClauseComponent component: components) {
			comps.put(component.export());
		}
		// Set<String> doneComps = new HashSet<String>();
		JSONArray oProps = new JSONArray();
		holder.put("objectProperties", oProps);
		for (WhereClauseComponent component: components) {
			for(WhereClausePredicate pred: component.getDomainPredicates()) {
				oProps.put(pred.export());
			}
		}
	}
	
	public void addComponent(WhereClauseComponent component) {
		components.add(component);
		lookupData.put(component.getID(), component);
		this.firePropertyChange(COMPONENTS_CHANGED, null, component);
	}
	
	//public void addClassComponent(ClassItem theClass) {
	//	ClassComponent newOne = ClassComponent.newClass(theClass, myQuery);
	//	addComponent(newOne);
	//}
	
	//public void addInstanceComponent(URI theInstance, ClassItem cls) {
	//	ClassComponent newOne = ClassComponent.newInstance(theInstance, cls, myQuery);
	//	addComponent(newOne);
	//}
	
	public void removeComponent(WhereClauseComponent comp) {
		if(!components.contains(comp))return;
		components.remove(comp);
		this.firePropertyChange(COMPONENTS_CHANGED, comp, null);
	}
	
	public List<WhereClauseComponent> getComponents() {
		return components;
	}
	
	public WhereClauseComponent lookupComponent(int id) {
		return lookupData.get(new Integer(id));
	}
	
	public List<WhereClausePredicate> getPredicates(){
		List<WhereClausePredicate> rslt = new ArrayList<WhereClausePredicate>();
		for (WhereClauseComponent component: components) {
			for(WhereClausePredicate pred: component.getDomainPredicates()) {
				rslt.add(pred);
			}
		}
		return rslt;
	}
	
	void SignalPredicateChange(WhereClausePredicate removed, WhereClausePredicate added) {
		this.firePropertyChange(PREDICATES_CHANGED, removed, added);
	}
	
	// for Zest layout support   JB

	@Override
	public void addEntity(LayoutEntity node) {
		// not needed here
		
	}

	@Override
	public void addRelationship(LayoutRelationship relationship) {
		// not needed here
		
	}

	@Override
	public List getEntities() {
		// TODO Auto-generated method stub
		return getComponents();
	}

	@Override
	public List getRelationships() {
		// TODO Auto-generated method stub
		return getPredicates();
	}

	@Override
	public boolean isBidirectional() {
		// SPARQL graphs are never bidirectional   JB
		return false;
	}

}
