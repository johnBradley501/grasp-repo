package uk.ac.kcl.cch.jb.sparql.editors;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import uk.ac.kcl.cch.jb.sparql.Activator;
import uk.ac.kcl.cch.jb.sparql.actions.DoExternalSaveAsAction;
import uk.ac.kcl.cch.jb.sparql.actions.DoExternalSaveAsAction;
import uk.ac.kcl.cch.jb.sparql.actions.DoLayoutAction;
import uk.ac.kcl.cch.jb.sparql.actions.DoWorkSpaceSaveAsAction;
import uk.ac.kcl.cch.jb.sparql.actions.InputCreatingAction;
import uk.ac.kcl.cch.jb.sparql.actions.SendQueryToWebAction;
import uk.ac.kcl.cch.jb.sparql.builder.Builder;
import uk.ac.kcl.cch.jb.sparql.commands.CanBeDirty;
import uk.ac.kcl.cch.jb.sparql.commands.UpdateQueryMetadataCommand;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery.QueryMetadata;
import uk.ac.kcl.cch.jb.sparql.parts.QueryWhereClausePart;
import uk.ac.kcl.cch.jb.sparql.utils.CompleteGraphButtonHandler;
import uk.ac.kcl.cch.jb.sparql.utils.CompleteGraphChecker;

public class QueryCreationEditor extends MultiPageEditorPart implements IResourceChangeListener, PropertyChangeListener,  CanBeDirty {
	
	public static final String EDITOR_ID = "uk.ac.kcl.cch.jb.sparql.queryeditor";
	private static Color infoPanelBackground = new Color(null, 255, 255, 200);
	
	private static Image clipboardIcon = null;
	private static Image getClipboardIcon() {
		if(clipboardIcon == null)
			clipboardIcon = Activator.getImageDescriptor("icons/clipboard-16.png").createImage();
		return clipboardIcon;
		
	}
	private static Image browserIcon = null;
	private static Image getBrowserIcon() {
		if(browserIcon == null)
			browserIcon = Activator.getImageDescriptor("icons/browserIcon.gif").createImage();
		return browserIcon;
		
	}
	
	private SPARQLQuery query = null;
	private IFile workspaceQueryFile = null;
	private File externalQueryFile = null;
	private Label queryTitle = null;
	
	private GEFComponentHandler gefHandler = null;
	private Control gefArea = null;
	private QueryEditorPartFactory myPartFactory = null;
	private CompleteGraphChecker graphChecker = null;
	private CompleteGraphButtonHandler layoutButtonChecker = null;
	private CompleteGraphButtonHandler sendtoClipboardButtonChecker = null;
	private CompleteGraphButtonHandler sendtoWebButtonChecker = null;
	
	private boolean isDirtyFlag = false;
	private Label serverLabel;
	private Combo layoutCombo;
	private static final String[] layoutNames = {"Spring", "Radial", "Vert Tree", "Horiz Tree"};
	private static LayoutAlgorithm[] layoutAlgos = null;
	
	public QueryCreationEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}
	
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		if(query != null)query.removePropertyChangeListener(this);
		if(gefHandler != null)gefHandler.dispose();
		if(query != null)query.dispose();
		if(layoutButtonChecker != null)layoutButtonChecker.dispose();
		if(sendtoClipboardButtonChecker != null)sendtoClipboardButtonChecker.dispose();
		if(sendtoWebButtonChecker != null)sendtoWebButtonChecker.dispose();
		if(graphChecker != null)graphChecker.dispose();
		super.dispose();
	}

	public void init(IEditorSite site, IEditorInput editorInput)
			throws PartInitException {
		//if (!((editorInput instanceof IFileEditorInput)||(editorInput instanceof ExternalFileEditorInput)||(editorInput instanceof IURIEditorInput)))
		//	throw new PartInitException("Invalid Input: Must be IFileEditorInput, IURIEditorInput or ExternalFileEditorInput");
		if (!((editorInput instanceof IFileEditorInput)||(editorInput instanceof IURIEditorInput)))
			throw new PartInitException("Invalid Input: Must be IFileEditorInput or IURIEditorInput");

		InputStream inputData = null;
		String partName = editorInput.getName();
		try {
			if(editorInput instanceof IFileEditorInput){
				IFileEditorInput fei = (IFileEditorInput)editorInput;
				workspaceQueryFile = fei.getFile();
				inputData = workspaceQueryFile.getContents();
				//partName = workspaceQueryFile.getName();
			//} else if(editorInput instanceof ExternalFileEditorInput) {
			//	ExternalFileEditorInput efei = (ExternalFileEditorInput)editorInput;
			//	externalQueryFile = efei.getFile();
			//	try {
			//		inputData = new FileInputStream(externalQueryFile);
			//	} catch (FileNotFoundException e) {
			//		throw new PartInitException("File not found: "+efei.getFile().getAbsolutePath());
			//	}
			//	//partName = externalQueryFile.getName();
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
		query.addPropertyChangeListener(this);


		super.init(site, editorInput);
		setPartName(partName);

		gefHandler = new QueryCreationEditorComponentHandler(query, this, EDITOR_ID);
		myPartFactory = new QueryEditorPartFactory();
		myPartFactory.setWorkbenchPart(this);
		gefHandler.setMyFactory(myPartFactory);
		
		graphChecker = new CompleteGraphChecker(query);
	}
	
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
		
		
		defineTitleArea(composite);
		
		defineGEFArea(composite);
		
		int index = addPage(composite);
		setPageText(index, "Builder");
	}
	
	private void defineTitleArea(Composite composite) {
		// Composite infoPanel = new Composite(composite, SWT.NONE);
		Group infoPanel = new Group(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 5;
		infoPanel.setLayout(layout);
		infoPanel.setBackground(infoPanelBackground); // ColorConstants.lightBlue);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		infoPanel.setLayoutData(gd);
		
		Label label = new Label(infoPanel, SWT.NULL);
		label.setText("Title:");
		queryTitle = new Label(infoPanel, SWT.NULL);
		queryTitle.setText(query.getTitle());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		queryTitle.setLayoutData(gd);
		
		Button doLayoutButton = new Button(infoPanel, SWT.PUSH);
		doLayoutButton.setText("Do Layout");
		doLayoutButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				   handleDoLayoutButtonPush();
				}
			
		});
		layoutButtonChecker = new CompleteGraphButtonHandler(graphChecker, doLayoutButton);
		
		Button changeMetatdataButton = new Button(infoPanel, SWT.PUSH);
		changeMetatdataButton.setText("Change Metadata");
		gd = new GridData();
		gd.horizontalSpan = 2;
		changeMetatdataButton.setLayoutData(gd);
		changeMetatdataButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
			   handleChangeMetadataButtonPush();
			}
		});

		label = new Label(infoPanel, SWT.NULL);
		label.setText("For RDF Server:");
		serverLabel = new Label(infoPanel, SWT.NULL);
		serverLabel.setText(query.getServerName()+" <"+query.getEndpoint().toString()+">");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		//gd.horizontalSpan = 2;
		serverLabel.setLayoutData(gd);
		
		BuildLayoutDropdown(infoPanel);
		
		Button sendToClipboardButton = new Button(infoPanel, SWT.FLAT);
		sendToClipboardButton.setImage(getClipboardIcon());
		sendToClipboardButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		sendToClipboardButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				handleSendToClipboard();
			}

		});
		sendtoClipboardButtonChecker = new CompleteGraphButtonHandler(graphChecker, sendToClipboardButton);
		
		IWorkbenchPartSite site = this.getSite();
		Button sendToWebButton = new Button(infoPanel, SWT.FLAT);
		sendToWebButton.setImage(getBrowserIcon());
		sendToWebButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		sendToWebButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Action theAction = new SendQueryToWebAction(query, site);
				theAction.run();
			}
		});
		sendtoWebButtonChecker = new CompleteGraphButtonHandler(graphChecker, sendToWebButton);

	}
	
	private void BuildLayoutDropdown(Group infoPanel) {
		layoutCombo = new Combo(infoPanel, SWT.READ_ONLY);
		layoutCombo.setItems(layoutNames);
		layoutCombo.select(0);
		// layoutNames = {"Spring", "Radial", "Vert Tree", "Horiz Tree"};
		if(layoutAlgos == null) {
			layoutAlgos = new LayoutAlgorithm[layoutNames.length];
			layoutAlgos[0] = new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
			layoutAlgos[1] = new RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
			layoutAlgos[2] = new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
			layoutAlgos[3] = new HorizontalTreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
		}

	}
	
	public LayoutAlgorithm getSelectedLayout() {
		int i = layoutCombo.getSelectionIndex();
		if((i == -1) || (i >= layoutNames.length))return layoutAlgos[0];
		return layoutAlgos[i];
	}

	private void handleChangeMetadataButtonPush() {
		QueryMetadataDialog dlg = new QueryMetadataDialog(Display.getCurrent().getActiveShell(), getQuery());
		int rc = dlg.open();
		if(rc == Window.OK) {
			QueryMetadata newMD = dlg.getMetadata();
			if(!newMD.sameAs(getQuery().getQueryMetadata())) {
				getCommandStack().execute(new UpdateQueryMetadataCommand(this, getQuery(), newMD));
			}
		}
	}


	private void handleDoLayoutButtonPush() {
		DoLayoutAction action = new DoLayoutAction(this);
		action.run();
	}
	
	private void handleSendToClipboard() {
		Builder b = new Builder(getQuery());
		//System.out.println(b.toSPARQL());
		// see http://www.avajava.com/tutorials/lessons/how-do-i-copy-a-string-to-the-clipboard.html
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		StringSelection strSel = new StringSelection(b.toSPARQL());
		clipboard.setContents(strSel, null);
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
		boolean didDoSaveAs = false;
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

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		if(arg0.getPropertyName().equals(SPARQLQuery.METADATA_CHANGED)) {
			QueryMetadata meta = (QueryMetadata)arg0.getNewValue();
			queryTitle.setText(meta.title);
			serverLabel.setText(query.getServerName()+" <"+query.getEndpoint().toString()+">");
		}
		
	}

}
