package uk.ac.kcl.cch.jb.sparql.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;

import org.eclipse.zest.layouts.LayoutBendPoint;
import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.LayoutRelationship;
import org.eclipse.zest.layouts.constraints.BasicEdgeConstraints;
import org.eclipse.zest.layouts.constraints.LabelLayoutConstraint;
import org.eclipse.zest.layouts.constraints.LayoutConstraint;

public class WhereClausePredicate implements HasURI, LayoutRelationship{
	
	private PropertyItem property;
	private WhereClauseComponent domain;
	private WhereClauseComponent range;
	
	// fields for Zest
	private Object graphData;
	private Object layoutInformation;

	public WhereClausePredicate(PropertyItem item, WhereClauseComponent domain, WhereClauseComponent range) {
		this.property = item;
		this.domain = domain;
		this.range = range;
	}
	
	public WhereClausePredicate(JSONObject data, QueryWhereClause clause) throws JSONException {
		IRI propertyIRI = IRI.create(data.getString("property"));
		OWLObjectProperty theProp = new OWLObjectPropertyImpl(propertyIRI);
		property = clause.getOntologyData().find(theProp);
		if(data.has("domain")) {
			int domainKey = data.getInt("domain");
			domain = clause.lookupComponent(domainKey);
			domain.addDomainPredicate(this);
		} else domain = null;
		if(data.has("range")) {
			range = clause.lookupComponent(data.getInt("range"));
			range.addRangePredicate(this);
		} else range = null;
	}
	
	public PropertyItem getProperty() {return property;}

	public WhereClauseComponent getDomain() {
		return domain;
	}

	public WhereClauseComponent getRange() {
		return range;
	}
	
	public void initializeRange(WhereClauseComponent range) {
		this.range = range;
	}
	
	public JSONObject export() throws JSONException {
		JSONObject rslt = new JSONObject();
		rslt.put("property", property.getMyIRI().toString());
		if(domain != null)rslt.put("domain", domain.getID());
		if(range != null)rslt.put("range", range.getID());
		return rslt;
	}

	@Override
	public String getDisplayURI() {
		return property.getDisplayURI();
	}

	@Override
	public String getFullURI() {
		return property.getMyIRI().toString();
	}
	
	// methods required for Zest    JB

	@Override
	public void setGraphData(Object o) {
		this.graphData = o;
		
	}

	@Override
	public Object getGraphData() {
		// TODO Auto-generated method stub
		return graphData;
	}

	@Override
	public LayoutEntity getSourceInLayout() {
		// TODO Auto-generated method stub
		return getDomain();
	}

	@Override
	public LayoutEntity getDestinationInLayout() {
		// TODO Auto-generated method stub
		return getRange();
	}

	@Override
	public void setLayoutInformation(Object layoutInformation) {
		this.layoutInformation = layoutInformation;
		
	}

	@Override
	public Object getLayoutInformation() {
		// TODO Auto-generated method stub
		return layoutInformation;
	}
	
	// in LayoutRelationship comments say that these can do nothing   JB

	@Override
	public void setBendPoints(LayoutBendPoint[] bendPoints) {
		// do Nothing
		
	}

	@Override
	public void clearBendPoints() {
		// do Nothing
		
	}

	// not at all sure this is actually needed in our context   JB
	@Override
	public void populateLayoutConstraint(LayoutConstraint constraint) {
		if (constraint instanceof LabelLayoutConstraint) {
			LabelLayoutConstraint labelConstraint = (LabelLayoutConstraint) constraint;
			labelConstraint.label = getDisplayURI();
			labelConstraint.pointSize = 18;
		} else if (constraint instanceof BasicEdgeConstraints) {
			// noop

		}
	}

}
