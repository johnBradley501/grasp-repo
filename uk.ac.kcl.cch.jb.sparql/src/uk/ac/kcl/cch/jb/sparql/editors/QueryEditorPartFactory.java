package uk.ac.kcl.cch.jb.sparql.editors;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.FreeformGraphicalRootEditPart;

import uk.ac.kcl.cch.jb.sparql.model.QuerySelectClause;
import uk.ac.kcl.cch.jb.sparql.model.QueryWhereClause;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery;
import uk.ac.kcl.cch.jb.sparql.model.SelectAreaFront;
import uk.ac.kcl.cch.jb.sparql.model.SelectClauseModifier;
import uk.ac.kcl.cch.jb.sparql.model.SelectVarItem;
import uk.ac.kcl.cch.jb.sparql.model.ClassComponent;
import uk.ac.kcl.cch.jb.sparql.model.DataConstraint;
import uk.ac.kcl.cch.jb.sparql.model.InstanceComponent;
import uk.ac.kcl.cch.jb.sparql.model.ModifierComponent;
import uk.ac.kcl.cch.jb.sparql.model.VariableComponent;
import uk.ac.kcl.cch.jb.sparql.model.WhereClausePredicate;
import uk.ac.kcl.cch.jb.sparql.parts.ClassComponentPart;
import uk.ac.kcl.cch.jb.sparql.parts.DataConstraintPart;
import uk.ac.kcl.cch.jb.sparql.parts.InstanceComponentPart;
import uk.ac.kcl.cch.jb.sparql.parts.QueryRootEditPart;
import uk.ac.kcl.cch.jb.sparql.parts.QuerySelectClausePart;
import uk.ac.kcl.cch.jb.sparql.parts.QueryWhereClausePart;
import uk.ac.kcl.cch.jb.sparql.parts.SelectAreaFrontPart;
import uk.ac.kcl.cch.jb.sparql.parts.SelectVarItemPart;
import uk.ac.kcl.cch.jb.sparql.parts.VariableComponentPart;
import uk.ac.kcl.cch.jb.sparql.parts.WhereClauseComponentPart;
import uk.ac.kcl.cch.jb.sparql.parts.WhereClausePredicatePart;

public class QueryEditorPartFactory extends WorkbenchAwarePartFactory {
	
	QueryWhereClausePart myQWC = null;

	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		QueryCreationEditor myEditor = (QueryCreationEditor)getWorkbenchPart();
		if(model instanceof SPARQLQuery) {
			return new QueryRootEditPart((SPARQLQuery)model, myEditor);
		}
		if(model instanceof QuerySelectClause) {
			return new QuerySelectClausePart((QuerySelectClause)model, myEditor );
		}
		if(model instanceof QueryWhereClause) {
			myQWC = new QueryWhereClausePart((QueryWhereClause)model, myEditor);
			return myQWC;
		}
		if(model instanceof ClassComponent) {
			return new ClassComponentPart((ClassComponent)model, myEditor);
		}
		if(model instanceof InstanceComponent) {
			return new InstanceComponentPart((InstanceComponent)model, myEditor);
		}
		if(model instanceof VariableComponent) {
			return new VariableComponentPart((VariableComponent)model, myEditor);
		}
		if(model instanceof WhereClausePredicate) {
			return new WhereClausePredicatePart((WhereClausePredicate)model, myEditor);
		}
		//if(model instanceof SelectClauseModifier) {
		//	return new SelectClauseModifierPart((SelectClauseModifier)model, myEditor);
		//}
		if(model instanceof SelectAreaFront) {
			return new SelectAreaFrontPart((SelectAreaFront)model, myEditor);
		}
		//if(model instanceof ModifierComponent) {
		//	return new ModifierComponentPart((ModifierComponent)model, myEditor);
		//}
		if(model instanceof SelectVarItem) {
			return new SelectVarItemPart((SelectVarItem)model, myEditor);
		}
		if(model instanceof DataConstraint) {
			return new DataConstraintPart((DataConstraint)model, myEditor);
		}
		return null;
	}
	
	public QueryWhereClausePart getQueryWhereClausePart() { return myQWC; }

}
