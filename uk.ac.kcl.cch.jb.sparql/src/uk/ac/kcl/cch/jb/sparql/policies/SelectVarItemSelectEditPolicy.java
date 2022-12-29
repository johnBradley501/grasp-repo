package uk.ac.kcl.cch.jb.sparql.policies;

import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editpolicies.SelectionEditPolicy;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.parts.SelectVarItemPart;

public class SelectVarItemSelectEditPolicy extends SelectionEditPolicy {
	
	private QueryCreationEditor myEditor;
	private boolean hoveringHappening = false;

	public SelectVarItemSelectEditPolicy(QueryCreationEditor myEditor) {
		super();
		this.myEditor = myEditor;
	}

	@Override
	protected void hideSelection() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void showSelection() {
	}
	
	protected SelectVarItemPart getMyPart() {return (SelectVarItemPart)getHost();}
	
	public void showTargetFeedback(Request request) {
		// super.showTargetFeedback(request);
		if (RequestConstants.REQ_SELECTION_HOVER.equals(request.getType())) {
			getMyPart().handleHover(true);
			hoveringHappening = true;
		}
	}
	
	public void eraseTargetFeedback(Request request) {
		super.eraseTargetFeedback(request);
		if(hoveringHappening) {
			getMyPart().handleHover(false);
			hoveringHappening = false;
		}
	}

	

}
