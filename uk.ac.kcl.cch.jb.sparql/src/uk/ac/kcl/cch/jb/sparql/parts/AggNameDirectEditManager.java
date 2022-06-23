package uk.ac.kcl.cch.jb.sparql.parts;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.TextCellEditor;

public class AggNameDirectEditManager extends DirectEditManager {
	
	private String initValue;
	
	public AggNameDirectEditManager(GraphicalEditPart source, CellEditorLocator locator, String initValue) {
		super(source, TextCellEditor.class, locator);
		this.initValue = initValue;
	}

	@Override
	protected void initCellEditor() {
		String startingLabel = initValue == null ? "":initValue;
		getCellEditor().setValue(startingLabel);
	}

}
