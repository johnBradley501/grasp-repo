package uk.ac.kcl.cch.jb.sparql.rcp;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import uk.ac.kcl.cch.jb.sparql.SPARQLQueryPerspectiveFactory;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {
	
	// http://help.eclipse.org/2021-12/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Fguide%2Fproduct_open_file.htm

	private OpenDocumentEventProcessor openDocProcessor;

	public ApplicationWorkbenchAdvisor( OpenDocumentEventProcessor openDocProcessor) {
		super();
		this.openDocProcessor = openDocProcessor;
	}
	@Override
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }
    
    @Override
	public String getInitialWindowPerspectiveId() {
		return SPARQLQueryPerspectiveFactory.ID;
	}
    
	public void eventLoopIdle(Display display) {
		openDocProcessor.openFiles();
		super.eventLoopIdle(display);
	}

}
