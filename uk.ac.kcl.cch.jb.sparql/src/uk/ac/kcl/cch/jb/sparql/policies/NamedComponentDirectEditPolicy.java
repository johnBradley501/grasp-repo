package uk.ac.kcl.cch.jb.sparql.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.parts.ClassComponentPart;
import uk.ac.kcl.cch.jb.sparql.parts.NamedComponentPart;
import uk.ac.kcl.cch.jb.sparql.parts.WhereClauseComponentPart;
import uk.ac.kcl.cch.jb.sparql.commands.NameUpdateCommand;
import uk.ac.kcl.cch.jb.sparql.figures.VarNameFigure;

public class NamedComponentDirectEditPolicy extends DirectEditPolicy {
	
	private QueryCreationEditor myEditor;

	public NamedComponentDirectEditPolicy(QueryCreationEditor myEditor) {
		super();
		this.myEditor = myEditor;
	}

	@Override
	protected Command getDirectEditCommand(DirectEditRequest request) {
		String text = ((String)request.getCellEditor().getValue()).trim();
		if(text.length() == 0)return null;
		NamedComponentPart part = (NamedComponentPart)getHost();
		if(part.getNamedComponent().getName().equals(text)) return null;
		return new NameUpdateCommand(part.getNamedComponent(), text, myEditor);
	}

	@Override
	protected void showCurrentEditValue(DirectEditRequest request) {
		String value = (String)request.getCellEditor().getValue();
		((VarNameFigure)getHostFigure()).setName(value);
		getHostFigure().getUpdateManager().performUpdate();
	}

}
