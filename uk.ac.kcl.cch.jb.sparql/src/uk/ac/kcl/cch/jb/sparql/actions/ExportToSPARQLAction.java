package uk.ac.kcl.cch.jb.sparql.actions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringBufferInputStream;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;

import uk.ac.kcl.cch.jb.sparql.Activator;
import uk.ac.kcl.cch.jb.sparql.builder.Builder;
import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery;
import uk.ac.kcl.cch.jb.sparql.utils.ExportAsWorkspaceFileDialog;

public class ExportToSPARQLAction extends Action {
	
	private static Image myIcon = null;
	
	public static Image getMyIcon() {
		if(myIcon == null)
			myIcon = Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/export.gif").createImage();
		return myIcon;
	}

	private QueryCreationEditor myEditor;

	public ExportToSPARQLAction(QueryCreationEditor myEditor) {
		super();
		this.myEditor = myEditor;
		this.setToolTipText("Export to SPARQL");
		this.setImageDescriptor(ImageDescriptor.createFromImage(getMyIcon()));
	}
	
	public void run() {
		if(myEditor.getWorkspaceQueryFile() != null)runInWorkspaceContext();
		else runInExternalContext();
	}

	private void runInWorkspaceContext() {
		// see similar code in DoWorkSpaceSaveAsAction   JB
		IFile qFile = myEditor.getWorkspaceQueryFile();
		IPath qPath = qFile.getFullPath().removeFileExtension().addFileExtension("txt");
		IFile startFile = ResourcesPlugin.getWorkspace().getRoot().getFile(qPath);
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

		ExportAsWorkspaceFileDialog dialog = new ExportAsWorkspaceFileDialog(shell);
		// dialog.setTitle("Export as SPARQL");
		dialog.setOriginalFile(startFile);
		int drslt = dialog.open();
		if(drslt != Dialog.OK)return;
		IPath rslt = dialog.getResult();
		
		if(rslt.getFileExtension() == null)
			rslt = rslt.addFileExtension("txt");
		else if(rslt.getFileExtension().length() == 0)
			rslt = rslt.removeFileExtension().addFileExtension("txt");
		else if(!rslt.getFileExtension().equals("txt"))
			rslt = rslt.addFileExtension("txt");
		IFile rsltFile = ResourcesPlugin.getWorkspace().getRoot().getFile(rslt);
		
		Builder b = new Builder(myEditor.getQuery());
		StringBufferInputStream stream = new StringBufferInputStream(b.toSPARQL());
		try {
			if(rsltFile.exists()) {
				rsltFile.setContents(stream, true, true, new NullProgressMonitor());
			} else {
				rsltFile.create(stream, true, new NullProgressMonitor());
			}
		} catch (CoreException e ) {
			e.printStackTrace();
		}
	}

	private void runInExternalContext() {
		// see similar process in DoExternalSaveAsAction   JB
		Preferences prefs = Activator.getDefault().getPluginPreferences();
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		FileDialog dlg = new FileDialog(shell, SWT.SAVE);
		dlg.setText("Export as SPARQL");
		dlg.setFilterNames(new String[] {"SPARQL as Text File (*.txt)", "All files (*.*)"});
		dlg.setFilterExtensions(new String[] {"*.txt", "*.*"});
		String startName = null;
		try {
			startName = FilenameUtils.removeExtension(myEditor.getExternalQueryFile().getCanonicalPath())+".txt";
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(startName != null) {
			dlg.setFilterPath(startName);
			dlg.setFileName(FilenameUtils.getName(startName));
		}
		String newFileName = dlg.open();

		if(newFileName == null)return;
		File file = new File(newFileName);
		if(file.exists()) {
			if(!MessageDialog.openConfirm(shell, "File Exists", 
					"File '"+newFileName+"' already exists. Overwrite?"))return;
		}
		try {
			FileWriter fw = new FileWriter(file);
			Builder b = new Builder(myEditor.getQuery());
			fw.write(b.toSPARQL());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
