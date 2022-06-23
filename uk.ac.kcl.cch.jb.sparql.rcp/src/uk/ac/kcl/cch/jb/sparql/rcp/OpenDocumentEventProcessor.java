package uk.ac.kcl.cch.jb.sparql.rcp;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import uk.ac.kcl.cch.jb.sparql.actions.OpenExternalFileAction;

public class OpenDocumentEventProcessor implements Listener {
	// see http://help.eclipse.org/2021-12/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Fguide%2Fproduct_open_file.htm
	
	private ArrayList<String> filesToOpen = new ArrayList<String>(1);

	@Override
	public void handleEvent(Event event) {
		if (event.text != null)
			filesToOpen.add(event.text);
	}
	
	public void openFiles() {
		if (filesToOpen.isEmpty()) return;

		String[] filePaths = filesToOpen.toArray(new String[filesToOpen.size()]);
		filesToOpen.clear();
		
		for (String path : filePaths) {
			Action openAction = new OpenExternalFileAction(path);
			openAction.run();
		}
	}

}
