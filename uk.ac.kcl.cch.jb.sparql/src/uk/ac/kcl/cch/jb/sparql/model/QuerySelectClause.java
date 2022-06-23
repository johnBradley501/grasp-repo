package uk.ac.kcl.cch.jb.sparql.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class QuerySelectClause extends PropertyChangeObject {
	
	public static final String CHANGE_ITEMS="Change_Items";

	private SPARQLQuery myQuery;
	private SelectClauseModifier myModifier = null;
	
	private LinkedList<SelectVarItem> items = null;

	public QuerySelectClause(SPARQLQuery myQuery) {
		this.myQuery = myQuery;
		items = new LinkedList<SelectVarItem>();
		myModifier = new SelectClauseModifier(myQuery);
	}
	
	public QuerySelectClause(SPARQLQuery query, JSONObject source) throws JSONException {
		this(query);
		if(!source.has("modifier")) return;
		JSONObject jmod = source.getJSONObject("modifier");
		myModifier = new SelectClauseModifier(query, jmod);
		
		if(!source.has("vars")) return;
		JSONArray vars = source.getJSONArray("vars");
		for(Object item: vars) {
			JSONObject jItem = (JSONObject)item;
			SelectVarItem varItem = new SelectVarItem(query, jItem);
			items.add(varItem);
		}

	}
	
	public JSONObject export() throws JSONException {
		JSONObject rslt = new JSONObject();
		rslt.put("modifier", myModifier.export());
		JSONArray vars = new JSONArray();
		rslt.put("vars", vars);
		for(SelectVarItem item: items) {
			vars.put(item.export());
		}
		return rslt;
	}
	
	public SelectClauseModifier getModifier() {return myModifier;}
	public List<SelectVarItem> getVars(){return items;}
	
	public void deleteItem(SelectVarItem item) {
		if(!items.contains(item))return;
		items.remove(item);
		this.firePropertyChange(CHANGE_ITEMS, item, null);
	}
	
	public SelectVarItem getItemBefore(SelectVarItem item) {
		if(!items.contains(item))return null;
		int idx = items.indexOf(item)-1;
		if(idx == -1)return null;
		return items.get(idx);
	}
	
	public SelectVarItem getItemAfter(SelectVarItem item) {
		if(!items.contains(item))return null;
		if(item == items.getLast())return null;
		int idx = items.indexOf(item)+1;
		return items.get(idx);
	}
	
	public int placeItem(SelectVarItem item, SelectVarItem after) {
		int afterPos = after == null? 0: items.indexOf(after)+1;
		if(items.contains(item)) {
			if(afterPos == items.indexOf(item))return items.indexOf(item);
			items.remove(item);
		}
		items.add(afterPos, item);
		int idx = items.indexOf(item);
		this.firePropertyChange(CHANGE_ITEMS, after, item);
		return idx;
	}
	
	public int appendItem(SelectVarItem item) {
		if(items.size() == 0)return placeItem(item, null);
		SelectVarItem lastItem = items.getLast();
		return placeItem(item, lastItem);
	}

}
