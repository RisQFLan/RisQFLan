package atree.core.processes.constraints;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import atree.core.model.AtreeModel;
import atree.core.nodes.Node;
import atree.core.processes.interfaces.IAction;
import atree.core.processes.interfaces.IConstraint;

public class UnorderedRelationConstraint implements IConstraint {

	private Set<Node> children;
	private Node root;
	private int k;
	
	public UnorderedRelationConstraint(Node root, Collection<Node> childSet, int k) {
		this.k = k;
		this.root = root;
		children = new HashSet<>(childSet.size());
		for (Node n : childSet)
		{
			children.add(n);
		}
	}
	
	public Node getRoot() {
		return root;
	}
	
	@Override
	public boolean eval(AtreeModel model) {
		int count = 0;
		for (Node n : children) {
			//if (model.whenAdded(n) >= 0) {
			if (model.isInstalled(n)) {
				count++;
				if (count >= k) {
					return true;
				}
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
		if (k == children.size()) {
			return root.getName() + " -AND-> {"+c.toString()+"}";
		} 	
		else if (k == 1) {
			return root.getName() + " -OR-> {"+c.toString()+"}";
		}
		else {
			return root.getName() + " -K"+ k +"-> {"+c.toString()+"}";
		}
	}
	
//	@Override
//	public String toString()
//	{
//		String s = root.getName() + "- Unordered k = " + k + " ->[";
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
