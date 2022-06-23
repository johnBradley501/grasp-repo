package uk.ac.kcl.cch.jb.sparql.commands;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.InstanceComponent;
import uk.ac.kcl.cch.jb.sparql.model.InstanceElement;

public class InstanceItemUpdateCommand extends DirtyCommand {

	private InstanceComponent instanceComponent;
	private InstanceElement newValue;
	private InstanceElement oldValue;

	public InstanceItemUpdateCommand(InstanceComponent instanceComponent, InstanceElement value, QueryCreationEditor myEditor) {
		super(myEditor);
		this.instanceComponent = instanceComponent;
		this.newValue = value;
	}
	
	public void execute() {
		oldValue = new InstanceElement(instanceComponent.getLabel(), instanceComponent.getMyInstance().toString()); 
		instanceComponent.updateInstance(newValue);
		super.execute();
	}
	
	public void undo() {
		super.undo();
		instanceComponent.updateInstance(oldValue);
	}

}
