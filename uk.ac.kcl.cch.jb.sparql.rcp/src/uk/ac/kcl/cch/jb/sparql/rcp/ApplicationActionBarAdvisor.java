package uk.ac.kcl.cch.jb.sparql.rcp;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private IWorkbenchAction exitAction;

	private IWorkbenchAction undoAction;
    private IWorkbenchAction redoAction;
    private IWorkbenchAction cutAction;
    private IWorkbenchAction copyAction;
    private IWorkbenchAction pasteAction;
    private IWorkbenchAction deleteAction;
    private IWorkbenchAction selectAllAction;
    private IWorkbenchAction preferencesAction;
	private IWorkbenchAction saveAsAction;

	private IWorkbenchAction saveAction;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}
	
	@Override
	protected void makeActions(IWorkbenchWindow window) {
        exitAction = ActionFactory.QUIT.create(window);
        register(exitAction);
       
        undoAction = ActionFactory.UNDO.create(window);
        register(undoAction);
        redoAction = ActionFactory.REDO.create(window);
        register(redoAction);
        cutAction = ActionFactory.CUT.create(window);
        register(cutAction);
        copyAction = ActionFactory.COPY.create(window);
        register(copyAction);
        pasteAction = ActionFactory.PASTE.create(window);
        register(pasteAction);
        deleteAction = ActionFactory.DELETE.create(window);
        register(deleteAction);
        selectAllAction = ActionFactory.SELECT_ALL.create(window);
        register(selectAllAction);
        
        saveAsAction = ActionFactory.SAVE_AS.create(window);
        register(saveAsAction);
        saveAction = ActionFactory.SAVE.create(window);
        register(saveAction);
}
	
	@Override
	protected void fillMenuBar(IMenuManager menuBar) {
		super.fillMenuBar(menuBar);

        MenuManager fileMenu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE);
        MenuManager editMenu = new MenuManager("&Edit", IWorkbenchActionConstants.M_EDIT);
        // Add a group marker indicating where action set menus will appear.
 
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));

		// File

        fileMenu.add(new Separator(IWorkbenchActionConstants.FILE_START));
        fileMenu.add(saveAsAction);
        fileMenu.add(saveAction);

        
        fileMenu.add(new Separator(IWorkbenchActionConstants.NEW_EXT));
        fileMenu.add(new Separator(IWorkbenchActionConstants.FILE_END));
        
        fileMenu.add(exitAction);
        
        // Edit
        editMenu.add(undoAction);
        editMenu.add(redoAction);
        editMenu.add(new Separator(IWorkbenchActionConstants.CUT));
        editMenu.add(cutAction);
        editMenu.add(copyAction);
        editMenu.add(pasteAction);
        editMenu.add(new Separator(IWorkbenchActionConstants.DELETE));
        editMenu.add(deleteAction);
        editMenu.add(selectAllAction);
	}
	
	@Override
	protected void fillCoolBar(ICoolBarManager coolBar) {
		super.fillCoolBar(coolBar);
	}

}

