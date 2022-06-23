package uk.ac.kcl.cch.jb.sparql.policies;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.OrderedLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.DropRequest;

import uk.ac.kcl.cch.jb.sparql.commands.AddVarItemCommand;
import uk.ac.kcl.cch.jb.sparql.commands.MoveVarItemCommand;
import uk.ac.kcl.cch.jb.sparql.model.INamedComponent;
import uk.ac.kcl.cch.jb.sparql.model.SelectVarItem;
import uk.ac.kcl.cch.jb.sparql.parts.QuerySelectClausePart;
import uk.ac.kcl.cch.jb.sparql.parts.SelectClauseModifierPart;
import uk.ac.kcl.cch.jb.sparql.parts.SelectVarItemPart;

public class SelectClauseLayoutPolicy extends OrderedLayoutEditPolicy {
	
	// modelled after organiser's BlockListLayoutPolicy  jb
	
	private QuerySelectClausePart myPart;

	public SelectClauseLayoutPolicy(QuerySelectClausePart myPart) {
		this.myPart = myPart;
	}

	@Override
	protected Command createAddCommand(EditPart child, EditPart after) {
		Object childModel = child.getModel();
		if(!((childModel instanceof INamedComponent) || (childModel instanceof SelectVarItem)))return null;
		SelectVarItem afterItem = null;
		if(after != null && (after instanceof SelectVarItemPart))
			afterItem = ((SelectVarItemPart)after).getVarItem();
		if(childModel instanceof INamedComponent) {
			return new AddVarItemCommand((INamedComponent)childModel, afterItem, myPart.getMyEditor());
		}
		if(childModel == afterItem) return null;
		return new MoveVarItemCommand((SelectVarItem)childModel, afterItem, myPart.getMyEditor());
	}

	@Override
	protected Command createMoveChildCommand(EditPart child, EditPart after) {
		if(child==after || myPart.getChildren().size() == 1)return null;
		int index = myPart.getChildren().indexOf(child);
		if(index == 0) {
			if (after == null)return null;
		}
		SelectVarItem afterItem = null;
		if(after != null && (after instanceof SelectVarItemPart))
			afterItem = ((SelectVarItemPart)after).getVarItem();
		return new MoveVarItemCommand((SelectVarItem)child.getModel(), afterItem, myPart.getMyEditor());
	}

	@Override
	protected EditPart getInsertionReference(Request request) {
		if(request == null)return null;
		if(((DropRequest)request).getLocation() == null)return null;
		int x = ((DropRequest)request).getLocation().x;
		List items = myPart.getChildren();
		AbstractGraphicalEditPart afterItem = null;
		for(Object obj:items) {
			AbstractGraphicalEditPart part = (AbstractGraphicalEditPart)obj;
			if(x < part.getFigure().getBounds().x) {
				return afterItem;
			}
			afterItem = part;
		}
		return afterItem;
	}

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

}
