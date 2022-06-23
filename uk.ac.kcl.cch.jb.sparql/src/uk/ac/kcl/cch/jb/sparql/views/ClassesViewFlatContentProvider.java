package uk.ac.kcl.cch.jb.sparql.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;

public class ClassesViewFlatContentProvider implements IStructuredContentProvider {
	
	private ClassesViewItem[] items;

	public ClassesViewFlatContentProvider(ClassesViewItem[] items) {
		this.items = items;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return items;
	}

}
