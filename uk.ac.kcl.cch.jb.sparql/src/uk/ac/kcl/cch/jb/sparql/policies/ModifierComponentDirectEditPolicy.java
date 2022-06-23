package uk.ac.kcl.cch.jb.sparql.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.draw2d.Label;

import org.eclipse.swt.widgets.Display;

import uk.ac.kcl.cch.jb.sparql.commands.SetQueryLimitCommand;
import uk.ac.kcl.cch.jb.sparql.commands.SetQueryOffsetCommand;
import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.ModifierComponent;
import uk.ac.kcl.cch.jb.sparql.model.SelectClauseModifier;
import uk.ac.kcl.cch.jb.sparql.parts.ModifierComponentPart;

public class ModifierComponentDirectEditPolicy extends DirectEditPolicy {

	private int type;
	private QueryCreationEditor myEditor;

	public ModifierComponentDirectEditPolicy(int type, QueryCreationEditor myEditor) {
		super();
		this.type = type;
		this.myEditor = myEditor;
	}
	@Override
	protected Command getDirectEditCommand(DirectEditRequest request) {
		String rslt = (String)request.getCellEditor().getValue();
		if(rslt == null)return null;
		if(rslt.isEmpty())rslt = "0";
		int rsltInt = 0;
		try {
			rsltInt = Integer.parseUnsignedInt(rslt);
		}catch (NumberFormatException e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", 
					"This value \""+rslt+"\" must be a positive integer number.");
			return null;
		}
		ModifierComponent myMC = ((ModifierComponentPart)getHost()).getModifierComponent();
		if(type == ModifierComponent.LIMIT_COMPONENT)return new SetQueryLimitCommand(myMC, rsltInt, myEditor);
		else if(type == ModifierComponent.OFFSET_COMPONENT) return new SetQueryOffsetCommand(myMC, rsltInt, myEditor);
		return null;
	}

	@Override
	protected void showCurrentEditValue(DirectEditRequest request) {
		String value = (String)request.getCellEditor().getValue();
		((Label)getHostFigure()).setText(value);
		getHostFigure().getUpdateManager().performUpdate();
	}

}
