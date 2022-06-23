package uk.ac.kcl.cch.jb.sparql.actions;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.Action;

import uk.ac.kcl.cch.jb.sparql.Activator;
import uk.ac.kcl.cch.jb.sparql.commands.AddInstanceConstraintCommand;
import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.ClassItem;
import uk.ac.kcl.cch.jb.sparql.model.ObjectPropertyItem;
import uk.ac.kcl.cch.jb.sparql.model.ClassComponent;
import uk.ac.kcl.cch.jb.sparql.model.WhereClausePredicate;

public class AddInstanceConstraintAction extends Action {
	
	private String fullText;
	private String shortText;
	private QueryCreationEditor myEditor;
	private ClassComponent domain;
	private CommandStack commandStack;
	private ObjectPropertyItem opi;
	private ClassItem rangeClass;

	public AddInstanceConstraintAction(QueryCreationEditor myEditor, ClassComponent domain, ObjectPropertyItem opi, ClassItem rangeClass, CommandStack commandStack) {
		super();
		setImageDescriptor(Activator.getImageDescriptor("icons/constraint.png"));
		String rangeURI = rangeClass.getDisplayURI();
		fullText = "thru "+opi.getDisplayURI()+" to "+rangeURI+" instance";
		shortText = opi.getDisplayURI()+"->"+rangeURI;
		setText(fullText);
		setToolTipText(fullText);
		
		this.myEditor = myEditor;
		this.domain = domain;
		this.opi = opi;
		this.rangeClass = rangeClass;
		this.commandStack = commandStack;
	}
	
	public void run() {
		// System.out.println("Selected: "+fullText);
		commandStack.execute(new AddInstanceConstraintCommand(myEditor, fullText, domain, opi, rangeClass));
	}

}
