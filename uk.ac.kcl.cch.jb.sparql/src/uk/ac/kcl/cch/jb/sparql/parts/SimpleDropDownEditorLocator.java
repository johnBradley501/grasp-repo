package uk.ac.kcl.cch.jb.sparql.parts;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Point;

public class SimpleDropDownEditorLocator  implements CellEditorLocator{
	private Label nameLabel;
	
	public SimpleDropDownEditorLocator(Label label) {
		this.nameLabel = label;
	}

	@Override
	public void relocate(CellEditor celleditor) {
		CCombo ccombo = (CCombo)celleditor.getControl();
		Point pref = ccombo.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		Rectangle rect = nameLabel.getBounds().getCopy();
		nameLabel.translateToAbsolute(rect);
		ccombo.setBounds(rect.x -1, rect.y -1, pref.x + 5, pref.y + 1);
	}

}
