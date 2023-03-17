package uk.ac.kcl.cch.jb.sparql.editors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

import uk.ac.kcl.cch.jb.sparql.actions.DoExternalSaveAsAction;
import uk.ac.kcl.cch.jb.sparql.actions.DoWorkSpaceSaveAsAction;
import uk.ac.kcl.cch.jb.sparql.actions.InputCreatingAction;
import uk.ac.kcl.cch.jb.sparql.commands.CanBeDirty;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery;
import uk.ac.kcl.cch.jb.sparql.parts.QueryWhereClausePart;
import uk.ac.kcl.cch.jb.sparql.parts.WhereClauseComponentPart;
import uk.ac.kcl.cch.jb.sparql.utils.CompleteGraphChecker;

public class QueryCreationEditor extends MultiPageEditorPart implements IResourceChangeListener, CanBeDirty { // PropertyChangeListener, 
	
	public static final String EDITOR_ID = "uk.ac.kcl.cch.jb.sparql.queryeditor";
	private static double[] bestPositionSin = null;
	private static double[] bestPositionCos = null;
	
	private SPARQLQuery query = null;
	private IFile workspaceQueryFile = null;
	private File externalQueryFile = null;
	
	private GEFComponentHandler gefHandler = null;
	private Control gefArea = null;
	private QueryEditorPartFactory myPartFactory = null;
	private CompleteGraphChecker graphChecker = null;
	
	private boolean isDirtyFlag = false;
	private TitleAreaManager titleAreaManager = null;
	
	public QueryCreationEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}
	
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		// if(query != null)query.removePropertyChangeListener(this);
		if(gefHandler != null)gefHandler.dispose();
		if(query != null)query.dispose();
		if(graphChecker != null)graphChecker.dispose();
		if(titleAreaManager != null)titleAreaManager.dispose();
		super.dispose();
	}

	public void init(IEditorSite site, IEditorInput editorInput)
			throws PartInitException {
		if (!((editorInput instanceof IFileEditorInput)||(editorInput instanceof IURIEditorInput)))
			throw new PartInitException("Invalid Input: Must be IFileEditorInput or IURIEditorInput");

		InputStream inputData = null;
		String partName = editorInput.getName();
		try {
			if(editorInput instanceof IFileEditorInput){
				IFileEditorInput fei = (IFileEditorInput)editorInput;
				workspaceQueryFile = fei.getFile();
				inputData = workspaceQueryFile.getContents();
			} else if(editorInput instanceof IURIEditorInput) {
				IURIEditorInput urii = (IURIEditorInput) editorInput;
				externalQueryFile = new File(urii.getURI());
				try {
					inputData = new FileInputStream(externalQueryFile);
				} catch (FileNotFoundException e) {
					throw new PartInitException("File not found: "+externalQueryFile.getAbsolutePath());
				}
			}
			query = SPARQLQuery.load(inputData);
		} catch (CoreException e) {
			throw new PartInitException("Opening of file failed: "+e.getLocalizedMessage());
		}
		if(query == null)
			throw new PartInitException("Invalid Input: The file is not in the SPARQL Query format.");

		super.init(site, editorInput);
		setPartName(partName);

		gefHandler = new QueryCreationEditorComponentHandler(query, this, EDITOR_ID);
		myPartFactory = new QueryEditorPartFactory();
		myPartFactory.setWorkbenchPart(this);
		gefHandler.setMyFactory(myPartFactory);
		
		graphChecker = new CompleteGraphChecker(query);
		titleAreaManager = new TitleAreaManager(this);
	}
	
	public CompleteGraphChecker getGraphChecker() {
		return graphChecker;
	}
	
	public IFile getWorkspaceQueryFile() {return workspaceQueryFile;}
	public File getExternalQueryFile() {return externalQueryFile;}
	
	public void doUpdateForSaveAs(IEditorInput editorInput) {
		if(editorInput == null)return;
		setPartName(editorInput.getName());
		if(editorInput instanceof IFileEditorInput){
			IFileEditorInput fei = (IFileEditorInput)editorInput;
			workspaceQueryFile = fei.getFile();
		} else if(editorInput instanceof IURIEditorInput) {
			IURIEditorInput urii = (IURIEditorInput) editorInput;
			externalQueryFile = new File(urii.getURI());
		}
		if(isDirtyFlag) {
			setDirtyFlag(false);
			fireDirtyEvent();
		}
		setInput(editorInput);
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		// TODO Auto-generated method stub

	}
	
	public SPARQLQuery getQuery() {
		return query;
	}
	
	
	public CommandStack getCommandStack(){
		return gefHandler.getEditDomain().getCommandStack();
	}
	
	public EditDomain getEditDomain(){
		return gefHandler.getEditDomain();
	}
	
	public GEFComponentHandler getGEFHandler() {
		return gefHandler;
	}
	
	public QueryWhereClausePart getQueryWhereClausePart() {
		if(myPartFactory == null)return null;
		return myPartFactory.getQueryWhereClausePart();
	}


	@Override
	protected void createPages() {
		createPage0();

	}
	
	public void setFocus() {
		query.getOntologyData().loadOntologyData();
		super.setFocus();
		
	}

	private void createPage0() {
		Composite composite = new Composite(getContainer(), SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		layout.numColumns = 1;
		
		
		// defineTitleArea(composite);
		titleAreaManager.defineTitleArea(composite);
		
		defineGEFArea(composite);
		
		int index = addPage(composite);
		setPageText(index, "Builder");
	}
	
	private void defineGEFArea(Composite composite) {
		gefArea = this.gefHandler.createGraphicalViewer(composite);
		gefArea.setLayoutData(new GridData(GridData.FILL_BOTH));
		
	}

	public Object getAdapter(Class adapter){
		if(adapter == CommandStack.class)
			return getCommandStack(); // needed in MinimizeAllAction
		                              // which is a WorkbenchPartAction
		if(adapter == ActionRegistry.class)
			return gefHandler.getActionRegistry();
		if(adapter == GraphicalViewer.class)
			return gefHandler.getGraphicalViewer();
		return super.getAdapter(adapter);
		
	}
	
	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return isDirtyFlag;
	}
	
	public void setDirtyFlag(boolean val) {
		isDirtyFlag = val;
	}
	
	public void markDirty() {
		setDirtyFlag(true);
	}
	
	public void fireDirtyEvent() {
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {

		InputStream rslt;
		try {
			rslt = query.buildContentStream();
			if(workspaceQueryFile != null) {
				if(workspaceQueryFile.exists()) {
					workspaceQueryFile.setContents(rslt, true, true, monitor);
				} else {
					workspaceQueryFile.create(rslt, true, monitor);
				}
			} else if(externalQueryFile != null) {
				// https://stackoverflow.com/questions/34266399/writing-an-inputstream-to-a-file-in-java
				// https://www.baeldung.com/convert-input-stream-to-a-file
				FileOutputStream fos = new FileOutputStream(externalQueryFile);
				IOUtils.copy(rslt, fos);
				fos.close();

			}
		} catch (CoreException | IOException e ) {
			e.printStackTrace();
		}
		isDirtyFlag = false;
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	@Override
	public void doSaveAs() {
		InputCreatingAction action = null;
		// boolean didDoSaveAs = false;
		if(workspaceQueryFile!=null) action = new DoWorkSpaceSaveAsAction(workspaceQueryFile);
		else if(externalQueryFile != null) action = new DoExternalSaveAsAction(externalQueryFile);
		if(action != null) {
			action.run();
			doUpdateForSaveAs(action.getEditorInput());
			doSave(null);
		}
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}
	
	public Rectangle findBestPosition(Rectangle centre, Dimension size, int dist) {
		/*
		 * This method is used by Commands that add a new component to the Where space, and tries to
		 * propose a location that reduces overlap on already present components.  At present, it tests out
		 * 8 places in a circle around the centre, and returns the one with the least overlap.  This is probably
		 * not an ideal algorithm for this.  It entirely ignores connecting lines, for example.  JB
		 */
		List eParts = gefHandler.getGraphicalViewer().getContents().getChildren();
		if(eParts.get(0) instanceof QueryWhereClausePart)eParts = ((EditPart)eParts.get(0)).getChildren();
		else eParts = ((EditPart)eParts.get(1)).getChildren();
		if(eParts.size() <= 1) {
			return new Rectangle(centre.x, centre.y+dist, size.width, size.height);
		}

		
		if(bestPositionSin == null) {
			double radianInc = Math.toRadians(45.0);
			bestPositionSin = new double[8];
			bestPositionCos = new double[8];
			double curRad = 2.0 * radianInc; // 0.0;
			for(int i = 0; i < 8; i++) {
				bestPositionSin[i] = Math.sin(curRad);
				bestPositionCos[i] = Math.cos(curRad);
				curRad = curRad + radianInc;
			}
		}
		
		int halfhorz = centre.width / 2;
		int halfvert = centre.height / 2;
		// Point centrePoint = new Point(centre.x+halfhorz, centre.y+halfvert);
		double actdist = (double)dist+halfhorz;
		double maskedHorizontal = (double)halfhorz+size.width/2;
		int newVert = size.height == -1 ? 36: size.height;
		double maskedVertical = (double)halfvert+newVert/2;
		double maskedMixed = (double)(maskedHorizontal+maskedVertical)/2.0;
		double[] maskedLength = new double[] { maskedHorizontal, maskedMixed, maskedVertical, maskedMixed, maskedHorizontal, maskedMixed, maskedVertical, maskedMixed};
		
		int smallestArea = 1000000;
		Rectangle bestMatch = null;
		for(int i = 0; i < 8; i++) {
			int proposedCentreX = halfhorz+centre.x + (int)(bestPositionSin[i]*(actdist+maskedLength[i]));
			int proposedCentreY = halfvert+centre.y - (int)(bestPositionCos[i]*(actdist+maskedLength[i]));
			Rectangle proposedRect = new Rectangle(
					proposedCentreX-size.width/2,
					proposedCentreY-size.height/2,
					size.width, size.height);
			if(proposedRect.x < 0)proposedRect.x = 0;
			if(proposedRect.y < 0)proposedRect.y = 0;
			Rectangle workingRect = new Rectangle(proposedRect);
			if(workingRect.height == -1)workingRect.height = 36;
			
			// Rectangle biggestOverlap = null;
			int biggestArea = -1;
			for(Object itm:eParts) {
				if(itm instanceof WhereClauseComponentPart) {
					WhereClauseComponentPart ep = (WhereClauseComponentPart)itm;
					Rectangle overlap = ep.getWhereClauseComponent().getMyBounds().getIntersection(workingRect);
					if(overlap.height * overlap.width > biggestArea) {
						// biggestOverlap = overlap;
						biggestArea = overlap.height * overlap.width;
					}
				}
			}
			if(biggestArea == 0)return proposedRect;
			if(biggestArea < smallestArea) {
				smallestArea = biggestArea;
				bestMatch = proposedRect;
			}
		}
		if(bestMatch == null)return new Rectangle(centre.x, centre.y+dist, size.width, size.height);
		return bestMatch;
	}

}
