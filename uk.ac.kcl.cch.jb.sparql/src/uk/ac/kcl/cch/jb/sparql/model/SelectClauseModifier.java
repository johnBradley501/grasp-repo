package uk.ac.kcl.cch.jb.sparql.model;

import org.json.JSONException;
import org.json.JSONObject;

public class SelectClauseModifier extends PropertyChangeObject {
	
	public static final String DISTINCT_SET = "DISTINCT_SET";
	public static final String LIMIT_SET = "LIMIT_SET";
	public static final String OFFSET_SET = "OFFSET_SET";
	
	private SPARQLQuery query;
	private boolean isDistinct = false;
	private int limit = 0;
	private int offset = 0;

	public SelectClauseModifier(SPARQLQuery query) {
		this.query = query;
	}
	
	public SelectClauseModifier(SPARQLQuery query, JSONObject source) throws JSONException {
		this(query);
		if(source.has("distinct")) {
			isDistinct = source.getBoolean("distinct");
		}
		if(source.has("limit")) limit = source.getInt("limit");
		if(source.has("offset")) offset = source.getInt("offset");
	}
	
	public JSONObject export() throws JSONException {
		JSONObject rslt = new JSONObject();
		rslt.put("distinct", isDistinct);
		rslt.put("limit", limit);
		rslt.put("offset", offset);
		return rslt;
	}
	
	public boolean isDistinct() {return isDistinct;}
	public void setDistinct(boolean val) {
		if(val == isDistinct)return;
		boolean oldval = isDistinct;
		isDistinct = val;
		this.firePropertyChange(DISTINCT_SET, oldval, isDistinct);
	}
	
	public int getLimit() {return limit;}
	public void setLimit(int val) {
		if(val == limit)return;
		int oldLimit = limit;
		limit = val;
		this.firePropertyChange(LIMIT_SET, oldLimit, limit);
	}
	
	public int getOffset() {return offset;}
	public void setOffset(int val) {
		if(val == offset)return;
		int oldOffset = offset;
		offset = val;
		this.firePropertyChange(OFFSET_SET, oldOffset, offset);
	}

}
