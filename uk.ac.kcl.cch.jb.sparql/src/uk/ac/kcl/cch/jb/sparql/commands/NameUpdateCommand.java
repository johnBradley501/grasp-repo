package uk.ac.kcl.cch.jb.sparql.commands;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.ClassComponent;
import uk.ac.kcl.cch.jb.sparql.model.INamedComponent;

public class NameUpdateCommand extends DirtyCommand {

	private INamedComponent component;
	private String newText;
	private String oldText;

	public NameUpdateCommand(INamedComponent component, String text,
			QueryCreationEditor myEditor) {
		super(myEditor);
		this.setLabel("Update variable name");
		this.component = component;
		this.newText = text;
		this.oldText = component.getName();
	}
	
	public void execute() {
		component.setName(newText);
		super.execute();
	}
	
	public void undo() {
		component.setName(oldText);
		super.undo();
	}

}
