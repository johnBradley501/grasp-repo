package uk.ac.kcl.cch.jb.sparql.utils;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SaveAsDialog;

public class ExportAsWorkspaceFileDialog extends SaveAsDialog {
	
	private static final String ExportTitle = "Export to SPARQL";
	private static final String ExportMessage = "Use this dialogue to export the current Query to SPARQL";

	public ExportAsWorkspaceFileDialog(Shell parentShell) {
		super(parentShell);
	}
	
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(ExportTitle);
	}
	
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		setTitle(ExportTitle);
		setMessage(ExportMessage);
		return contents;
	}

}
