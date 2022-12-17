package atree.core.processes.constraints;

import atree.core.model.AtreeModel;
import atree.core.processes.interfaces.IAction;
import atree.core.processes.interfaces.IConstraint;

public class FalseConstraint implements IConstraint {
	
	@Override
	public String toString() {
		return "false";
	}

	@Override
	public boolean eval(AtreeModel model, IAction actionToExecute) {
		return false;
	}
	
	@Override
	public boolean eval(AtreeModel model) {
		return false;
	} 

}
