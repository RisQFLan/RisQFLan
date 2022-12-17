package atree.core.processes.constraints;

import atree.core.attributes.interfaces.IAttributeExpr;
import atree.core.model.AtreeModel;
import atree.core.processes.interfaces.IAction;
import atree.core.processes.interfaces.IConstraint;

public class DisequationOfAttributeExpressions implements IConstraint {

	private IAttributeExpr lhs;
	private IAttributeExpr rhs;
	private AttributeExprComparator comp;
	
	public DisequationOfAttributeExpressions(IAttributeExpr lhs,
			IAttributeExpr rhs, AttributeExprComparator comp) {
		super();
		this.lhs = lhs;
		this.rhs = rhs;
		this.comp = comp;
	}

	private String getMathSymbol(AttributeExprComparator c) {
		switch (c) {
		case EQ:
			return "==";
		case GE:
			return ">";
		case GEQ:
			return ">=";
		case LE:
			return "<";
		case LEQ:
			return "<=";
		case NOTEQ:
			return "!=";
		default:
			throw new UnsupportedOperationException(c.toString());
		}
	}

	@Override
	public String toString() {
		return "(" + lhs + " "+ getMathSymbol(comp) +" " + rhs + ")";
	}
	
	@Override
	public boolean eval(AtreeModel model){
		double lhsVal = lhs.eval(model.getVariableAssignment(),model.getAttributeEvaluator());		//model.eval(lhs);
		double rhsVal = rhs.eval(model.getVariableAssignment(),model.getAttributeEvaluator());		//model.eval(rhs);
		switch (comp) {
		case EQ:
			return lhsVal == rhsVal;
		case GE:
			return lhsVal > rhsVal;
		case GEQ:
			return lhsVal >= rhsVal;
		case LE:
			return lhsVal < rhsVal;
		case LEQ:
			return lhsVal <= rhsVal;
		case NOTEQ:
			return lhsVal != rhsVal;
		default:
			throw new UnsupportedOperationException(comp.toString());
		}
	}

	@Override
	public boolean eval(AtreeModel model, IAction action) {
		return eval(model);
	}
}
