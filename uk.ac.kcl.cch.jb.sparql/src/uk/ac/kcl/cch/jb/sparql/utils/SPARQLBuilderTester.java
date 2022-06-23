package uk.ac.kcl.cch.jb.sparql.utils;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.sparqlbuilder.constraint.Expression;
import org.eclipse.rdf4j.sparqlbuilder.constraint.Expressions;
import org.eclipse.rdf4j.sparqlbuilder.core.Prefix;
import org.eclipse.rdf4j.sparqlbuilder.core.SparqlBuilder;
import org.eclipse.rdf4j.sparqlbuilder.core.Variable;
import org.eclipse.rdf4j.sparqlbuilder.core.query.Queries;
import org.eclipse.rdf4j.sparqlbuilder.core.query.SelectQuery;
import org.eclipse.rdf4j.sparqlbuilder.graphpattern.GraphPattern;
import org.eclipse.rdf4j.sparqlbuilder.graphpattern.GraphPatternNotTriples;
import org.eclipse.rdf4j.sparqlbuilder.graphpattern.GraphPatterns;
import org.eclipse.rdf4j.sparqlbuilder.graphpattern.TriplePattern;
import org.eclipse.rdf4j.sparqlbuilder.rdf.Rdf;
import org.eclipse.rdf4j.sparqlbuilder.rdf.RdfPredicate;
import org.eclipse.rdf4j.sparqlbuilder.rdf.RdfPredicateObjectList;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class SPARQLBuilderTester extends Action implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;

	@Override
	public void run(IAction action) {
		SelectQuery query = Queries.SELECT();
		
		Prefix foaf = SparqlBuilder.prefix("foaf", Rdf.iri("http://xmlns.com/foaf/0.1/"));
		Variable name = SparqlBuilder.var("name");
		Variable x = SparqlBuilder.var("x");
		Variable y = SparqlBuilder.var("y");
		Variable address = SparqlBuilder.var("address");
		
		Expression countAgg = Expressions.count(y);
		System.out.println("countAgg: "+countAgg.getQueryString());
		Variable count = SparqlBuilder.var("count");
		
		GraphPattern pat = x.isA(foaf.iri("Person"))
				.andHas(foaf.iri("name"), name).andHas(foaf.iri("affiliation"), y)
				.filter(Expressions.regex(name, "abcd")).filter(Expressions.regex(y,"DFGH"));
		
	
		GraphPattern pat2 = y.isA(foaf.iri("Institution"))
				.andHas(foaf.iri("address"), address);
		
		query.select(x).select(name)
		.select(countAgg.as(count))
		.where(pat).where(pat2)
	    .orderBy(name)
	    .limit(5)
	    .offset(10).prefix(foaf);
		
		System.out.println(query.getQueryString());
		
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// not needed
		
	}

	@Override
	public void dispose() {
		// not needed
		
	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
		
	}

}
