package atree.core.processes.constraints;

import atree.core.model.AtreeModel;
import atree.core.nodes.Node;
import atree.core.processes.interfaces.IAction;
import atree.core.processes.interfaces.IConstraint;

public class AllowedNodeConstraint implements IConstraint {

	private Node node;
	
	public AllowedNodeConstraint(Node node) {
		this.node = node;
		if(node==null){
			System.out.println("null");
		}
	}
	
	@Override
	public boolean eval(AtreeModel model) {
		// Evaluate if doing an add(node) violates any hierarchical constraints..
		if (model.isInstalled(node)) {
			return model.checkHierarchicalConstraints(node);
		}
		else {
			model.add(node, false);
			boolean sat = model.checkHierarchicalConstraints(node);
			model.remove(node, false);
			return sat;
		}
	}

	@Override
	public boolean eval(AtreeModel model, IAction action) {
		return eval(model);
	}
	
	@Override
	public String toString()
	{
		return "allowed(" + node.getName() + ")";
	}

}
