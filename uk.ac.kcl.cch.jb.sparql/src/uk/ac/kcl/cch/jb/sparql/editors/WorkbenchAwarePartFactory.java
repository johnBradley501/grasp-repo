package uk.ac.kcl.cch.jb.sparql.editors;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.ui.IWorkbenchPart;

public abstract class WorkbenchAwarePartFactory implements EditPartFactory {
	
	private IWorkbenchPart myPart;

	public WorkbenchAwarePartFactory() {
		this.myPart = null;
	}
	
	public void setWorkbenchPart(IWorkbenchPart part) {
		this.myPart = part;
	}
	
	protected IWorkbenchPart getWorkbenchPart() {
		return myPart;
	}

	@Override
	public abstract EditPart createEditPart(EditPart context, Object model);

}
