package uk.ac.kcl.cch.jb.sparql.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.ClassExpressionNotInProfileException;
import org.semanticweb.owlapi.reasoner.FreshEntitiesException;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;
import org.semanticweb.owlapi.reasoner.TimeOutException;

import uk.ac.kcl.cch.jb.sparql.model.OntologyData;

public class ClassesViewHierContentProvider implements IStructuredContentProvider, ITreeContentProvider {

	private OntologyData ontologyData;

	public ClassesViewHierContentProvider(OntologyData ontologyData) {
		this.ontologyData = ontologyData;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		ClassesViewItem item = (ClassesViewItem)parentElement;
		return item.getSubClassItems().toArray();
	}

	@Override
	public Object getParent(Object element) {
		ClassesViewItem item = (ClassesViewItem)element;
		return item.getMyParent();
	}

	@Override
	public boolean hasChildren(Object element) {
		ClassesViewItem item = (ClassesViewItem)element;
		return item.getSubClassItems().size() > 0;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement); 
	}

}
