package uk.ac.kcl.cch.jb.sparql.utils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class CompleteGraphButtonHandler implements PropertyChangeListener {
	
	private CompleteGraphChecker checker;
	private boolean complete;
	private Button myButton = null;

	public CompleteGraphButtonHandler(CompleteGraphChecker checker, Button myButton) {
		this.checker = checker;
		complete = checker.joinChecker(this);
		this.myButton = myButton;
		myButton.setEnabled(complete);
	}
	
	public void dispose() {
		checker.removePropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		String propName = arg0.getPropertyName();
		if(propName==CompleteGraphChecker.CompletenessChange) {
			Boolean newVal = (Boolean)arg0.getNewValue();
			complete = newVal.booleanValue();
			myButton.setEnabled(complete);
		}
	}

}
