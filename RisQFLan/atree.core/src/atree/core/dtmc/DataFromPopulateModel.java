package atree.core.dtmc;

import java.util.LinkedHashMap;

import atree.core.attributes.interfaces.IAttributeDef;
import atree.core.nodes.AttackNode;
import atree.core.variables.AtreeVariable;

public class DataFromPopulateModel {

	private LinkedHashMap<String, AtreeVariable> variables;
	private LinkedHashMap<String, IAttributeDef> attributes;
	private LinkedHashMap<String, AttackNode> attackNodes;
	public LinkedHashMap<String, AtreeVariable> getVariables() {
		return variables;
	}
	public LinkedHashMap<String, IAttributeDef> getAttributes() {
		return attributes;
	}
	public LinkedHashMap<String, AttackNode> getAttackNodes() {
		return attackNodes;
	}
	
	
	public void setVariables(LinkedHashMap<String, AtreeVariable> variables) {
		this.variables = variables;
	}
	public void setAttributes(LinkedHashMap<String, IAttributeDef> attributes) {
		this.attributes = attributes;
	}
	public void setAttackNodes(LinkedHashMap<String, AttackNode> attackNodes) {
		this.attackNodes = attackNodes;
	}
	
}
