package uk.ac.kcl.cch.jb.sparql.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;

import uk.ac.kcl.cch.jb.sparql.model.RDFServer;
import uk.ac.kcl.cch.jb.sparql.model.RDFServerList;

public class DeleteRDFServerAction extends Action {

	private TableViewer viewer;

	public DeleteRDFServerAction(TableViewer viewer) {
		super("Delete RDF Server");
		this.viewer = viewer;
	}
	
	public void run() {
		IStructuredSelection selection = viewer.getStructuredSelection();
		Object obj = selection.getFirstElement();
		if(obj == null || (!(obj instanceof RDFServer))) return;
		RDFServer server = (RDFServer)obj;
		boolean b = MessageDialog.openQuestion(viewer.getControl().getShell(), "Confirm Deletion", "Are you sure you want to delete '"+server.getName()+"'?");
		if(b) {
			RDFServerList servers = RDFServerList.getList();
			servers.delete(server);
		}
	}
}
