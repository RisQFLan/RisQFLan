package atree.core.processes.interfaces;

import atree.core.model.AtreeModel;

public interface IConstraint {

	boolean eval(AtreeModel model);					// Needed for Hierarchical constraints and has/allowed..
	boolean eval(AtreeModel model, IAction action);	// Needed for ActionRequiresConstraints..
}
