package atree.core.dtmc;

import atree.core.model.AtreeModel;

public class DTMCState {

	private AtreeModel state;
	private int id;
	public AtreeModel getState() {
		return state;
	}
	public int getId() {
		return id;
	}
	public DTMCState(AtreeModel state, int id) {
		super();
		this.state = state;
		this.id = id;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "s"+getId();
	}
	
	
}
