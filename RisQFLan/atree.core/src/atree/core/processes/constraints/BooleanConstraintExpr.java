package atree.core.processes.constraints;

import atree.core.model.AtreeModel;
import atree.core.processes.interfaces.IAction;
import atree.core.processes.interfaces.IConstraint;

public class BooleanConstraintExpr implements IConstraint {

	private IConstraint first;
	private IConstraint second;
	private BooleanConnector op;
	
	public BooleanConstraintExpr(IConstraint first, IConstraint second,
			BooleanConnector op) {
		super();
		this.first = first;
		this.second = second;
		this.op = op;
		//no need to add this constraint to involved actions
	}
	
	@Override
	public String toString() {
		return "("+first.toString()+getSymbol(op)+second.toString()+")";
	}

	private static String getSymbol(BooleanConnector op) {
		switch (op) {
		case AND:
			return " /\\ ";
		case IMPLIES:
			return " -> ";
		case OR:
			return " \\/ ";
		default:
			throw new UnsupportedOperationException(op.toString());
		}
	}

	@Override
	public boolean eval(AtreeModel model, IAction actionToExecute) {
		switch (op) {
		case AND:
			return   first.eval(model, actionToExecute)  && second.eval(model, actionToExecute);
		case IMPLIES:
			return (!first.eval(model, actionToExecute)) || second.eval(model, actionToExecute);
		case OR:
			return   first.eval(model, actionToExecute)  || second.eval(model, actionToExecute);
		default:
			throw new UnsupportedOperationException(op.toString());
		}
	}
	
	@Override
	public boolean eval(AtreeModel model) {
		switch (op) {
		case AND:
			return   first.eval(model)  && second.eval(model);
		case IMPLIES:
			return (!first.eval(model)) || second.eval(model);
		case OR:
			return   first.eval(model)  || second.eval(model);
		default:
			throw new UnsupportedOperationException(op.toString());
		}
	} 

}
