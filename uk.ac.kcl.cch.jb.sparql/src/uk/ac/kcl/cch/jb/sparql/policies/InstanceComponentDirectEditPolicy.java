package uk.ac.kcl.cch.jb.sparql.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;

import uk.ac.kcl.cch.jb.sparql.commands.NameUpdateCommand;
import uk.ac.kcl.cch.jb.sparql.commands.InstanceItemUpdateCommand;
import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.InstanceElement;
import uk.ac.kcl.cch.jb.sparql.parts.InstanceComponentPart;

public class InstanceComponentDirectEditPolicy extends DirectEditPolicy {
	
	private QueryCreationEditor myEditor;

	public InstanceComponentDirectEditPolicy(QueryCreationEditor myEditor) {
		super();
		this.myEditor = myEditor;
	}

	@Override
	protected Command getDirectEditCommand(DirectEditRequest request) {
		InstanceElement value = (InstanceElement)request.getCellEditor().getValue();
		InstanceComponentPart part = (InstanceComponentPart)getHost();
		return new InstanceItemUpdateCommand(part.getInstanceComponent(), value, myEditor);
	}

	@Override
	protected void showCurrentEditValue(DirectEditRequest request) {
		InstanceElement value = (InstanceElement)request.getCellEditor().getValue();

	}

}
