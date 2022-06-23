package uk.ac.kcl.cch.jb.sparql.dnd;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;

import uk.ac.kcl.cch.jb.sparql.figures.WhereComponentFigure;
import uk.ac.kcl.cch.jb.sparql.model.QueryWhereClause;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery;
import uk.ac.kcl.cch.jb.sparql.parts.QueryWhereClausePart;
import uk.ac.kcl.cch.jb.sparql.views.ClassesViewItem;

public class WhereClauseDropTargetListener extends AbstractTransferDropTargetListener {
	
	// inspired by Pliny's PlinyObjectTransferDropTargetListener     JB
	
	private ClassesViewItem sourceItem = null;
	private WhereClauseComponentFactory myFactory = null;
	// private SPARQLQuery query;
	private QueryWhereClause clause;
	
	public WhereClauseDropTargetListener(EditPartViewer viewer, QueryWhereClause clause) {
		super(viewer, TransferHandler.TRANSFER);
		this.clause = clause;
		myFactory = new WhereClauseComponentFactory(clause);
		
	}

	public boolean isEnabled(DropTargetEvent event){
		//System.out.println("isEnabled entered: "+(++enabledCount));
		//System.out.flush();
		if(!super.isEnabled(event))return false;
		boolean rslt = testCanDrop();
		return rslt;
	}

	private boolean testCanDrop() {
		updateTargetEditPart();
		Object target = getTargetEditPart();
		if(target == null)return false;
		if(!(target instanceof QueryWhereClausePart)) return false;
		Object source = TransferHandler.getCurrentObject();
		if(source == null) return false;
		if(source instanceof ClassesViewItem) {
			sourceItem = (ClassesViewItem)source;
			return true;
		}
		return false;
	}

	protected void handleDragOver() {
		   getCurrentEvent().detail = DND.DROP_COPY;
		   super.handleDragOver();
		}
	
	protected Request createTargetRequest() {
		CreateRequest request = new CreateRequest();
		request.setFactory(myFactory);
		request.setSize(WhereComponentFigure.DEFAULT_DIMENSION);
		return request;
	}


	@Override
	protected void updateTargetRequest() {
		Request theRequest = getTargetRequest();
		CreateRequest myRequest = (CreateRequest)theRequest;
		myRequest.setLocation(getDropLocation());
		myRequest.setSize(WhereComponentFigure.DEFAULT_DIMENSION);
	}
	
	public void drop(DropTargetEvent event) {
		Object source=getCurrentEvent().data;
		if(TransferHandler.TRANSFER.isSupportedType(event.currentDataType)){
			if(testCanDrop())
			  myFactory.setupObject((ClassesViewItem)event.data);
		}
		super.drop(event);
		TransferHandler.TRANSFER.setObject(null);
		TransferHandler.setCurrentObject(null);
	}

}
