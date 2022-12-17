package atree.core.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import atree.core.attacker.Attacker;
import atree.core.attributes.AttributeEvaluator;
import atree.core.attributes.interfaces.IAttributeDef;
import atree.core.attributes.interfaces.IAttributeExpr;
import atree.core.multivesta.SimulationStateInformation;
import atree.core.nodes.AttackNode;
import atree.core.nodes.CountermeasureNode;
import atree.core.nodes.DefenseNode;
import atree.core.nodes.Node;
import atree.core.nodes.interfaces.INode;
import atree.core.processes.Commitment;
import atree.core.processes.ProcessState;
import atree.core.processes.ProcessTransition;
import atree.core.processes.actions.Action;
import atree.core.processes.actions.AddAction;
import atree.core.processes.actions.FailAction;
import atree.core.processes.actions.NormalAction;
import atree.core.processes.actions.QueryAction;
import atree.core.processes.actions.RemoveAction;
import atree.core.processes.constraints.ActionRequiresConstraint;
import atree.core.processes.constraints.OrderedRelationConstraint;
import atree.core.processes.constraints.RoleChangeConstraint;
import atree.core.processes.constraints.UnorderedRelationConstraint;
import atree.core.processes.interfaces.IAction;
import atree.core.processes.interfaces.IConstraint;
import atree.core.variables.AtreeVariable;
import atree.core.variables.SideEffect;
import atree.core.variables.VariableAssignment;
import cern.jet.random.engine.MersenneTwister;
import it.imtlucca.util.RandomEngineFacilities;

public class AtreeModel {
	
	private RandomEngineFacilities randomGenerator;
	
	private HashMap<String, Node> _nodeDefinitions;
	private Collection<CountermeasureNode> _countermeasureNodes;
	private Collection<DefenseNode> _defenseNodes;
	private Collection<AttackNode> _attackNodes;
	private HashMap<String, AtreeVariable> _variables;
	private Set<IConstraint> _constraints;
	private HashMap<Node,Set<IConstraint>> autoHierarchicalConstraints;
	private HashMap<Node,Set<IConstraint>> hierarchicalConstraints;
	
	private Collection<IAttributeDef> attributeDefs;
	private HashMap<String,IAttributeDef> nameToAttributeDefs;
	
	private Collection<Attacker> attackers;		// For future work with multiple attackers
	private Attacker currentAttacker; 			// For future work with multiple attackers
	
	private Collection<Action> _actions;
	private HashMap<NormalAction, Set<IConstraint>> _normalActionConstraints;
	private HashMap<AttackNode, Set<IConstraint>> _addActionConstraints;
	private HashMap<AttackNode, Set<IConstraint>> _failActionConstraints;
	private HashMap<Node, Set<IConstraint>> _queryActionConstraints;
	private HashMap<AttackNode, Set<IConstraint>> _removeActionConstraints;
	
	private HashMap<String,Integer> _installedAttackNodes;
	private HashMap<String,Integer> _installedDefenseNodes;
	private HashMap<String,Integer> _installedCountermeasureNodes;
	private Set<AttackNode> initialAttackNodes;
	private Set<CountermeasureNode> activatedCountermeasureNodes;
	//private ArrayList<INode> allInstalledNodes;
	
	private HashMap<String,HashMap<String,HashMap<String,Double>>> defenseEffectiveness;
	private ProcessState currentState;
	private ProcessState initialState;
	
	//A counter incremented every time we add a new node. Used to know which among two nodes has been added first  
	private int addedWhen;
	private int addedWhenInit=1;
	private int nodeNumber = 0;
	
	
	private VariableAssignment asgn;
	private AttributeEvaluator attrEval;

	private HashMap<OrderedRelationConstraint, ArrayList<AttackNode>> allOANDConstraints;
		
	public AtreeModel()
	{
		nodeNumber=0;
		_nodeDefinitions = new HashMap<>();
		_countermeasureNodes = new LinkedHashSet<>();
		_defenseNodes = new LinkedHashSet<>();
		_attackNodes = new LinkedHashSet<>();
		_variables = new HashMap<>();
		asgn = new VariableAssignment();
		_installedAttackNodes = new HashMap<>();
		_installedDefenseNodes = new HashMap<>();
		_installedCountermeasureNodes = new HashMap<>();
		//allInstalledNodes=new ArrayList<>();
		allOANDConstraints = new HashMap<OrderedRelationConstraint,ArrayList<AttackNode>>();
		
		autoHierarchicalConstraints = new HashMap<>();
		hierarchicalConstraints = new HashMap<>();
		_constraints = new LinkedHashSet<>();
		
		attackers = new LinkedHashSet<>();
		attributeDefs = new LinkedHashSet<>();
		nameToAttributeDefs = new HashMap<>();
		attrEval = new AttributeEvaluator();
		defenseEffectiveness = new HashMap<>();
		
		_actions = new LinkedHashSet<>();
		_normalActionConstraints = new HashMap<>();
		_addActionConstraints = new HashMap<>();
		_failActionConstraints = new HashMap<>();
		_queryActionConstraints = new HashMap<>();
		_removeActionConstraints = new HashMap<>();
		
		addedWhen = addedWhenInit;
	}
	
	
	public AtreeModel clone() {
		AtreeModel clone = new AtreeModel();
		
		/* BEGIN STATIC INFORMATION */
		clone._nodeDefinitions = _nodeDefinitions;
		clone._countermeasureNodes = _countermeasureNodes;
		clone._defenseNodes = _defenseNodes;
		clone._attackNodes = _attackNodes;
		clone._variables = _variables;
		clone._constraints = _constraints;
		clone.autoHierarchicalConstraints = autoHierarchicalConstraints;
		clone.hierarchicalConstraints = hierarchicalConstraints;
		clone.attributeDefs = attributeDefs;
		clone.nameToAttributeDefs = nameToAttributeDefs;
		clone.attackers = attackers;
	
		clone._actions = _actions;
		clone._normalActionConstraints = _normalActionConstraints;
		clone._addActionConstraints = _addActionConstraints;
		clone._failActionConstraints = _failActionConstraints;
		clone._queryActionConstraints = _queryActionConstraints;
		clone._removeActionConstraints = _removeActionConstraints;
		
		clone.initialAttackNodes=initialAttackNodes;
		
		clone.defenseEffectiveness = defenseEffectiveness;
		
		clone.initialState=initialState;
		clone.nodeNumber=nodeNumber;
		
		
		/* END STATIC INFORMATION */
		
		/* BEGIN DYNAMIC INFORMATION */
		clone.currentAttacker = currentAttacker;
		
		//These hashmaps map the name of a node to the step in which it has been added
		clone._installedAttackNodes = new HashMap<>(_installedAttackNodes);
		clone._installedDefenseNodes = new HashMap<>(_installedDefenseNodes);
		clone._installedCountermeasureNodes = new HashMap<String, Integer>(_installedCountermeasureNodes);
		//clone.allInstalledNodes= new ArrayList<>(allInstalledNodes);
		clone.allOANDConstraints= new HashMap<OrderedRelationConstraint, ArrayList<AttackNode>>(allOANDConstraints.size());
		for(Entry<OrderedRelationConstraint, ArrayList<AttackNode>> entry : allOANDConstraints.entrySet()) {
			clone.allOANDConstraints.put(entry.getKey(), new ArrayList<>(entry.getValue()));
		}
		
		clone.activatedCountermeasureNodes=new LinkedHashSet<>(activatedCountermeasureNodes);
		
		clone.currentState = currentState;
		clone.addedWhen = addedWhen;
		
		//I avoid doing this clone because I can't control it. Also, I clone only to generate the whole DTMC, where I don't need this object
		//clone.randomGenerator=(RandomEngineFacilities) randomGenerator.clone();
		clone.randomGenerator=null;
		
		//The actual 'current state'
 

		clone.asgn=asgn.clone();
		//		for(Entry<String, AtreeVariable> entry : _variables.entrySet()) {
		//			AtreeVariable var = entry.getValue();
		//			AtreeVariable cloneVar = new AtreeVariable(var.getName(), var.eval());
		//			clone._variables.put(entry.getKey(), cloneVar);
		//		}

		//AttributeDefs actually contains also the values of the attributes. This is unfortunate
		clone.attrEval=attrEval.clone();
//		clone.attributeDefs = new LinkedHashSet<>(attributeDefs.size());
//		clone.nameToAttributeDefs = new HashMap<>(attributeDefs.size());
//		for(IAttributeDef attr : attributeDefs) {
//			IAttributeDef clonedAttr = attr.clone();
//			clone.attributeDefs.add(clonedAttr);
//			clone.nameToAttributeDefs.put(clonedAttr.getName(), clonedAttr);
//		}
		

		/* END DYNAMIC INFORMATION */
		
		return clone;
	}
	
	
	
	/*
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof AtreeModel)) {
			return false;
		}
		AtreeModel o = (AtreeModel)obj;
		//Should I use this counter to compare? 
//		if(addedWhen!=o.addedWhen) {
//			return false;
//		}
		if(!currentAttacker.getName().equals(o.currentAttacker.getName())) {
			return false;
		}
		if(!currentState.getName().equals(o.currentState.getName())) {
			return false;
		}
		if(!activatedCountermeasureNodes.equals(o.activatedCountermeasureNodes)) {
			return false;
		}
		if(!_installedAttackNodes.equals(o._installedAttackNodes)) {
			return false;
		}
		if(!_installedDefenseNodes.equals(o._installedDefenseNodes)) {
			return false;
		}
		if(!_installedCountermeasureNodes.equals(o._installedCountermeasureNodes)) {
			return false;
		}
		if(!asgn.equals(o.asgn)) {
			return false;
		}
		if(!attrEval.equals(o.attrEval)) {
			return false;
		}
		
		return true;
		//super.equals(obj);
	}
	*/
	
	public void setInitialState(ProcessState state) {
		initialState = state;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_installedAttackNodes == null) ? 0 : _installedAttackNodes.hashCode());
		result = prime * result
				+ ((_installedCountermeasureNodes == null) ? 0 : _installedCountermeasureNodes.hashCode());
		result = prime * result + ((_installedDefenseNodes == null) ? 0 : _installedDefenseNodes.hashCode());
		result = prime * result
				+ ((activatedCountermeasureNodes == null) ? 0 : activatedCountermeasureNodes.hashCode());
//		result = prime * result
//				+ ((allInstalledNodes == null) ? 0 : allInstalledNodes.hashCode());
		result = prime * result
				+ ((allOANDConstraints == null) ? 0 : allOANDConstraints.hashCode());
		result = prime * result + ((asgn == null) ? 0 : asgn.hashCode());
		result = prime * result + ((attrEval == null) ? 0 : attrEval.hashCode());
		result = prime * result + ((currentAttacker == null) ? 0 : currentAttacker.hashCode());
		result = prime * result + ((currentState == null) ? 0 : currentState.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AtreeModel other = (AtreeModel) obj;
		if (_installedAttackNodes == null) {
			if (other._installedAttackNodes != null)
				return false;
		} else if (!_installedAttackNodes.equals(other._installedAttackNodes))
			return false;
		if (_installedCountermeasureNodes == null) {
			if (other._installedCountermeasureNodes != null)
				return false;
		} else if (!_installedCountermeasureNodes.equals(other._installedCountermeasureNodes))
			return false;
		if (_installedDefenseNodes == null) {
			if (other._installedDefenseNodes != null)
				return false;
		} else if (!_installedDefenseNodes.equals(other._installedDefenseNodes))
			return false;
		if (activatedCountermeasureNodes == null) {
			if (other.activatedCountermeasureNodes != null)
				return false;
		} else if (!activatedCountermeasureNodes.equals(other.activatedCountermeasureNodes))
			return false;
//		if (allInstalledNodes == null) {
//			if (other.allInstalledNodes != null)
//				return false;
//		} else if (!allInstalledNodes.equals(other.allInstalledNodes))
//			return false;
		if (allOANDConstraints == null) {
			if (other.allOANDConstraints != null)
				return false;
		} else if (!allOANDConstraints.equals(other.allOANDConstraints))
			return false;
		if (asgn == null) {
			if (other.asgn != null)
				return false;
		} else if (!asgn.equals(other.asgn))
			return false;
		if (attrEval == null) {
			if (other.attrEval != null)
				return false;
		} else if (!attrEval.equals(other.attrEval))
			return false;
		if (currentAttacker == null) {
			if (other.currentAttacker != null)
				return false;
		} else if (!currentAttacker.equals(other.currentAttacker))
			return false;
		if (currentState == null) {
			if (other.currentState != null)
				return false;
		} else if (!currentState.equals(other.currentState))
			return false;
		return true;
	}

	public HashMap<String,Integer> getInstalledAttackNodes()
	{
		return _installedAttackNodes;
	}
	public Set<CountermeasureNode> getActivatedCountermeasures()
	{
		return activatedCountermeasureNodes;
	}
	
	public void init(Attacker a, Collection<AttackNode> aNodes)
	{
		
		currentState = initialState;
		activatedCountermeasureNodes = new LinkedHashSet<>();
		initialAttackNodes = new LinkedHashSet<>();

		installDefenseNodes();
		
		currentAttacker = a;
		
		for (AttackNode an : aNodes) {
			initialAttackNodes.add(an);
			add(an,false);		// TODO: Should these increment attribute value or not? Are there any constraints to this????
		}
		
		// Set current attacker
		// Add initial attacks (These might have to saved in private list in order for resetToInitialState() to work)
		//  - Initial attacks can remove initial defenses. 
		//  - However, these defenses should be part of cumulative defender attribute value.
	}
	
	private void installDefenseNodes() {
		for (DefenseNode n : _defenseNodes) {
			add(n);
		}
	}

	public void addActionConstraint(ActionRequiresConstraint arc)
	{
		Set<IConstraint> constraints = null;
		IAction a = arc.getAction();
		if (a instanceof NormalAction)
		{
			constraints=_normalActionConstraints.get((NormalAction)a);
			if(constraints==null)
			{
				constraints=new LinkedHashSet<IConstraint>();
				_normalActionConstraints.put((NormalAction)a, constraints);	
			}
		}
		else if (a instanceof AddAction)
		{
			AttackNode n = ((AddAction)a).getAttackNode();
			constraints=_addActionConstraints.get(n);
			if(constraints==null)
			{
				constraints=new LinkedHashSet<IConstraint>();
				_addActionConstraints.put(n, constraints);	
			}
		}
		else if (a instanceof FailAction)
		{
			AttackNode n = ((FailAction)a).getAttackNode();
			constraints=_failActionConstraints.get(n);
			if(constraints==null)
			{
				constraints=new LinkedHashSet<IConstraint>();
				_failActionConstraints.put(n, constraints);	
			}
		}
		else if (a instanceof RemoveAction)
		{
			AttackNode n = ((RemoveAction)a).getAttackNode();
			constraints=_removeActionConstraints.get(n);
			if(constraints==null)
			{
				constraints=new LinkedHashSet<IConstraint>();
				_removeActionConstraints.put(n, constraints);	
			}	
		}
		else if (a instanceof QueryAction)
		{
			Node n = ((QueryAction)a).getNode();
			constraints=_queryActionConstraints.get(n);
			if(constraints==null)
			{
				constraints=new LinkedHashSet<IConstraint>();
				_queryActionConstraints.put(n, constraints);	
			}
		}
		
		if (constraints != null)
		{
			constraints.add(arc.getConstraint());
		}
		else
		{
			System.out.println("It is not possible to assign action constraints to "+a.getName());
		}
	}

	public void addNormalAction(NormalAction a)
	{
		_actions.add(a);
	}
	
	public AtreeVariable addVariable(String name, IAttributeExpr expr)
	{
		double value = expr.eval(asgn,attrEval);
		AtreeVariable var = new AtreeVariable(name, value);
		_variables.put(name, var);
		asgn.setValue(var, value);
		return var;
	}
	
	public Set<IConstraint> getActionConstraints(IAction a)
	{
		Set<IConstraint> constraints = null;
		
		if (a instanceof AddAction) {
			constraints = _addActionConstraints.get(((AddAction) a).getAttackNode());
		}
		else if (a instanceof RemoveAction) {
			constraints = _removeActionConstraints.get(((RemoveAction) a).getAttackNode());
		}
		else if (a instanceof QueryAction) {
			constraints = _queryActionConstraints.get(((QueryAction) a).getNode());
		}
		else if (a instanceof FailAction) {
			constraints = _failActionConstraints.get(((FailAction) a).getAttackNode());
		}
		else if (a instanceof NormalAction) {
			constraints = _normalActionConstraints.get((NormalAction) a);
		}
		return constraints;
	}
	
	public void addDefenseNodeDefinition(DefenseNode n)
	{
		_nodeDefinitions.put(n.getName(), n);
		_defenseNodes.add(n);
	}
	
	public void addAttackNodeDefinition(AttackNode n)
	{
		_nodeDefinitions.put(n.getName(), n);
		_attackNodes.add(n);
	}
	
	public void addCountermeasureNodeDefinition(CountermeasureNode n,Collection<AttackNode> aNodes)
	{
		_nodeDefinitions.put(n.getName(), n);
		_countermeasureNodes.add(n);
		for (AttackNode aNode : aNodes)
		{
			aNode.addCountermeasure(n);
		}
	}
	
	public void addAttacker(Attacker a)
	{
		attackers.add(a);
	}
	
	//TODO: RELATION BOOKMARK
	public void addAllRelations(LinkedHashMap<String,Set<String>> orRelations, LinkedHashMap<String,Set<String>> andRelations,
			LinkedHashMap<String,Set<String>> knRelations, LinkedHashMap<String,Set<String>> oandRelations,
			LinkedHashMap<String,List<String>> childrenMap,LinkedHashMap<String,Integer> knChildren) {
		
		LinkedHashMap<String,List<String>> roleChanges = new LinkedHashMap<>();
		
		orRelations = roleChangeModifications(orRelations, roleChanges,childrenMap);
		andRelations = roleChangeModifications(andRelations, roleChanges,childrenMap);
		knRelations = roleChangeModifications(knRelations, roleChanges,childrenMap);
		oandRelations = roleChangeModifications(oandRelations, roleChanges,childrenMap);
		
		// merge role changes (use rolechange map)
		addAllRoleChangeRelations(roleChanges);
		
		// merge OR-Relations
		mergeOrRelations(orRelations, childrenMap);
		
		// modify relations
		modifyAndCreateRelations(orRelations, andRelations, knRelations, oandRelations, childrenMap, knChildren);
	}

	private void modifyAndCreateRelations(HashMap<String, Set<String>> orRelations,
			HashMap<String, Set<String>> andRelations, HashMap<String, Set<String>> knRelations,
			HashMap<String, Set<String>> oandRelations, HashMap<String, List<String>> childrenMap,
			HashMap<String, Integer> knChildren) {
		Set<String> allParents = new HashSet<>();
		for (String s : orRelations.keySet()) {
			allParents.add(s);
		}
		for (String s : andRelations.keySet()) {
			allParents.add(s);
		}
		for (String s : knRelations.keySet()) {
			allParents.add(s);
		}
		for (String s : oandRelations.keySet()) {
			allParents.add(s);
		}
		for (String parentNode : allParents) 
		{	
			int numberOfRelations = (orRelations.containsKey(parentNode)?1:0) + (andRelations.containsKey(parentNode)?andRelations.get(parentNode).size():0) +
					(knRelations.containsKey(parentNode)?knRelations.get(parentNode).size():0) + 
					(oandRelations.containsKey(parentNode)?oandRelations.get(parentNode).size():0);
			if (numberOfRelations > 1) {
				String[] newNodes = new String[numberOfRelations];
				boolean an = (_nodeDefinitions.get(parentNode) instanceof AttackNode);
				
				for (int i=0; i<numberOfRelations;i++) 
				{
					String name = generateNodeName();
					newNodes[i] = name;

					if (an) {
						AttackNode n = new AttackNode(name,true);
						addAttackNodeDefinition(n);
					}
					else {
						DefenseNode n = new DefenseNode(name);
						addDefenseNodeDefinition(n);
					}
				}
				addOrRelation(parentNode, Arrays.asList(newNodes));
				// Add relations from each child..
				int i = 0;
				if (orRelations.containsKey(parentNode)) {
					for (String setID : orRelations.get(parentNode)) {
						addOrRelation(newNodes[i], childrenMap.get(setID));
						i++;
					}
				}
				if (andRelations.containsKey(parentNode)) {
					for (String setID : andRelations.get(parentNode)) {
						addAndRelation(newNodes[i], childrenMap.get(setID));
						i++;
					}
				}
				if (knRelations.containsKey(parentNode)) {
					for (String setID : knRelations.get(parentNode)) {
						addKNRelation(newNodes[i], childrenMap.get(setID),knChildren.get(setID));
						i++;
					}
				}
				if (oandRelations.containsKey(parentNode)) {
					for (String setID : oandRelations.get(parentNode)) {
						addOAndRelation(newNodes[i], childrenMap.get(setID));
						i++;
					}
				}
				
			}
			else {
				if (orRelations.containsKey(parentNode)) {
					for (String s : orRelations.get(parentNode)) {
						addOrRelation(parentNode, childrenMap.get(s));
					}
				}
				else if (andRelations.containsKey(parentNode)) {
					for (String s : andRelations.get(parentNode)) {
						addAndRelation(parentNode, childrenMap.get(s));
					}
				}
				else if (knRelations.containsKey(parentNode)) {
					for (String s : knRelations.get(parentNode)) {
						addKNRelation(parentNode, childrenMap.get(s),knChildren.get(s));
					}
				}
				else if (oandRelations.containsKey(parentNode)) {
					for (String s : oandRelations.get(parentNode)) {
						addOAndRelation(parentNode, childrenMap.get(s));
					}
				}
			}
		}
	}

	private void mergeOrRelations(HashMap<String, Set<String>> orRelations, HashMap<String, List<String>> childrenMap) {
		for (String s : orRelations.keySet()) {
			if (orRelations.get(s).size() > 1) {
				List<String> newSet = new ArrayList<>();
				Set<String> newIDSet = new HashSet<>();
				String newID = "";
				for (String setID : orRelations.get(s)) {
					if (newIDSet.isEmpty()) {
						newIDSet.add(setID);
						newID = setID;
					}
					for (String child : childrenMap.get(setID)) {
						newSet.add(child);
					}
				}
				orRelations.put(s,newIDSet);
				childrenMap.put(newID, newSet);
			}
		}
	}

	private void addAllRoleChangeRelations(HashMap<String, List<String>> roleChanges) {
		for (String parent : roleChanges.keySet()) {
			if (roleChanges.get(parent).size() > 1) {
				String name = generateNodeName();
				List<String> children = new ArrayList<>();
				for (String child : roleChanges.get(parent)) {
					children.add(child);
				}
				if (_nodeDefinitions.get(parent) instanceof AttackNode) {
					DefenseNode child = new DefenseNode(name);
					addDefenseNodeDefinition(child);
				}
				else {
					AttackNode child = new AttackNode(name,true);
					addAttackNodeDefinition(child);
				}
				addRoleChangeRelation(parent, name);
				addOrRelation(name, children);
			}
			else {
				addRoleChangeRelation(parent, roleChanges.get(parent).get(0));
			}
		}
	}

	private LinkedHashMap<String, Set<String>> roleChangeModifications(LinkedHashMap<String, Set<String>> relations,LinkedHashMap<String, List<String>> roleChanges, 
			LinkedHashMap<String, List<String>> childrenMap) {
		
		LinkedHashMap<String, Set<String>> returnMap = new LinkedHashMap<>();
		
		for (String parentNode : relations.keySet()) {
			Set<String> newSet = new HashSet<>();
			for (String setID : relations.get(parentNode)) {
				List<String> lst = childrenMap.get(setID);
				if (_nodeDefinitions.get(lst.get(0)).getClass() == _nodeDefinitions.get(parentNode).getClass()) {
					newSet.add(setID);
				}
				else {
					String name;
					if (lst.size() > 1) {		// No need for intermediate node, if only one child anyways
						// Generating new node and assigning children
						name = generateNodeName();
						Set<String> set = new HashSet<>();
						set.add(setID);
						returnMap.put(name, set);
						if (_nodeDefinitions.get(lst.get(0)) instanceof AttackNode) {
							AttackNode an = new AttackNode(name,true);
							addAttackNodeDefinition(an);
						}					
						else {
							// Not countermeasure node. These will be part of the refinement of this node.
							DefenseNode dn = new DefenseNode(name);
							addDefenseNodeDefinition(dn);
						}
					}
					else {
						name = lst.get(0);
					}
					if (roleChanges.containsKey(parentNode)) {
						roleChanges.get(parentNode).add(name);
					}
					else {
						List<String> roleChangeSet = new ArrayList<>();
						roleChangeSet.add(name);
						roleChanges.put(parentNode, roleChangeSet);
					}
				}
			}
			returnMap.put(parentNode,newSet);
		}
		return returnMap;
	}
	private String generateNodeName() {
		String ret;
		do{
			ret = "node" + nodeNumber;
			nodeNumber++;
		} while (_nodeDefinitions.containsKey(ret));
		
		return ret;
	}
	public void addOrRelation(String root,Collection<String> children) {
		Node n = _nodeDefinitions.get(root);
		List<Node> ln = new ArrayList<>();
		for (String s : children) {
			ln.add(_nodeDefinitions.get(s));
		}
		addOrRelation(n,ln);
	}
	public void addAndRelation(String root,Collection<String> children) {
		Node n = _nodeDefinitions.get(root);
		List<Node> ln = new ArrayList<>();
		for (String s : children) {
			ln.add(_nodeDefinitions.get(s));
		}
		addAndRelation(n,ln);
	}
	public void addKNRelation(String root,Collection<String> children, int k) {
		Node n = _nodeDefinitions.get(root);
		List<Node> ln = new ArrayList<>();
		for (String s : children) {
			ln.add(_nodeDefinitions.get(s));
		}
		addKNRelation(n,ln,k);
	}
	public void addOAndRelation(String root,Collection<String> children) {
		Node n = _nodeDefinitions.get(root);
		List<Node> ln = new ArrayList<>();
		for (String s : children) {
			ln.add(_nodeDefinitions.get(s));
		}
		addOAndRelation(n,ln);
	}
	public void addRoleChangeRelation(String root,String child) {
		addRoleChangeRelation(_nodeDefinitions.get(root), _nodeDefinitions.get(child));
	}

	public void addOrRelation(Node root, Collection<Node> children) {
		UnorderedRelationConstraint constraint = new UnorderedRelationConstraint(root, children, 1);
		
		addHierarchicalConstraint(root, children, constraint);
	}	
	public void addAndRelation(Node root, Collection<Node> children) {
		UnorderedRelationConstraint constraint = new UnorderedRelationConstraint(root, children, children.size());
		
		addHierarchicalConstraint(root, children, constraint);
	}	
	public void addKNRelation(Node root, Collection<Node> children, int k) {
		UnorderedRelationConstraint constraint = new UnorderedRelationConstraint(root, children, k);
		
		addHierarchicalConstraint(root, children, constraint);
	}	
	public void addOAndRelation(Node root, Collection<Node> children) {
		OrderedRelationConstraint constraint = new OrderedRelationConstraint(root, children);
		
		allOANDConstraints.put(constraint, new ArrayList<>());
		
		addHierarchicalConstraint(root, children, constraint);
	}
	public void addRoleChangeRelation(Node root, Node child) {
		child.addParent(root);

		if (!(root instanceof AttackNode)) {
			RoleChangeConstraint constraint = new RoleChangeConstraint(root, child);
			if (autoHierarchicalConstraints.containsKey(root)) {
				autoHierarchicalConstraints.get(root).add(constraint);
			}
			else {
				Set<IConstraint> set = new LinkedHashSet<>();
				set.add(constraint);
				autoHierarchicalConstraints.put(root, set);
			}
		}
	}

	private void addHierarchicalConstraint(Node root, Collection<Node> children, IConstraint constraint) {
		for (Node child : children) {
			child.addParent(root);
		}
		if (root instanceof AttackNode && !(((AttackNode)root).isGhost())) {
			if (hierarchicalConstraints.containsKey(root)) {
				hierarchicalConstraints.get(root).add(constraint);
			}
			else {
				Set<IConstraint> set = new LinkedHashSet<>();
				set.add(constraint);
				hierarchicalConstraints.put(root, set);
			}
		}
		else {
			if (autoHierarchicalConstraints.containsKey(root)) {
				autoHierarchicalConstraints.get(root).add(constraint);
			}
			else {
				Set<IConstraint> set = new LinkedHashSet<>();
				set.add(constraint);
				autoHierarchicalConstraints.put(root, set);
			}
		}
	}
	 
	public void addConstraint(IConstraint constraint) {
		_constraints.add(constraint);
	}
	
	public void addAttackDetectionRate(AttackNode n,double r) {
		((AttackNode)_nodeDefinitions.get(n.getName())).setDetectionRate(r);
	}
	
	public void addAttributeDef(IAttributeDef attributeDef) {
		attributeDefs.add(attributeDef);
		nameToAttributeDefs.put(attributeDef.getName(),attributeDef);
	}
	
	public void setDefenseEffectivenesss(Collection<Attacker> al,Collection<AttackNode> anl,Node dn, double val) 
	{
		for (Attacker a : al) 
		{
			String aName = a.getName();
			if (!defenseEffectiveness.containsKey(aName)) 
			{
				defenseEffectiveness.put(aName, new HashMap<String,HashMap<String,Double>>());
			}
			HashMap<String,HashMap<String,Double>> map = defenseEffectiveness.get(aName);
			for (AttackNode an : anl)
			{
				String anName = an.getName();
				if (!map.containsKey(anName))
				{
					map.put(anName, new HashMap<String,Double>());
				}
				HashMap<String,Double> map2 = map.get(anName);
				String dnName = dn.getName();
				if (!map2.containsKey(dnName)) // No overwriting
				{
					map2.put(dnName, val);
				}
			}
		}
	}
	public List<Double> getDefenseEffectiveness(AttackNode an){
		return getDefenseEffectiveness(currentAttacker, an);
	}
	public List<Double> getDefenseEffectiveness(Attacker a, AttackNode an)//,DefenseNode dn)
	{
		String aName = a.getName();
		String anName = an.getName();
		List<Double> returnVal = new ArrayList<>();
		if (defenseEffectiveness.containsKey(aName) && defenseEffectiveness.get(aName).containsKey(anName)) 
		{			
			HashMap<String,Double> aMap = defenseEffectiveness.get(aName).get(anName);
			for (String s : aMap.keySet())
			{
				// Check if defense is installed!
				if (isActiveInDefenseRefinement(an.getName(),s))
				{
					// TODO:Maybe check if defense node is actually part of subtree?? Or is this necessary?
					returnVal.add(aMap.get(s));
				}
			}
		}
		return returnVal;
	}
	
	private boolean isActiveInDefenseRefinement(String name, String s) {
		if (name.equals(s)) {
			return true;
		}
		else if (_installedCountermeasureNodes.containsKey(s) || _installedDefenseNodes.containsKey(s)) {
			for (Node p : _nodeDefinitions.get(s).getParents()) {
				if (isActiveInDefenseRefinement(name, p.getName())) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	public int whenAdded(Node n, OrderedRelationConstraint oand) { //TODO Rename as it only considers attackNodes
		ArrayList<AttackNode> installedNodes = allOANDConstraints.get(oand);
		if(installedNodes==null) {
			return -1;
		}
		else {
			return installedNodes.indexOf(n);
		}
		/*
		return allInstalledNodes.indexOf(n);
		*/
		/*
		String name = n.getName();
		if (_installedAttackNodes.containsKey((name)))
		{
			return _installedAttackNodes.get((name));
		}
		else if (_installedDefenseNodes.containsKey(name)) 
		{
			return _installedDefenseNodes.get(name);
		}
		else if (_installedCountermeasureNodes.containsKey(name))
		{
			return _installedCountermeasureNodes.get(name);
		}
		return -1;
		*/
	}

	public HashMap<String, Integer> getInstalledDefenseNodes() {
		return _installedDefenseNodes;
	}

	public HashMap<String, Integer> getInstalledCountermeasureNodes() {
		return _installedCountermeasureNodes;
	}

	public boolean isInstalled(INode node) {
		return	_installedAttackNodes.containsKey(node.getName()) || 
				_installedDefenseNodes.containsKey(node.getName()) || 
				_installedCountermeasureNodes.containsKey(node.getName());
	}

	public void resetToInitialState() {
		// TODO Check for correctness
		for (AtreeVariable var : _variables.values()) {
			//var.setValue(var.getInitialValue());
			asgn.setValue(var, var.getInitialValue());
		}
		
		addedWhen=addedWhenInit;
		
		attrEval.resetValues();
//		for (IAttributeDef att : attributeDefs) {
//			att.resetValues();
//		}
		_installedAttackNodes.clear();
		_installedCountermeasureNodes.clear();
		_installedDefenseNodes.clear();
		activatedCountermeasureNodes.clear();
		//allInstalledNodes.clear();
		for(Entry<OrderedRelationConstraint, ArrayList<AttackNode>> entry : allOANDConstraints.entrySet()) {
			entry.getValue().clear();
		}
		
		
		init(currentAttacker,initialAttackNodes);		
	}
	
	/*
	 * Returns a number in (0,1] if this action might actually activate countermeasures. That is if
	 *  * this is an add(attackNode1), 
	 *  * attackNode.getDetectionRate()>0, 
	 *  * node.getActivatesCountermeasureNodes().size() and
	 *  It returns the detection rate of the node 
	 */
	public static double mightActivateCountermeasure(IAction action) {
		AttackNode node = null;
		if(action instanceof AddAction) {
			node = ((AddAction) action).getAttackNode();
		}
		else if (action instanceof FailAction) {
			node = ((FailAction) action).getAttackNode();
		}
		if(node!=null) {
			if(node.getDetectionRate() > 0 && node.getActivatesCountermeasureNodes().size() > 0) {
				return node.getDetectionRate();
			}
		}
		return -1;
	}

	/*
	 * Applies a commitment. If useRandom is false, decision is used to decide whether countermeasures should be activated (if a detectable attack is attempted
	 * If useRandom is true, decision is ignored
	 */
	public void apply(Commitment commitment) {
		apply(commitment,true,true);
	}
	
	public void apply(Commitment commitment,boolean useRandom,boolean decision) {
		currentState = commitment.getTransition().getTarget();
		IAction action = commitment.getTransition().getAction();
		AttackNode node = null;
		boolean isAddAction = action instanceof AddAction;
		boolean isFailAction = action instanceof FailAction;
		if (isAddAction) {
			node = ((AddAction) action).getAttackNode();
		}
		else if (isFailAction) {
			node = ((FailAction) action).getAttackNode();
		}
		if (node != null) 
		{
			if(useRandom) {
				//TODO: DETECTION RATE - SHOULD COUNTERMEASURE BE ADDED? CHECK CORRECTNESS (Math.random())
				if (node.getDetectionRate() > 0 && node.getActivatesCountermeasureNodes().size() > 0) {
					double ran = randomGenerator.nextDouble(); //Math.random();
					if (ran <= node.getDetectionRate()) {
						activateCountermeasures(node);
					}
				}
			}
			else {
				if(decision) {
					activateCountermeasures(node);
				}
			}
			if(isAddAction) {
				add(node);
			}
			else if(isFailAction) {
				incrementAttributes(node);
			}
		}
		else if (action instanceof RemoveAction)
		{
			remove(((RemoveAction) action).getAttackNode());
		}
		
		applySideEffects(commitment.getTransition().getSideEffects());
		
	}


	private void activateCountermeasures(AttackNode node) {
		for (CountermeasureNode n : node.getActivatesCountermeasureNodes()) {
			if (!activatedCountermeasureNodes.contains(n)) {
				if(canBeActivated(n)) {
					activatedCountermeasureNodes.add(n);
					add(n);
				}
			}
		}
	}

	/**
	 * Checks whether a countermeasure node con be activated: a countermeasure node might have a role-changing son (an attack node). If this attack node is active, then we cannot activate this countermeasure 
	 * @param n
	 * @return
	 */
	private boolean canBeActivated(CountermeasureNode n) {
		for(String attackNodeName : getInstalledAttackNodes().keySet()) {
			Node attackNode = _nodeDefinitions.get(attackNodeName);
			if(attackNode instanceof AttackNode) {
				if(attackNode.getParents().contains(n)) {
					return false;
				}
			}
		}
		//returns getInstalledAttackNodes(n.getParents())
		//return false;
		
		return true;
	}

	public double[] applySideEffects(SideEffect[] sideEffects) {
		double[] oldValues=null;
		if(sideEffects!=null&&sideEffects.length>0){
			oldValues = new double[sideEffects.length];
			double[] newValues = new double[sideEffects.length];
			for (int i = 0; i < sideEffects.length; i++) {
				AtreeVariable var = sideEffects[i].getVariable();
				oldValues[i]=var.eval(asgn,attrEval);//null);
				newValues[i] = sideEffects[i].evalUpdateExpression(asgn,attrEval);//installedFeatures);
			}
			for (int i = 0; i < sideEffects.length; i++) {
				AtreeVariable var = sideEffects[i].getVariable();
				//var.setValue(newValues[i]);
				asgn.setValue(var, newValues[i]);
			}
		}
		return oldValues;
		
	}

	public void revertSideEffects(SideEffect[] sideEffects, double[] oldValues) {
		if(sideEffects!=null){
			for (int i = 0; i < sideEffects.length; i++) {
				AtreeVariable var = sideEffects[i].getVariable();
				//var.setValue(oldValues[i]);
				asgn.setValue(var, oldValues[i]);
			}
		}
	}

	public boolean checkHierarchicalConstraints(Node node) {
		if (hierarchicalConstraints.containsKey(node)) {
			for (IConstraint constraint : hierarchicalConstraints.get(node)) {
				if (!constraint.eval(this)) {
					return false;
				}
			}
		}
		return checkConstraints();
	}
	public boolean checkConstraints(IAction action) {
		return checkConstraints();
	}
	public boolean checkConstraints() {	
		if (!_constraints.isEmpty()) {
			for (IConstraint constraint : _constraints) {
				if (!constraint.eval(this)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void add(Node node) { //TODO: just a bookmark..
		add(node,true);
	}
	public void add(Node node, boolean incrementAttributes) {
		add(node,incrementAttributes,false);
	}
	public void add(Node node, boolean incrementAttributes, boolean doNotAddNode_justAttributes) {
	//public void add(Node node, boolean incrementAttributes) {
		String nName = node.getName();
		
		if(!doNotAddNode_justAttributes) {
			if (node instanceof AttackNode) {
				_installedAttackNodes.put(nName,addedWhen);
				//allInstalledNodes.add(node);
				updateInfoOnOAndOrders((AttackNode)node,true);
			}
			else if (node instanceof DefenseNode) {
				_installedDefenseNodes.put(nName, addedWhen);
				//allInstalledNodes.add(node);
			}
			else if (node instanceof CountermeasureNode) {
				_installedCountermeasureNodes.put(nName, addedWhen);
				//allInstalledNodes.add(node);
			}
		}
		
		//I removed this line after having added allInstalledNodes. Now the _installed__Nodes could become just sets 
		//addedWhen++;
		
		if (incrementAttributes) {
			incrementAttributes(node);
		}
		
		if(!doNotAddNode_justAttributes) {
			checkAutomaticAddOrRemove(node);
		}
	}
	private void updateInfoOnOAndOrders(AttackNode node,boolean add) {
		for(Entry<OrderedRelationConstraint, ArrayList<AttackNode>> entry : allOANDConstraints.entrySet()) {
			OrderedRelationConstraint oand = entry.getKey();
			List<Node> possibleChildren = oand.getPossibleChilderInOrder();
			if(possibleChildren.contains(node)) {
				if(add) {
					entry.getValue().add(node);
				}
				else {
					boolean removed = entry.getValue().remove(node);
					if(!removed) {
						throw new RuntimeException("I could not remove "+node+" from the OAND with "+possibleChildren);
					}
				}
			}
		}
		
	}


	public void fail(Node node) {
		incrementAttributes(node);
	}
	public void remove(Node node) {
		remove(node,false);
	}
	public void remove(Node node, boolean decrementAttributes){
		remove(node,decrementAttributes,false);
	}
	public void remove(Node node, boolean decrementAttributes,boolean doNotRemoveNode_justAttributes){
		if(!doNotRemoveNode_justAttributes) {
			String nName = node.getName();
			if (_installedAttackNodes.containsKey(nName)) {
				_installedAttackNodes.remove(nName);
				//allInstalledNodes.remove(allInstalledNodes.indexOf(node));
				updateInfoOnOAndOrders((AttackNode)node,false);
			}
			else if (_installedDefenseNodes.containsKey(nName)) {
				_installedDefenseNodes.remove(nName);
				//allInstalledNodes.remove(allInstalledNodes.indexOf(node));
			}
			else if (_installedCountermeasureNodes.containsKey(nName)) {
				_installedCountermeasureNodes.remove(nName);
				//allInstalledNodes.remove(allInstalledNodes.indexOf(node));
			}
		}
		
		if (decrementAttributes) {
			decrementAttributes(node);
		}
		
		if(!doNotRemoveNode_justAttributes) {
			checkAutomaticAddOrRemove(node);
		}
	}
	private void checkAutomaticAddOrRemove(Node node) {
		// Purpose is to remove or add defense nodes which may or may not be active after adding or removing nodes
		// TODO: Check correctness. Consider moving this function call to "apply".
		for (Node parent : node.getParents()) {
//			boolean changes = false;
			if (autoHierarchicalConstraints.containsKey(parent)) {
				for (IConstraint constraint : autoHierarchicalConstraints.get(parent)) {
					if (constraint.eval(this)) {
						if (!isInstalled(parent) && !(parent instanceof CountermeasureNode && !activatedCountermeasureNodes.contains(parent))) {
							add(parent,false);
//							changes = true;
						}
					}
					else {
						if (isInstalled(parent)) {
							remove(parent,false);
//							changes = true;
						}
					}
				}
			}
//			if (changes) {
//				checkAutomaticAddOrRemove(parent);
//			}
		}
	}

	public void decrementAttributes(INode node) {
		for (IAttributeDef att : attributeDefs) {
			att.decrementValue(node,attrEval);
		}
	}
	public void incrementAttributes(INode node) {
		for (IAttributeDef att : attributeDefs) {
			att.incrementValue(node,attrEval);
		}
	}

	public void computeAllowedTransitions(SimulationStateInformation stateInfo) {
		Collection<ProcessTransition> allTransitions = currentState.getTransitions();
		
		for(ProcessTransition transition : allTransitions) {
			if (transition.isAllowed(this)) {
				double actualRate = transition.computeActualRate(this);
				stateInfo.addAllowedCommitment(transition, actualRate);
			}
			else {
				stateInfo.addForbiddenTransition(transition);
			}
		}
	}
	public ProcessState getCurrentState() {
		return currentState;
	}
	public Collection<AttackNode> getAttackNodes() {
		return _attackNodes;
	}
	public Collection<CountermeasureNode> getCountermeasureNodes() {
		return _countermeasureNodes;
	}

	public Collection<Attacker> getAttackers() {
		return attackers;
	}

	public INode getNode(String whichObservation) {
		return _nodeDefinitions.get(whichObservation);
	}

	public AtreeVariable getVariable(String whichObservation) {
		return _variables.get(whichObservation);
	}

	public IAttributeDef getAttributeDef(String attName) {
		return nameToAttributeDefs.get(attName);
	}

	public double eval(IAttributeExpr attExpr) {
		return attExpr.eval(asgn,attrEval);
	}

	public Attacker getCurrentAttacker() {
		return currentAttacker;
	}

	public String computeSATMessage() {
		StringBuilder sb=new StringBuilder();
		IConstraint unsat = checkSATCurrentState(); 
		if(unsat==null){
			sb.append("The current state satisfies all constraints.");
		}
		else{
			sb.append("The current state DOES NOT SATISFY all constraints (e.g.:\n");
			sb.append(unsat.toString());
		}
		return sb.toString();
	}

	public IConstraint checkSATCurrentState() {
		for (IConstraint constraint : _constraints) {
			if(!constraint.eval(this)){
				return constraint;
			}
		}
		for (AttackNode an : initialAttackNodes) {
			if (hierarchicalConstraints.containsKey(an)) {
				for (IConstraint constraint : hierarchicalConstraints.get(an)) {
					if (!constraint.eval(this)) {
						return constraint;
					}
				}
			}
		}
		
		return null;
	}
	
	public String currentStatus(){
		return currentStatus(new StringBuilder()).toString();
	}
	
	private StringBuilder currentStatus(StringBuilder sb) {
		//TODO: Implement
//		sb.append("\nBehavior:\n ");
//		sb.append(currentState.toString());
//		sb.append("\n\nInstalled concrete features:\n");
//		for (Node node : installedFeatures) {
//			sb.append(" "+concreteFeature.getName());
//			sb.append("\n");
//		}
//		//sb.append(installedFeatures);
//		sb.append("\nVariables:\n");
//		for (Entry<String, QFLanVariable> v : variables.entrySet()) {
//			sb.append(" "+v.getValue().toStringWithValue()+"\n");
//		}
		return sb;
	}

	public String printVariables() {
		if(_variables.isEmpty()) {
			return "No variables\n";
		}
		else {
			return asgn.toString();
//			StringBuffer sb = new StringBuffer();
//			sb.append(" ");
//			for (AtreeVariable var : _variables.values()) {
//				sb.append(var.print(asgn));
//				sb.append("\n");
//			}
//			return sb.toString();
		}
	}

	public String printRelations() {
		StringBuffer sb = new StringBuffer();
		for (Node key : autoHierarchicalConstraints.keySet()) {
			for (IConstraint constraint : autoHierarchicalConstraints.get(key)) {
				sb.append(constraint.toString());
				sb.append("\n");
			}
		}
		for (Node key : hierarchicalConstraints.keySet()) {
			for (IConstraint constraint : hierarchicalConstraints.get(key)) {
				sb.append(constraint.toString());
				sb.append("\n");
			}
		}
		
		return sb.toString();
	}

	public VariableAssignment getVariableAssignment() {
		return asgn;
	}
	public void setVariableAssignment(VariableAssignment asgn) {
		this.asgn=asgn;
	}

	public AttributeEvaluator getAttributeEvaluator() {
		return attrEval;
	}
	public void setAttributeEvaluator(AttributeEvaluator attrEval) {
		this.attrEval=attrEval;
	}

//	public ArrayList<INode> getAllInstalledNodes() {
//		return allInstalledNodes;
//	}


	public void setRandomSeed(int randomSeed) {
		randomGenerator = new RandomEngineFacilities(new MersenneTwister(randomSeed));
	}
	public void dropRandomGenerator() {
		randomGenerator=null;
	}
}
