package uk.ac.kcl.cch.jb.sparql.parts;

import java.util.List;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

import uk.ac.kcl.cch.jb.sparql.model.ClassItem;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery;
import uk.ac.kcl.cch.jb.sparql.utils.InstanceListManager;
import uk.ac.kcl.cch.jb.sparql.model.InstanceElement;

public class InstanceComponentDirectEditManager extends DirectEditManager {
	// see https://www.eclipse.org/forums/index.php/t/69000/
	// https://bugs.eclipse.org/bugs/show_bug.cgi?format=multiple&id=85936
	
	private class PatchedComboBoxCellEditor extends ComboBoxCellEditor {

		public PatchedComboBoxCellEditor(Composite parent, String[] editorValues) {
			super(parent, editorValues);
		}

		protected void doSetValue(Object value) {
			super.doSetValue(value);
			fireEditorValueChanged(true, true);
		}
		
		
		public Object doGetValue() {
			if(selection < 0)return null;
			return currentList.get(selection);
		}

	}
	
	private SPARQLQuery query;
	private ClassItem clsFamily;
	private InstanceListManager manager;
	private List<InstanceElement> currentList = null;
	private int selection = 0;
	
	public InstanceComponentDirectEditManager(GraphicalEditPart source, CellEditorLocator locator, SPARQLQuery query, ClassItem clsFamily ) {
		super(source, PatchedComboBoxCellEditor.class, locator);
		this.query = query;
		this.clsFamily = clsFamily;
		this.manager = InstanceListManager.find(query, clsFamily);
	}
	
	protected CellEditor createCellEditorOn(Composite composite) {
		return new PatchedComboBoxCellEditor(composite, new String[0]);
	}
	
	private ComboBoxCellEditor getTypedCellEditor() {
		return (ComboBoxCellEditor)this.getCellEditor();
	}
	
	private CCombo getMyCCombo() {
		return (CCombo)getTypedCellEditor().getControl();
	}

	@Override
	protected void initCellEditor() {
		currentList = manager.getInstances("");
		getTypedCellEditor().setItems(InstanceListManager.getNames(currentList).toArray(new String[0]));
		getMyCCombo().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				selection = getMyCCombo().getSelectionIndex();
				setDirty(true);
				commit();

			}
			
			// see https://www.programcreek.com/java-api-examples/?api=org.eclipse.swt.custom.CCombo example 12

			@Override
			public void widgetSelected(SelectionEvent event) {
				selection = getMyCCombo().getSelectionIndex();
				if(selection < 0) {
					// System.out.println("selection: "+getTypedCellEditor().getValue());
					String text = getMyCCombo().getText();
					if(text == null || text.length() == 0)return;
					selection = getMyCCombo().indexOf(text);
				}
				setDirty(true);
				commit();
			}
		});
		getMyCCombo().addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				CCombo myCombo = (CCombo)e.widget;
				// myCombo.deselectAll();
				currentList = manager.getInstances(myCombo.getText().trim());
				myCombo.setItems(InstanceListManager.getNames(currentList).toArray(new String[0]));
				// System.out.println("string: "+myCombo.getText());
				
			}
			
		});

	}
	
	//public Object getValue() {
	//	return currentList.get(selection);
	//}
	
	//protected Object doGetValue() {
	//	return currentList.get(selection); // Integer.valueOf(selection);
	//}

}
