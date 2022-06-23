package uk.ac.kcl.cch.jb.sparql.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;

import uk.ac.kcl.cch.jb.sparql.model.RDFServer;
import uk.ac.kcl.cch.jb.sparql.model.RDFServerList;
import uk.ac.kcl.cch.jb.sparql.views.RDFServerEditorDialog;

public class EditRDFServerAction extends Action {

	private TableViewer viewer;

	public EditRDFServerAction(TableViewer viewer) {
		super("Edit RDF Server");
		this.viewer = viewer;
		this.setToolTipText("Edit selected RDF Server");
	}
	
	public void run() {
		IStructuredSelection selection = viewer.getStructuredSelection();
		Object obj = selection.getFirstElement();
		if(!(obj instanceof RDFServer)) return;
		RDFServer server = (RDFServer)obj;
		String oldName = server.getName();
		RDFServerEditorDialog dlg = new RDFServerEditorDialog(viewer.getControl().getShell(),server);
		int rc = dlg.open();
		if(rc == Window.OK) {
			if(!oldName.equals(server.getName().trim())) {
				RDFServerList servers = RDFServerList.getList();
			 	servers.sort();
			}
		}
	}

}
