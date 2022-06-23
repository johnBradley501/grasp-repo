package uk.ac.kcl.cch.jb.sparql.commands;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.ObjectPropertyItem;
import uk.ac.kcl.cch.jb.sparql.model.ClassComponent;
import uk.ac.kcl.cch.jb.sparql.model.WhereClausePredicate;

public class AddPropertyLinkCommand extends DirtyCommand {

	private ObjectPropertyItem prop;
	private ClassComponent domain;
	private ClassComponent range;
	private WhereClausePredicate pred;

	public AddPropertyLinkCommand(QueryCreationEditor myEditor, String shortTitle, ObjectPropertyItem prop, ClassComponent domain,
			ClassComponent range) {
		super(myEditor);
		this.setLabel(shortTitle);
		this.prop = prop;
		this.domain = domain;
		this.range = range;
		this.pred = new WhereClausePredicate(prop, domain, range);
	}
	
	public void execute() {
		domain.addDomainPredicate(pred);
		range.addRangePredicate(pred);
		super.execute();
	}
	
	public void undo() {
		super.undo();
		domain.removeDomainPredicate(pred);
		range.removeRangePredicate(pred);
		
	}

}
