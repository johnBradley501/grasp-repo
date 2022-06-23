package uk.ac.kcl.cch.jb.sparql.model;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.ClassExpressionNotInProfileException;
import org.semanticweb.owlapi.reasoner.FreshEntitiesException;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;
import org.semanticweb.owlapi.reasoner.TimeOutException;

import uk.ac.kcl.cch.jb.sparql.views.ClassesViewItem;

public class ClassItem {
	
	/* private static Map<String, ClassItem> items = new HashMap<String, ClassItem>();

	
	public static ClassItem find(OWLClass cls, OntologyData ontData) {
		String iri = cls.getIRI().toString();
		if(items.containsKey(iri)) {
			return items.get(iri);
		}
		ClassItem newItem = new ClassItem(cls, ontData);
		items.put(iri, newItem);
		return newItem;
	}     See this in OntologyData now... */
	
	private OWLClass cls;
	private OntologyData ontData;
	private List<ClassItem> subClassItems = null;
	private Set<ClassItem> superClassItems = new HashSet<ClassItem>();
	private Set<DataPropertyItem> dataProperties = new HashSet<DataPropertyItem>();
	private Set<ObjectPropertyItem> objectProperties = new HashSet<ObjectPropertyItem>();
	private Set<AnnotationPropertyItem> annotationProperties = new HashSet<AnnotationPropertyItem>();

	ClassItem(OWLClass cls, OntologyData ontData) {
		this.cls = cls;
		this.ontData = ontData;
	}
	
	public OWLClass getOWLClass() {
		return cls;
	}
	
	public Set<ClassItem>getSuperClassItems(){
		return superClassItems;
	}
	
	public void addSuperClassItem(ClassItem superItem) {
		superClassItems.add(superItem);
	}
	
	public String getDisplayURI() {
		try {
			return ontData.getIRIDisplay(cls.getIRI());
		} catch (OWLOntologyCreationException e) {
			return cls.getIRI().toQuotedString();
		}
	}
	
	public List<ClassItem> getSubClassItems(){
		if(subClassItems != null)return subClassItems;
		NodeSet<OWLClass> nodes = null;
		try {
			// about "true", see https://owlcs.github.io/owlapi/apidocs_4/org/semanticweb/owlapi/reasoner/OWLReasoner.html#getSubClasses-org.semanticweb.owlapi.model.OWLClassExpression-boolean-   JB
			nodes = ontData.getReasoner().getSubClasses(cls, true); // false);
		} catch (TimeOutException | FreshEntitiesException | InconsistentOntologyException
				| ClassExpressionNotInProfileException | ReasonerInterruptedException
				| OWLOntologyCreationException e) {
			throw new RuntimeException("Failure to process ontology.", e);
		}
		subClassItems = new ArrayList<ClassItem>();
		for (Node<OWLClass> node : nodes) {
		    if(!node.isBottomNode() && !node.isTopNode()){
		    	ClassItem theItem = ontData.find(node.getRepresentativeElement());
		    	subClassItems.add(theItem);
		    	theItem.addSuperClassItem(this);
		    }
		  }
		return subClassItems;
	}
	
	public Set<ObjectPropertyItem> getObjectProperties(){
		return objectProperties;
	}
	
	public Set<ObjectPropertyItem> getInclusiveObjectProperties(){
		Set<ObjectPropertyItem> rslt = new HashSet<ObjectPropertyItem>();
		buildInclusiveObjectProperties(rslt);
		return rslt;
	}
	
	protected void buildInclusiveObjectProperties(Set<ObjectPropertyItem> list){
		list.addAll(objectProperties);
		for(ClassItem superItem: this.superClassItems) {
			superItem.buildInclusiveObjectProperties(list);
		}
	}
	
	public void addObjectPropertyItem(ObjectPropertyItem item) {
		objectProperties.add(item);
	}
	
	public Set<DataPropertyItem> getDataProperties(){
		return dataProperties;
	}
	
	public void addDataPropertyItem(DataPropertyItem item) {
		dataProperties.add(item);
	}
	
	public Set<AnnotationPropertyItem> getAnnotationProperties(){
		return annotationProperties;
	}
	
	public void addAnnotationPropertyItem(AnnotationPropertyItem item) {
		annotationProperties.add(item);
	}
	
	public String toString() {
		if(cls != null) return cls.getIRI().toString()+","+super.hashCode();
		return "null class,"+super.hashCode();
	}

}
