package uk.ac.kcl.cch.jb.sparql.builder;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rdf4j.sparqlbuilder.core.Variable;
import org.eclipse.rdf4j.sparqlbuilder.core.query.Queries;
import org.eclipse.rdf4j.sparqlbuilder.core.query.SelectQuery;

import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery;
import uk.ac.kcl.cch.jb.sparql.model.SelectClauseModifier;

public class Builder {
	
	private SPARQLQuery data;
	private SelectQuery query;
	private SelectBuilder selectBuilder;
	private WhereBuilder whereBuilder;
	private VarHandler varHandler = new VarHandler();
	private PrefixHandler prefixHandler;
	
	public Builder(SPARQLQuery data) {
		this.data = data;
		this.query = Queries.SELECT();
		this.prefixHandler = new PrefixHandler(data);
		this.selectBuilder = new SelectBuilder(data, varHandler);
		this.whereBuilder = new WhereBuilder(data, varHandler, prefixHandler);
	}
	
	public String toSPARQL() {
		query = selectBuilder.addToQuery(query);
		query = whereBuilder.addToQuery(query);
		query = prefixHandler.addToQuery(query);
		query = handleQueryModifier(query);
		return query.getQueryString();
	}

	private SelectQuery handleQueryModifier(SelectQuery query2) {
		SelectClauseModifier scm = data.getSelectClause().getModifier();
		if(scm.isDistinct())query = query.distinct();
		if(scm.getLimit() > 0)query = query.limit(scm.getLimit());
		if(scm.getOffset() > 0)query = query.offset(scm.getLimit());
		return query;
	}

}
