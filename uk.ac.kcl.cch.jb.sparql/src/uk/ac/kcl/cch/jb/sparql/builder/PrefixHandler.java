package uk.ac.kcl.cch.jb.sparql.builder;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.rdf4j.sparqlbuilder.core.Prefix;
import org.eclipse.rdf4j.sparqlbuilder.core.SparqlBuilder;
import org.eclipse.rdf4j.sparqlbuilder.core.query.SelectQuery;
import org.eclipse.rdf4j.sparqlbuilder.rdf.Iri;
import org.eclipse.rdf4j.sparqlbuilder.rdf.Rdf;

import uk.ac.kcl.cch.jb.sparql.model.HasURI;
import uk.ac.kcl.cch.jb.sparql.model.OntologyData;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery;

public class PrefixHandler {
	
	private class MapData {
		public String mapValue = null;
		public Prefix prefix = null;
		
		public MapData(String map) {
			mapValue = map;
		}
	}

	private SPARQLQuery data;
	private Map<String, MapData> prefixMap = new TreeMap<String, MapData>();
	private OntologyData ontData;

	public PrefixHandler(SPARQLQuery data) {
		this.data = data;
		ontData = data.getOntologyData();
		Map<String, String> givenMap = new TreeMap(ontData.getPrefixName2PrefixMap());
		if(!givenMap.containsKey(":")) {
			String base = ontData.getDefaultPrefix();
			if(base != null)givenMap.put(":", base);
		}
		
		Iterator<String> it = givenMap.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			String val = givenMap.get(key);
			prefixMap.put(key, new MapData(val));
		}
	}
	
	public Iri getTheIri(HasURI item) {
		String displayVersion = item.getDisplayURI();
		if(displayVersion.startsWith("<")){
			return Rdf.iri(item.getFullURI());
		}
		String [] parts = displayVersion.split(":");
		if(prefixMap.containsKey(parts[0]+":")) {
			MapData theOne = prefixMap.get(parts[0]+":");
			if(theOne.prefix == null)
				theOne.prefix = SparqlBuilder.prefix(parts[0], Rdf.iri(theOne.mapValue));
			return theOne.prefix.iri(parts[1]);
		}
		return Rdf.iri(item.getFullURI());
	}
	
	public SelectQuery addToQuery(SelectQuery query) {
		for(String prefix: prefixMap.keySet()) {
			MapData data = prefixMap.get(prefix);
			if(data.prefix != null) {
				query = query.prefix(data.prefix);
			}
		}
		return query;
	}

}
