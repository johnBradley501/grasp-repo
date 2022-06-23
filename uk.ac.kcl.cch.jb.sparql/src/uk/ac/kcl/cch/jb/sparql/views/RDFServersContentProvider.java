package uk.ac.kcl.cch.jb.sparql.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;

import uk.ac.kcl.cch.jb.sparql.model.RDFServerList;

public class RDFServersContentProvider implements IStructuredContentProvider, PropertyChangeListener {
	
	private TableViewer viewer;

	public RDFServersContentProvider(TableViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(!(inputElement instanceof RDFServerList))return null;
		RDFServerList servers = (RDFServerList)inputElement;
		return servers.getServers().toArray();
	}

}
