package uk.ac.kcl.cch.jb.sparql.actions;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import uk.ac.kcl.cch.jb.sparql.Activator;
import uk.ac.kcl.cch.jb.sparql.builder.Builder;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery;

public class SendQueryToWebAction extends Action {
	
	// https://www.baeldung.com/java-url-encoding-decoding
	// http://books.gigatux.nl/mirror/eclipseplugins/032142672X/ch20lev1sec4.html
	
	private SPARQLQuery query;
	private IWorkbenchPartSite site;

	public SendQueryToWebAction(SPARQLQuery query, IWorkbenchPartSite site) {
		super();
		this.setToolTipText("Add new RDF Server");
		this.setImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/browserIcon.gif"));
		this.query = query;
		this.site = site;
	}
	
	public void run() {
        IWorkbenchBrowserSupport browserSupport = site
                .getWorkbenchWindow().getWorkbench()
                .getBrowserSupport();
        
        // String incipit = "https://www.poms.ac.uk/rdf/repositories/poms/query?query=";
        String incipit = query.getBrowserEndpoint().toString();
        Builder b = new Builder(query);
        URL webUrl = null;
        try {
			String urlString = incipit+URLEncoder.encode(b.toSPARQL(), StandardCharsets.UTF_8.toString());
			webUrl = new URL(urlString);
		} catch (MalformedURLException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}
        
        IWebBrowser browser;
        try {
        	browser = browserSupport.getExternalBrowser();
            browser.openURL(webUrl);
         }
         catch (PartInitException e) {
            e.printStackTrace();
            return;
         }
	}

}
