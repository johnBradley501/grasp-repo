package uk.ac.kcl.cch.jb.sparql.editors;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.gef.ui.actions.RedoAction;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.gef.ui.actions.UndoAction;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPageSite;

import uk.ac.kcl.cch.jb.sparql.actions.GEFSelectAllAction;

// see Pliny's ResourceAreaManager, from which this code was largely copied.   JB

public class GEFComponentHandler  implements CommandStackListener, ISelectionChangedListener{

	protected GraphicalViewer graphicalViewer = null;
	//private ISelectionChangedListener listener;
	private IWorkbenchPart myPart = null;
	private IPageSite myPageSite = null;
	private EditDomain editDomain = null;
	private ActionRegistry actionRegistry = null;
    private Object rootObject;
    private String ownerID;

	private ScrollingGraphicalViewer myScrollingGraphicalViewer = null;
	private RootEditPart myRootEditPart = null;
	private WorkbenchAwarePartFactory myEditPartFactory = null;
	private GraphicalViewerKeyHandler keyHandler = null;
	private ContextMenuProvider provider= null;  

    private UndoAction undoAction;
	private RedoAction redoAction;
	private DeleteAction deleteAction;
	private List<SelectionAction> selectionActions = new ArrayList<SelectionAction>();
	
    public GEFComponentHandler(Object rootObject, IWorkbenchPart myPart, String ownerID){
    	this.rootObject = rootObject;
    	this.myPart = myPart;
    	this.ownerID = ownerID;
    	createActions();
    	
    }
    
    public void dispose() {
    	Iterator<SelectionAction> it = selectionActions.iterator();
    	while(it.hasNext()){
    		it.next().dispose();
    	}
    	
    	if(graphicalViewer != null)graphicalViewer.removeSelectionChangedListener(this);
    	
    }
    
    public void setPageSite(IPageSite site){
    	myPageSite = site;
    }
	
	public void setRootObject(Object rootObject){
		this.rootObject = rootObject;
		if(graphicalViewer != null)graphicalViewer.setContents(rootObject);
	}
	
	public EditDomain getEditDomain(){
		if(editDomain == null)editDomain = new DefaultEditDomain(null);
		return editDomain;
	}

	public ActionRegistry getActionRegistry(){
		if(actionRegistry == null)actionRegistry = new ActionRegistry();
        return actionRegistry;			
	}
	
	public void setActionRegistry(ActionRegistry registry) {
		actionRegistry = registry;
	}

	public GraphicalViewer getGraphicalViewer(){
		return graphicalViewer;
	}
	
	public void addSelectionAction(SelectionAction action){
		getActionRegistry();
		selectionActions.add(action);
		actionRegistry.registerAction(action);
		action.setSelectionProvider(myPart.getSite().getSelectionProvider());
	}
    
    private void createActions(){
    	getActionRegistry();
		undoAction = new UndoAction(myPart);
		actionRegistry.registerAction(undoAction);
		redoAction = new RedoAction(myPart);
		actionRegistry.registerAction(redoAction);
		deleteAction = new DeleteAction(myPart);
		actionRegistry.registerAction(deleteAction);
		selectionActions.add(deleteAction);

	    Action selectAllAction = new GEFSelectAllAction(myPart);
	    actionRegistry.registerAction(selectAllAction);
		}

	public Control createGraphicalViewer(Composite parent){
		if(graphicalViewer == null)graphicalViewer = new ScrollingGraphicalViewer();
		graphicalViewer.createControl(parent);
		
		graphicalViewer.getControl().setBackground(ColorConstants.white);
		if(myRootEditPart == null)myRootEditPart = new ScalableRootEditPart();
		graphicalViewer.setRootEditPart(myRootEditPart);
		//graphicalViewer.setRootEditPart(new ScalableFreeformRootEditPart());
		graphicalViewer.addSelectionChangedListener(this);
		
		getEditDomain().addViewer(graphicalViewer);
		getEditDomain().getCommandStack().addCommandStackListener(this);
		
		handleSelectionProvider();
		
		//graphicalViewer.setEditPartFactory(new PlinyGEFEditFactory(myPart)); //IWorkbenchPart myPart
		graphicalViewer.setEditPartFactory(getMyFactory()); //IWorkbenchPart myPart
		if(rootObject != null)graphicalViewer.setContents(rootObject);
		
		keyHandler = new GraphicalViewerKeyHandler(graphicalViewer);
		graphicalViewer.setKeyHandler(keyHandler);
		
		provider = getMyMenuProvider();
		if(provider != null) {
			graphicalViewer.setContextMenu(provider);
			if(myPageSite != null)
				myPageSite.registerContextMenu(ownerID+".contextmenu", provider, graphicalViewer);
			else if(myPart != null)
				myPart.getSite().registerContextMenu(ownerID+".contextmenu", provider, graphicalViewer);
		}
		
		TransferDropTargetListener dropListener = getMyDropTargetListener(graphicalViewer);
		if(dropListener != null)graphicalViewer.addDropTargetListener(dropListener);
		dropListener = getMyTextDropTargetListener(graphicalViewer);
		if(dropListener != null)graphicalViewer.addDropTargetListener(dropListener);
		
		TransferDragSourceListener listener = getMyDragSourceListener(graphicalViewer);
		if(listener != null) graphicalViewer.addDragSourceListener(listener);
		listener = getMyDragTextListener(graphicalViewer);
		if(listener != null)graphicalViewer.addDragSourceListener(listener);
		
		doMoreSetup(graphicalViewer);

		return graphicalViewer.getControl();
	}

	protected void doMoreSetup(GraphicalViewer graphicalViewer) {
	}

	protected TransferDragSourceListener getMyDragTextListener(GraphicalViewer graphicalViewer2) {
		// TODO Auto-generated method stub
		return null; // new PlinyDragTextListener(graphicalViewer);
	}

	protected TransferDragSourceListener getMyDragSourceListener(GraphicalViewer graphicalViewer2) {
		return null; // new PlinyDragSourceListener(graphicalViewer);
	}

	// override to provide a GEF menu provider.
	protected ContextMenuProvider getMyMenuProvider() {
		return null; // new PlinyMenuProvider(myPart,getGraphicalViewer(), getEditDomain().getCommandStack());
	}

    
    public void setMyFactory(WorkbenchAwarePartFactory factory){
    	myEditPartFactory = factory;
    	if(myEditPartFactory != null)myEditPartFactory.setWorkbenchPart(myPart);
    }
    
	private EditPartFactory getMyFactory() {
		return myEditPartFactory;
	}

	protected void handleSelectionProvider(){
		if(myPageSite != null)myPageSite.setSelectionProvider(graphicalViewer);
		else if(myPart != null)
		   myPart.getSite().setSelectionProvider(graphicalViewer);
	}
	
	
	protected TransferDropTargetListener getMyDropTargetListener(EditPartViewer viewer){
		return null; // new PlinyObjectTransferDropTargetListener(viewer);
	}
	
	protected TransferDropTargetListener getMyTextDropTargetListener(EditPartViewer viewer){
		return null; // new PlinyTextTransferDropTargetListener(graphicalViewer);
	}



	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		updateCommandStackActions();
	}

	public void updateCommandStackActions(){
		Iterator it = selectionActions.iterator();
		while(it.hasNext()){
			SelectionAction action = (SelectionAction)it.next();
			action.update();
		}
	}

	@Override
	public void commandStackChanged(EventObject event) {
		undoAction.update();
		redoAction.update();
	}

}
