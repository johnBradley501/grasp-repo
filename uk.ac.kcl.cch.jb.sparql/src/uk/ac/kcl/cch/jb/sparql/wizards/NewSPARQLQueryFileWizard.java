package uk.ac.kcl.cch.jb.sparql.wizards;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class NewSPARQLQueryFileWizard extends NewSPARQLQueryBaseWizard {
	
	// public final String MY_EXTENSION = "sqf";
	
	private ISelection selection;
	private NewSPARQLQueryFilePage page;
	
	public NewSPARQLQueryFileWizard() {
		super();
	}
	
	public void addPages() {
		page = new NewSPARQLQueryFilePage(selection);
		addPage(page);
	}


	@Override
	public boolean performFinish() {
		final String containerName = page.getContainerName();
		final String fileName = dealWithExtension(page.getFileName());
		
		IRunnableWithProgress op = monitor -> {
			try {
				//final String finalFileName = fileName;
				doFinish(containerName, fileName, monitor);
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

	private void doFinish(String containerName, String fileName, IProgressMonitor monitor) throws CoreException{
		monitor.beginTask("Creating " + fileName, 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Container \"" + containerName + "\" does not exist.",null);
		}
		IContainer container = (IContainer) resource;
		final IFile file = container.getFile(new Path(fileName));
		try {
			// https://docs.oracle.com/javase/7/docs/api/java/io/StringWriter.html
			if (file.exists()) {
				// file.setContents(stream, true, true, monitor);
				throwCoreException("File \""+file.getFullPath().toString()+"\" already exists.",null);
			} else {
				InputStream stream = buildContentStream(page);
				file.create(stream, true, monitor);
				stream.close();
			}
		} catch (IOException e) {
			throwCoreException("IO File error occurred for file \""+file.getFullPath().toString()+"\"",e);
		}
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(() -> {
			IWorkbenchPage page =
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			try {
				IDE.openEditor(page, file, true);
			} catch (PartInitException e) {
			}
		});
		monitor.worked(1);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}
