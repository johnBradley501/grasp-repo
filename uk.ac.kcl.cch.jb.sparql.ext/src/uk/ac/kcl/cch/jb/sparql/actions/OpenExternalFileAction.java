package uk.ac.kcl.cch.jb.sparql.actions;

import java.io.File;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;

import uk.ac.kcl.cch.jb.sparql.Activator;
import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery;
import uk.ac.kcl.cch.jb.sparql.wizards.NewExternalQueryFilePage;

public class OpenExternalFileAction extends Action implements IWorkbenchWindowActionDelegate {
	
	private IWorkbenchWindow window;
	private String fileName = null;

	public OpenExternalFileAction() {
		super();
		this.setText("Open external SPARQL Query File");
		this.setToolTipText("Open external SPARQL Query File");
	}
	
	public OpenExternalFileAction(String filePath) {
		super();
		this.fileName  = filePath;
	}
	
	public void run() {
		if(fileName == null) { 
			Preferences prefs = Activator.getDefault().getPluginPreferences();
			FileDialog dlg = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
			if(prefs.contains(NewExternalQueryFilePage.FOLDER_PATH)) {
				dlg.setFilterPath(prefs.getString(NewExternalQueryFilePage.FOLDER_PATH));
			}
			dlg.setFilterNames(new String[] {"SPARQL Query File (*."+SPARQLQuery.FILE_EXTENSION+")", "All files (*.*)"});
			dlg.setFilterExtensions(new String[] {"*."+SPARQLQuery.FILE_EXTENSION, "*.*"});
			dlg.setText("Open");

			fileName = dlg.open();
			if(fileName == null) return;
		}
		File file = new File(fileName);
		fileName = null;
		IFileStore fileStore = EFS.getLocalFileSystem().fromLocalFile(file);
		//ExternalFileEditorInput efei = new ExternalFileEditorInput(file);
		IEditorInput input = new FileStoreEditorInput(fileStore);
		Display.getCurrent().asyncExec(() -> {
			//IWorkbenchPage page =
			//		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IWorkbenchPage page = window.getActivePage();
			try {
				IDE.openEditor(page, input, QueryCreationEditor.EDITOR_ID);
			} catch (PartInitException e) {
			}
		});
	}

	@Override
	public void run(IAction action) {
		run();
		
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// nothing needed here   JB
	}

	@Override
	public void dispose() {
		// nothing needed here   JB
	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

}
