package atree.core.variables;

import java.util.LinkedHashMap;
import java.util.Set;

public class VariableAssignment {
	private LinkedHashMap<AtreeVariable, Double> assignment;
	
	public VariableAssignment() {
		this(0);
	}
	
	public VariableAssignment(int numberOfVariables) {
		assignment = new LinkedHashMap<>(numberOfVariables);
	}
	
	public Set<AtreeVariable> getVariables() {
		return assignment.keySet();
	}
	
	
	
	public VariableAssignment clone() {
		VariableAssignment asgn = new VariableAssignment(0);
		asgn.assignment= new LinkedHashMap<AtreeVariable, Double>(assignment);
		return asgn;
	}
	
	public double getValue(AtreeVariable variable) {
		return assignment.get(variable);
	}
	public void setValue(AtreeVariable variable, double value) {
		assignment.put(variable, value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assignment == null) ? 0 : assignment.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if(!(obj instanceof VariableAssignment)) {
			return false;
		}
		return assignment.equals(((VariableAssignment)obj).assignment);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return (assignment==null)?"{}":assignment.toString();
	}
	
	
	
	
}
