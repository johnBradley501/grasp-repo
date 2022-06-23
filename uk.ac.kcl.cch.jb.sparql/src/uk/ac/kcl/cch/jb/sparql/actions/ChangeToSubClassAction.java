package uk.ac.kcl.cch.jb.sparql.actions;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.Action;

import uk.ac.kcl.cch.jb.sparql.Activator;
import uk.ac.kcl.cch.jb.sparql.commands.AddInstanceConstraintCommand;
import uk.ac.kcl.cch.jb.sparql.commands.ChangeToSubClassCommand;
import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.ClassComponent;
import uk.ac.kcl.cch.jb.sparql.model.ClassItem;

public class ChangeToSubClassAction extends Action {

	private String labelText;
	private ClassComponent componentItem;
	private ClassItem subClass;
	private CommandStack commandStack;
	private QueryCreationEditor myEditor;

	public ChangeToSubClassAction(QueryCreationEditor myEditor, ClassComponent item, ClassItem subClass, CommandStack commandStack) {
		super();
		setImageDescriptor(Activator.getImageDescriptor("icons/to_subclass.gif"));
		this.labelText = "Change to subclass "+subClass.getDisplayURI();
		setText(labelText);
		
		this.myEditor = myEditor;
		this.componentItem = item;
		this.subClass = subClass;
		this.commandStack = commandStack;
	}
	
	public void run() {
		// System.out.println("Selected: "+fullText);
		commandStack.execute(new ChangeToSubClassCommand(myEditor, labelText, componentItem, subClass));
	}

}
