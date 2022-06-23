package uk.ac.kcl.cch.jb.sparql.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editparts.FreeformGraphicalRootEditPart;
// import org.eclipse.gef.editparts.SimpleRootEditPart;

import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.figures.EditorRootFigure;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery;

public class QueryRootEditPart extends AbstractGraphicalEditPart {

	private QueryCreationEditor editor;

	public QueryRootEditPart(SPARQLQuery model, QueryCreationEditor queryCreationEditor) {
		setModel(model);
		this.editor = queryCreationEditor;
	}
	
	public SPARQLQuery getQuery() {
		return (SPARQLQuery)getModel();
	}
	
	protected void addChildVisual(EditPart childEditPart,int index) {
		super.addChildVisual(childEditPart, index);
		if(childEditPart instanceof QueryWhereClausePart) {
			QueryWhereClausePart part = (QueryWhereClausePart)childEditPart;
			getFigure().getLayoutManager().setConstraint(part.getFigure(), BorderLayout.CENTER);
		}
		if(childEditPart instanceof QuerySelectClausePart) {
			QuerySelectClausePart part = (QuerySelectClausePart)childEditPart;
			getFigure().getLayoutManager().setConstraint(part.getFigure(), BorderLayout.TOP);
		}
	}


	@Override
	protected IFigure createFigure() {
		return new EditorRootFigure();
	}

	@Override
	protected void createEditPolicies() {
		// none needed JB
		
	}
	
	public List getModelChildren() {
		List children = new ArrayList();
		children.add(getQuery().getWhereClause());
		children.add(getQuery().getSelectClause());
		return children;
	}

}
