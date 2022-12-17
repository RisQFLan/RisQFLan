package atree.core.processes.actions;

import atree.core.model.AtreeModel;
import atree.core.nodes.Node;

public class QueryAction extends Action {
	private Node node;
	
	public QueryAction(Node node)
	{
		super("query(" + node.getName() + ")");
		this.node = node;
	}

	@Override
	public boolean allowedByOtherConstraints(AtreeModel model) {
		return model.checkConstraints(this);//return model.validateOtherConstriants(this);
	}

	public Node getNode() {
		return node;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((node == null) ? 0 : node.hashCode());
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
		QueryAction other = (QueryAction) obj;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		return true;
	}
}
