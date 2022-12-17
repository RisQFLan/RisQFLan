package atree.core.processes;

import java.util.ArrayList;
import java.util.Collection;

public class ProcessState {

	private String name;
	private Collection<ProcessTransition> transitions;
	
	
	
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
		ProcessState other = (ProcessState) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public ProcessState(String name) {
		this.name = name;
		transitions = new ArrayList<>();
	}
	
	public void addTransition(ProcessTransition transition) {
		transitions.add(transition);
	}

	public Collection<ProcessTransition> getTransitions() {
		return transitions;
	}

	public String getName() {
		return name;
	}
	
	@Override 
	public String toString() {
		return name;
	}
}
