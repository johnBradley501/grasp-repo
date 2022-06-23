package uk.ac.kcl.cch.jb.sparql.actions;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.Action;

import uk.ac.kcl.cch.jb.sparql.Activator;
import uk.ac.kcl.cch.jb.sparql.commands.AddPropertyLinkCommand;
import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.ObjectPropertyItem;
import uk.ac.kcl.cch.jb.sparql.model.ClassComponent;

public class AddPropertyLinkAction extends Action {

	private String fullTitle;
	private String shortTitle;
	private ObjectPropertyItem prop;
	private CommandStack commandStack;
	private ClassComponent domain;
	private ClassComponent range;
	private QueryCreationEditor myEditor;

	public AddPropertyLinkAction(QueryCreationEditor myEditor, ObjectPropertyItem prop, ClassComponent domain, ClassComponent range,
			CommandStack commandStack) {
		super();
		setImageDescriptor(Activator.getImageDescriptor("icons/link.gif"));
		this.shortTitle = "Link with "+prop.getDisplayURI();
		this.fullTitle = "Link "+domain.getDisplayURI()+" and "+range.getDisplayURI()+" with "+prop.getDisplayURI();
		this.setText(shortTitle);
		this.setToolTipText(fullTitle);
		this.myEditor = myEditor;
		this.prop = prop;
		this.commandStack = commandStack;
		this.domain = domain;
		this.range = range;
		
	}
	
	public void run() {
		commandStack.execute(new AddPropertyLinkCommand(myEditor, shortTitle, prop, domain, range));
	}

}
