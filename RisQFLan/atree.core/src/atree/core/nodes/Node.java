package atree.core.nodes;

import java.util.Collection;
import java.util.LinkedHashSet;

import atree.core.nodes.interfaces.INode;

public class Node implements INode {
	private String name;
	
	private Collection<Node> parents;
	
	public Node(String name)
	{
		this.name = name;
		
		parents = new LinkedHashSet<>();		
	}

	public String getName() {
		return name;
	}
	
	public Collection<Node> getParents() {
		return parents;
	}

	public void addParent(Node node) {
		parents.add(node);
	}
	
	public void addParentFor(Node node) {
		node.addParent(this);
	}
	
	@Override
	public String toString() {
		return getName();
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
		Node other = (Node) obj;
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;
		return true;
	}
}
