package uk.ac.kcl.cch.jb.sparql.views;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;

import uk.ac.kcl.cch.jb.sparql.Activator;
import uk.ac.kcl.cch.jb.sparql.actions.AddRDFServerAction;
import uk.ac.kcl.cch.jb.sparql.actions.DeleteRDFServerAction;
import uk.ac.kcl.cch.jb.sparql.actions.EditRDFServerAction;
import uk.ac.kcl.cch.jb.sparql.model.RDFServer;
import uk.ac.kcl.cch.jb.sparql.model.RDFServerList;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
// import javax.inject.Inject;


/**
 * 
 */

public class RDFServersView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "uk.ac.kcl.cch.jb.sparql.views.RDFServersView";

	// @Inject IWorkbench workbench;
	
	private TableViewer viewer;
	private Action addNewServerAction;
	//private Action action2;
	//private Action doubleClickAction;
	private Action editServerAction = null;
	private DeleteRDFServerAction deleteServerAction = null;

	private RDFServersViewManager viewManager = null;
	private RDFServerList servers = null;
	
	private static Image rdfServerIcon = null;
	private static Image getRDFServerIcon() {
		if(rdfServerIcon == null) rdfServerIcon = Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/rdf-server-16.gif").createImage();
		return rdfServerIcon;
	}
	 

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		@Override
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		@Override
		public Image getImage(Object obj) {
			// return workbench.getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
			return getRDFServerIcon();
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL); // plus SWT.MULTI | 
		
		servers = RDFServerList.getList();
		//servers.add(new RDFServer("One"));
		//servers.add(new RDFServer("Two"));
		//servers.add(new RDFServer("Three"));
		
		viewManager  = new RDFServersViewManager(viewer);
		
		viewer.setContentProvider(new RDFServersContentProvider(viewer));
		viewer.setInput(servers);
		viewer.setLabelProvider(new ViewLabelProvider());

		// Create the help context id for the viewer's control
		// workbench.getHelpSystem().setHelp(viewer.getControl(), "uk.ac.kcl.cch.jb.sparql.viewer");
		getSite().setSelectionProvider(viewer);
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}
	
	public void dispose() {
		viewManager.dispose();
		servers.save();
		super.dispose();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				RDFServersView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
		bars.setGlobalActionHandler(ActionFactory.DELETE.getId(), deleteServerAction);
	}

	private void fillLocalPullDown(IMenuManager manager) {
		//manager.add(action1);
		//manager.add(new Separator());
		//manager.add(action2);
		manager.add(addNewServerAction);
	}

	private void fillContextMenu(IMenuManager manager) {
		//manager.add(action1);
		manager.add(addNewServerAction);
		IStructuredSelection sel = (IStructuredSelection)viewer.getSelection();
		// System.out.println("selection: "+sel);
		if(sel.size() == 1) {
			manager.add(editServerAction);
		}
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(addNewServerAction);
		// manager.add(action2);
	}

	private void makeActions() {
		addNewServerAction = new AddRDFServerAction(viewer);
		editServerAction = new EditRDFServerAction(viewer);
		deleteServerAction = new DeleteRDFServerAction(viewer);
		
		//action2 = new Action() {
		//	public void run() {
		//		showMessage("Action 2 executed");
		//	}
		//};
		//action2.setText("Action 2");
		//action2.setToolTipText("Action 2 tooltip");
		//action2.setImageDescriptor(workbench.getSharedImages().
		//		getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		// doubleClickAction = new Action() {
		//	public void run() {
		//		IStructuredSelection selection = viewer.getStructuredSelection();
		//		Object obj = selection.getFirstElement();
		//		showMessage("Double-click detected on "+obj.toString());
		//	}
		//};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				editServerAction.run();
			}
		});
	}
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"RDF Source View",
			message);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
