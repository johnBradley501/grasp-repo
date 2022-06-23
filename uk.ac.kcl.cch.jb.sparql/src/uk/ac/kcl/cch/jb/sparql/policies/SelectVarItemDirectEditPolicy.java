package uk.ac.kcl.cch.jb.sparql.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;

import uk.ac.kcl.cch.jb.sparql.commands.ItemOrderUpdateCommand;
import uk.ac.kcl.cch.jb.sparql.commands.NameUpdateCommand;
import uk.ac.kcl.cch.jb.sparql.commands.UpdateAggNameCommand;
import uk.ac.kcl.cch.jb.sparql.commands.UpdateAggregateTypeCommand;
import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.figures.VarNameFigure;
import uk.ac.kcl.cch.jb.sparql.model.SelectVarItem;
import uk.ac.kcl.cch.jb.sparql.parts.NamedComponentPart;
import uk.ac.kcl.cch.jb.sparql.parts.SelectVarItemPart;

public class SelectVarItemDirectEditPolicy extends DirectEditPolicy {
	
	private QueryCreationEditor myEditor;
	private int currentField = -1;

	public SelectVarItemDirectEditPolicy(QueryCreationEditor myEditor) {
		super();
		this.myEditor = myEditor;
	}

	@Override
	protected Command getDirectEditCommand(DirectEditRequest request) {
		if(currentField == SelectVarItem.VAR_COMPONENT) return getVarComponentCommand(request);
		if(currentField == SelectVarItem.ORDER_COMPONENT) return getOrderComponentCommand(request);
		if(currentField == SelectVarItem.AGGREGATE_COMPONENT) return getAggregateComponentCommand(request);
		if(currentField == SelectVarItem.AGG_NAME_COMPONENT) return getAggregateNameComponentCommand(request);
		return null;
	}

	private Command getVarComponentCommand(DirectEditRequest request) {
		String text = (String)request.getCellEditor().getValue();
		NamedComponentPart part = (NamedComponentPart)getHost();
		return new NameUpdateCommand(part.getNamedComponent(), text, myEditor);
	}
	
	private Command getOrderComponentCommand(DirectEditRequest request) {
		Integer rslt = (Integer)request.getCellEditor().getValue();
		if(rslt == null)return null;
		SelectVarItemPart part = (SelectVarItemPart)getHost();
		if(rslt.intValue() == part.getVarItem().getSortOrder())return null;
		return new ItemOrderUpdateCommand(part.getVarItem(), rslt.intValue(), myEditor);
	}

	private Command getAggregateComponentCommand(DirectEditRequest request) {
		Integer rslt = (Integer)request.getCellEditor().getValue();
		if(rslt == null)return null;
		SelectVarItemPart part = (SelectVarItemPart)getHost();
		SelectVarItem myItem = part.getVarItem();
		int chosenOne = part.getChosenAggType(rslt);
		if(myItem.getAggType() == chosenOne)return null;
		return new UpdateAggregateTypeCommand(myItem, chosenOne, myEditor);
	}

	private Command getAggregateNameComponentCommand(DirectEditRequest request) {
		String text = ((String)request.getCellEditor().getValue()).trim();
		SelectVarItemPart part = (SelectVarItemPart)getHost();
		SelectVarItem myItem = part.getVarItem();
		if(text.length() == 0)text = null;
		return new UpdateAggNameCommand(myItem, text, myEditor);
	}

	@Override
	protected void showCurrentEditValue(DirectEditRequest request) {
		if(currentField == SelectVarItem.VAR_COMPONENT) showCurrentVarNameValue(request);
		// nothing needs to be done for SelectVarItem.ORDER_COMPONENT

	}
	
	private void showCurrentVarNameValue(DirectEditRequest request) {
		String value = (String)request.getCellEditor().getValue();
		((VarNameFigure)getHostFigure()).setName(value);
		getHostFigure().getUpdateManager().performUpdate();
	}

	public void setField(int val) {currentField = val;}

}
