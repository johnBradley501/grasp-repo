package uk.ac.kcl.cch.jb.sparql.editors;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import uk.ac.kcl.cch.jb.sparql.Activator;
import uk.ac.kcl.cch.jb.sparql.actions.DoLayoutAction;
import uk.ac.kcl.cch.jb.sparql.actions.ExportToSPARQLAction;
import uk.ac.kcl.cch.jb.sparql.actions.SendQueryToWebAction;
import uk.ac.kcl.cch.jb.sparql.builder.Builder;
import uk.ac.kcl.cch.jb.sparql.commands.ResetDistinctFlagCommand;
import uk.ac.kcl.cch.jb.sparql.commands.SetQueryLimitCommand;
import uk.ac.kcl.cch.jb.sparql.commands.SetQueryOffsetCommand;
import uk.ac.kcl.cch.jb.sparql.commands.UpdateQueryMetadataCommand;
import uk.ac.kcl.cch.jb.sparql.model.ModifierComponent;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery.QueryMetadata;
import uk.ac.kcl.cch.jb.sparql.model.SelectClauseModifier;
import uk.ac.kcl.cch.jb.sparql.utils.CompleteGraphButtonHandler;
import uk.ac.kcl.cch.jb.sparql.utils.NaturalNumberTextFieldManager;
import uk.ac.kcl.cch.jb.sparql.utils.NaturalNumberTextFieldManager.ValueAccessor;

public class TitleAreaManager implements PropertyChangeListener {
	private static Color infoPanelBackground = new Color(null, 255, 255, 200);

	private static Image clipboardIcon = null;
	private static Image getClipboardIcon() {
		if(clipboardIcon == null)
			clipboardIcon = Activator.getImageDescriptor("icons/clipboard-16.png").createImage();
		return clipboardIcon;
	}
	
	private static Image browserIcon = null;
	private static Image getBrowserIcon() {
		if(browserIcon == null)
			browserIcon = Activator.getImageDescriptor("icons/browserIcon.gif").createImage();
		return browserIcon;
	}
	
	private QueryCreationEditor myEditor;
	private Label queryTitle;

	private Label serverLabel;
	private Button distinctButton;
	private Combo layoutCombo;

	private CompleteGraphButtonHandler sendtoClipboardButtonChecker = null;
	private CompleteGraphButtonHandler layoutButtonChecker = null;
	private CompleteGraphButtonHandler sendtoWebButtonChecker = null;
	private CompleteGraphButtonHandler exportButtonChecker = null;

	private NaturalNumberTextFieldManager limitManager;
	private NaturalNumberTextFieldManager offsetManager;
	
	private static final String[] layoutNames = {"Spring", "Radial", "Vert Tree", "Horiz Tree"};
	private static LayoutAlgorithm[] layoutAlgos = null;
	
	public TitleAreaManager(QueryCreationEditor myEditor) {
		this.myEditor = myEditor;
		getQuery().addPropertyChangeListener(this);
		getQuery().getSelectClause().getModifier().addPropertyChangeListener(this);
	}
	
	public void dispose() {
		if(getQuery() != null) {
			getQuery().removePropertyChangeListener(this);
			getQuery().getSelectClause().getModifier().removePropertyChangeListener(this);
		}
		if(layoutButtonChecker != null)layoutButtonChecker.dispose();
		if(sendtoClipboardButtonChecker != null)sendtoClipboardButtonChecker.dispose();
		if(sendtoWebButtonChecker != null)sendtoWebButtonChecker.dispose();
		if(exportButtonChecker != null)exportButtonChecker.dispose();
	}
	
	private SPARQLQuery getQuery() {
		return myEditor.getQuery();
	}
	
	private CommandStack getCommandStack() {
		return myEditor.getCommandStack();
	}
	
	public void defineTitleArea(Composite composite) {
		Composite infoPanel = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		infoPanel.setLayout(layout);
		infoPanel.setBackground(infoPanelBackground); // ColorConstants.lightBlue);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		infoPanel.setLayoutData(gd);
		defineMetadataArea(infoPanel);
		defineSelectModifierArea(infoPanel);
		defineLayoutArea(infoPanel);
		defineGenerationArea(infoPanel);
	}

	private void defineMetadataArea(Composite infoPanel) {
		Group metadataArea = new Group(infoPanel, SWT.NONE);
		metadataArea.setText("Metadata");
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		metadataArea.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		metadataArea.setLayoutData(gd);
		// 1
		Label label = new Label(metadataArea, SWT.NULL);
		label.setText("Title:");
		// 2
		queryTitle = new Label(metadataArea, SWT.NULL);
		queryTitle.setText(myEditor.getQuery().getTitle());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		queryTitle.setLayoutData(gd);
		// 3
		Button changeMetatdataButton = new Button(metadataArea, SWT.PUSH);
		changeMetatdataButton.setText("Change");
		changeMetatdataButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
			   handleChangeMetadataButtonPush();
			}
		});
		// 4
		label = new Label(metadataArea, SWT.NULL);
		label.setText("For RDF Server:");
		// 5
		serverLabel = new Label(metadataArea, SWT.NULL);
		serverLabel.setText(getQuery().getServerName()+" <"+getQuery().getEndpoint().toString()+">");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		serverLabel.setLayoutData(gd);
	}
	
	private void handleChangeMetadataButtonPush() {
		QueryMetadataDialog dlg = new QueryMetadataDialog(Display.getCurrent().getActiveShell(), getQuery());
		int rc = dlg.open();
		if(rc == Window.OK) {
			QueryMetadata newMD = dlg.getMetadata();
			if(!newMD.sameAs(getQuery().getQueryMetadata())) {
				getCommandStack().execute(new UpdateQueryMetadataCommand(myEditor, getQuery(), newMD));
			}
		}
	}
	
	public class LimitValueAccessor extends NaturalNumberTextFieldManager.ValueAccessor {

		private SelectClauseModifier scm;
		public LimitValueAccessor(SelectClauseModifier scm) {
			super();
			this.scm = scm;
		}
		
		@Override
		public int getValue() {
			return scm.getLimit();
		}

		@Override
		public void setValue(int newValue) {
			if(newValue == getValue())return;
			myEditor.getCommandStack().execute(
					new SetQueryLimitCommand(scm, newValue, myEditor));
			
			
		}
		
	}


	private void defineSelectModifierArea(Composite parent) {
		Group selectModArea = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		selectModArea.setLayout(layout);

		distinctButton = new Button(selectModArea, SWT.CHECK | SWT.CENTER);
		distinctButton.setText("Distinct?");
		GridData gd = new GridData();
		distinctButton.setSelection(getQuery().getSelectClause().getModifier().isDistinct());
		gd.horizontalSpan = 2;
		distinctButton.setLayoutData(gd);
		distinctButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				handleDistinctSelection();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				handleDistinctSelection();
			}
			
		});

		Label label = new Label(selectModArea, SWT.RIGHT);
		label.setText("Limit:");
		
		limitManager = new NaturalNumberTextFieldManager(selectModArea, 
				new ValueAccessor() {
			@Override
			public int getValue() {
				return getQuery().getSelectClause().getModifier().getLimit();
			}

			@Override
			public void setValue(int newValue) {
				if(newValue == getValue())return;
				myEditor.getCommandStack().execute(
						new SetQueryLimitCommand(getQuery().getSelectClause().getModifier(), newValue, myEditor));
			}
		});
		Text limitText = limitManager.getText();
		gd = new GridData(GridData.FILL_HORIZONTAL);
		limitText.setLayoutData(gd);

		label = new Label(selectModArea, SWT.RIGHT);
		label.setText("Offset:");
		
		offsetManager = new NaturalNumberTextFieldManager(selectModArea, 
				new ValueAccessor() {
			@Override
			public int getValue() {
				return getQuery().getSelectClause().getModifier().getOffset();
			}

			@Override
			public void setValue(int newValue) {
				if(newValue == getValue())return;
				myEditor.getCommandStack().execute(
						new SetQueryOffsetCommand(getQuery().getSelectClause().getModifier(), newValue, myEditor));
			}
		});
		Text offsetText = offsetManager.getText();
		gd = new GridData(GridData.FILL_HORIZONTAL);
		offsetText.setLayoutData(gd);
	}

	protected void handleDistinctSelection() {
		boolean userSetValue = distinctButton.getSelection();
		SelectClauseModifier scm = getQuery().getSelectClause().getModifier();
		if(scm.isDistinct()==userSetValue)return;
		myEditor.getCommandStack().execute(new ResetDistinctFlagCommand(scm, myEditor));
	}

	private void defineLayoutArea(Composite parent) {
		Group layoutModArea = new Group(parent, SWT.NONE);
		FillLayout layout = new FillLayout(SWT.VERTICAL);
		layoutModArea.setLayout(layout);
		
		Button doLayoutButton = new Button(layoutModArea, SWT.PUSH);
		doLayoutButton.setText("Do Layout");
		doLayoutButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				   handleDoLayoutButtonPush();
				}
			
		});
		
		buildLayoutDropdown(layoutModArea);
		
	}

private void handleDoLayoutButtonPush() {
	DoLayoutAction action = new DoLayoutAction(myEditor, getSelectedLayout());
	action.run();
}


private void buildLayoutDropdown(Group parent) {
	layoutCombo = new Combo(parent, SWT.READ_ONLY);
	layoutCombo.setItems(layoutNames);
	layoutCombo.select(0);
	// layoutNames = {"Spring", "Radial", "Vert Tree", "Horiz Tree"};
	if(layoutAlgos == null) {
		layoutAlgos = new LayoutAlgorithm[layoutNames.length];
		layoutAlgos[0] = new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
		layoutAlgos[1] = new RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
		layoutAlgos[2] = new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
		layoutAlgos[3] = new HorizontalTreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
	}
}

public LayoutAlgorithm getSelectedLayout() {
	int i = layoutCombo.getSelectionIndex();
	if((i == -1) || (i >= layoutNames.length))return layoutAlgos[0];
	return layoutAlgos[i];
}


private void defineGenerationArea(Composite parent) {
	Group generationArea = new Group(parent, SWT.NONE);
	FillLayout layout = new FillLayout(SWT.VERTICAL);
	generationArea.setLayout(layout);


	Button sendToClipboardButton = new Button(generationArea, SWT.FLAT);
	sendToClipboardButton.setImage(getClipboardIcon());
	// sendToClipboardButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
	sendToClipboardButton.addSelectionListener(new SelectionAdapter() {
		public void widgetSelected(SelectionEvent event) {
			handleSendToClipboard();
		}

	});
	sendtoClipboardButtonChecker = new CompleteGraphButtonHandler(myEditor.getGraphChecker(), sendToClipboardButton);

	IWorkbenchPartSite site = myEditor.getSite();
	Button sendToWebButton = new Button(generationArea, SWT.FLAT);
	sendToWebButton.setImage(getBrowserIcon());
	// sendToWebButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
	sendToWebButton.addSelectionListener(new SelectionAdapter() {
		public void widgetSelected(SelectionEvent event) {
			Action theAction = new SendQueryToWebAction(myEditor.getQuery(), site);
			theAction.run();
		}
	});
	sendtoWebButtonChecker = new CompleteGraphButtonHandler(myEditor.getGraphChecker(), sendToWebButton);
	
	Button exportButton = new Button(generationArea, SWT.FLAT);
	exportButton.setImage(ExportToSPARQLAction.getMyIcon());
	exportButton.addSelectionListener(new SelectionAdapter() {
		public void widgetSelected(SelectionEvent event) {
			Action theAction = new ExportToSPARQLAction(myEditor);
			theAction.run();
		}
	});
	exportButtonChecker = new CompleteGraphButtonHandler(myEditor.getGraphChecker(), exportButton);

}


private void handleSendToClipboard() {
	Builder b = new Builder(getQuery());
	// see http://www.avajava.com/tutorials/lessons/how-do-i-copy-a-string-to-the-clipboard.html
	Toolkit toolkit = Toolkit.getDefaultToolkit();
	Clipboard clipboard = toolkit.getSystemClipboard();
	StringSelection strSel = new StringSelection(b.toSPARQL());
	clipboard.setContents(strSel, null);
}


	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if(prop.equals(SPARQLQuery.METADATA_CHANGED)) {
			QueryMetadata meta = (QueryMetadata)evt.getNewValue();
			queryTitle.setText(meta.title);
			serverLabel.setText(getQuery().getServerName()+" <"+getQuery().getEndpoint().toString()+">");
		}
		else if(prop==SelectClauseModifier.DISTINCT_SET)
			distinctButton.setSelection(((Boolean)evt.getNewValue()).booleanValue());
		else if(prop==SelectClauseModifier.LIMIT_SET)
			limitManager.setFieldValue((Integer)evt.getNewValue());
		else if(prop==SelectClauseModifier.OFFSET_SET)
			offsetManager.setFieldValue((Integer)evt.getNewValue());
	}

}
