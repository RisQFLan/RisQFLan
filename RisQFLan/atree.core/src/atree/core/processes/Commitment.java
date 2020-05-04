package atree.core.processes;

public class Commitment {

	private ProcessTransition transition;
	private double actualRate;

	public Commitment(ProcessTransition transition, double rate) {
		this.transition = transition;
		this.actualRate = rate;
	}
	
	public ProcessTransition getTransition() {
		return transition;
	}

	public void setTransition(ProcessTransition transition) {
		this.transition = transition;
	}

	public double getActualRate() {
		return actualRate;
	}

	public void setActualRate(double actualRate) {
		this.actualRate = actualRate;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Commitment:\n");
		sb.append(transition.toString());
		sb.append("Actual Rate: " + actualRate + "\n");
		return sb.toString();
	}
}
