package atree.core.processes.actions;

import java.util.Set;

import atree.core.model.AtreeModel;
import atree.core.processes.interfaces.IAction;
import atree.core.processes.interfaces.IConstraint;

public abstract class Action implements IAction {
	
	String name;
	
	public Action(String name)
	{
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean modifiesConstraintStore() {
		return false;
	}
	
	@Override
	public boolean allowedByActionConstraints(AtreeModel model) {
		Set<IConstraint> constraints = model.getActionConstraints(this);
		if (constraints != null) {
			for (IConstraint c : constraints) {
				if (!c.eval(model,this)) {
					return false;
				}
			}
		}
		return true;
	}
}
