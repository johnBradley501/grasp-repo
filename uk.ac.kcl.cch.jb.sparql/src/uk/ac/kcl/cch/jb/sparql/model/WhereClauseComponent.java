package uk.ac.kcl.cch.jb.sparql.model;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.constraints.BasicEntityConstraint;
import org.eclipse.zest.layouts.constraints.EntityPriorityConstraint;
import org.eclipse.zest.layouts.constraints.LabelLayoutConstraint;
import org.eclipse.zest.layouts.constraints.LayoutConstraint;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class WhereClauseComponent extends PropertyChangeObject implements LayoutEntity{

	public static final String DOMAIN_PRED_CHANGE = "DOMAIN_PRED_CHANGE";
	public static final String RANGE_PRED_CHANGE = "RANGE_PRED_CHANGE";
	public static final String NEW_BOUNDS = "NEW_BOUNDS";
	
	public static WhereClauseComponent loadComponent(JSONObject data, QueryWhereClause clause) { // SPARQLQuery query) {
		try {
			String type = ClassComponent.myType;
			if(data.has("type")) type = data.getString("type");
			if(type.equals(ClassComponent.myType))
				return new ClassComponent(data, clause);
			else if(type.equals(InstanceComponent.myType))
				return new InstanceComponent(data, clause);
			else if(type.equals(VariableComponent.myType))
				return new VariableComponent(data, clause);
			else return null;
		} catch (JSONException | OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	protected SPARQLQuery query;
	protected QueryWhereClause whereClause;
	protected int ID;
	protected Rectangle bounds = null;

	private List<WhereClausePredicate> domainPredicates = new ArrayList<WhereClausePredicate>();
	private List<WhereClausePredicate> rangePredicates = new ArrayList<WhereClausePredicate>();
	
	// fields for Zest layout structures
	
	private Object myGraphData = null;
	private boolean ignoreInLayout;
	private double centreX = 0.0;
	private double centreY = 0.0;
	private Object internalEntity;

	public WhereClauseComponent(QueryWhereClause whereClause) {
		this.whereClause = whereClause;
		this.query = whereClause.getMyQuery();
		this.ID = query.incrementCounter();
	}
	
	public WhereClauseComponent(JSONObject data, QueryWhereClause whereClause) throws JSONException{
		this.whereClause = whereClause;
		this.query = whereClause.getMyQuery();
		// URL ontURL = query.getOntologyURL();
		ID = data.getInt("id");
		if(ID > query.getComponentCounter())query.setComponentCounter(ID);
		JSONArray bd = data.getJSONArray("bounds");
		bounds = new Rectangle(bd.getInt(0),bd.getInt(1),bd.getInt(2),bd.getInt(3));
		calculateCentre();
	}
	
	public SPARQLQuery getMyQuery() {return query;}
	
	private void calculateCentre() {
		if(bounds == null)return;
		centreX = bounds.preciseX()+bounds.preciseWidth()/2.0;
		centreY = bounds.preciseY()+bounds.preciseHeight()/2.0;
		
	}

	public JSONObject export() throws JSONException {
		JSONObject rslt = new JSONObject();
		rslt.put("id", ID);
		
		JSONArray bj = new JSONArray();
		bj.put(bounds.x);
		bj.put(bounds.y);
		bj.put(bounds.width);
		bj.put(bounds.height);
		rslt.put("bounds", bj);
		return rslt;
	}

	public int getID() {return ID;}
	public Rectangle getMyBounds() {return bounds;}
	public String getName() {return "";} // this method is overridden when a name is available...  JB
	public void setMyBounds(Rectangle b) {
		Rectangle oldBounds = bounds;
		bounds = b;
		calculateCentre();
		this.firePropertyChange(NEW_BOUNDS, oldBounds, bounds);
	}

	
	public void addDomainPredicate(WhereClausePredicate pred) {
		if(domainPredicates.contains(pred))return;
		domainPredicates.add(pred);
		this.firePropertyChange(DOMAIN_PRED_CHANGE, null, pred);
		whereClause.SignalPredicateChange(null,  pred);
	}
	
	public void removeDomainPredicate(WhereClausePredicate pred) {
		if(!domainPredicates.contains(pred))return;
		domainPredicates.remove(pred);
		this.firePropertyChange(DOMAIN_PRED_CHANGE, pred, null);
		whereClause.SignalPredicateChange(pred, null);
	}
	
	public List<WhereClausePredicate> getDomainPredicates(){return domainPredicates;}
	
	public void addRangePredicate(WhereClausePredicate pred) {
		if(rangePredicates.contains(pred))return;
		rangePredicates.add(pred);
		this.firePropertyChange(RANGE_PRED_CHANGE, null, pred);
		whereClause.SignalPredicateChange(null,  pred);
	}

	public void removeRangePredicate(WhereClausePredicate pred) {
		if(!rangePredicates.contains(pred))return;
		rangePredicates.remove(pred);
		this.firePropertyChange(RANGE_PRED_CHANGE, pred, null);
		whereClause.SignalPredicateChange(pred, null);
	}
	
	public List<WhereClausePredicate> getRangePredicates(){return rangePredicates;}

	// methods needed for use of Zest layout algorithms
	
	// all objects are equal?
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setGraphData(Object o) {
		this.myGraphData = o;
		
	}

	@Override
	public Object getGraphData() {
		// TODO Auto-generated method stub
		return myGraphData;
	}

	@Override
	public void setLocationInLayout(double x, double y) {
		if (!ignoreInLayout) {
			this.centreX = x;
			this.centreY = y;
			updateBounds();
		}
		
	}

	private void updateBounds() {
		Rectangle newBounds = new Rectangle(bounds);
		double cornerX = centreX-bounds.preciseWidth()/2.0;
		double cornerY = centreY-bounds.preciseHeight()/2.0;

		newBounds.x = (int)cornerX;
		newBounds.y = (int)cornerY;
		if(newBounds.x == bounds.x && newBounds.y == bounds.y)return;
		Rectangle oldBounds = bounds;
		bounds = newBounds;
		this.firePropertyChange(NEW_BOUNDS, oldBounds, bounds);
	}

	@Override
	public void setSizeInLayout(double width, double height) {
		// size of object should not be changed.. do nothing here.
		
	}

	@Override
	public double getXInLayout() {
		// TODO Auto-generated method stub
		return this.centreX;
	}

	@Override
	public double getYInLayout() {
		// TODO Auto-generated method stub
		return this.centreY;
	}

	@Override
	public double getWidthInLayout() {
		// TODO Auto-generated method stub
		return bounds.preciseWidth();
	}

	@Override
	public double getHeightInLayout() {
		// TODO Auto-generated method stub
		return bounds.preciseHeight();
	}

	@Override
	public Object getLayoutInformation() {
		// TODO Auto-generated method stub
		return internalEntity;
	}

	@Override
	public void setLayoutInformation(Object internalEntity) {
		this.internalEntity = internalEntity;
		
	}
	
	// I'm not sure this is needed... (copied from SimpleNode...)

	@Override
	public void populateLayoutConstraint(LayoutConstraint constraint) {
		if (constraint instanceof LabelLayoutConstraint) {
			LabelLayoutConstraint labelConstraint = (LabelLayoutConstraint) constraint;
			labelConstraint.label = getName();
			labelConstraint.pointSize = 18;
		} else if (constraint instanceof BasicEntityConstraint) {
			// noop
		} else if (constraint instanceof EntityPriorityConstraint) {
			EntityPriorityConstraint priorityConstraint = (EntityPriorityConstraint) constraint;
			priorityConstraint.priority = Math.random() * 10 + 1;
		}
	}
}
