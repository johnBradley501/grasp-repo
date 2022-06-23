package uk.ac.kcl.cch.jb.sparql.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import uk.ac.kcl.cch.jb.sparql.commands.DeleteComponentCommand;
import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.parts.WhereClauseComponentPart;

public class WhereClauseComponentsEditPolicy extends ComponentEditPolicy {

	private QueryCreationEditor myEditor;

	public WhereClauseComponentsEditPolicy(QueryCreationEditor myEditor) {
		super();
		this.myEditor = myEditor;
	}
	
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		WhereClauseComponentPart comp = (WhereClauseComponentPart)getHost();
		return new DeleteComponentCommand(comp, myEditor);
	}


}
