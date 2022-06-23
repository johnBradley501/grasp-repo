package uk.ac.kcl.cch.jb.sparql.dnd;

import org.eclipse.gef.dnd.SimpleObjectTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;

import uk.ac.kcl.cch.jb.sparql.views.ClassesViewItem;

public class ClassesViewDragListener  implements DragSourceListener{
	
	// similar code in Pliny's TypeManagerDragListener   JB
	

	private StructuredViewer viewer;

	public ClassesViewDragListener(StructuredViewer viewer) {
		this.viewer = viewer;
		DragSource source = new DragSource(viewer.getControl(), DND.DROP_COPY);
		source.setTransfer(new Transfer[] {TransferHandler.TRANSFER});
		source.addDragListener(this);
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		event.doit = true;
		IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
		if (selection.size() != 1){
			event.doit = false;
			return;
		}
		Object item = selection.getFirstElement();
		if(item instanceof ClassesViewItem) {
			TransferHandler.setCurrentObject(item);
			return;			
		}
		TransferHandler.setCurrentObject(null);
		event.doit = false;
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		if (TransferHandler.TRANSFER.isSupportedType(event.dataType)){
			IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
			if(selection.size() != 1)event.detail = DND.DROP_NONE;
			else event.data = selection.getFirstElement();
		}
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		TransferHandler.setCurrentObject(null);
	}

}
