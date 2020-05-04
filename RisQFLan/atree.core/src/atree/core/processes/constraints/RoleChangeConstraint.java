package atree.core.processes.constraints;

import atree.core.model.AtreeModel;
import atree.core.nodes.Node;
import atree.core.processes.interfaces.IAction;
import atree.core.processes.interfaces.IConstraint;

public class RoleChangeConstraint implements IConstraint {

	private Node root;
	private Node child;
	
	public RoleChangeConstraint(Node root,Node child) {
		this.root = root;
		this.child = child;
	}
	
	@Override
	public boolean eval(AtreeModel model) {
//		return (!model.isInstalled(child) && model.isInstalled(root)) || (model.isInstalled(child) && !model.isInstalled(root));
		return !model.isInstalled(child);

	}

	@Override
	public boolean eval(AtreeModel model, IAction action) {
		return eval(model);
	}
	
	@Override
	public String toString()
	{
		return root.getName() + " -RoleChange-> " + child.getName();
	}
}
