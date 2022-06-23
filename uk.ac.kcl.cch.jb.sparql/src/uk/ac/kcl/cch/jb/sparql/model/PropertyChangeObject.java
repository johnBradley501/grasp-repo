package uk.ac.kcl.cch.jb.sparql.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * this class provides methods useful for classes that which
 * to act as property change announcers.  All persisted rdb2java
 * provide this service so that changes to their values can be
 * announced to listeners.
 * 
 * @author John Bradley
 *
 */

public abstract class PropertyChangeObject {
	 private final /*transient*/ PropertyChangeSupport pcsDelegate = new PropertyChangeSupport(this);
	 
	 /* (non-Javadoc)
	 * @see uk.ac.kcl.cch.rdb2java.dynData.IPropertyChangeObject#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	 public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
	 	if (l == null) {
	 		throw new IllegalArgumentException();
	 	}
	 	pcsDelegate.addPropertyChangeListener(l);
	 }

	 /* (non-Javadoc)
	 * @see uk.ac.kcl.cch.rdb2java.dynData.IPropertyChangeObject#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	 public synchronized void removePropertyChangeListener(PropertyChangeListener l) {
	 	if (l != null) {
	 		pcsDelegate.removePropertyChangeListener(l);
	 	}
	 }

	 /** 
	  * Report a property change to registered listeners (for example edit parts).
	  * @param property the programmatic name of the property that changed
	  * @param oldValue the old value of this property
	  * @param newValue the new value of this property
	  */
	 protected void firePropertyChange(String property, Object oldValue, Object newValue) {
	 	if (pcsDelegate.hasListeners(property)) {
	 		pcsDelegate.firePropertyChange(property, oldValue, newValue);
	 	}
	 }
	 
	 protected void firePropertyChange(String property){
		 firePropertyChange(property, null, this);
	 }


}
