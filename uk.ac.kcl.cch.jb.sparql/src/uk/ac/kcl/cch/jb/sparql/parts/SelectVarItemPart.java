package uk.ac.kcl.cch.jb.sparql.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.LocationRequest;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import uk.ac.kcl.cch.jb.sparql.builder.ExpressionMaker;
import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.figures.SelectVarItemFigure;
import uk.ac.kcl.cch.jb.sparql.figures.VariableComponentFigure;
import uk.ac.kcl.cch.jb.sparql.model.ClassComponent;
import uk.ac.kcl.cch.jb.sparql.model.INamedComponent;
import uk.ac.kcl.cch.jb.sparql.model.VariableComponent;
import uk.ac.kcl.cch.jb.sparql.model.SelectVarItem;
import uk.ac.kcl.cch.jb.sparql.policies.SelectVarItemComponentPolicy;
import uk.ac.kcl.cch.jb.sparql.policies.SelectVarItemDirectEditPolicy;
import uk.ac.kcl.cch.jb.sparql.policies.SelectVarItemSelectEditPolicy;

public class SelectVarItemPart extends AbstractGraphicalEditPart implements PropertyChangeListener, NamedComponentPart{

	private QueryCreationEditor myEditor;
	private SelectVarItemDirectEditPolicy directEditPolicy;
	private String sourceType = null;
	private String[] dropDownNames = null;
	private int[] dropDownValues = null;

	public SelectVarItemPart(SelectVarItem model, QueryCreationEditor myEditor) {
		super();
		setModel(model);
		this.myEditor = myEditor;
		INamedComponent source = getNamedComponent();
		if(source instanceof ClassComponent)sourceType = "anyURI";
		else sourceType = ((VariableComponent)source).getDataType().getFragment();
		dropDownNames = ExpressionMaker.instance.getAggregateNameList(sourceType);
		dropDownValues = ExpressionMaker.instance.getAggregateValLists(sourceType);
	}
	
	public SelectVarItem getVarItem() {
		return (SelectVarItem)getModel();
	}
	
	public INamedComponent getNamedComponent() {
		return getVarItem().getComponent();
	}
	
	public int getChosenAggType(int item) {
		return this.dropDownValues[item];
	}


	@Override
	protected IFigure createFigure() {
		return new SelectVarItemFigure(getVarItem());
	}
	
	protected SelectVarItemFigure getMyFigure() {return (SelectVarItemFigure)getFigure();}
	
	public void activate() {
		if(!isActive()){
			super.activate();
			getNamedComponent().addPropertyChangeListener(this);
			getVarItem().addPropertyChangeListener(this);
		}
	}
	
	public void deactivate() {
		if(isActive()) {
			super.deactivate();
			getVarItem().removePropertyChangeListener(this);
			getNamedComponent().removePropertyChangeListener(this);
		}
	}
	
	public void performRequest(Request req) {
		if(req.getType() == RequestConstants.REQ_DIRECT_EDIT ||
				req.getType() == RequestConstants.REQ_OPEN) {
			performDirectEditing((LocationRequest)req);
		}
	}

	protected void performDirectEditing(LocationRequest req) {
		int myType = getMyFigure().getSelectedType(req.getLocation());
		directEditPolicy.setField(myType);
		if(myType == SelectVarItem.VAR_COMPONENT) {
			Label label = getMyFigure().getNameLabel();
			NamedComponentDirectEditManager manager =
					new NamedComponentDirectEditManager(this, new NamedComponentEditorLocator(label), label);
			manager.show();
			
		} else if(myType == SelectVarItem.ORDER_COMPONENT) {
			Label label = getMyFigure().getOrderLabel();
			SimpleDropDownDirectEditManager manager =
					new SimpleDropDownDirectEditManager(this, new SimpleDropDownEditorLocator(label), SelectVarItem.orderings, getVarItem().getSortOrder());
			manager.show();
		} else if(myType == SelectVarItem.AGGREGATE_COMPONENT) {
			Label label = getMyFigure().getAggregateLabel();
			DirectEditManager man = new ValuedDropDownDirectEditManager(this, new SimpleDropDownEditorLocator(label),
					dropDownNames,dropDownValues, getVarItem().getAggType());
			man.show();
		} else if(myType == SelectVarItem.AGG_NAME_COMPONENT) {
			Label label = getMyFigure().getAggNameLabel();
			AggNameDirectEditManager manager =
					new AggNameDirectEditManager(this, new NamedComponentEditorLocator(label), getVarItem().getAggVarName());
			manager.show();
		}
	}


	@Override
	protected void createEditPolicies() {
		this.directEditPolicy = new SelectVarItemDirectEditPolicy(myEditor);
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, directEditPolicy);
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new SelectVarItemComponentPolicy(myEditor));
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new SelectVarItemSelectEditPolicy(myEditor));

	}

	@Override
	public VariableComponentFigure getVariableComponentFigure() {
		// not needed in this context   JB
		return null;
	}
	
	public void handleHover(boolean isHovering) {
		// System.out.println("Hovering status: "+isHovering);
		getMyFigure().handleHover(isHovering);
		List eps = getParent().getParent().getChildren();
		for(Object p: eps) {
			if(p instanceof QueryWhereClausePart) {
				QueryWhereClausePart qwcp = (QueryWhereClausePart)p;
				EditPart tep = qwcp.findPartForModel(getVarItem().getComponent());
				if(tep == null);
				if(!(tep instanceof NamedComponentPart))return;
				NamedComponentPart ncp = (NamedComponentPart)tep;
				ncp.handleHover(isHovering);
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		String prop = arg0.getPropertyName();
		if(prop==INamedComponent.NEW_NAME)
			getMyFigure().setName(getNamedComponent().getName());
		else if(prop==SelectVarItem.CHANGED_ORDER)
			getMyFigure().setOrderLabelText(SelectVarItem.orderings[getVarItem().getSortOrder()]);
		else if(prop==SelectVarItem.CHANGED_AGGTYPE)
			getMyFigure().getAggregateLabel().setText(getVarItem().getAggTypeName());
		else if(prop==SelectVarItem.CHANGED_AGGVARNAME)
			getMyFigure().setAggName(getVarItem().getAggVarName());
		
	}
	
	
	//public EditPart getTargetEditPart(Request request) {
	//	EditPart result = super.getTargetEditPart(request);
	//	System.out.println("SelectVarItemPart: Target "+result);
	//	return result;
	//}

	

}
