package uk.ac.kcl.cch.jb.sparql.editors;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery.QueryMetadata;

public class QueryMetadataDialog extends TitleAreaDialog {
	private SPARQLQuery query;
	private Text serverNameField = null;
	private Text titleField = null;
	private Text sparqlProcessorURIField = null;
	private Text sparqlBrowserURIField = null;
	private Text owlOntologyURIField = null;
	
	private URL sparqlProcessorURL = null;
	private URL sparqlBrowserURL = null;
	private URL owlOntologyURL = null;
	private QueryMetadata metadata;

	public QueryMetadataDialog(Shell parentShell, SPARQLQuery query) {
		super(parentShell);
		this.query = query;
		this.setHelpAvailable(false);
		metadata = query.getQueryMetadata();
	}
	
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		setTitle("Query Metadata Information");
		return contents;
	}
	
	private void setFieldStr(String value, Text field) {
		if (value == null) field.setText("");
		else field.setText(value);
	}
	
	private void setFieldURL(URL value, Text field) {
		if(value == null) field.setText("");
		else field.setText(value.toString());
	}
	
	public Control createDialogArea(Composite parent) {
		Composite comp = (Composite)super.createDialogArea(parent);
		Composite form = new Composite(comp, SWT.NONE);
		form.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 10;
		layout.marginWidth = 5;
		layout.numColumns = 2;
		form.setLayout(layout);
		
		Label titleLabel = new Label(form, SWT.RIGHT);
		titleLabel.setText("Query Title: ");
		
		titleField = new Text(form, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		titleField.setLayoutData(data);
		setFieldStr(metadata.title.trim(), titleField);
		
		
		Label serverNameLabel = new Label(form, SWT.RIGHT);
		serverNameLabel.setText("RDF Server Name: ");
		
		serverNameField = new Text(form, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		serverNameField.setLayoutData(data);
		setFieldStr(metadata.serverName.trim(), serverNameField);

		Label sparqlProcessorURILabel = new Label(form, SWT.RIGHT);
		sparqlProcessorURILabel.setText("SPARQL Server URI (data): ");
		
		sparqlProcessorURIField = new Text(form, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		sparqlProcessorURIField.setLayoutData(data);
		setFieldURL(metadata.endpoint,sparqlProcessorURIField);
		
		Label sparqlBrowserURILabel = new Label(form, SWT.RIGHT);
		sparqlBrowserURILabel.setText("SPARQL Server URI (browser): ");
		
		sparqlBrowserURIField = new Text(form, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		sparqlBrowserURIField.setLayoutData(data);
		setFieldURL(metadata.browserEndpoint,sparqlBrowserURIField);

		Label owlOntologyURILabel = new Label(form, SWT.RIGHT);
		owlOntologyURILabel.setText("OWL Ontology URI: ");
		
		owlOntologyURIField = new Text(form, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		owlOntologyURIField.setLayoutData(data);
		setFieldURL(metadata.ontologyURL, owlOntologyURIField);

		return comp;
		
	}
	
	private boolean validateFields() {
		if(serverNameField.getText().trim().length() == 0) {
			setErrorMessage("A name for the server must be given.");
			return false;
		}
		if(sparqlProcessorURIField.getText().trim().length() == 0) {
			setErrorMessage("A URL for the SPARQL endpoint (data) must be given.");
			return false;
		}
		if(sparqlBrowserURIField.getText().trim().length() == 0) {
			setErrorMessage("A URL for the SPARQL endpoint(browser) must be given.");
			return false;
		}
		if(owlOntologyURIField.getText().trim().length() == 0) {
			setErrorMessage("A URL for the OWL Ontology must be given.");
			return false;
		}
		try {
			sparqlProcessorURL = new URL(sparqlProcessorURIField.getText().trim());
		} catch (MalformedURLException e) {
			setErrorMessage("the SPARQL endpoint (data) URL is invalid.");
			return false;
		}
		try {
			sparqlBrowserURL = new URL(sparqlBrowserURIField.getText().trim());
		} catch (MalformedURLException e) {
			setErrorMessage("the SPARQL endpoint (browser) URL is invalid.");
			return false;
		}
		try {
			owlOntologyURL = new URL(owlOntologyURIField.getText().trim());
		} catch (MalformedURLException e) {
			setErrorMessage("the OWL Ontology URL is invalid.");
			return false;
		}
		return true;
	}
	
	public QueryMetadata getMetadata() {
		return metadata;
	}
	
	protected void okPressed() {
		boolean dataOK = validateFields();
		if(dataOK) {
			metadata = query.new QueryMetadata();
			metadata.title = titleField.getText().trim();
			metadata.serverName = serverNameField.getText().trim();
			metadata.endpoint = sparqlProcessorURL;
			metadata.browserEndpoint = sparqlBrowserURL;
			metadata.ontologyURL = owlOntologyURL;
			super.okPressed();
		}
	}

}
