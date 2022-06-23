package uk.ac.kcl.cch.jb.sparql.views;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.ClassExpressionNotInProfileException;
import org.semanticweb.owlapi.reasoner.FreshEntitiesException;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;
import org.semanticweb.owlapi.reasoner.TimeOutException;

import uk.ac.kcl.cch.jb.sparql.model.ClassItem;
import uk.ac.kcl.cch.jb.sparql.model.OntologyData;

public class ClassesViewItem {

	private ClassItem cls;
	private ClassesViewItem parent;
	private OntologyData ontData;
	private String className = null;
	private List<ClassesViewItem> subClassItems = null; // new ArrayList<ClassesViewItem>();
	
	public ClassesViewItem(ClassItem cls, ClassesViewItem parent, OntologyData ontData) {
		this.cls = cls;
		this.parent = parent;
		this.ontData = ontData;
	}
	
	// public OWLClass getMyClass() {return cls.getOWLClass();}
	public ClassItem getMyClassItem() {return cls;}
	public ClassesViewItem getMyParent() {return parent;}
	public String getClassName() throws OWLOntologyCreationException {
		if(className == null) {
			className = ontData.getIRIDisplay(cls.getOWLClass().getIRI());
		}
		return className;
	}
	public OntologyData getOntology() {return ontData; }
	
	public List<ClassesViewItem> getSubClassItems(){
		if(subClassItems != null)return subClassItems;
		/* NodeSet<OWLClass> nodes = null;
		try {
			// about "true", see https://owlcs.github.io/owlapi/apidocs_4/org/semanticweb/owlapi/reasoner/OWLReasoner.html#getSubClasses-org.semanticweb.owlapi.model.OWLClassExpression-boolean-   JB
			nodes = ontData.getReasoner().getSubClasses(cls), true); // false);
		} catch (TimeOutException | FreshEntitiesException | InconsistentOntologyException
				| ClassExpressionNotInProfileException | ReasonerInterruptedException
				| OWLOntologyCreationException e) {
			throw new RuntimeException("Failure to process ontology.", e);
		}
		subClassItems = new ArrayList<ClassesViewItem>();
		for (Node<OWLClass> node : nodes) {
		    if(!node.isBottomNode() && !node.isTopNode()){
		    	subClassItems.add(new ClassesViewItem(node.getRepresentativeElement(), this, ontData));
		    }
		  } */
		subClassItems = new ArrayList<ClassesViewItem>();
		for (ClassItem item : cls.getSubClassItems()) {
			subClassItems.add(new ClassesViewItem(item, this, ontData));
		}
		return subClassItems;

	}
	
}
