package uk.ac.kcl.cch.jb.sparql.actions;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.Action;

import uk.ac.kcl.cch.jb.sparql.Activator;
import uk.ac.kcl.cch.jb.sparql.commands.AddInstanceConstraintCommand;
import uk.ac.kcl.cch.jb.sparql.commands.AddNewVariableComponentCommand;
import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.ClassComponent;
import uk.ac.kcl.cch.jb.sparql.model.PropertyItem;

public class AddNewVariableComponentAction extends Action {

	private QueryCreationEditor myEditor;
	private ClassComponent item;
	private PropertyItem theProperty;
	private CommandStack commandStack;
	private String myLabel;

	public AddNewVariableComponentAction(QueryCreationEditor myEditor, ClassComponent item, PropertyItem prop,
			CommandStack commandStack) {
		super();
		// setImageDescriptor(Activator.getImageDescriptor("icons/constraint.png"));
		myLabel = "Add variable for Property "+prop.getDisplayURI();
		setText(myLabel);
		
		this.myEditor = myEditor;
		this.item = item;
		this.theProperty = prop;
		this.commandStack = commandStack;
	}

	public void run() {
		// System.out.println("Selected: "+myLabel);
		commandStack.execute(new AddNewVariableComponentCommand(myEditor, myLabel, item, theProperty));
	}

}
