package atree.core.processes.constraints;

import atree.core.model.AtreeModel;
import atree.core.nodes.interfaces.INode;
import atree.core.processes.interfaces.IAction;
import atree.core.processes.interfaces.IConstraint;

public class HasNodeConstraint implements IConstraint {
	
	private INode node;
	
	public HasNodeConstraint(INode node) {
		this.node=node;
		if(node==null){
			System.out.println("null");
		}
	}
	
	@Override
	public String toString() {
		return "has("+node.getName()+")";
	}

	@Override
	public boolean eval(AtreeModel model, IAction actionToExecute) {
		//Pay attention: we already have the effect of actionToExecute in the store. 
		return eval(model);
	}
	
	@Override
	public boolean eval(AtreeModel model) {
		//Pay attention: we already have the effect of actionToExecute in the store. 
		return model.isInstalled(node);
	} 

}
