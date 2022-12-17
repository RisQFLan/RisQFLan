package atree.core.attributes;

import atree.core.attributes.interfaces.IAttributeExpr;
import atree.core.processes.interfaces.IConstraint;
import atree.core.variables.VariableAssignment;

public class Constant implements IAttributeExpr {

	private double val;
	
	public Constant(double val) {
		this.val=val;
	}
	
	@Override
	public double eval(VariableAssignment asgn,AttributeEvaluator attrEval) {//Set<Node> installedNodes) {
		return val;
	}
	
	@Override
	public String toString() {
		return String.valueOf(val);
	}

	@Override
	public void addConstraintToInvolvedActions(IConstraint constraintToAdd) {	
	}
}
