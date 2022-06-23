package uk.ac.kcl.cch.jb.sparql.wizards;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import uk.ac.kcl.cch.jb.sparql.model.RDFServer;
import uk.ac.kcl.cch.jb.sparql.model.RDFServerList;

public abstract class NewSPARQLQueryBasePage extends WizardPage {

	private Combo serverWidget;
	private RDFServer[] servers = null;
	private RDFServer selectedServer = null;
	private Text titleText;
	private String title = null;

	protected NewSPARQLQueryBasePage(String pageName) {
		super(pageName);
		// TODO Auto-generated constructor stub
	}
	
	public RDFServer getRDFServer() {
		return selectedServer; // servers[serverWidget.getSelectionIndex()];
	}
	
	protected void setTitleText(String text) {
		title = text;
	}
	
	public String getTitleText() {
		return title;
	}
	
	protected abstract void buildControlTop(Composite container);
	protected abstract void initialize();
	protected abstract void dialogChanged();

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		
		buildControlTop(container);

		
		Label label = new Label(container, SWT.NULL);
		label.setText("&SPARQL Server:");
		
		serverWidget = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		serverWidget.setLayoutData(gd);
		setupServerWidget(serverWidget);
		
		label = new Label(container, SWT.NULL);
		label.setText("&Query Title:");
		
		titleText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		titleText.setLayoutData(gd);
		titleText.addModifyListener(e -> dialogChanged());
		
		initialize();
		dialogChanged();
		setControl(container);
	}

	
	private void setupServerWidget(Combo serverWidget) {
		RDFServerList serverdata = RDFServerList.getList();
		List<RDFServer> list = serverdata.getServers();
		servers = new RDFServer[list.size()];
		Iterator<RDFServer> it = list.iterator();
		int i = 0;
		while(it.hasNext()) {
			RDFServer server = it.next();
			servers[i] = server;
			serverWidget.add(server.getName());
			i++;
		}
		if(i > 0)selectedServer = servers[0];
		serverWidget.select(0);
		serverWidget.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int i = serverWidget.getSelectionIndex();
				if(i >= 0)selectedServer = servers[i];
			}
			
		});
	}
	
	protected void copyTitleText() {
		title = titleText.getText();

	}
	
	protected void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}
}
