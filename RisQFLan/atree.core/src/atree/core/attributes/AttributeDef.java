package atree.core.attributes;

import java.util.HashMap;

import atree.core.attributes.interfaces.IAttributeDef;
import atree.core.nodes.AttackNode;
import atree.core.nodes.Node;
import atree.core.nodes.interfaces.INode;

public class AttributeDef implements IAttributeDef{

	private HashMap<Node,Double> values;
	private String name;
	
	public AttributeDef(String name) {
		this.name = name;
		values = new HashMap<>();
		//attackerVal = 0;
		//defenderVal = 0;
	}
	
	
	
//	@SuppressWarnings("unchecked")
//	@Override
//	public IAttributeDef clone() {
//		AttributeDef clone = new AttributeDef(name);
//		clone.attackerVal = attackerVal;
//		clone.defenderVal = defenderVal;
//		clone.values=null;
//		if(values!=null) {
//			values=(HashMap<Node,Double>)(values.clone());
//		}
//		
//		return clone;
//	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		AttributeDef other = (AttributeDef) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}



	private void changeValue(INode node,AttributeEvaluator attrEval,boolean increment) {
		if(values.containsKey(node)) {
			double nodeVal=values.get(node);
			if (node instanceof AttackNode) {
				double attackerVal = attrEval.getAttackerValue(this);
				if(increment) {
					attrEval.setAttackerValue(this, attackerVal+nodeVal);
					//attackerVal += values.get(node);
				}
				else {
					attrEval.setAttackerValue(this, attackerVal-nodeVal);
					//attackerVal -= values.get(node);
				}
			}
			else {
				double defenderVal = attrEval.getDefenderValue(this);
				if(increment) {
					attrEval.setDefenderValue(this, defenderVal+nodeVal);
					//defenderVal += values.get(node);
				}
				else {
					attrEval.setDefenderValue(this, defenderVal-nodeVal);
					//defenderVal -= values.get(node);
				}
			}
		}
	}
	
	@Override
	public void incrementValue(INode node,AttributeEvaluator attrEval) {
		changeValue(node, attrEval, true);
//		if(values.containsKey(node)) {
//			double nodeVal=values.get(node);
//			if (node instanceof AttackNode) {
//				double attackerVal = attrEval.getAttackerValue(this);
//				attrEval.setAttackerValue(this, attackerVal+nodeVal);
//				//attackerVal += values.get(node);
//			}
//			else {
//				double defenderVal = attrEval.getDefenderValue(this);
//				attrEval.setDefenderValue(this, defenderVal+nodeVal);
//				//defenderVal += values.get(node);
//			}
//		}
	}
	
	@Override
	public void decrementValue(INode node,AttributeEvaluator attrEval) {
		changeValue(node, attrEval, false);
//		if(values.containsKey(node)) {
//			if (node instanceof AttackNode) {
//				attackerVal -= values.get(node);
//			}
//			else {
//				defenderVal -= values.get(node);
//			}
//		}
	}
	
//	@Override
//	public void resetValues() {
//		attackerVal = 0;
//		defenderVal = 0;		
//	}
	
	@Override
	public double getAttackerVal(AttributeEvaluator attrEval) {
		return attrEval.getAttackerValue(this);
	}
	
	@Override
	public double getDefenderVal(AttributeEvaluator attrEval) {
		return attrEval.getDefenderValue(this);
	}

	@Override
	public void setNodeValue(Node node, double val) {
		values.put(node, val);
	}

	@Override
	public double eval(AttributeEvaluator attrEval) {//Node node) {
		if(values==null){
			return 0;
		}
		//return attackerVal;
		return getAttackerVal(attrEval);
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getName();
	}

}
