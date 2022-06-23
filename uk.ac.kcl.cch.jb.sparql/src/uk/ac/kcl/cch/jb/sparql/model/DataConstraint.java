package uk.ac.kcl.cch.jb.sparql.model;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.kcl.cch.jb.sparql.builder.ExpressionMaker;

public class DataConstraint extends PropertyChangeObject {
	
	// https://www.w3.org/TR/2012/REC-xmlschema11-2-20120405/datatypes.html
	
	public static final String TYPE_CHANGED = "TYPE_CHANGED";
	public static final String VALUE_CHANGED = "VALUE_CHANGED";
	

	private int constraintType;
	private String constraintValue;
	private VariableComponent owner;

	public DataConstraint(VariableComponent owner) {
		this.owner = owner;
		constraintType = -1;
		constraintValue = null;
	}
	
	public DataConstraint(VariableComponent owner, JSONObject data) throws JSONException{
		this(owner);
		if(data.has("constraintType"))
			constraintType = data.getInt("constraintType");
		if(data.has("constraintValue"))
			constraintValue = data.getString("constraintValue");
	}

	public JSONObject export() throws JSONException {
		JSONObject rslt = new JSONObject();
		rslt.put("constraintType", constraintType);
		rslt.put("constraintValue", constraintValue);
		return rslt;
	}

	
	public int getConstraintType() {return constraintType;}
	
	public void setConstraintType(int type) {
		if(type == constraintType)return;
		int oldConstraintType = constraintType;
		constraintType = type;
		this.firePropertyChange(TYPE_CHANGED, oldConstraintType, this);
	}
	
	public String getConstraintValue() {return constraintValue;}
	
	public void setConstraintValue(String val) {
		if(val == null) {
			if(constraintValue == null)return;
		}
		else if(val.equals(constraintValue)) return;
		String oldValue = constraintValue;
		constraintValue = val;
		this.firePropertyChange(VALUE_CHANGED, oldValue, this);
	}
	
	public VariableComponent getOwner() {return owner;}
}
