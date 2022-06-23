package uk.ac.kcl.cch.jb.sparql.editors;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.ui.IWorkbenchPart;

import uk.ac.kcl.cch.jb.sparql.dnd.FileOpenerDropTargetListener;
import uk.ac.kcl.cch.jb.sparql.dnd.WhereClauseDropTargetListener;

public class QueryCreationEditorComponentHandler extends GEFComponentHandler {

	private QueryCreationEditor myEditor;

	public QueryCreationEditorComponentHandler(Object rootObject, QueryCreationEditor myPart, String ownerID) {
		super(rootObject, myPart, ownerID);
		this.myEditor = myPart;
	}
	
	protected TransferDropTargetListener getMyDropTargetListener(EditPartViewer viewer){
		return new WhereClauseDropTargetListener(viewer, myEditor.getQuery().getWhereClause());
	}
	
	protected ContextMenuProvider getMyMenuProvider() {
		return new SPARQLContextMenuProvider(getGraphicalViewer(), getEditDomain().getCommandStack(), myEditor);
	}
	

	protected void doMoreSetup(GraphicalViewer graphicalViewer) {
		graphicalViewer.addDropTargetListener(new FileOpenerDropTargetListener(graphicalViewer, myEditor));
	}


}
