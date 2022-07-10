package uk.ac.kcl.cch.jb.sparql.model;

public class SelectAreaFront {

	private QuerySelectClause select;

	public SelectAreaFront(QuerySelectClause select) {
		this.select = select;
	}
	
	public QuerySelectClause getClause() {return select;}
}
