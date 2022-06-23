package uk.ac.kcl.cch.jb.sparql.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import uk.ac.kcl.cch.jb.sparql.commands.DeleteDataConstraintCommand;
import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.parts.DataConstraintPart;

public class DataConstraintComponentEditPolicy extends ComponentEditPolicy {

	private QueryCreationEditor myEditor;

	public DataConstraintComponentEditPolicy(QueryCreationEditor myEditor) {
		super();
		this.myEditor = myEditor;
	}
	
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
	DataConstraintPart dc = (DataConstraintPart)getHost();
	return new DeleteDataConstraintCommand(dc.getDataConstraint(), myEditor);
	}

}
