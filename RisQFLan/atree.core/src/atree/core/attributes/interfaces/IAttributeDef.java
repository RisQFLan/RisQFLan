package atree.core.attributes.interfaces;

import atree.core.attributes.AttributeEvaluator;
import atree.core.nodes.Node;
import atree.core.nodes.interfaces.INode;

public interface IAttributeDef {

	void setNodeValue(Node node, double val);
	double eval(AttributeEvaluator eval);//Node node);
	String getName();
	double getAttackerVal(AttributeEvaluator attrEval);
	double getDefenderVal(AttributeEvaluator attrEval);
	void incrementValue(INode node,AttributeEvaluator attrEval);
	void decrementValue(INode node,AttributeEvaluator attrEval);
	//void resetValues();
	//IAttributeDef clone();
}
