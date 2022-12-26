package uk.ac.kcl.cch.jb.sparql.commands;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.WhereClausePredicate;

public class SwitchOptionalSettingCommand extends DirtyCommand {

	private WhereClausePredicate predicate;
	private boolean oldSetting;
	private boolean newSetting;

	public SwitchOptionalSettingCommand(WhereClausePredicate predicate, QueryCreationEditor myEditor) {
		super(myEditor);
		this.predicate = predicate;
		this.oldSetting = predicate.isOptional();
		this.newSetting = !oldSetting;
		if(newSetting)this.setLabel("Change to Optional");
		else this.setLabel("Change to Required");
	}
	
	public void execute() {
		super.execute();
		predicate.setOptional(newSetting);
	}
	
	public void undo() {
		super.undo();
		predicate.setOptional(oldSetting);
	}

}
