package atree.core.dtmc;



public class DTMCTransition {

	private DTMCState source;
	private DTMCState target;
	private double rate;
	public static final String DECIMALFORMAT = "%.3f";
	public DTMCTransition(DTMCState source, DTMCState target, double rate) {
		super();
		this.source = source;
		this.target = target;
		this.rate = rate;
	}
	public DTMCState getSource() {
		return source;
	}
	public DTMCState getTarget() {
		return target;
	}
	public double getRate() {
		return rate;
	}
	public String getRateToPrint() {
		String rateStr = String.format(DECIMALFORMAT, rate);
		return rateStr;
	}
	
	@Override
	public String toString() {
		//
		return "s"+source.getId() + " -> s"+target.getId()+" , "+getRateToPrint();
	}
	
	
}
