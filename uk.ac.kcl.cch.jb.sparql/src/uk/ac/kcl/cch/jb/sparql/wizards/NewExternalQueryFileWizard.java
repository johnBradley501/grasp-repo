package uk.ac.kcl.cch.jb.sparql.wizards;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;

public class NewExternalQueryFileWizard extends NewSPARQLQueryFileWizard {

	private ISelection selection;
	private NewExternalQueryFilePage page;
	
	public NewExternalQueryFileWizard() {
		super();
	}
	
	public void addPages() {
		page = new NewExternalQueryFilePage(selection);
		addPage(page);
	}
	
	public boolean performFinish() {
		final String fileName = dealWithExtension(page.getFileName());
		
		IRunnableWithProgress op = monitor -> {
			try {
				//final String finalFileName = fileName;
				doFinish(fileName, monitor);
			} catch (CoreException e) {
				throw new InvocationTargetException(e);
			} finally {
				monitor.done();
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}

	private void doFinish(String fileName, IProgressMonitor monitor) throws CoreException {
		monitor.beginTask("Creating " + fileName, 2);
		File file = new File(fileName);
		if(file.exists()) {
			throwCoreException("File \""+file.getAbsolutePath().toString()+"\" already exists.",null);
		}
		ByteArrayInputStream stream = buildContentStream(page);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			IOUtils.copy(stream, fos);
			//byte[] buffer;
			//stream.read(buffer, 0, 100000);
			//fos.write(buffer);
			fos.close();
		} catch (IOException e) {
			throwCoreException("Creation of file \""+file.getAbsolutePath().toString()+"\" failed.",e);
		}
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		IFileStore fileStore = EFS.getLocalFileSystem().fromLocalFile(file);
		IEditorInput input = new FileStoreEditorInput(fileStore);
		// ExternalFileEditorInput efei = new ExternalFileEditorInput(file);
		getShell().getDisplay().asyncExec(() -> {
			IWorkbenchPage page =
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			try {
				IDE.openEditor(page, input, QueryCreationEditor.EDITOR_ID);
			} catch (PartInitException e) {
			}
		});
		monitor.worked(1);
	}

}
