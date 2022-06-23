package uk.ac.kcl.cch.jb.sparql.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import uk.ac.kcl.cch.jb.sparql.builder.ExpressionMaker;
import uk.ac.kcl.cch.jb.sparql.commands.UpdateDataConstraintTypeCommand;
import uk.ac.kcl.cch.jb.sparql.commands.UpdateDataConstraintValueCommand;
import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.figures.DataConstraintFigure;
import uk.ac.kcl.cch.jb.sparql.parts.DataConstraintPart;
import uk.ac.kcl.cch.jb.sparql.model.DataConstraint;

public class DataConstraintDirectEditPolicy extends DirectEditPolicy {

	private int currentField = -1;
	private QueryCreationEditor myEditor;
	
	public DataConstraintDirectEditPolicy(QueryCreationEditor myEditor) {
		super();
		this.myEditor = myEditor;
	}

	@Override
	protected Command getDirectEditCommand(DirectEditRequest request) {
		if(currentField == DataConstraintFigure.TYPE_FIELD) return getDirectEditTypeCommand(request);
		if(currentField == DataConstraintFigure.VAL_FIELD) return getDirectEditValCommand(request);
		return null;
	}

	private Command getDirectEditTypeCommand(DirectEditRequest request) {
		Integer rslt = (Integer)request.getCellEditor().getValue();
		if(rslt == null)return null;
		int chosenType = ((DataConstraintPart)getHost()).getChosenConstraintType(rslt);
		DataConstraint mydc = ((DataConstraintPart)getHost()).getDataConstraint();
		if(mydc.getConstraintType() == chosenType) return null;
		return new UpdateDataConstraintTypeCommand(mydc, chosenType, myEditor);
	}

	private Command getDirectEditValCommand(DirectEditRequest request) {
		String rslt = (String)request.getCellEditor().getValue();
		if(rslt == null)return null;
		DataConstraint mydc = ((DataConstraintPart)getHost()).getDataConstraint();
		if(rslt.equals(mydc.getConstraintValue()))return null;
		String dataType = ExpressionMaker.instance.getCheckType(mydc.getOwner().getDataType().getFragment());
		if(!typeChecked(rslt, dataType)) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", 
					"This value \""+rslt+"\" is not acceptable for this kind of data ("+dataType+").");
			return null;
		}
		return new UpdateDataConstraintValueCommand(mydc, rslt, myEditor);
	}

	private boolean typeChecked(String rslt, String dataType) {
		if(dataType.equals("int")) {
			try {
				int testval = Integer.parseInt(rslt.trim());
			} catch (NumberFormatException e) {
				return false;
			}
		} else if(dataType.equals("float")) {
			try {
				float testval = Float.parseFloat(rslt.trim());
			} catch (NumberFormatException e) {
						return false;
			}
		} else if(dataType.equals("bool")) {
			String test = rslt.trim().toLowerCase();
			if(!(test.equals("true"))||(test.equals("false"))) return false;
		}
		return true;
	}

	@Override
	protected void showCurrentEditValue(DirectEditRequest request) {
		if(currentField != DataConstraintFigure.VAL_FIELD) return;
		String value = (String)request.getCellEditor().getValue();
		((DataConstraintFigure)getHostFigure()).setValue(value);
		getHostFigure().getUpdateManager().performUpdate();
	}
	
	public void setField(int val) {currentField  = val;}

}
