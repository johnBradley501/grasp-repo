package uk.ac.kcl.cch.jb.sparql.parts;

import org.eclipse.draw2d.Label;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.TextCellEditor;

public class NamedComponentDirectEditManager extends DirectEditManager {
	
	Label label;
	
	public NamedComponentDirectEditManager(GraphicalEditPart source, CellEditorLocator locator, Label label) {
		super(source, TextCellEditor.class, locator);
		this.label = label;
	}

	@Override
	protected void initCellEditor() {
		String initialLabelText = label.getText();
		getCellEditor().setValue(initialLabelText);
	}

}
