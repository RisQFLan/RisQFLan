package atree.core.processes.constraints;

import atree.core.model.AtreeModel;
import atree.core.processes.interfaces.IAction;
import atree.core.processes.interfaces.IConstraint;

public class ActionRequiresConstraint implements IConstraint {

	private IAction action;
	private IConstraint constraint;
	
	public ActionRequiresConstraint(IAction action, IConstraint constraint) {
		super();
		this.action = action;
		this.constraint = constraint;
	}
	
	@Override
	public String toString() {
		return "(do("+action.getName()+") -> "+constraint+")";
	}

	@Override
	public boolean eval(AtreeModel model, IAction actionToExecute) {
		if(actionToExecute.equals(action)){
			return constraint.eval(model, actionToExecute);
		}
		else{
			return true;
		}
	}
	
	@Override
	public boolean eval(AtreeModel model) {
		throw new UnsupportedOperationException("An action constraint must be evaluated wrt an action to be executed.");
	}
	
	public IAction getAction(){
		return action;
	}

	public IConstraint getConstraint() {
		return constraint;
	}

}
