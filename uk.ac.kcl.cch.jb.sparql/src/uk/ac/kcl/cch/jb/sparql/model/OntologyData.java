package uk.ac.kcl.cch.jb.sparql.model;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

public class OntologyData {
	
//	private static final Set<ObjectPropertyItem> emptyObjectPropertySet = new HashSet<ObjectPropertyItem>();

	private static Map<URL, OntologyData> ontologies = new HashMap<URL, OntologyData>();

	private static OWLOntologyManager man = OWLManager.createOWLOntologyManager();
	private static OWLReasonerFactory rf = new ReasonerFactory();

	public static IRI OWLThingClass = IRI.create("http://www.w3.org/2002/07/owl#Thing");
	public static IRI OWLNothingClass = IRI.create("http://www.w3.org/2002/07/owl#Nothing");
	public static OWLDataFactory df = man.getOWLDataFactory();

	public static OWLClass getOWLThingClass() {
		return df.getOWLClass(OWLThingClass)
				;	}

	public static OntologyData getOntologyData(URL ontURL) {
		if(ontologies.containsKey(ontURL))return ontologies.get(ontURL);
		OntologyData newOnt = new OntologyData(ontURL);
		ontologies.put(ontURL,  newOnt);
		return newOnt;
	}


	private URL ontologyURL;
	private OWLOntology myOwlOntology = null;
	private OWLReasoner myReasoner = null;
	private PrefixOWLOntologyFormat docFormat = null;
	private Map<String, ClassItem> classItems = new HashMap<String, ClassItem>();
	private Map<String, ObjectPropertyItem> objectProperties = new HashMap<String, ObjectPropertyItem>();
	private Map<String, DataPropertyItem> dataProperties = new HashMap<String, DataPropertyItem>();
	private Map<String, AnnotationPropertyItem> annotationProperties = new HashMap<String, AnnotationPropertyItem>();
	private Map<String, Set<ObjectPropertyItem>> drLookup = new HashMap<String, Set<ObjectPropertyItem>>();
	private ClassItem topClassItem = null;
	private boolean objectPropertiesLoaded = false;


	private OntologyData(URL ontURL) {
		this.ontologyURL = ontURL;

	}

	public OWLOntology getOWLOntology() throws OWLOntologyCreationException {
		if(myOwlOntology == null) {
			myOwlOntology = man.loadOntology(IRI.create(ontologyURL.toString()));
		}
		return myOwlOntology;
	}

	public OWLReasoner getReasoner() throws OWLOntologyCreationException {
		if(myReasoner != null)return myReasoner;

		try {
			// PlatformUI.getWorkbench().getProgressService().busyCursorWhile(new IRunnableWithProgress() {
			// see p 407 in Gamma et al, Eclipse: Building Commercial-Quality Plugins
			IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			win.run(false, false, new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						try {
							monitor.beginTask("Loading Ontology",0);

							myReasoner = rf.createReasoner(getOWLOntology());
							myReasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY, 
									InferenceType.DATA_PROPERTY_HIERARCHY, InferenceType.OBJECT_PROPERTY_HIERARCHY);
						} catch (OWLOntologyCreationException e) {
							throw new InvocationTargetException(e);
						}
					}

				});
		} catch (InvocationTargetException | InterruptedException e) {
			throw new RuntimeException("Loading of ontology failed.", e);
		}
		// myReasoner = rf.createReasoner(getOWLOntology());
		// myReasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY, InferenceType.DATA_PROPERTY_HIERARCHY, InferenceType.OBJECT_PROPERTY_HIERARCHY);
		return myReasoner;
	}


	public PrefixOWLOntologyFormat getDocumentFormat() throws OWLOntologyCreationException {
		if(docFormat != null)return docFormat;
		OWLOntologyFormat form = man.getOntologyFormat(getOWLOntology());
		if(form.isPrefixOWLOntologyFormat()) {
			docFormat = form.asPrefixOWLOntologyFormat();
		}
		return docFormat;
	}

	public Map<String, String> getPrefixName2PrefixMap() {
		try {
			if(getDocumentFormat() == null)return new HashMap<String, String>();
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
			return new HashMap<String, String>();
		}
		return docFormat.getPrefixName2PrefixMap();
	}
	
	public String getDefaultPrefix() {
		if(docFormat != null)return docFormat.getDefaultPrefix();
		return null;
	}

	public String getIRIDisplay(IRI iri) throws OWLOntologyCreationException {
		if(getDocumentFormat() == null) return iri.toQuotedString();
		String rslt = docFormat.getPrefixIRI(iri);
		if(rslt == null)return iri.toQuotedString();
		return rslt;
	}
	
	public Collection<ClassItem> getClassItems() {
		return classItems.values();
	}

	public ClassItem getTopClassItem() {
		return topClassItem;
	}

	public ClassItem find(OWLClass cls) {
		String iri = cls.getIRI().toString();
		if(classItems.containsKey(iri)) {
			return classItems.get(iri);
		}
		ClassItem newItem = new ClassItem(cls, this);
		classItems.put(iri, newItem);
		return newItem;
	}

	public ObjectPropertyItem find(OWLObjectProperty prop) {
		String iri = prop.getIRI().toString();
		if(objectProperties.containsKey(iri)) {
			return objectProperties.get(iri);
		}
		ObjectPropertyItem newItem = new ObjectPropertyItem(prop, this);
		objectProperties.put(iri, newItem);
		return newItem;
	}

	public DataPropertyItem find(OWLDataProperty prop) {
		String iri = prop.getIRI().toString();
		if(dataProperties.containsKey(iri)) {
			return dataProperties.get(iri);
		}
		DataPropertyItem newItem = new DataPropertyItem(prop, this);
		dataProperties.put(iri, newItem);
		return newItem;
	}

	public AnnotationPropertyItem find(OWLAnnotationProperty prop) {
		String iri = prop.getIRI().toString();
		if(annotationProperties.containsKey(iri)) {
			return annotationProperties.get(iri);
		}
		AnnotationPropertyItem newItem = new AnnotationPropertyItem(prop, this);
		annotationProperties.put(iri, newItem);
		return newItem;
	}

	public void loadOntologyData() {
		buildClassHierarchy();
		try {
			buildPropertiesData();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void buildClassHierarchy() {
		if(topClassItem != null)return;
		topClassItem = find(getOWLThingClass());
		processBuildingClass(topClassItem);
	}

	private void processBuildingClass(ClassItem classItem) {
		Iterator<ClassItem> it = classItem.getSubClassItems().iterator();
		while(it.hasNext()) {
			processBuildingClass(it.next());
		}
	}

	private void buildPropertiesData() throws OWLOntologyCreationException {
		if(objectPropertiesLoaded)return;
		objectPropertiesLoaded = true;
		// objectProperties = new HashMap<String, ObjectPropertyItem>();
		processPropertyDomains();
		processPropertyRanges();
		buildDomainRangeLookup();

	}
	
	// is using reasoner better here?
	// https://owlcs.github.io/owlapi/apidocs_4/org/semanticweb/owlapi/reasoner/OWLReasoner.html

	private void processPropertyDomains() throws OWLOntologyCreationException {
		// first for ObjectProperties...
		for (OWLObjectPropertyDomainAxiom op : getOWLOntology().getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN)) {
			Set<OWLClass> classes = op.getDomain().getClassesInSignature();
			if(!classes.isEmpty()) {
				for(OWLObjectProperty oop : op.getObjectPropertiesInSignature()){
					ObjectPropertyItem item = find(oop);
					for(OWLClass cls: classes) {
						ClassItem clsItm = find(cls);
						item.addDomain(clsItm);
						clsItm.addObjectPropertyItem(item);
					}
				}
			}
		}
		// ... then for DataProperties
		for (OWLDataPropertyDomainAxiom op : getOWLOntology().getAxioms(AxiomType.DATA_PROPERTY_DOMAIN)) {
			Set<OWLClass> classes = op.getDomain().getClassesInSignature();
			if(!classes.isEmpty()) {
				for(OWLDataProperty oop : op.getDataPropertiesInSignature()){
					DataPropertyItem item = find(oop);
					for(OWLClass cls: classes) {
						ClassItem clsItm = find(cls);
						item.addDomain(clsItm);
						clsItm.addDataPropertyItem(item);
					}
				}
			}
		}
		
		// https://www.programcreek.com/java-api-examples/?class=org.semanticweb.owlapi.model.OWLOntology&method=getAnnotationAssertionAxioms
		
		for (OWLAnnotationPropertyDomainAxiom op: getOWLOntology().getAxioms(AxiomType.ANNOTATION_PROPERTY_DOMAIN)) {
			Set<OWLClass> classes = op.getDomain().getClassesInSignature();
			OWLAnnotationProperty oap = op.getProperty();
			Set<OWLDatatype> a = oap.getDatatypesInSignature();
			// System.out.println("Annotation type: "+oap.getEntityType());
			// System.out.println("isOWLDataProperty: "+oap.isOWLDataProperty()+", isOWLObjectProperty: "+oap.isOWLDataProperty()+", isOWLDatatype: "+oap.isOWLDatatype());
			AnnotationPropertyItem item = find(oap);
			OWLClass domain = df.getOWLClass(op.getDomain());
			ClassItem domainItm = find(domain);
			item.addDomain(domainItm);
			domainItm.addAnnotationPropertyItem(item);
			//if(!classes.isEmpty()) {
			//	for(OWLAnnotation annot: op.getAnnotations()) {
			//		OWLAnnotationProperty oap = annot.getProperty();
			//		AnnotationPropertyItem newOne = find(oap);
			//		for(OWLClass cls: classes) {
			//			ClassItem clsItm = find(cls);
			//			newOne.addDomain(clsItm);
			//			clsItm.addAnnotationPropertyItem(newOne);
						
			//		}
			//	}
			//}
			
		}

	}

	private void processPropertyRanges() throws OWLOntologyCreationException{
		// first for ObjectProperties...
		for (OWLObjectPropertyRangeAxiom op : getOWLOntology().getAxioms(AxiomType.OBJECT_PROPERTY_RANGE)) {
			Set<OWLClass> classes = op.getRange().getClassesInSignature();
			if(!classes.isEmpty()) {
				for(OWLObjectProperty oop : op.getObjectPropertiesInSignature()){
					ObjectPropertyItem item = find(oop);
					for(OWLClass cls: classes) {
						item.addRange(find(cls));
						// ClassItem clsItm = find(cls);
						// clsItm.addObjectPropertyItem(newOne);
					}
				}
			}
		}
		// ... then for DataProperties
		for (OWLDataPropertyRangeAxiom op : getOWLOntology().getAxioms(AxiomType.DATA_PROPERTY_RANGE)) {
			// Set<OWLClass> classes = op.getRange().getClassesInSignature();
			Set<OWLDatatype> types = op.getRange().getDatatypesInSignature();
			if(!types.isEmpty()) {
				for(OWLDataProperty oop : op.getDataPropertiesInSignature()){
					DataPropertyItem item = find(oop);
					for(OWLDatatype type: types) {
						item.addDatatype(type);
					}
				}
			}
		}
		
		for (OWLAnnotationPropertyRangeAxiom op: getOWLOntology().getAxioms(AxiomType.ANNOTATION_PROPERTY_RANGE)) {
			Set<OWLClass> classes = op.getRange().getClassesInSignature();
			OWLAnnotationProperty oap = op.getProperty();
			// System.out.println("Annotation type:"+oap.getEntityType());
			AnnotationPropertyItem item = find(oap);
			item.setAnnotation(true);
			OWLClass range = df.getOWLClass(op.getRange());
			ClassItem rangeItm = find(range);
			//item.addRange(rangeItm);
			rangeItm.addAnnotationPropertyItem(item);

		}
	}
		


	private void buildDomainRangeLookup() {
		for (ObjectPropertyItem itm: objectProperties.values()) {
			Set<ClassItem> domains = itm.getDomains();
			Set<ClassItem> ranges = itm.getRanges();
			for(ClassItem domain: domains) {
				for(ClassItem range: ranges) {
					addSubDomainItems(itm, domain, range);
				}
			}

		}

	}
	
	private void addSubDomainItems(ObjectPropertyItem itm, ClassItem domain, ClassItem range) {
		//addItem(itm, domain, range);
		//List<ClassItem> subRanges = range.getSubClassItems();
		addSubRangeItems(itm, domain, range);
		for(ClassItem subDomain: domain.getSubClassItems()) {
			//addSubRangeItems(itm, subDomain, range);
			addSubDomainItems(itm, subDomain, range);
		}
	}
	
	private void addSubRangeItems(ObjectPropertyItem itm, ClassItem domain, ClassItem range) {
		addItem(itm, domain, range);
		List<ClassItem> subRanges = range.getSubClassItems();
		for(ClassItem subRange: subRanges) {
			addSubRangeItems(itm, domain, subRange);
			//addItem(itm, domain, subRange);
			//for(ClassItem subDomain: domain.getSubClassItems()) {
			//	addSubRangeItems(itm, subDomain, ranges);
			//}
		}
	}

	public Set<ObjectPropertyItem> lookupFromDomainRange(ClassItem domain, ClassItem range){
		String domainIRI = domain.getOWLClass().getIRI().toString();
		String rangeIRI = range.getOWLClass().getIRI().toString();
		String key = domainIRI+"\t"+rangeIRI;
		if(drLookup.containsKey(key))return drLookup.get(key);
		return null;
	}

	
	private void addItem(ObjectPropertyItem itm, ClassItem domain, ClassItem range) {
		String domainIRI = domain.getOWLClass().getIRI().toString();
		String rangeIRI = range.getOWLClass().getIRI().toString();
		String key = domainIRI+"\t"+rangeIRI;
		// System.out.println(key);
		Set<ObjectPropertyItem> props = null;
		if(drLookup.containsKey(key))	props = drLookup.get(key);
		else {
			props = new HashSet<ObjectPropertyItem>();
			drLookup.put(key,  props);
		}
		props.add(itm);
	}
/*	
	private Set<ObjectPropertyItem> handleSuperRanges(ClassItem domain, ClassItem range){
		Set<ObjectPropertyItem> rslt = lookupFromDomainRangeDetail(domain, range);
		if(rslt != null)return rslt;
		Set<ClassItem> superRanges = range.getSuperClassItems();
		for(ClassItem item: superRanges) {
			rslt = lookupFromDomainRangeDetail(domain, item);
			if(rslt != null)return rslt;
		}
		return null;
	}
	
	public Set<ObjectPropertyItem> lookupFromDomainRange(ClassItem domain, ClassItem range){
		Set<ObjectPropertyItem> rslt = lookupFromDomainRangeDetail(domain, range);
		if(rslt != null)return rslt;
		Set<ClassItem> superDomains = domain.getSuperClassItems();
		for(ClassItem item: superDomains) {
			rslt = lookupFromDomainRangeDetail(item, range);
			if(rslt != null)return rslt;
		}
		return null;
	} */

}
