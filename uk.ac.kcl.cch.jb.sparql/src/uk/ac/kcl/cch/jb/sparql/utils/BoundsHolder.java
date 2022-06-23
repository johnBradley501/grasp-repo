package uk.ac.kcl.cch.jb.sparql.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.draw2d.geometry.Rectangle;

import uk.ac.kcl.cch.jb.sparql.model.QueryWhereClause;
import uk.ac.kcl.cch.jb.sparql.model.WhereClauseComponent;

public class BoundsHolder {
	
	private QueryWhereClause clause;
	private Map<Integer, Rectangle> classItems = new HashMap<Integer, Rectangle>();

	public BoundsHolder(QueryWhereClause clause) {
		this.clause = clause;
		for(WhereClauseComponent item: clause.getComponents()) {
			classItems.put(item.getID(),item.getMyBounds());
		}
	}
	
	public void setBounds() {
		for(WhereClauseComponent item: clause.getComponents()) {
			item.setMyBounds(classItems.get(item.getID()));
		}
		
	}

}
