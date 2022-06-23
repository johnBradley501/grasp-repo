package uk.ac.kcl.cch.jb.sparql.model;

import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.kcl.cch.jb.sparql.builder.ExpressionMaker;

public class SelectVarItem extends PropertyChangeObject {
	
	public static final String CHANGED_ORDER = "Changed_Order";
	public static final String CHANGED_AGGTYPE = "Changed_AggType";
	public static final String CHANGED_AGGVARNAME = "Changed_AggName";

	public static final int VAR_COMPONENT = 0;
	public static final int ORDER_COMPONENT = 1;
	public static final int AGGREGATE_COMPONENT = 2;
	public static final int AGG_NAME_COMPONENT = 3;
	
	public static final String[] orderings = {"", "Ascending", "Descending"};
	public static final int UNORDERED = 0;
	public static final int ASCENDING = 1;
	public static final int DESCENDING = 2;
	
	public static final int GROUPBY = -2;
	public static final int NOAGG = -1;
	
	private SPARQLQuery query;
	private INamedComponent namedComponent = null;
	private int sortOrder = 0;
	private int aggType = -1;
	private String aggVarName = null;

	public SelectVarItem(SPARQLQuery query, INamedComponent namedComponent) {
		this.query = query;
		this.namedComponent = namedComponent;
	}
	
	public SelectVarItem(SPARQLQuery query, JSONObject source) throws JSONException {
		this.query = query;
		if(!source.has("component")) return;
		namedComponent = (INamedComponent)query.getWhereClause().lookupComponent(source.getInt("component"));
		if(source.has("order"))sortOrder = source.getInt("order");
		if(source.has("aggType"))aggType = source.getInt("aggType");
		if(source.has("aggName"))aggVarName = source.getString("aggName");
				
	}
	
	public JSONObject export()  throws JSONException{
		JSONObject rslt = new JSONObject();
		if(namedComponent != null)rslt.put("component", namedComponent.getID());
		rslt.put("order", sortOrder);
		rslt.put("aggType", aggType);
		if(aggVarName != null)rslt.put("aggName", aggVarName);
		return rslt;
	}
	
	public INamedComponent getComponent() {return namedComponent;}
	
	public int getSortOrder() {return sortOrder;}
	public void setSortOrder(int val) {
		if(val == sortOrder)return;
		int oldOrder = sortOrder;
		sortOrder = val;
		this.firePropertyChange(CHANGED_ORDER, oldOrder, this);
	}
	
	public int getAggType() {return aggType;}
	public void setAggType(int val) {
		if(val == aggType)return;
		int oldAggType = aggType;
		aggType = val;
		this.firePropertyChange(CHANGED_AGGTYPE, oldAggType, this);
	}
	
	public String getAggTypeName() {
		if(aggType == SelectVarItem.NOAGG)return "--";
		if(aggType == SelectVarItem.GROUPBY)return "group by";
		return ExpressionMaker.ExpressionNames[aggType];
	}
	
	public String getAggVarName() {return aggVarName;}
	public void setAggVarName(String val) {
		if(val == aggVarName)return;
		String oldAggVarName = aggVarName;
		aggVarName = val;
		this.firePropertyChange(CHANGED_AGGVARNAME, oldAggVarName, this);
	}
}
