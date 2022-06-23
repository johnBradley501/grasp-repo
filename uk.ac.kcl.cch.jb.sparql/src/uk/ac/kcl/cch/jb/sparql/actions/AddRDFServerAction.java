package uk.ac.kcl.cch.jb.sparql.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;

import uk.ac.kcl.cch.jb.sparql.Activator;
import uk.ac.kcl.cch.jb.sparql.model.RDFServer;
import uk.ac.kcl.cch.jb.sparql.model.RDFServerList;
import uk.ac.kcl.cch.jb.sparql.views.RDFServerEditorDialog;

public class AddRDFServerAction extends Action {

	private TableViewer viewer;

	public AddRDFServerAction(TableViewer viewer) {
		super("Add new RDF Server");
		this.viewer = viewer;
		this.setToolTipText("Add new RDF Server");
		this.setImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/add-button.png"));
	}
	
	public void run() {
		RDFServer newServer = new RDFServer("");
		// InputDialog dlg = new InputDialog(viewer.getControl().getShell(),"RDF Server", "Specify server name", "", null);
		RDFServerEditorDialog dlg = new RDFServerEditorDialog(viewer.getControl().getShell(),newServer);
		int rc = dlg.open();
		if(rc == Window.OK) {
			String newName = newServer.getName();
			if(newName != null && newName.length() > 0) {
				// RDFServer newServer = new RDFServer(newName);
				RDFServerList servers = RDFServerList.getList();
				servers.add(newServer);

			}
		}
	}
}
