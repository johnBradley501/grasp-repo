package uk.ac.kcl.cch.jb.sparql.views;


import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.kcl.cch.jb.sparql.Activator;

public class ClassesViewLabelProvider implements ILabelProvider {
	
	Image classImage = null;
	
	private Image getClassImage() {
		if(classImage == null) {
			classImage = Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/class.gif").createImage();
		}
		return classImage;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// nothing to do here... JB
	}

	@Override
	public void dispose() {
		if(classImage != null) {
			classImage.dispose();
			classImage = null;
		}
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// not needed here   JB
	}

	@Override
	public Image getImage(Object element) {
		return getClassImage();
	}

	@Override
	public String getText(Object element) {
		ClassesViewItem item = (ClassesViewItem)element;
		try {
			return item.getClassName();
		} catch (OWLOntologyCreationException e) {
			throw new RuntimeException("Failure to process Ontology.", e);
		}
	}

}
