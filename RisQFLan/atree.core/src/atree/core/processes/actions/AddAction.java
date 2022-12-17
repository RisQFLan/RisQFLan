package atree.core.processes.actions;

import atree.core.model.AtreeModel;
import atree.core.nodes.AttackNode;

public class AddAction extends Action {
	
	private AttackNode node;
	
	public AddAction(AttackNode node)
	{
		super("add(" + node.getName() + ")");
		this.node = node;
	}
	
	@Override
	public boolean modifiesConstraintStore()
	{
		return true;
	}
	
	public AttackNode getAttackNode()
	{
		return node;
	}

	@Override
	public boolean allowedByOtherConstraints(AtreeModel model) {
		//if (model.whenAdded(node) != -1) //The attack has already been performed previously
		if(model.isInstalled(node))
		{
			return false;
		}
		model.add(node);
		boolean sat = model.checkConstraints(this);
		boolean sat2 = model.checkHierarchicalConstraints(node);
		model.remove(node,true);
		
		return sat && sat2;//model.validateOtherConstraints(this);  // Do this inside this class!!
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((node == null) ? 0 : node.hashCode());
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
		AddAction other = (AddAction) obj;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		return true;
	} 
}
