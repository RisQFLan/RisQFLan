package atree.core.processes.actions;

import atree.core.model.AtreeModel;
import atree.core.nodes.AttackNode;

public class FailAction extends Action {
	private AttackNode node;
	
	public FailAction(AttackNode node)
	{
		super("fail(" + node.getName() + ")");
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
 		//if (model.whenAdded(node) != -1) //The attack have already been performed previously. We decided to not allow a fail if we already have the attack node
		if(model.isInstalled(node))
		{
			return false;
		}
 		//FIXAndrea: fail does not add nodes
		//model.add(node);
		boolean sat = model.checkConstraints(this);
		return sat;
		/*FIXAndrea: I don't need to check the hierarchical constraints, becuase I did not change them
		boolean sat2 = model.checkHierarchicalConstraints(node);
		//FIXAndrea: fail does not add nodes
		//model.remove(node,true);
		
		return sat && sat2; //model.validateOtherConstraints(this);
		*/
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
		FailAction other = (FailAction) obj;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		return true;
	} 
}
