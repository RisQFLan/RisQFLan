package atree.core.processes.constraints;

import atree.core.model.AtreeModel;
import atree.core.processes.interfaces.IAction;
import atree.core.processes.interfaces.IConstraint;

public class TrueConstraint implements IConstraint {

	@Override
	public String toString() {
		return "true";
	}

	@Override
	public boolean eval(AtreeModel model, IAction actionToExecute) {
		return eval(model);
	}
	
	@Override
	public boolean eval(AtreeModel model) {
		return true;
	}
}
