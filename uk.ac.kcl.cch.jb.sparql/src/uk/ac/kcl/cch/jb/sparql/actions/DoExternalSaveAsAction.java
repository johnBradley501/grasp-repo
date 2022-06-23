package uk.ac.kcl.cch.jb.sparql.actions;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;

import uk.ac.kcl.cch.jb.sparql.Activator;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery;
import uk.ac.kcl.cch.jb.sparql.wizards.NewExternalQueryFilePage;

public class DoExternalSaveAsAction extends InputCreatingAction {

	private File oldFile;

	public DoExternalSaveAsAction(File oldFile) {
		super();
		this.oldFile = oldFile;
	}
	
	public void run() {
		Preferences prefs = Activator.getDefault().getPluginPreferences();
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		FileDialog dlg = new FileDialog(shell, SWT.SAVE);
		dlg.setText("Save As");
		//if(prefs.contains(NewExternalQueryFilePage.FOLDER_PATH)) {
		//	dlg.setFilterPath(prefs.getString(NewExternalQueryFilePage.FOLDER_PATH));
		//}
		dlg.setFilterNames(new String[] {"SPARQL Query File (*."+SPARQLQuery.FILE_EXTENSION+")", "All files (*.*)"});
		dlg.setFilterExtensions(new String[] {"*."+SPARQLQuery.FILE_EXTENSION, "*.*"});
		try {
			dlg.setFilterPath(oldFile.getCanonicalPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String newFileName = dlg.open();
		if(newFileName == null)return;
		File file = new File(newFileName);
		if(file.exists()) {
			if(!MessageDialog.openConfirm(shell, "File Exists", 
					"File '"+newFileName+"' already exists. Overwrite?"))return;
		}
		IFileStore fileStore = EFS.getLocalFileSystem().fromLocalFile(file);
		//ExternalFileEditorInput efei = new ExternalFileEditorInput(file);
		IEditorInput input = new FileStoreEditorInput(fileStore);
		this.setEditorInput(input);
	}

}
