package atree.core.attributes;

import atree.core.attributes.interfaces.IAttributeDef;
import atree.core.attributes.interfaces.IAttributeExpr;
import atree.core.processes.interfaces.IConstraint;
import atree.core.variables.VariableAssignment;

public class Attribute implements IAttributeExpr {

	private IAttributeDef attributeDef;
	
	public Attribute(IAttributeDef attributeDef) {
		super();
		this.attributeDef = attributeDef;
	}

	@Override
	public double eval(VariableAssignment asgn, AttributeEvaluator eval) {//Set<Node> installedNodes) {
		return attributeDef.eval(eval);//installedNodes);
	}

	@Override
	public String toString() {
		return attributeDef.getName();
	}

	@Override
	public void addConstraintToInvolvedActions(IConstraint constraintToAdd) {
	}
}
