package atree.core.processes.constraints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import atree.core.model.AtreeModel;
import atree.core.nodes.Node;
import atree.core.processes.interfaces.IAction;
import atree.core.processes.interfaces.IConstraint;

public class OrderedRelationConstraint implements IConstraint {

	private List<Node> children;
	private Node root;
	private int k;
	
	public OrderedRelationConstraint(Node root, Collection<Node> childrenSet) {//, int k) {
		this.root = root;
		k = childrenSet.size();

		children = new ArrayList<>(k);
		
		for (Node n : childrenSet)
		{
			children.add(n);
		}
	}
	
	public List<Node> getPossibleChilderInOrder(){
		return children;
	}
	
	public Node getRoot() {
		return root;
	}
	
	@Override
	public boolean eval(AtreeModel model) {
		int lastAdded = -1;
		int count = 0;
		for (Node n : children) {
			int added = model.whenAdded(n,this);
			if (added < 0) {
				//Do nothing because n is not present
			}
			else if (added > lastAdded) {
				lastAdded = added;
				count++;
				if (count >= k)
				{
					return true;
				}
			}
			else {
				return false;
			}
		}
		return false;
	}

	@Override
	public boolean eval(AtreeModel model, IAction action) {
		return eval(model);
	}

	@Override
	public String toString() {
		StringBuffer c = new StringBuffer();
		for (Node n : children) {
			c.append(n.getName()+",");
		}
		c.delete(c.length()-1, c.length());
		return root.getName() + "-OAND-> {" + c.toString() +"}";
	}
	
//	@Override
//	public String toString()
//	{
//		String s = root.getName() + "- Ordered k = " + k + " ->[";
//		int i = children.size();
//		for(Node n : children)
//		{
//			s = s + n.getName();
//			i--;
//			if (i > 0)
//			{
//				s = s + ", ";
//			}
//		}
//		s = s + "]";
//		return s;
//	}
}
