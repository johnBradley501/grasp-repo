package uk.ac.kcl.cch.jb.sparql.builder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.rdf4j.sparqlbuilder.constraint.Expression;
import org.eclipse.rdf4j.sparqlbuilder.constraint.Expressions;
import org.eclipse.rdf4j.sparqlbuilder.core.QueryPattern;
import org.eclipse.rdf4j.sparqlbuilder.core.Variable;
import org.eclipse.rdf4j.sparqlbuilder.core.query.SelectQuery;
import org.eclipse.rdf4j.sparqlbuilder.graphpattern.GraphPattern;
import org.eclipse.rdf4j.sparqlbuilder.graphpattern.GraphPatterns;
import org.eclipse.rdf4j.sparqlbuilder.graphpattern.TriplePattern;
import org.eclipse.rdf4j.sparqlbuilder.rdf.Iri;
import org.eclipse.rdf4j.sparqlbuilder.rdf.Rdf;

import uk.ac.kcl.cch.jb.sparql.model.ClassComponent;
import uk.ac.kcl.cch.jb.sparql.model.ClassItem;
import uk.ac.kcl.cch.jb.sparql.model.DataConstraint;
import uk.ac.kcl.cch.jb.sparql.model.InstanceComponent;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery;
import uk.ac.kcl.cch.jb.sparql.model.VariableComponent;
import uk.ac.kcl.cch.jb.sparql.model.WhereClauseComponent;
import uk.ac.kcl.cch.jb.sparql.model.WhereClausePredicate;

public class WhereBuilder {

	private SPARQLQuery data;
	private VarHandler varHandler;
	private PrefixHandler prefixHandler;

	public WhereBuilder(SPARQLQuery data, VarHandler varHandler, PrefixHandler prefixHandler) {
		this.data = data;
		this.varHandler = varHandler;
		this.prefixHandler = prefixHandler;
	}
	
	public SelectQuery addToQuery(SelectQuery query) {
		//List<WhereClauseComponent> leftItems = new LinkedList<WhereClauseComponent>(data.getWhereClause().getComponents());
		//List<WhereClauseComponent> items = locateConnectedGraph(leftItems);
		//query = processSelectedItems(items);
		
		TreeMap<Integer,ClassComponent> classComponents = getClassComponents();
		
		ClassComponent first = classComponents.get(classComponents.firstKey());
		Set<ClassComponent> doneSet = new HashSet<ClassComponent>();
		
		GraphPattern pat = processComponent(first, null, doneSet);
		return query.where(pat);
	}

	private TreeMap<Integer,ClassComponent> getClassComponents() {
		TreeMap<Integer,ClassComponent> rslt = new TreeMap<Integer,ClassComponent>();
		for(WhereClauseComponent item: data.getWhereClause().getComponents()) {
			if(item instanceof ClassComponent) {
				ClassComponent cc = (ClassComponent)item;
				rslt.put(cc.getMyBounds().x, cc); // to get left to right ordering   jb
			}
		}
		return rslt;
	}

	private GraphPattern processComponent(ClassComponent curr, GraphPattern gpat, Set<ClassComponent> doneSet) {
		// System.out.println("Starting: "+curr.getName());
		
		for(WhereClausePredicate pred: curr.getRangePredicates()) {
			WhereClauseComponent predec = pred.getDomain();
			if(predec instanceof ClassComponent) {
				ClassComponent predecl = (ClassComponent)predec;
				if(!doneSet.contains(predecl))gpat = processComponent(predecl, gpat, doneSet);
			}
					
		}
		if(doneSet.contains(curr))return gpat;
		doneSet.add(curr);
		
		// System.out.println("Outputting: "+curr.getName());
		
		Variable varName = varHandler.find(curr.getName());
		
		TriplePattern pat = varName.isA(prefixHandler.getTheIri(curr));
		// GraphPattern pat = varName.isA(prefixHandler.getTheIri(curr));
		List<ClassComponent>processSoon = new ArrayList<ClassComponent>();
		List<Expression> expressions = new ArrayList<Expression>();
		List<WhereClausePredicate> optionalPredicates = new ArrayList<WhereClausePredicate>();
		
		for(WhereClausePredicate pred: curr.getDomainPredicates()) {
			if(pred.isOptional())optionalPredicates.add(pred);
			else {
				WhereClauseComponent domComp = pred.getRange();
				if(domComp instanceof ClassComponent)pat = processDomainClass((ClassComponent)domComp, pat, pred, processSoon);
				else if(domComp instanceof InstanceComponent)pat = processInstance((InstanceComponent)domComp, pat, pred);
				else if(domComp instanceof VariableComponent)pat = processVariable((VariableComponent)domComp, pat, pred, expressions);
			}
		}
		
		GraphPattern qpat = pat;
		if(expressions.size() == 1) {
			qpat = qpat.filter(expressions.get(0));
		} else if(expressions.size() > 1) {
			qpat = qpat.filter(Expressions.and(expressions.toArray(new Expression[0])));
		} 
		if(gpat == null)gpat = qpat;
		else gpat = gpat.and(qpat);
		
		for(WhereClausePredicate pred: optionalPredicates) {
			qpat = handleOptional(curr,pred, gpat, doneSet);
			gpat = gpat.and(qpat);
		}
		
		for(ClassComponent inst: processSoon) {
			gpat = processComponent(inst, gpat, doneSet);
		}
		return gpat;
	}

	private GraphPattern handleOptional(ClassComponent curr, WhereClausePredicate pred, GraphPattern qpat, Set<ClassComponent> doneSet) {
		
		WhereClauseComponent domComp = pred.getDomain();
		WhereClauseComponent rangeComp = pred.getRange();
		GraphPattern optPat = GraphPatterns.tp(varHandler.find(domComp.getName()),
				prefixHandler.getTheIri(pred),
				varHandler.find(rangeComp.getName()));
		if(rangeComp instanceof ClassComponent) {
			ClassComponent cc = (ClassComponent)rangeComp;
			optPat = processComponent(cc, optPat, doneSet);
		}
		return optPat.optional();
	}

	private TriplePattern processDomainClass(ClassComponent component, TriplePattern pat, WhereClausePredicate pred, List<ClassComponent> processSoon) {
		processSoon.add(component);
		Variable varName = varHandler.find(component.getName());
		return ((TriplePattern) pat).andHas(prefixHandler.getTheIri(pred), varName);
		
	}

	private TriplePattern processInstance(InstanceComponent component, TriplePattern pat, WhereClausePredicate pred) {
		return pat.andHas(prefixHandler.getTheIri(pred), prefixHandler.getTheIri(component));
	}

	private TriplePattern processVariable(VariableComponent component, TriplePattern pat, WhereClausePredicate pred, List<Expression> expressions) {
		Variable varName = varHandler.find(component.getName());
		pat = pat.andHas(prefixHandler.getTheIri(pred), varName);
		processDataConstraints(component, expressions);
		return pat;
	}

	private void processDataConstraints(VariableComponent component, List<Expression> expressions) {
		Variable varName = varHandler.find(component.getName());
		String dataType = ExpressionMaker.instance.getCheckType(component.getDataType().getFragment());
		for (DataConstraint constraint: component.getConstraints()) {
			if(constraint.getConstraintType()>= 0 && constraint.getConstraintValue()!= null) {
				Expression exp = null;
				String cval = constraint.getConstraintValue();
				if(dataType.equals("int")) {
					int val = Integer.parseInt(cval.trim());
					exp = ExpressionMaker.instance.getExpression(constraint.getConstraintType(), varName, val);
				} else if(dataType.equals("float")) {
					float val = Float.parseFloat(cval.trim());
					exp = ExpressionMaker.instance.getExpression(constraint.getConstraintType(), varName, val);
				} else if(dataType.equals("bool")) {
					boolean val = Boolean.parseBoolean(cval.trim());
					exp = ExpressionMaker.instance.getExpression(constraint.getConstraintType(), varName, val);
				} else {
					exp = ExpressionMaker.instance.getExpression(constraint.getConstraintType(), varName, cval);
				}
				if(exp != null)expressions.add(exp);
			}
		}
	}

}
