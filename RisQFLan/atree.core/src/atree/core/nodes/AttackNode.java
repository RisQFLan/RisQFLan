package atree.core.nodes;

import java.util.HashSet;
import java.util.Set;

public class AttackNode extends Node {
	
	double detectionRate;
	Set<CountermeasureNode> activatesCountermeasureNodes;
	boolean ghostNode;
	
	public AttackNode(String name) {
		super(name);
		activatesCountermeasureNodes = new HashSet<>();
		ghostNode = false;
		detectionRate = 0;
	}
	public AttackNode(String name, boolean gn) {
		super(name);
		activatesCountermeasureNodes = new HashSet<>();
		ghostNode = gn;
		detectionRate = 0;
	}
	
	public void addCountermeasure(CountermeasureNode cn) {
		activatesCountermeasureNodes.add(cn);
	}
	public double getDetectionRate() {
		return detectionRate;
	}
	public Set<CountermeasureNode> getActivatesCountermeasureNodes() {
		return activatesCountermeasureNodes;
	}
	public void setDetectionRate(double r) {
		detectionRate = r;
	}
	
	public boolean isGhost() {
		return ghostNode;
	}
}
