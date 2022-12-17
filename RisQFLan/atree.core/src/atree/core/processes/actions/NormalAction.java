package atree.core.processes.actions;

import atree.core.model.AtreeModel;

public class NormalAction extends Action {
	
	public NormalAction(String name)
	{
		super(name);
	}

	@Override
	public boolean allowedByOtherConstraints(AtreeModel model) {
		return model.checkConstraints(this);//return model.validateOtherConstraints(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
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
		NormalAction other = (NormalAction) obj;
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;
		return true;
	}
}
