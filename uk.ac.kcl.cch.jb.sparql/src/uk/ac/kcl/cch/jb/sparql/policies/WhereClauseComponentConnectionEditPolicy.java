package uk.ac.kcl.cch.jb.sparql.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;

public class WhereClauseComponentConnectionEditPolicy extends GraphicalNodeEditPolicy {

	private QueryCreationEditor myEditor;

	public WhereClauseComponentConnectionEditPolicy(QueryCreationEditor myEditor) {
		super();
		this.myEditor = myEditor;
	}

	@Override
	protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Command getReconnectTargetCommand(ReconnectRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Command getReconnectSourceCommand(ReconnectRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

}
