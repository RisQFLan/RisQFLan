package atree.core.variables;

import atree.core.attributes.AttributeEvaluator;
import atree.core.attributes.interfaces.IAttributeExpr;

public class SideEffect {

	private AtreeVariable variable;
	private IAttributeExpr updateExpression;
	public SideEffect(AtreeVariable variable, IAttributeExpr updateExpression) {
		super();
		this.variable = variable;
		this.updateExpression = updateExpression;
	}
	
	public AtreeVariable getVariable() {
		return variable;
	}
	public double evalUpdateExpression(VariableAssignment asgn,AttributeEvaluator attrEval) {//Set<Node> installedNodes){
		return updateExpression.eval(asgn,attrEval);//installedNodes);
	}
	
	@Override
	public String toString() {
		return variable.getName()+"="+updateExpression;
	}

	
	public static String sideEffectsToString(SideEffect[] se) {
		StringBuilder sb = new StringBuilder("{");
		for(int i=0;i<se.length;i++){
			sb.append(se[i].toString());
			if(i<se.length-1){
				sb.append(",");
			}
		}
		sb.append("}");
		return sb.toString();
	}
}
