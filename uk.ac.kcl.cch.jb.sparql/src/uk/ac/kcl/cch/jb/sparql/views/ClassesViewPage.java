package uk.ac.kcl.cch.jb.sparql.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.part.Page;

import uk.ac.kcl.cch.jb.sparql.dnd.ClassesViewDragListener;
import uk.ac.kcl.cch.jb.sparql.model.ClassItem;
import uk.ac.kcl.cch.jb.sparql.model.OntologyData;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery.QueryMetadata;

public class ClassesViewPage extends Page  implements PropertyChangeListener {
	
	private URL ontologyURL;
	//private OntologyData ontologyData = null;
	private StackLayout stackLayout = new StackLayout();
	
	private static final String HierControl = "HIER";
	private static final String FlatControl = "FLAT";
	
	private TreeViewer hierViewer = null;
	private IStructuredContentProvider myHierContentProvider = null;
	// private ILabelProvider myLabelProvider = null;
	private TableViewer flatViewer = null;;
	private Composite holder = null;
	private SPARQLQuery query;

	public ClassesViewPage(SPARQLQuery query) {
		super();
		this.query = query;
		this.ontologyURL = query.getOntologyURL();
		query.addPropertyChangeListener(this);
	}
	
	public void dispose() {
		super.dispose();
		query.removePropertyChangeListener(this);
	}

	@Override
	public void createControl(Composite parent) {
		//Composite holder = parent;
		holder = new Composite(parent,SWT.NONE);
		holder.setLayout(stackLayout);
		//holder.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		ontologyURL = query.getOntologyURL();
		OntologyData ontologyData = OntologyData.getOntologyData(query.getOntologyURL());
		ontologyData.loadOntologyData();

		hierViewer = new TreeViewer(holder, SWT.H_SCROLL | SWT.V_SCROLL);
		// hierViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		myHierContentProvider = new ClassesViewHierContentProvider(ontologyData);
		hierViewer.setContentProvider(myHierContentProvider);
		// myLabelProvider = new ClassesViewLabelProvider();
		// hierViewer.setLabelProvider(myLabelProvider);
		hierViewer.setLabelProvider(new ClassesViewLabelProvider());
		hierViewer.setComparator(new ViewerComparator());
		ClassesViewItem topThing = new ClassesViewItem(ontologyData.getTopClassItem(), null, ontologyData);
		// see https://www.eclipse.org/forums/index.php/t/203015/
		//System.setProperty("javax.xml.parsers.SAXParserFactory","org.apache.xerces.jaxp.SAXParserFactoryImpl");
		hierViewer.setInput(topThing);
		Tree myTree = hierViewer.getTree();
		// System.out.println("Top Tree size: "+myTree.getItems().length);
		if(myTree.getItems().length == 1)hierViewer.expandToLevel(2);
		hierViewer.getControl().setData(HierControl);
		
		new ClassesViewDragListener(hierViewer);
		
		flatViewer  = new TableViewer(holder, SWT.H_SCROLL | SWT.V_SCROLL);
		flatViewer.setContentProvider(new ClassesViewFlatContentProvider(getFlatClassList(ontologyData)));
		flatViewer.setLabelProvider(new ClassesViewLabelProvider());
		flatViewer.setComparator(new ViewerComparator());
		flatViewer.setInput(topThing);
		flatViewer.getControl().setData(FlatControl);
		
		new ClassesViewDragListener(flatViewer);
		
		stackLayout.topControl = flatViewer.getControl();
	}
	
	private ClassesViewItem[] getFlatClassList(OntologyData ontologyData) {
		Collection<ClassItem> classes = ontologyData.getClassItems();
		List<ClassesViewItem> viewItems = new ArrayList<ClassesViewItem>();
		for(ClassItem item: classes) {
			viewItems.add(new ClassesViewItem(item, null, ontologyData));
		}
		return viewItems.toArray(new ClassesViewItem[0]);
		
	}
	
	public void switchDisplay() {
		if(stackLayout.topControl.getData() == HierControl)
			stackLayout.topControl = flatViewer.getControl();
		else stackLayout.topControl = hierViewer.getControl();
		stackLayout.topControl.requestLayout();
	}

	@Override
	public Control getControl() {
		// return hierViewer.getControl();
		// return stackLayout.topControl;
		return holder;
	}

	@Override
	public void setFocus() {
		// hierViewer.getTree().setFocus();
		stackLayout.topControl.setFocus();
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		String propName = arg0.getPropertyName();
		if(propName == SPARQLQuery.METADATA_CHANGED) {
			QueryMetadata newMD = (QueryMetadata)arg0.getNewValue();
			if(!newMD.ontologyURL.equals(ontologyURL)) {
				refreshDisplay(newMD.ontologyURL);
			}
		}
		
	}

	private void refreshDisplay(URL newURL) {
		ontologyURL = newURL;
		OntologyData ontologyData = OntologyData.getOntologyData(ontologyURL);
		ontologyData.loadOntologyData();
		
		myHierContentProvider = new ClassesViewHierContentProvider(ontologyData);
		hierViewer.setContentProvider(myHierContentProvider);
		ClassesViewItem topThing = new ClassesViewItem(ontologyData.getTopClassItem(), null, ontologyData);
		hierViewer.setInput(topThing);
		hierViewer.refresh();
		
		Tree myTree = hierViewer.getTree();
		if(myTree.getItems().length == 1)hierViewer.expandToLevel(2);
		
		flatViewer.setContentProvider(new ClassesViewFlatContentProvider(getFlatClassList(ontologyData)));
		flatViewer.setInput(topThing);
	}

}
