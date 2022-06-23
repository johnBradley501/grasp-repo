package uk.ac.kcl.cch.jb.sparql.wizards;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import uk.ac.kcl.cch.jb.sparql.Activator;
import uk.ac.kcl.cch.jb.sparql.model.RDFServer;
import uk.ac.kcl.cch.jb.sparql.model.RDFServerList;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery;

public class NewSPARQLQueryFilePage extends NewSPARQLQueryBasePage {
	
	private static final String CONTAINER_PATH = "NewSPARQLQueryFilePage.containerPath";
	
	private ISelection selection;
	
	private Text containerText;
	private Text fileText;
	private String fileName = null;
	private String containerName = null;

	public NewSPARQLQueryFilePage(ISelection selection) {
		super("SPARQL Query Wizard Page");
		setTitle("SPARQL Query Wizard Page");
		setDescription("This wizard creates a new SPARQL Query file that can be opened by the SPARQL Query Editor.");
		this.selection = selection;
	}
	
	public String getContainerName() {
		return containerName;
	}

	public String getFileName() {
		return fileName;
	}

	@Override
	protected void buildControlTop(Composite container) {
		
		Label label = new Label(container, SWT.NULL);
		label.setText("&Container:");

		containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);
		if(Activator.getDefault().getPluginPreferences().contains(CONTAINER_PATH)) {
			containerText.setText(Activator.getDefault().getPluginPreferences().getString(CONTAINER_PATH));
		}
		containerText.addModifyListener(e -> dialogChanged());
		
		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}

		});
		
		label = new Label(container, SWT.NULL);
		label.setText("&File name:");
		
		fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		fileText.setLayoutData(gd);
		fileText.addModifyListener(e -> dialogChanged());
	}

	protected void dialogChanged() {
		containerName = containerText.getText();
		
		fileName = fileText.getText();
		copyTitleText();

		if (containerName.length() == 0) {
			updateStatus("File container must be specified");
			return;
		}
		
		IResource container = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(new Path(containerName));
		
		if (container == null
				|| (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			updateStatus("File container must exist");
			return;
		}
		if (!container.isAccessible()) {
			updateStatus("Container must be writable");
			return;
		}
		if (fileName.length() == 0) {
			updateStatus("File name must be specified");
			return;
		}
		if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
			updateStatus("File name must be valid");
			return;
		}
		int dotLoc = fileName.lastIndexOf('.');
		if (dotLoc != -1) {
			String ext = fileName.substring(dotLoc + 1);
			if (ext.equalsIgnoreCase(SPARQLQuery.FILE_EXTENSION) == false) {
				updateStatus("File extension must be \""+SPARQLQuery.FILE_EXTENSION+"\"");
				return;
			}
		}
		IContainer cont = (IContainer) container;
		final IFile file = cont.getFile(new Path(fileName));
		if(file.exists()) {
			updateStatus("File '"+fileName+"' already exists in "+containerName);
			return;
		}
		updateStatus(null);
	}


	protected void initialize() {
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
				return;
			Object obj = ssel.getFirstElement();
			if (obj instanceof IResource) {
				IContainer container;
				if (obj instanceof IContainer)
					container = (IContainer) obj;
				else
					container = ((IResource) obj).getParent();
				containerText.setText(container.getFullPath().toString());
			}
		}
		fileText.setText("new_file."+SPARQLQuery.FILE_EXTENSION);
	}

	private void handleBrowse() {
		Preferences prefs = Activator.getDefault().getPluginPreferences();
		IContainer initialPath = ResourcesPlugin.getWorkspace().getRoot();
		if(prefs.contains(CONTAINER_PATH)) {
			String containerPath = prefs.getString(CONTAINER_PATH);
			IContainer candidate = ResourcesPlugin.getWorkspace()
					.getRoot().getContainerForLocation(new Path(containerPath));
			if(candidate != null)initialPath = candidate;
		}
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				getShell(), initialPath, false,
				"Select new file container...");
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				String containerPath = ((Path) result[0]).toString();
				containerText.setText(containerPath);
				prefs.setValue(CONTAINER_PATH, containerPath);
			}
		}
	}

}
