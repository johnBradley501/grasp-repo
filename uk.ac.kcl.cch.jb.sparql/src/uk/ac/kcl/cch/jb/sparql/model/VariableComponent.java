package uk.ac.kcl.cch.jb.sparql.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDatatype;

import uk.ac.kcl.cch.jb.sparql.figures.VariableComponentFigure;

public class VariableComponent extends NamedComponent {
	public static final String myType = "variable";
	public static final String CHANGE_CONSTRAINTS = "CHANGE_CONSTRAINTS";

	private IRI dataType;
	
	private List<DataConstraint> constraints;
	private WhereClausePredicate myProperty = null;

	public VariableComponent(QueryWhereClause clause, IRI myType, WhereClausePredicate pred) {
		super(clause);
		// name = createName();
		this.dataType = myType;
		this.myProperty = pred;
		name = getMyQuery().getVarNameManager().proposeName(this);
		constraints = new ArrayList<DataConstraint>();
	}

	public VariableComponent(JSONObject data, QueryWhereClause clause)throws JSONException {
		super(data,clause);
		constraints = new ArrayList<DataConstraint>();
		//if(data.has("name")) {
		//	name = data.getString("name");
		//	query.getVarNameManager().addName(this);
		//}
		//else name = query.getVarNameManager().proposeName(this); // createName();
		if(data.has("datatype")) dataType = IRI.create(data.getString("datatype"));
		else dataType = IRI.create(DataPropertyItem.UNDEFINED_TYPE);
		if(data.has("constraints")) {
			JSONArray theCons = data.getJSONArray("constraints");
			for(Object item: theCons) {
				constraints.add(new DataConstraint(this, (JSONObject)item));
			}
		}
	}

	public JSONObject export() throws JSONException {
		JSONObject rslt = super.export();
		rslt.put("type", myType);
		//rslt.put("name", name);
		rslt.put("datatype", dataType.toString());
		JSONArray cons = new JSONArray();
		rslt.put("constraints", cons);
		for(DataConstraint constr: constraints) {
			cons.put(constr.export());
		}
		return rslt;
	}

	//private String createName() {
	//	return "var_"+getID();
	//}
	
	/*public String getName() {return name;}
	
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		this.firePropertyChange(NEW_NAME, oldName, name);
	} */
	
	public IRI getDataType() {return dataType;}
	
	public WhereClausePredicate getMyProperty() {return myProperty;}
	
	public List<DataConstraint> getConstraints(){return constraints;}
	
	public void addConstraint(DataConstraint constr) {
		if(constraints.contains(constr))return;
		constraints.add(constr);
		this.firePropertyChange(CHANGE_CONSTRAINTS, null, constr);
	}
	
	public void deleteConstraint(DataConstraint constr) {
		if(!constraints.contains(constr))return;
		constraints.remove(constr);
		this.firePropertyChange(CHANGE_CONSTRAINTS, constr, null);
	}
	
	public void insertConstraint(DataConstraint constr, int index) {
		if(constraints.contains(constr))return;
		constraints.add(index, constr);
		this.firePropertyChange(CHANGE_CONSTRAINTS, null, constr);
	}
	
	public Rectangle getMyBounds() {
		Rectangle r = new Rectangle(super.getMyBounds());
		r.height = VariableComponentFigure.nameHeight+VariableComponentFigure.constraintHeight*constraints.size();
		return r;
	}

	@Override
	public String getBasisForName() {
		if(getMyProperty() == null)return "var";
		return getMyProperty().getDisplayURI();
	}

}
