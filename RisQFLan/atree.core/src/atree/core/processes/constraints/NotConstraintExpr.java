package atree.core.processes.constraints;

import atree.core.model.AtreeModel;
import atree.core.processes.interfaces.IAction;
import atree.core.processes.interfaces.IConstraint;

public class NotConstraintExpr implements IConstraint {

	private IConstraint innerConstraint;
	
	public NotConstraintExpr(IConstraint first) {
		super();
		this.innerConstraint = first;
	}
	
	@Override
	public String toString() {
		return "(!"+innerConstraint.toString()+")";
	}

	@Override
	public boolean eval(AtreeModel model, IAction actionToExecute) {
		return !innerConstraint.eval(model, actionToExecute);
	}
	
	@Override
	public boolean eval(AtreeModel model) {
		return !innerConstraint.eval(model);
	}

}
