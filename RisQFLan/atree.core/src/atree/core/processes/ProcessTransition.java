package atree.core.processes;

import java.util.ArrayList;
import java.util.List;

import atree.core.model.AtreeModel;
import atree.core.processes.actions.AddAction;
import atree.core.processes.interfaces.IAction;
import atree.core.processes.interfaces.IConstraint;
import atree.core.variables.SideEffect;

public class ProcessTransition {

	private double rate;
	private IAction action;
	private ProcessState target;
	private SideEffect[] sideEffects;
	private IConstraint actionGuard;	//CONSIDER LIST
	
	public ProcessTransition(double rate, IAction action, ProcessState target, SideEffect[] sideEffects, IConstraint actionGuard) {
		this.rate = rate;
		this.action = action;
		this.target = target;
		this.sideEffects = sideEffects;
		this.actionGuard = actionGuard;
	}	
	
	public void setActionGuard(IConstraint constraint) {
		actionGuard = constraint;
	}

	public boolean isAllowed(AtreeModel model) {
		
		if (!action.allowedByActionConstraints(model)){
			return false;
		}
		if (!actionGuard.eval(model)) {
			return false;
		}
		
		if (action.modifiesConstraintStore() || (sideEffects != null && sideEffects.length > 0)) {
			double[] oldValues=model.applySideEffects(sideEffects);
			boolean allowed=action.allowedByOtherConstraints(model);
			model.revertSideEffects(sideEffects, oldValues);
			//revert side effects
			if(!allowed){
				return false;
			}
		}
		return true;
	}

	public double computeActualRate(AtreeModel model) {
		
		double actualRate = rate;
		List<Double> defEfSet = new ArrayList<>();
		if (action instanceof AddAction) {
			defEfSet = model.getDefenseEffectiveness(((AddAction)action).getAttackNode());
		}
		
		for (double val : defEfSet)
		{
			//TODO: Check if this is correct.. 
			actualRate *= (1 - val);
		}
		
		return actualRate;
	}

	public double getRate() {
		return rate;
	}

	public IAction getAction() {
		return action;
	}

	public ProcessState getTarget() {
		return target;
	}

	public SideEffect[] getSideEffects() {
		return sideEffects;
	}

	public IConstraint getActionGuard() {
		return actionGuard;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Process Transition:\n");
		sb.append("Target: " + target.getName() + "\n");
		sb.append("Action: " + action.getName() + "\n");
		sb.append("Rate: " + rate + "\n");
		
		sb.append("Side Effects: {");
		int i = sideEffects.length;
		for (SideEffect se : sideEffects)
		{
			sb.append(se.toString());
			i--;
			if (i > 1)
			{
				sb.append(",");
			}
		}
		sb.append("}\n");		
		
		sb.append("Action Guard: " + actionGuard.toString() + "\n");
		return sb.toString();
	}
}
