package uk.ac.kcl.cch.jb.sparql.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import uk.ac.kcl.cch.jb.sparql.Activator;
import uk.ac.kcl.cch.jb.sparql.wizards.NewSPARQLQueryFileWizard;

public class CreateNewFileAction extends Action implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;

	public CreateNewFileAction() {
		super();
		setText("Create new SPARQL File...");
		setImageDescriptor(Activator.getImageDescriptor("icons/sw-cube-16.gif"));
	}
	
	@Override
	public void run(IAction action) {
		run();

	}
	
	public void run() {
		NewSPARQLQueryFileWizard wizard = new NewSPARQLQueryFileWizard();
		WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
		dialog.open();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// nothing to do here
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;

	}

}
