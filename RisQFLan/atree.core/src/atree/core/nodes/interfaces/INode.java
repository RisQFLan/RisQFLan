package atree.core.nodes.interfaces;

import atree.core.nodes.Node;

//import atree.core.nodes.AttackNode;
//import atree.core.nodes.CountermeasureNode;
//import atree.core.nodes.DefenseNode;

public interface INode {
	String getName();
	void addParent(Node node);
//	void addDefenseParent(DefenseNode n);
//	void addAttackParent(AttackNode n);
//	void addCountermeasureParent(CountermeasureNode n);
}
