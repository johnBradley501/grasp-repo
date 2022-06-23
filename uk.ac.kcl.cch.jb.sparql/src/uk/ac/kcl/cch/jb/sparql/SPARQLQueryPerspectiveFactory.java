package uk.ac.kcl.cch.jb.sparql;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class SPARQLQueryPerspectiveFactory implements IPerspectiveFactory {
	
	public static final String ID="uk.ac.kcl.cch.jb.sparql.perspective";
	
	private static final String leftFolderID="uk.ac.kcl.cch.jb.sparql.leftFolder";

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();

		IFolderLayout left = layout.createFolder(
				leftFolderID,
				IPageLayout.LEFT,
				0.20f,
				editorArea
				);
		
		left.addView("uk.ac.kcl.cch.jb.sparql.classesView");
		left.addPlaceholder("uk.ac.kcl.cch.jb.sparql.views.RDFServersView");
		
		// layout.addNewWizardShortcut("uk.ac.kcl.cch.jb.sparql.NewSPARQLQueryFileWizard");
	}

}
