package uk.ac.kcl.cch.jb.sparql.editors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;

import uk.ac.kcl.cch.jb.sparql.actions.AddDataConstraintAction;
import uk.ac.kcl.cch.jb.sparql.actions.AddInstanceConstraintAction;
import uk.ac.kcl.cch.jb.sparql.actions.AddNewVariableComponentAction;
import uk.ac.kcl.cch.jb.sparql.actions.AddPropertyLinkAction;
import uk.ac.kcl.cch.jb.sparql.actions.ChangeToSubClassAction;
import uk.ac.kcl.cch.jb.sparql.actions.DeleteWhereClausePredicateAction;
import uk.ac.kcl.cch.jb.sparql.actions.SwitchOptionalSettingAction;
import uk.ac.kcl.cch.jb.sparql.model.ClassItem;
import uk.ac.kcl.cch.jb.sparql.model.ObjectPropertyItem;
import uk.ac.kcl.cch.jb.sparql.model.OntologyData;
import uk.ac.kcl.cch.jb.sparql.model.PropertyItem;
import uk.ac.kcl.cch.jb.sparql.model.VariableComponent;
import uk.ac.kcl.cch.jb.sparql.model.AnnotationPropertyItem;
import uk.ac.kcl.cch.jb.sparql.model.ClassComponent;
import uk.ac.kcl.cch.jb.sparql.model.WhereClausePredicate;
import uk.ac.manchester.cs.owl.owlapi.OWLAnnotationPropertyImpl;

public class SPARQLContextMenuProvider extends ContextMenuProvider {

	private GraphicalViewer viewer;
	private CommandStack commandStack;
	private QueryCreationEditor myEditor;
	private OntologyData ontData;
	
	private static List<AnnotationPropertyItem> standardAnnotations = null;
	
	private List<AnnotationPropertyItem> getStandardAnnotationProps(){
		if(standardAnnotations != null)return standardAnnotations;
		
		standardAnnotations = new ArrayList<AnnotationPropertyItem>();
		
		IRI iri = IRI.create("http://www.w3.org/2000/01/rdf-schema#label");
		OWLAnnotationProperty prop = new OWLAnnotationPropertyImpl(iri);
		AnnotationPropertyItem item = new AnnotationPropertyItem(prop, ontData);
		standardAnnotations.add(item);
		
		iri = IRI.create("http://www.w3.org/2000/01/rdf-schema#comment");
		prop = new OWLAnnotationPropertyImpl(iri);
		item = new AnnotationPropertyItem(prop, ontData);
		standardAnnotations.add(item);
		return standardAnnotations;
	}

	public SPARQLContextMenuProvider(GraphicalViewer viewer, CommandStack commandStack, QueryCreationEditor myEditor) {
		super(viewer);
		this.viewer = viewer;
		this.commandStack = commandStack;
		this.myEditor = myEditor;
		this.ontData = myEditor.getQuery().getOntologyData();
	}

	@Override
	public void buildContextMenu(IMenuManager menu) {
		List selected = getSelectedObjects();
		handleLinkActions(selected, menu);
		handlePredicateActions(selected, menu);
		handleConstraintActions(selected, menu);
		handleSubClassActions(selected, menu);
		handleAddVariableActions(selected, menu);

	}

	private void handleLinkActions(List selected, IMenuManager menu) {
		if(selected.size() != 2)return;
		if((!(selected.get(0) instanceof ClassComponent)) || 
				(!(selected.get(1) instanceof ClassComponent)))
			return;
		ClassComponent one = (ClassComponent)selected.get(0);
		ClassComponent two = (ClassComponent)selected.get(1);
		if(alreadyLinked(one, two))return;
		ClassItem domain, range;
		Set<ObjectPropertyItem>drprops = ontData.lookupFromDomainRange(one.getMyClass(), two.getMyClass());
		Set<ObjectPropertyItem>rdprops = ontData.lookupFromDomainRange(two.getMyClass(), one.getMyClass());
		if(((drprops != null) && (drprops.size()> 0)) || ((rdprops != null) && (rdprops.size() > 0))) {
			buildLinkActions(menu, drprops, one, two);
			buildLinkActions(menu, rdprops, two, one);
		}
	}
	
	private void buildLinkActions(IMenuManager menu, Set<ObjectPropertyItem> props, ClassComponent domain,
			ClassComponent range) {
		if(props == null)return;
		for(ObjectPropertyItem prop: props) {
			menu.add(new AddPropertyLinkAction(myEditor, prop, domain, range, commandStack));
		}
		
	}

	private boolean alreadyLinked(ClassComponent one, ClassComponent two) {
		if(one.getDomainPredicates() != null)
			for(WhereClausePredicate item: one.getDomainPredicates()) {
				if((item.getRange() != null) && (item.getRange().getID() == two.getID()))return true;
			}
		if(one.getRangePredicates() != null)
			for(WhereClausePredicate item: one.getRangePredicates()) {
				if((item.getDomain() != null) && (item.getDomain().getID() == two.getID()))return true;
			}
		return false;
	}

	protected List getSelectedObjects() {
		List rslts = new ArrayList();
		IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
		Iterator it = selection.iterator();
		while(it.hasNext()) {
			Object item = it.next();
			if(item instanceof AbstractEditPart) {
				rslts.add(((AbstractEditPart)item).getModel());
			}
		}
		return rslts;
	}
	
	private void handleConstraintActions(List selected, IMenuManager menu) {
		if(selected.size() != 1)return;
		if(selected.get(0) instanceof VariableComponent) {
			VariableComponent item = (VariableComponent)selected.get(0);
			menu.add(new AddDataConstraintAction(myEditor, item, commandStack));
			return;
		}
		if((!(selected.get(0) instanceof ClassComponent))) return;
		ClassComponent item = (ClassComponent)selected.get(0);
		Set<PropertyItem> propsPresent = new HashSet<PropertyItem>(); // what properties are already present?
		for(WhereClausePredicate pred: item.getDomainPredicates()) {
			propsPresent.add(pred.getProperty());
		}
		MenuManager constraints = new MenuManager("Constraints for "+item.getDisplayURI()+"("+item.getName()+")");
		boolean needsSubmenu = true;
		for(ObjectPropertyItem opi : item.getMyClass().getInclusiveObjectProperties()) { // what ObjectProperties have this item's class as its domain
			if(!propsPresent.contains(opi)) {
				if(needsSubmenu)menu.add(constraints);
				needsSubmenu = false;
				for(ClassItem rangeClass: opi.getRanges()) {
				    constraints.add(new AddInstanceConstraintAction(myEditor, item, opi, rangeClass, commandStack));
				}
			}
		}
	}

	private void handleSubClassActions(List selected, IMenuManager menu) {
		if(selected.size() != 1)return;
		if((!(selected.get(0) instanceof ClassComponent))) return;
		ClassComponent item = (ClassComponent)selected.get(0);
		List<ClassItem> subClasses = item.getMyClass().getSubClassItems();
		if(subClasses.size()== 0)return;
		MenuManager subClassesMenu = new MenuManager("Subclasses for "+item.getDisplayURI()+"("+item.getName()+")");
		menu.add(subClassesMenu);
		insertSubClassActionsImpl(item, item.getMyClass(), subClassesMenu, false);
	}
	
	private void insertSubClassActionsImpl(ClassComponent component, ClassItem item, IMenuManager menu, boolean doSelf) {
		if(doSelf)menu.add(new ChangeToSubClassAction(myEditor, component, item, commandStack));
		for(ClassItem subClass: item.getSubClassItems()) {
			insertSubClassActionsImpl(component, subClass, menu, true);
		}
	}

	private void handleAddVariableActions(List selected, IMenuManager menu) {
		if(selected.size() != 1)return;
		if((!(selected.get(0) instanceof ClassComponent))) return;
		ClassComponent item = (ClassComponent)selected.get(0);
		ClassItem theClass = item.getMyClass();
		Set<PropertyItem> propsPresent = new HashSet<PropertyItem>();
		Set<OWLClass> classDoneList = new HashSet<OWLClass>();
		buildPropsPresent(theClass, propsPresent, classDoneList);
		if(propsPresent.size() == 0) return;
		
		MenuManager varPropsMenu = new MenuManager("Add Variables for "+item.getDisplayURI()+"("+item.getName()+") properties");
		menu.add(varPropsMenu);
		MenuManager annPropsMenu = new MenuManager("Annotations");
		varPropsMenu.add(annPropsMenu);
		for(PropertyItem prop: propsPresent) {
			varPropsMenu.add(new AddNewVariableComponentAction(myEditor, item, prop, commandStack));
		}
		buildAnnotationProps(annPropsMenu, item);
	}

	private void buildAnnotationProps(MenuManager annPropsMenu, ClassComponent item) {
		for(PropertyItem prop: getStandardAnnotationProps()) {
			annPropsMenu.add(new AddNewVariableComponentAction(myEditor, item, prop, commandStack));
		}
		Set<AnnotationPropertyItem> props = item.getMyClass().getAnnotationProperties();
		if(props == null)return;
		for(PropertyItem prop: props) {
			annPropsMenu.add(new AddNewVariableComponentAction(myEditor, item, prop, commandStack));
		}
		
	}

	private void buildPropsPresent(ClassItem theClass, Set<PropertyItem> propsPresent, Set<OWLClass> classDoneList) {
		classDoneList.add(theClass.getOWLClass());
		if(theClass.getDataProperties() != null)
			propsPresent.addAll(theClass.getDataProperties());
		if(theClass.getObjectProperties() != null)
			propsPresent.addAll(theClass.getObjectProperties());
		for(ClassItem theSuper: theClass.getSuperClassItems()) {
			if(!classDoneList.contains(theSuper.getOWLClass())) {
				buildPropsPresent(theSuper, propsPresent, classDoneList);
				classDoneList.add(theSuper.getOWLClass());
			}
			
		}
	}
	

	private void handlePredicateActions(List selected, IMenuManager menu) {
		if(selected.size() != 1)return;
		if((!(selected.get(0) instanceof WhereClausePredicate))) return;
		WhereClausePredicate pred = (WhereClausePredicate)selected.get(0);
		menu.add(new DeleteWhereClausePredicateAction(pred, myEditor, commandStack));
		menu.add(new SwitchOptionalSettingAction(pred, myEditor, commandStack));
	}


}
