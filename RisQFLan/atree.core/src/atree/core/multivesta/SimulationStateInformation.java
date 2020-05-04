package atree.core.multivesta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import atree.core.processes.Commitment;
import atree.core.processes.ProcessTransition;

public class SimulationStateInformation {

	private Collection<ProcessTransition> forbiddenTransition;
	private List<Commitment> allowedCommitments;
	private double cumulativeRate;
	
	public SimulationStateInformation() {
		super();
		this.forbiddenTransition = new ArrayList<>();
		this.allowedCommitments = new ArrayList<>();
		this.cumulativeRate = 0;
	}
	public Collection<ProcessTransition> getForbiddenCommitments() {
		return forbiddenTransition;
	}
	public List<Commitment> getAllowedCommitments() {
		return allowedCommitments;
	}
	public double getCumulativeRate() {
		return cumulativeRate;
	}
	
	public void addAllowedCommitment(ProcessTransition t, double r) {
		allowedCommitments.add(new Commitment(t,r));
		cumulativeRate += r;
	}
	
	public void addForbiddenTransition(ProcessTransition t) {
		forbiddenTransition.add(t);
	}
	
	public Commitment getAllowedCommitment(double sampledNumber) {
		double cumulativeRate = 0;
		Commitment currentCommitment = null;
		for (Commitment c : allowedCommitments) {
			currentCommitment = c;
			cumulativeRate += currentCommitment.getActualRate();
			if (sampledNumber < cumulativeRate)
			{
				return currentCommitment;
			}
		}
		
		return currentCommitment;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Forbidden Transitions:\n");
		if(forbiddenTransition.isEmpty()) {
			sb.append("NONE\n");
		}
		else {
			for(ProcessTransition t : forbiddenTransition)
			{
				sb.append(t.toString() + "\n");
			}
		}
		sb.append("\n");
		
		sb.append("Allowed Tranisitions:\n");
		if(allowedCommitments.isEmpty()) {
			sb.append("NONE\n");
		}
		else {
			for(Commitment c : allowedCommitments)
			{
				sb.append(c.toString() + "\n");
			}
		}
		sb.append("\n");
		sb.append("Cumulative rate: " + cumulativeRate + "\n");
		return sb.toString();
	}
}
