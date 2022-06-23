package uk.ac.kcl.cch.jb.sparql.actions;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.actions.SelectAllAction;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;

public class GEFSelectAllAction extends SelectAllAction {

	private IWorkbenchPart part;

	/**
	 * constructor for the SelectAllAction.  Items on display
	 * are taken from information available from the part which
	 * is assumed to be a GEF GraphicalViewer, and the list of
	 * items is passed back to the Graphical Viewer for it to handle.
	 * @param part
	 */
	public GEFSelectAllAction(IWorkbenchPart part) {
		super(part);
		this.part = part;
	}
	
	public void updatePart(IWorkbenchPart part){
		this.part = part;
	}

	public void run() {
		GraphicalViewer viewer = (GraphicalViewer)part.getAdapter(GraphicalViewer.class);
		if (viewer != null){
			Set items = new HashSet();
			Iterator it = viewer.getContents().getChildren().iterator();
			while(it.hasNext()){
				Object item = it.next();
				items.add(item);
				if(item instanceof GraphicalEditPart){
					items.addAll(((GraphicalEditPart)item).getSourceConnections());
					items.addAll(((GraphicalEditPart)item).getTargetConnections());
				}
			}
			Vector allItems = new Vector(items);
			viewer.setSelection(new StructuredSelection(allItems));
		}
	}

}
