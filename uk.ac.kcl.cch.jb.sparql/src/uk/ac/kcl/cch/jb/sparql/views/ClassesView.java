package uk.ac.kcl.cch.jb.sparql.views;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;

import uk.ac.kcl.cch.jb.sparql.actions.SwitchClassViewAction;
import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery;

public class ClassesView extends PageBookView {
	// see https://dzone.com/articles/how-create-pagebookview
	// https://stackoverflow.com/questions/9348767/how-to-get-active-editor-in-eclipse-plugin
	
	// https://stackoverflow.com/questions/23389432/eclipse-plugin-how-to-get-the-last-worked-on-editor
	
	// private Map<URL, IPageBookViewPage> ontPages = new HashMap<URL, IPageBookViewPage>();

	@Override
	protected IPage createDefaultPage(PageBook book) {
		MessagePage messagePage = new MessagePage();
		initPage(messagePage);
		messagePage.setMessage("Current Editor must be a SPARQL Query Editor.");
		messagePage.createControl(book);
		return messagePage;
	}
	
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		contributeToActionBars();
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		IToolBarManager manager = bars.getToolBarManager();
		manager.add(new SwitchClassViewAction(this));
	}

	@Override
	protected PageRec doCreatePage(IWorkbenchPart part) {
		if(!(part instanceof QueryCreationEditor))return null;
		QueryCreationEditor ed = (QueryCreationEditor)part;
		SPARQLQuery edsQuery = ed.getQuery();
		IPageBookViewPage thePage = null;
		thePage = new ClassesViewPage(edsQuery);
		initPage(thePage);
		thePage.createControl(getPageBook());
		return new PageRec(part, thePage);
	}

	@Override
	protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord) {
		pageRecord.page.dispose();
	}
	
	//public Object getAdapter(Class adapter) {
	//	return super.getAdapter(adapter);
	//}
	
	public void partActivated(IWorkbenchPart part) {
		super.partActivated(part);
	}

	@Override
	protected IWorkbenchPart getBootstrapPart() {
		  IWorkbenchPage page = getSite().getPage();
		  if(page != null) {
		   // check whether the active part is important to us
		   IWorkbenchPart activePart = page.getActivePart();
		   return isImportant(activePart)?activePart:null;
		  }
		  return null;
	}

	@Override
	protected boolean isImportant(IWorkbenchPart part) {
		// TODO Auto-generated method stub
		return part instanceof QueryCreationEditor;
	}

}
