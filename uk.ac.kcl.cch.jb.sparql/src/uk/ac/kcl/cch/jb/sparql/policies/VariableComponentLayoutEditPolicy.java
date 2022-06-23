package uk.ac.kcl.cch.jb.sparql.policies;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.OrderedLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;

public class VariableComponentLayoutEditPolicy extends OrderedLayoutEditPolicy {

	private QueryCreationEditor myEditor;

	public VariableComponentLayoutEditPolicy(QueryCreationEditor myEditor) {
		this.myEditor = myEditor;
	}

	@Override
	protected Command createAddCommand(EditPart child, EditPart after) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Command createMoveChildCommand(EditPart child, EditPart after) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected EditPart getInsertionReference(Request request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

}
