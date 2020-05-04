package atree.core.processes.actions;

import atree.core.model.AtreeModel;
import atree.core.nodes.AttackNode;

public class RemoveAction extends Action {
	private AttackNode node;
	
	public RemoveAction(AttackNode node)
	{
		super("remove(" + node.getName() + ")");
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
		//if (model.whenAdded(node) == -1) //The attack have never been performed previously
		if(!model.isInstalled(node))
		{
			return false;
		}
		model.remove(node, false);
		boolean sat = model.checkConstraints(this);
		if(!sat) {
			return false;
		}
		//FIXAndrea: why don't we do 'boolean sat2 = model.checkHierarchicalConstraints(node);' //We probably need to check if by removing a node we break the constraints
		boolean sat2 = model.checkHierarchicalConstraints(node);
		
		
		model.add(node, false);
		
		return sat&&sat2;//model.validateOtherConstraints(this);;
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
		RemoveAction other = (RemoveAction) obj;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		return true;
	} 
}
