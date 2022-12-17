package atree.core.variables;

import atree.core.attributes.AttributeEvaluator;
import atree.core.attributes.interfaces.IAttributeExpr;
import atree.core.processes.interfaces.IConstraint;

public class AtreeVariable implements IAttributeExpr{
	
	private String name;
	//private double value;
	private final double initialValue;
	
	public AtreeVariable(String name, double initialValue) {
		//super();
		this.name = name;
		//this.value=value;
		this.initialValue=initialValue;
	}
	
	public double getInitialValue(){
		return initialValue;
	}
	
	public String getName() {
		return name;
	}
	/*
	public void setValue(double value) {
		this.value=value;
	}
	*/
	
	@Override
	public String toString() {
		return name;
	}
	
	/*
	public String toStringWithValue() {
		return name+"="+value;
	}
	*/
	


	
	@Override
	public double eval(VariableAssignment asgn,AttributeEvaluator attrEval) {//Set<Node> installedNodes) {
		return asgn.getValue(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AtreeVariable other = (AtreeVariable) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public void addConstraintToInvolvedActions(IConstraint constraintToAdd) {
		//TODO check if we need to do something here
		
	}

	public String print(VariableAssignment asgn) {
		return name + ": " + asgn.getValue(this);
	}
}