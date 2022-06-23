package uk.ac.kcl.cch.jb.sparql.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import org.eclipse.jface.viewers.TableViewer;

import uk.ac.kcl.cch.jb.sparql.model.RDFServer;
import uk.ac.kcl.cch.jb.sparql.model.RDFServerList;

public class RDFServersViewManager implements PropertyChangeListener{

	private TableViewer viewer;

	public RDFServersViewManager(TableViewer viewer) {
		this.viewer = viewer;
		RDFServerList servers = RDFServerList.getList();
		servers.addPropertyChangeListener(this);
		Iterator<RDFServer> it = servers.getServers().iterator();
		while(it.hasNext()) {
			RDFServer server = it.next();
			server.addPropertyChangeListener(this);
		}
	}
	
	public void dispose() {
		RDFServerList servers = RDFServerList.getList();
		servers.addPropertyChangeListener(this);
		Iterator<RDFServer> it = servers.getServers().iterator();
		while(it.hasNext()) {
			RDFServer server = it.next();
			server.removePropertyChangeListener(this);
		}
		servers.removePropertyChangeListener(this);
		
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		String prop = arg0.getPropertyName();
		if(prop.equals(RDFServer.NAME_CHANGED)) {
			RDFServer server = (RDFServer)arg0.getNewValue();
			viewer.update(server, null);
		}else if(prop.equals(RDFServerList.LIST_CHANGED)) {
			if(arg0.getNewValue() != null) {
				RDFServer newServer = (RDFServer)arg0.getNewValue();
				newServer.addPropertyChangeListener(this);
			} else if(arg0.getOldValue() != null) {
				RDFServer oldServer = (RDFServer)arg0.getOldValue();
				oldServer.removePropertyChangeListener(this);
			}
			viewer.refresh(true);
		}
		
	}

}
