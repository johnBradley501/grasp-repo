package uk.ac.kcl.cch.jb.sparql.model;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class NamedComponent extends WhereClauseComponent implements INamedComponent {
	
	protected String name = null;
	
	public NamedComponent(QueryWhereClause clause) {
		super(clause);
	}
	
	public NamedComponent(JSONObject data, QueryWhereClause clause)throws JSONException {
		super(data, clause);
		if(data.has("name")) {
			name = data.getString("name");
			getMyQuery().getVarNameManager().addName(this);
		}
		else name = query.getVarNameManager().proposeName(this); // createName();
	}
	
	public JSONObject export() throws JSONException {
		JSONObject rslt = super.export();
		rslt.put("name", name);
		return rslt;
	}
	
	@Override
	public String getName() {return name;}

	@Override
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		this.firePropertyChange(NEW_NAME, oldName, name);
	}

}
