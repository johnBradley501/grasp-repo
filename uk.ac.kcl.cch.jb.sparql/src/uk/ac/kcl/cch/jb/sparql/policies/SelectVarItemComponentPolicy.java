package uk.ac.kcl.cch.jb.sparql.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import uk.ac.kcl.cch.jb.sparql.commands.DeleteSelectVarItemCommand;
import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.SelectVarItem;
import uk.ac.kcl.cch.jb.sparql.parts.SelectVarItemPart;

public class SelectVarItemComponentPolicy extends ComponentEditPolicy {

	private QueryCreationEditor myEditor;

	public SelectVarItemComponentPolicy(QueryCreationEditor myEditor) {
		this.myEditor = myEditor;
	}
	
	protected Command createDeleteCommand(GroupRequest request) {
		SelectVarItemPart myPart = (SelectVarItemPart)getHost();
		SelectVarItem item = myPart.getVarItem();
		return new DeleteSelectVarItemCommand(item, myEditor);
	}
	
}
