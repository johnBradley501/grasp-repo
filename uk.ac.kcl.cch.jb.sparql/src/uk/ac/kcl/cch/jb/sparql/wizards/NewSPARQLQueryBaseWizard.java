package uk.ac.kcl.cch.jb.sparql.wizards;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.kcl.cch.jb.sparql.model.RDFServer;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery;

public abstract class NewSPARQLQueryBaseWizard extends Wizard implements INewWizard {
	
	public NewSPARQLQueryBaseWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	
	protected String dealWithExtension(String fileName) {
		// see https://www.baeldung.com/java-file-extensio
		if(!fileName.contains("."))
			fileName = fileName+"."+SPARQLQuery.FILE_EXTENSION;
		else {
			String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
			if(ext.length() == 0) fileName = fileName+SPARQLQuery.FILE_EXTENSION;
			else if (!ext.toLowerCase().equals(SPARQLQuery.FILE_EXTENSION))
				fileName = fileName+"."+SPARQLQuery.FILE_EXTENSION;
		}
		return fileName;
	}
	
	protected ByteArrayInputStream buildContentStream(NewSPARQLQueryBasePage page) throws CoreException{
		// String fileName = page.getFileName();
		String titleText = page.getTitleText();
		if(titleText == null)titleText = "";
		RDFServer server = page.getRDFServer();

		StringWriter w = new StringWriter();

		JSONObject contents = new JSONObject();
		JSONObject metadata = new JSONObject();
		JSONObject query = new JSONObject();
		try {
			contents.put("metadata", metadata);
			contents.put("query", query); // deliberatively left empty
			
			metadata.put("title", titleText);
			metadata.put("serverName", server.getName());
			metadata.put("endpoint", server.getSparqlEngine());
			metadata.put("browserEndpoint", server.getSparqlBrowser());
			metadata.put("ontology", server.getOntologyURL());
			contents.write(w);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throwCoreException("Unexpected failure of JSON handling.",e);
		}
		
		return new ByteArrayInputStream(w.toString().getBytes());
	}


	protected void throwCoreException(String message, Exception e) throws CoreException {
		IStatus status =
			new Status(IStatus.ERROR, "uk.ac.kcl.cch.jb.sparql", IStatus.OK, message, e);
		throw new CoreException(status);
	}


}
