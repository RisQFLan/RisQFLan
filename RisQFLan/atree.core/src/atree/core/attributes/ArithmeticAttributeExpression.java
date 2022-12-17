package atree.core.attributes;

import atree.core.attributes.interfaces.IAttributeExpr;
import atree.core.processes.interfaces.IConstraint;
import atree.core.variables.VariableAssignment;

public class ArithmeticAttributeExpression implements IAttributeExpr {
	
	private IAttributeExpr firstOperand;
	private IAttributeExpr secondOperand;
	private ArithmeticOperation op;
	
	public ArithmeticAttributeExpression(IAttributeExpr firstOperand,
			IAttributeExpr secondOperand, ArithmeticOperation op) {
		super();
		this.firstOperand = firstOperand;
		this.secondOperand = secondOperand;
		this.op = op;
	}

	@Override
	public double eval(VariableAssignment asgn,AttributeEvaluator attrEval) {//Set<Node> installedNodes) {
		double first = firstOperand.eval(asgn,attrEval);//installedNodes);
		double second = secondOperand.eval(asgn,attrEval);//installedNodes);
		switch (op) {
		case SUM:
			return first+second;
		case SUB:
			return first-second;
		case MULT:
			return first*second;
		default:
			throw new UnsupportedOperationException(op.toString());
		} 
	}

	@Override
	public String toString() {
		return "(" + firstOperand + getMathSymbol(op) + secondOperand+")";
	}
	
	
	public static char getMathSymbol(ArithmeticOperation o){
		switch (o) {
		case SUM:
			return '+';
		case SUB:
			return '-';
		case MULT:
			return '*';
		default:
			throw new UnsupportedOperationException(o.toString());
		} 
	}

	@Override
	public void addConstraintToInvolvedActions(IConstraint constraintToAdd) {
		firstOperand.addConstraintToInvolvedActions(constraintToAdd);
		secondOperand.addConstraintToInvolvedActions(constraintToAdd);
	}



}
