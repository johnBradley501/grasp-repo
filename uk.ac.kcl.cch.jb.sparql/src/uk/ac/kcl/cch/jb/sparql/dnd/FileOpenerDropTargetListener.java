package uk.ac.kcl.cch.jb.sparql.dnd;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery;

public class FileOpenerDropTargetListener extends AbstractTransferDropTargetListener {

	// see https://www.eclipse.org/articles/Article-GEF-dnd/GEF-dnd.html
	
	private QueryCreationEditor myEditor;

	public FileOpenerDropTargetListener(GraphicalViewer viewer, QueryCreationEditor myEditor) {
		super(viewer, FileTransfer.getInstance());
		this.myEditor = myEditor;
	}
	
	protected void handleDragOver() {
		// needed to ensure file is not deleted by the opSys...
		   getCurrentEvent().detail = DND.DROP_COPY;
		   super.handleDragOver();
		}

	@Override
	protected void updateTargetRequest() {
		// nothing to do in here...   JB

	}
	
	private boolean testExtension(String fiemName) {
		int dotLoc = fiemName.lastIndexOf('.');
		if (dotLoc != -1) {
			String ext = fiemName.substring(dotLoc + 1);
			if (ext.equalsIgnoreCase(SPARQLQuery.FILE_EXTENSION)) return true;
		}
		return false;
	}
	
	public boolean isEnabled(DropTargetEvent event) {
		return super.isEnabled(event);
	}
	//public boolean isEnabled(DropTargetEvent event){
	//	FileTransfer.getInstance().
	//	if(event.data == null)return false;
	//	if(event.data instanceof String[]) {
	//		String[] items = (String [])event.data;
	//		for(String item: items) {
	//				if(testExtension(item)) return true;

	//		}
	//	}
	//	return false;
	//}
	
	protected void handleDrop() {
		String[] items = (String [])getCurrentEvent().data;
		IWorkbenchPage activePage = myEditor.getSite().getPage();
		Display.getCurrent().asyncExec(() -> {
			for(String item: items) {
				if(testExtension(item)) {
					File file = new File(item);
					// ExternalFileEditorInput efei = new ExternalFileEditorInput(file);
					IFileStore fileStore = EFS.getLocalFileSystem().fromLocalFile(file);
					IEditorInput input = new FileStoreEditorInput(fileStore);
					try {
						IDE.openEditor(activePage, input, QueryCreationEditor.EDITOR_ID);
					} catch (PartInitException e) {
					}
				}
			}
		});
	}

}
