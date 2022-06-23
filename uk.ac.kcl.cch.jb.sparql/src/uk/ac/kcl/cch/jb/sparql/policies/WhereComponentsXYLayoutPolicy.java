package uk.ac.kcl.cch.jb.sparql.policies;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

import uk.ac.kcl.cch.jb.sparql.commands.CreateClassComponentCommand;
import uk.ac.kcl.cch.jb.sparql.commands.MoveComponentCommand;
import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.ClassComponent;
import uk.ac.kcl.cch.jb.sparql.model.WhereClauseComponent;
;

public class WhereComponentsXYLayoutPolicy extends XYLayoutEditPolicy {
	
	private QueryCreationEditor myEditor;

	public WhereComponentsXYLayoutPolicy(QueryCreationEditor myEditor) {
		super();
		this.myEditor = myEditor;
	}

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		Object newObject = request.getNewObject();
		if(!(newObject instanceof ClassComponent))return null;
		ClassComponent newComponent = (ClassComponent)newObject;
		return new CreateClassComponentCommand(newComponent, myEditor, (Rectangle)getConstraintFor(request));
	}
	
	protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
		Rectangle newBounds = (Rectangle) constraint;
		return new MoveComponentCommand((WhereClauseComponent)child.getModel(), newBounds, myEditor);
	}

}
