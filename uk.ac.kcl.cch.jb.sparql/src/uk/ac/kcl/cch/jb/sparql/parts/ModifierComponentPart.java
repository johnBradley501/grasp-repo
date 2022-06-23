package uk.ac.kcl.cch.jb.sparql.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.Clickable;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import uk.ac.kcl.cch.jb.sparql.commands.ResetDistinctFlagCommand;
import uk.ac.kcl.cch.jb.sparql.editors.QueryCreationEditor;
import uk.ac.kcl.cch.jb.sparql.model.ModifierComponent;
import uk.ac.kcl.cch.jb.sparql.model.SelectClauseModifier;
import uk.ac.kcl.cch.jb.sparql.policies.ModifierComponentDirectEditPolicy;

public class ModifierComponentPart extends AbstractGraphicalEditPart implements PropertyChangeListener{

	private QueryCreationEditor myEditor;
	private Clickable distinctButton = null;
	private Label distinctLabel = new Label();
	private Label otherLabel = null;
	private CommandStack commandStack;

	public ModifierComponentPart(ModifierComponent model, QueryCreationEditor myEditor) {
		super();
		setModel(model);
		this.myEditor = myEditor;
		this.commandStack = myEditor.getCommandStack();
	}
	
	public ModifierComponent getModifierComponent() {
		return (ModifierComponent)getModel();
	}
	
	private SelectClauseModifier getModifier() {
		return getModifierComponent().getModifier();
	}
	
	public int getMyType() {return getModifierComponent().getType(); }
	
	public void activate() {
		if(!isActive()){
			super.activate();
			getModifier().addPropertyChangeListener(this);
		}
	}
	
	public void deactivate() {
		if(isActive()) {
			super.deactivate();
			getModifier().removePropertyChangeListener(this);
		}
	}


	@Override
	protected IFigure createFigure() {
		if(getMyType() == ModifierComponent.DISTINCT_COMPONENT)
			return makeDistinctButton();
		otherLabel = new Label();
		otherLabel.setPreferredSize(new Dimension(60,16));
		otherLabel.setBorder(new LineBorder());
		if(getMyType() == ModifierComponent.LIMIT_COMPONENT) {
			setNumericLabel(getModifier().getLimit());
			otherLabel.setToolTip(new Label("Query Limit:"));
		}
		else if(getMyType() == ModifierComponent.OFFSET_COMPONENT) {
			setNumericLabel(getModifier().getOffset());
			otherLabel.setToolTip(new Label("Query Offset:"));
		}
		return otherLabel;
	}

	private IFigure makeDistinctButton() {
		setDistinctButtonLabel();
		distinctButton = new Clickable(distinctLabel);
		distinctButton.setPreferredSize(new Dimension(60,16));
		distinctButton.setBorder(new LineBorder());
		distinctButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				commandStack.execute(new ResetDistinctFlagCommand(getModifier(), myEditor));
				//getModifier().setDistinct(!getModifier().isDistinct());
			}
			
		});
		return distinctButton;
	}
	
	private void setDistinctButtonLabel() {
		if(getModifier().isDistinct())distinctLabel.setText("Distinct");
		else distinctLabel.setText("Multiple");
	}

	@Override
	protected void createEditPolicies() {
		if((getMyType() == ModifierComponent.LIMIT_COMPONENT) || (getMyType() == ModifierComponent.OFFSET_COMPONENT))
			installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new ModifierComponentDirectEditPolicy(getMyType(), myEditor));
	}
	

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		String prop = arg0.getPropertyName();
		if(getMyType()==ModifierComponent.DISTINCT_COMPONENT && prop==SelectClauseModifier.DISTINCT_SET)
			setDistinctButtonLabel();
		else if(getMyType()==ModifierComponent.LIMIT_COMPONENT  && prop==SelectClauseModifier.LIMIT_SET)
			setNumericLabel((Integer)arg0.getNewValue());
		else if(getMyType()==ModifierComponent.OFFSET_COMPONENT  && prop==SelectClauseModifier.OFFSET_SET)
			setNumericLabel((Integer)arg0.getNewValue());
		
	}

	private void setNumericLabel(Integer newValue) {
		if(newValue == null) {
			otherLabel.setText("");
			return;
		}
		int val = newValue.intValue();
		if(val == 0)otherLabel.setText("");
		else otherLabel.setText(""+val);
		
	}

	
	public void performRequest(Request req) {
		if(!((getMyType() == ModifierComponent.LIMIT_COMPONENT) || (getMyType() == ModifierComponent.OFFSET_COMPONENT))) return;
		if(req.getType() == RequestConstants.REQ_DIRECT_EDIT ||
				req.getType() == RequestConstants.REQ_OPEN) {
			performDirectEditing();
		}
	}

	private void performDirectEditing() {
		NamedComponentDirectEditManager manager =
				new NamedComponentDirectEditManager(this, new NamedComponentEditorLocator(otherLabel), otherLabel);
		manager.show();
		
	}

}
