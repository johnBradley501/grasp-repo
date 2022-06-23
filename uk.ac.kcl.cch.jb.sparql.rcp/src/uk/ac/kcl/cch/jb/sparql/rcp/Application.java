package uk.ac.kcl.cch.jb.sparql.rcp;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {

	@Override
	public Object start(IApplicationContext context) throws Exception {
		
		// see http://help.eclipse.org/2021-12/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Fguide%2Fproduct_open_file.htm
		
		OpenDocumentEventProcessor openDocProcessor = new OpenDocumentEventProcessor();
		
		Display display = PlatformUI.createDisplay();
		display.addListener(SWT.OpenDocument, openDocProcessor);
		
		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor(openDocProcessor));
			if (returnCode == PlatformUI.RETURN_RESTART)
				return IApplication.EXIT_RESTART;
			else
				return IApplication.EXIT_OK;
		} finally {
			display.dispose();
		}
		
	}

	@Override
	public void stop() {
		if (!PlatformUI.isWorkbenchRunning())
			return;
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		display.syncExec(() -> {
			if (!display.isDisposed())
				workbench.close();
		});
	}
}
