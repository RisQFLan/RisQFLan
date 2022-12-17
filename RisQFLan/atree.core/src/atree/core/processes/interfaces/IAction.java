package atree.core.processes.interfaces;

import atree.core.model.AtreeModel;

public interface IAction {

	boolean allowedByOtherConstraints(AtreeModel model);
	boolean allowedByActionConstraints(AtreeModel model);

	String getName();

	boolean modifiesConstraintStore();

}