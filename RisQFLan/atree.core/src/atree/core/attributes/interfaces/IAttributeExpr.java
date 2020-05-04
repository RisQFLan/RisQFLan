package atree.core.attributes.interfaces;

import atree.core.attributes.AttributeEvaluator;

//import java.util.Set;

import atree.core.processes.interfaces.IConstraint;
import atree.core.variables.VariableAssignment;
//import atree.core.nodes.Node;

public interface IAttributeExpr {
	
	//double eval();//Set<Node> installedNodes);
	double eval(VariableAssignment asgn, AttributeEvaluator attrEval);

	void addConstraintToInvolvedActions(IConstraint constraintToAdd);

}
