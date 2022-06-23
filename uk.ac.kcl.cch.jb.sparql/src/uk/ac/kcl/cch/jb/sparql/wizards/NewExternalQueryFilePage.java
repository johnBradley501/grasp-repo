package uk.ac.kcl.cch.jb.sparql.wizards;

import java.io.File;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import uk.ac.kcl.cch.jb.sparql.Activator;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery;

public class NewExternalQueryFilePage extends NewSPARQLQueryBasePage {
	
	public static final String FOLDER_PATH = "NewExternalQueryFilePage.folderPath";

	private ISelection selection;
	private Label folderLabel;
	private Label fileNameLabel;
	private String fileName = null;

	public NewExternalQueryFilePage(ISelection selection) {
		super("External SPARQL Query Wizard Page");
		setTitle("External SPARQL Query Wizard Page");
		setDescription("This wizard creates a new SPARQL Query file and opens it in the SPARQL Query Editor.");
		this.selection = selection;
	}

	@Override
	protected void buildControlTop(Composite container) {
		Label label = new Label(container, SWT.NULL);
		label.setText("&Folder:");
		
		folderLabel = new Label(container, SWT.LEFT);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		folderLabel.setLayoutData(gd);
		if(Activator.getDefault().getPluginPreferences().contains(FOLDER_PATH)) {
			folderLabel.setText(Activator.getDefault().getPluginPreferences().getString(FOLDER_PATH));
		}
		label = new Label(container, SWT.NULL);
		label.setText("&File name:");
		
		fileNameLabel = new Label(container, SWT.LEFT);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fileNameLabel.setLayoutData(gd);

		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}

		});
		

	}
	
	public String getFileName() {return fileName;}

	private void handleBrowse() {
		Preferences prefs = Activator.getDefault().getPluginPreferences();
		FileDialog dlg = new FileDialog(getShell(), SWT.SAVE);
		if(prefs.contains(FOLDER_PATH)) {
			dlg.setFilterPath(prefs.getString(FOLDER_PATH));
		}
		dlg.setFilterNames(new String[] {"SPARQL Query File (*."+SPARQLQuery.FILE_EXTENSION+")", "All files (*.*)"});
		dlg.setFilterExtensions(new String[] {"*."+SPARQLQuery.FILE_EXTENSION, "*.*"});
		dlg.setText("Create new SPARQL Query file...");
		
		fileName = dlg.open();
		if(fileName != null) {
			String filterPath = dlg.getFilterPath();
			folderLabel.setText(filterPath);
			prefs.setValue(FOLDER_PATH, filterPath);
			fileNameLabel.setText(dlg.getFileName());
		}
		dialogChanged();
	}

	@Override
	protected void initialize() {
		
		// do nothing here...   JB
	}

	@Override
	protected void dialogChanged() {
		copyTitleText();
		if(fileName == null) {
			updateStatus("Push the Browse button to specify a new file");
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
		File file = new File(fileName);
		if(file.exists()) {
			updateStatus("The file '"+fileName+"' already exists.");
			return;
		}
		updateStatus(null);
	}

}
