package uk.ac.kcl.cch.jb.sparql.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import uk.ac.kcl.cch.jb.sparql.Activator;
import uk.ac.kcl.cch.jb.sparql.wizards.NewExternalQueryFileWizard;
import uk.ac.kcl.cch.jb.sparql.wizards.NewSPARQLQueryFileWizard;

public class CreateNewExtFileAction extends Action implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;

	public CreateNewExtFileAction() {
		super();
		setText("Create new External SPARQL File...");
		setImageDescriptor(Activator.getImageDescriptor("icons/sw-cube-ext.gif"));
	}
	@Override
	public void run(IAction action) {
		run();

	}
	
	public void run() {
		NewExternalQueryFileWizard wizard = new NewExternalQueryFileWizard();
		WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
		dialog.open();
	}


	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// nothing to do here
	}

	@Override
	public void dispose() {
		// nothing to do here
	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

}
