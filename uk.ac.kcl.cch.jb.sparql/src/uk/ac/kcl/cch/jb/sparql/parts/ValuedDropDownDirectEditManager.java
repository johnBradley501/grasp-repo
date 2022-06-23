package uk.ac.kcl.cch.jb.sparql.parts;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;

public class ValuedDropDownDirectEditManager extends DirectEditManager {

	private String[] items;
	private int curItem;
	private int[] values;


	public ValuedDropDownDirectEditManager(GraphicalEditPart source, CellEditorLocator locator, String[] items, int[] values,int initVal) {
		super(source, ComboBoxCellEditor.class, locator);
		this.items = items;
		this.values = values;
		this.curItem = findInVals(initVal);
	}

	
	private int findInVals(int initVal) {
		for(int i = 0; i<values.length; i++) {
			if(initVal == values[i])return i;
		}
		return 0;
	}

	private ComboBoxCellEditor getTypedCellEditor() {
		return (ComboBoxCellEditor)this.getCellEditor();
	}
	
	private CCombo getMyCCombo() {
		return (CCombo)getTypedCellEditor().getControl();
	}

	protected CellEditor createCellEditorOn(Composite composite) {
		return new ComboBoxCellEditor(composite, items, SWT.READ_ONLY);
	}

	@Override
	protected void initCellEditor() {
		getMyCCombo().select(curItem);
		getMyCCombo().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				curItem = getMyCCombo().getSelectionIndex();
				setDirty(true);
				commit();

			}

			@Override
			public void widgetSelected(SelectionEvent event) {
				curItem = getMyCCombo().getSelectionIndex();
				if(curItem < 0)return;
				// System.out.println("selection set: "+selection);
				setDirty(true);
				commit();
			}
		});
	}
	
	public int getSelection() {
		return values[curItem];
	}

}
