package uk.ac.kcl.cch.jb.sparql.commands;

import org.eclipse.draw2d.geometry.Rectangle;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.ClassComponent;
import uk.ac.kcl.cch.jb.sparql.model.WhereClauseComponent;

public class MoveComponentCommand extends DirtyCommand {

	private Rectangle newBounds;
	private Rectangle oldBounds = null;
	private WhereClauseComponent component;

	public MoveComponentCommand(WhereClauseComponent component, Rectangle newBounds, QueryCreationEditor myEditor) {
		super(myEditor);
		this.setLabel("Move Component");
		this.component = component;
		this.newBounds = newBounds;
		oldBounds = component.getMyBounds();
	}
	
	public void execute() {
		component.setMyBounds(newBounds);
		super.execute();
	}
	
	public void undo() {
		component.setMyBounds(oldBounds);
		super.undo();
	}

}
