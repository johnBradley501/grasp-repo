package uk.ac.kcl.cch.jb.sparql.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;

public class OpenServersViewAction implements IWorkbenchWindowActionDelegate {

	private static final String THE_VIEW_ID =
			"uk.ac.kcl.cch.jb.sparql.views.RDFServersView";
		
	private IWorkbenchWindow window;

	@Override
	public void run(IAction action) {
		// Get the active page
		if(window == null)return;
		IWorkbenchPage page = window.getActivePage();
		if(page == null)return;
		
		// open and activite the RDF Server view.
		try {
			page.showView(THE_VIEW_ID);
		}
		catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// not needed.   j.b.
	}

	@Override
	public void dispose() {
		// not needed.   j.b.

	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

}
