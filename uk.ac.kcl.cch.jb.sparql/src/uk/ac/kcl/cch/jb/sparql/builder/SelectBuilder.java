package uk.ac.kcl.cch.jb.sparql.builder;

import java.util.List;
import java.util.Map;

import org.eclipse.rdf4j.sparqlbuilder.constraint.Expression;
import org.eclipse.rdf4j.sparqlbuilder.core.SparqlBuilder;
import org.eclipse.rdf4j.sparqlbuilder.core.Variable;
import org.eclipse.rdf4j.sparqlbuilder.core.query.SelectQuery;

import uk.ac.kcl.cch.jb.sparql.model.QuerySelectClause;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery;
import uk.ac.kcl.cch.jb.sparql.model.SelectVarItem;

public class SelectBuilder {

	private SPARQLQuery data;
	private VarHandler vars;

	public SelectBuilder(SPARQLQuery data, VarHandler varHandler) {
		this.data = data;
		this.vars = varHandler;
	}
	
	public SelectQuery addToQuery(SelectQuery query) {
		QuerySelectClause sc =  data.getSelectClause();
		for(SelectVarItem item: sc.getVars()) {
			String varname = item.getComponent().getName();
			Variable var = vars.find(varname);
			if(item.getAggType() < 0) {
				query.select(var);
				if(item.getAggType() == SelectVarItem.GROUPBY) query.groupBy(var);
			}
			else {
				Expression aggexp = ExpressionMaker.instance.getExpression(item.getAggType(), var);
				var = vars.find(item.getAggVarName());
				query.select(aggexp.as(var));
			}
			if(item.getSortOrder() != SelectVarItem.UNORDERED) {
				if(item.getSortOrder()==SelectVarItem.ASCENDING)query.orderBy(var);
				else if(item.getSortOrder()==SelectVarItem.DESCENDING)query.orderBy(SparqlBuilder.desc(var));
			}
		}
		return query;
	}

}
