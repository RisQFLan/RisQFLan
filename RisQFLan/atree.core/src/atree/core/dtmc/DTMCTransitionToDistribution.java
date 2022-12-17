package atree.core.dtmc;

import java.util.ArrayList;


public class DTMCTransitionToDistribution {

	private DTMCState source;
	private ArrayList<TargetAndRate> ratesToTargets;
	//private double rate;
	public DTMCTransitionToDistribution(DTMCState source,int expNumberOfTargets) {
		super();
		this.source = source;
		ratesToTargets=new ArrayList<>(expNumberOfTargets);
	}
	public DTMCState getSource() {
		return source;
	}
	public ArrayList<TargetAndRate> getTargets() {
		return ratesToTargets;
	}
	public void addTarget(DTMCState target, String rate) {
		ratesToTargets.add(new TargetAndRate(target, rate));
	}
	
	public String toPRISMFormat() {
		StringBuilder sb = new StringBuilder();
		
		/*
		 printlnInBWOut(bwOut,"[] state = "+
						transition.getSource().getId()+
						" -> "+transition.getRateToPrint()+
						" : (state'="+transition.getTarget().getId()+");"
						);
		 */
		//[] state = 0 -> p : (state'=state+2) + (1-p) : (state'=state+1);
		sb.append("[] s = "+getSource().getId()+" -> ");
		for(int i=0;i<ratesToTargets.size();i++) {
			sb.append("\n\t");
			TargetAndRate t= ratesToTargets.get(i);
			sb.append(t.getRate());
			sb.append(" : (s'="+t.getTargetState().getId()+")");
			if(i<ratesToTargets.size()-1) {
				sb.append(" + ");
				//sb.append(skip);
			}
		}
		sb.append(";");
		
		return sb.toString();
		
	}
	
	@Override
	public String toString() {
		return source + " -> " + ratesToTargets.toString();
	}
	
}
