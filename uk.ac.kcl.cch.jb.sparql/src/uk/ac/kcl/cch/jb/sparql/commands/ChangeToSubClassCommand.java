package uk.ac.kcl.cch.jb.sparql.commands;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.ClassComponent;
import uk.ac.kcl.cch.jb.sparql.model.ClassItem;

public class ChangeToSubClassCommand extends DirtyCommand {

	private ClassComponent componentItem;
	private ClassItem subClass;
	private ClassItem oldClass;

	public ChangeToSubClassCommand(QueryCreationEditor myEditor, String labelText, ClassComponent componentItem, ClassItem subClass) {
		super(myEditor);
		this.setLabel(labelText);
		this.componentItem = componentItem;
		this.subClass = subClass;
		this.oldClass = componentItem.getMyClass();
	}
	
	public void execute() {
		super.execute();
		componentItem.setMyClass(subClass);
	}
	
	public void undo() {
		componentItem.setMyClass(oldClass);
		super.undo();
	}

}
