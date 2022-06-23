package uk.ac.kcl.cch.jb.sparql.model;

import java.beans.PropertyChangeListener;

public interface INamedComponent {
	
	public static final String NEW_NAME = "NEW_NAME";
	
	public void addPropertyChangeListener(PropertyChangeListener l);
	public void removePropertyChangeListener(PropertyChangeListener l);

	public String getName();
	public void setName(String newName);
	
	public int getID();
	
	public String getBasisForName();
}
