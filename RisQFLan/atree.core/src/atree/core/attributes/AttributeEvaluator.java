package atree.core.attributes;

import java.util.HashMap;

import atree.core.attributes.interfaces.IAttributeDef;

public class AttributeEvaluator {
	private HashMap<IAttributeDef, Double> attackerVal;
	private HashMap<IAttributeDef, Double> defenderVal;
	//private int numberOfAttributes;
	
	
	
	
	@Override
	public String toString() {
		return "[attackerVal=" + attackerVal + ", defenderVal=" + defenderVal + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attackerVal == null) ? 0 : attackerVal.hashCode());
		result = prime * result + ((defenderVal == null) ? 0 : defenderVal.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if(!(obj instanceof AttributeEvaluator)) {
			return false;
		}
		return attackerVal.equals(((AttributeEvaluator)obj).attackerVal) &&
				defenderVal.equals(((AttributeEvaluator)obj).defenderVal);
	}
	
	public AttributeEvaluator() {
		this(0);
	}
	
	public AttributeEvaluator(int numberOfAttributes) {
		//this.numberOfAttributes=numberOfAttributes;
		attackerVal = new HashMap<>(numberOfAttributes);
		defenderVal = new HashMap<>(numberOfAttributes);
	}
	
	public AttributeEvaluator clone() {
		AttributeEvaluator clone = new AttributeEvaluator(0);
		clone.attackerVal=new HashMap<>(attackerVal);
		clone.defenderVal=new HashMap<>(defenderVal);
		return clone;
	}
	
	public double getAttackerValue(IAttributeDef attr) {
		Double value = attackerVal.get(attr);
		if(value==null) {
			return 0;
		}
		else {
			return value;
		}
	}
	public double getDefenderValue(IAttributeDef attr) {
		Double value=defenderVal.get(attr);
		if(value==null) {
			return 0;
		}
		else {
			return value;
		}
	}
	public void setAttackerValue(IAttributeDef attr,double value) {
		attackerVal.put(attr,value);
	}
	public void setDefenderValue(IAttributeDef attr,double value) {
		defenderVal.put(attr,value);
	}
	
	public void resetValues() {
		attackerVal=new HashMap<>();
		defenderVal=new HashMap<>();
	}
	
}
