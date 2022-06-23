package uk.ac.kcl.cch.jb.sparql.builder;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rdf4j.sparqlbuilder.core.SparqlBuilder;
import org.eclipse.rdf4j.sparqlbuilder.core.Variable;

public class VarHandler {

	private Map<String, Variable> vars;
	
	public VarHandler() {
		vars = new HashMap<String, Variable>();
	}
	
	public Variable find(String name) {
		if(!vars.containsKey(name))vars.put(name, SparqlBuilder.var(name));
		return vars.get(name);
	}

}
