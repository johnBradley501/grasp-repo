package uk.ac.kcl.cch.jb.sparql.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

public class NaturalNumberTextFieldManager {

	static public abstract class ValueAccessor {
		public abstract int getValue();
		public abstract void setValue(int newValue);
	}

	private ValueAccessor va;
	private Text field;
	private boolean inField = false;
	private VerifyListener verifyListener;
	
	public NaturalNumberTextFieldManager(Composite parent, ValueAccessor va) {
		this.va = va;
		this.field = new Text(parent, SWT.BORDER);
		setFieldValue(va.getValue());
		
		field.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				// inField = true;
			}

			@Override
			public void focusLost(FocusEvent e) {
				handleFieldFinish();
			}
		});
		field.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleFieldFinish();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				handleFieldFinish();
			}
		});
		verifyListener = new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent event) {
				// see model pg 167 in Warner/Harris
				event.doit=false;
				char myChar = event.character;
				
				if(Character.isDigit(myChar))event.doit = true;
				else if(myChar == '\b')event.doit = true;
				if(event.doit)inField = true; 
			}
		};
		field.addVerifyListener(verifyListener);
	}
	
	protected void handleFieldFinish() {
		if(!inField) return;
		inField = false;
		int newVal = Integer.parseInt(field.getText());
		if(newVal != va.getValue())va.setValue(newVal);
	}

	public Text getText() {return field;}
	
	public void setFieldValue(int val) {
		String rslt = "";
		if(val > 0)rslt = ""+val;
		if(verifyListener != null)field.removeVerifyListener(verifyListener); // don't see why this is needed JB
		field.setText(rslt);
		// System.out.println("newVal:"+val+",Text Value:"+field.getText());
		if(verifyListener != null)field.addVerifyListener(verifyListener);
	}

}
