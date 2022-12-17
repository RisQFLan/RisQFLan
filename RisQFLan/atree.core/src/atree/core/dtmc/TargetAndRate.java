package atree.core.dtmc;

public class TargetAndRate {

	private DTMCState targetState;
	private String rate;
	public DTMCState getTargetState() {
		return targetState;
	}
	public String getRate() {
		return rate;
	}
	public TargetAndRate(DTMCState targetState, String rate) {
		super();
		this.targetState = targetState;
		this.rate = rate;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return rate+"-"+targetState;
	}
	
	public static final String DECIMALFORMAT = "%.3f";
	public String getRateToPrint() {
		String rateStr = String.format(DECIMALFORMAT, rate);
		return rateStr;
	}
	
	
}
