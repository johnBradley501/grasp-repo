package uk.ac.kcl.cch.jb.sparql.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.part.IPage;

import uk.ac.kcl.cch.jb.sparql.Activator;
import uk.ac.kcl.cch.jb.sparql.views.ClassesView;
import uk.ac.kcl.cch.jb.sparql.views.ClassesViewPage;

public class SwitchClassViewAction extends Action {
	
	private ClassesView classesView;

	public SwitchClassViewAction(ClassesView classesView) {
		super();
		this.setToolTipText("Switch Classes Display");
		this.setImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/switch-view.gif"));
		this.classesView = classesView;
	}
	
	public void run() {
		IPage currentPage = classesView.getCurrentPage();
		if(currentPage instanceof ClassesViewPage) {
			ClassesViewPage thisPage = (ClassesViewPage)currentPage;
			thisPage.switchDisplay();
		}
	}

}
