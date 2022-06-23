package uk.ac.kcl.cch.jb.sparql.commands;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery.QueryMetadata;

public class UpdateQueryMetadataCommand extends DirtyCommand {

	private SPARQLQuery query;
	private QueryMetadata oldMetadata;
	private QueryMetadata newMetadata;

	public UpdateQueryMetadataCommand(QueryCreationEditor myEditor, SPARQLQuery query, QueryMetadata newMD) {
		super(myEditor);
		this.setLabel("Update Metadata");
		this.query = query;
		this.oldMetadata = query.getQueryMetadata();
		this.newMetadata = newMD;
	}
	
	public void execute() {
		super.execute();
		query.setQueryMetadata(newMetadata);
	}
	
	public void undo() {
		super.undo();
		query.setQueryMetadata(oldMetadata);
	}

}
