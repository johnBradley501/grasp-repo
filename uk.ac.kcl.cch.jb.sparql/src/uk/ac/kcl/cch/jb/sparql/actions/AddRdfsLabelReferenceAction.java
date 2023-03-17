package uk.ac.kcl.cch.jb.sparql.actions;

import org.eclipse.jface.action.Action;

import uk.ac.kcl.cch.jb.sparql.commands.AddRdfsLabelReferenceCommand;
import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.ClassComponent;

public class AddRdfsLabelReferenceAction extends Action {

	private String myLabel;
	private QueryCreationEditor myEditor;
	private ClassComponent item;

	public AddRdfsLabelReferenceAction(QueryCreationEditor myEditor, ClassComponent item) {
		super();
		myLabel = "Add rdfs:label for "+item.getDisplayURI();
		setText(myLabel);
		this.myEditor = myEditor;
		this.item = item;
	}
	
	public void run() {
		myEditor.getCommandStack().execute(new AddRdfsLabelReferenceCommand(myEditor, myLabel, item));
	}
}
