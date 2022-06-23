package uk.ac.kcl.cch.jb.sparql.actions;

import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerGenerator;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.FileEditorInput;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;

public class DoWorkSpaceSaveAsAction extends InputCreatingAction {

	private IFile oldFile;

	// https://www.ibm.com/docs/en/rsar/9.5?topic=SS5JSH_9.5.0/org.eclipse.platform.doc.isv/reference/api/org/eclipse/ui/dialogs/SaveAsDialog.html
	// https://www.ibm.com/docs/api/v1/content/SS5JSH_9.5.0/org.eclipse.platform.doc.isv/reference/api/org/eclipse/ui/dialogs/ContainerGenerator.html
	
	public DoWorkSpaceSaveAsAction(IFile oldFile) {
		super();
		this.oldFile = oldFile;
	}
	
	public void run() {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		SaveAsDialog dialog = new SaveAsDialog(shell);
		dialog.setOriginalFile(oldFile);
		int drslt = dialog.open();
		if(drslt != Dialog.OK)return;
		IPath rslt = dialog.getResult();
		IPath oldPath = oldFile.getFullPath();
		if(oldPath.matchingFirstSegments(rslt) == oldPath.segmentCount()) {
			MessageDialog.openError(shell, "Save As", "Your chosen file is the file you are already editing.");
			return;
		}
		//File file = rslt.toFile();
		//if(file.exists()) {
		//	boolean answ = MessageDialog.openConfirm(shell, "Save As", "File already exists: Replace content?");
		//	if(!answ)return;
		//}
		
		// see https://www.tabnine.com/code/java/classes/org.eclipse.ui.dialogs.SaveAsDialog
		
		IFile rsltFile = ResourcesPlugin.getWorkspace().getRoot().getFile(rslt);
		//ContainerGenerator cg = new ContainerGenerator(rslt);
		//IFile rsltFile = null;
		//try {
		//	IContainer cont = cg.generateContainer(new NullProgressMonitor());
		//	System.out.println(cont);
			//rsltFile = (IFile)cg.generateContainer(new NullProgressMonitor());
		//} catch (CoreException e) {
		//	e.printStackTrace();
		//	return;
		//}
		this.setEditorInput(new FileEditorInput(rsltFile));
	}

}
