package atree.core.models;


import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import atree.core.attacker.Attacker;
import atree.core.attributes.AttributeDef;
import atree.core.attributes.Constant;
import atree.core.model.AtreeModel;
import atree.core.model.IAtreeModelBuilder;
import atree.core.nodes.AttackNode;
import atree.core.nodes.CountermeasureNode;
import atree.core.nodes.DefenseNode;
import atree.core.processes.ProcessState;
import atree.core.processes.ProcessTransition;
import atree.core.processes.actions.AddAction;
import atree.core.processes.actions.FailAction;
import atree.core.processes.constraints.TrueConstraint;
import atree.core.variables.AtreeVariable;
import atree.core.variables.SideEffect;

public class Test implements IAtreeModelBuilder {
	
	public Test(){
		System.out.println("Model builder instantiated");
	}
	@SuppressWarnings("unused")
	public AtreeModel createModel(){
		
		AtreeModel model = new AtreeModel();		
		
		//////////////////
		/////Variables////
		//////////////////
		AtreeVariable x = model.addVariable("x", new Constant(3.0));
		AtreeVariable y = model.addVariable("y", new Constant(4.0));
		
		
		/////////////
		////Nodes////
		/////////////
		AttackNode attackroot = new AttackNode("attackroot");
		model.addAttackNodeDefinition(attackroot);
		AttackNode attack1 = new AttackNode("attack1");
		model.addAttackNodeDefinition(attack1);
		AttackNode attack2 = new AttackNode("attack2");
		model.addAttackNodeDefinition(attack2);
		AttackNode attack3 = new AttackNode("attack3");
		model.addAttackNodeDefinition(attack3);
		AttackNode attack4 = new AttackNode("attack4");
		model.addAttackNodeDefinition(attack4);
		AttackNode attack5 = new AttackNode("attack5");
		model.addAttackNodeDefinition(attack5);
		AttackNode attack6 = new AttackNode("attack6");
		model.addAttackNodeDefinition(attack6);
		AttackNode attack7 = new AttackNode("attack7");
		model.addAttackNodeDefinition(attack7);
		AttackNode attack8 = new AttackNode("attack8");
		model.addAttackNodeDefinition(attack8);
		AttackNode attack9 = new AttackNode("attack9");
		model.addAttackNodeDefinition(attack9);
		
		DefenseNode defense1 = new DefenseNode("defense1");
		model.addDefenseNodeDefinition(defense1);
		DefenseNode defense2 = new DefenseNode("defense2");
		model.addDefenseNodeDefinition(defense2);
		DefenseNode defense3 = new DefenseNode("defense3");
		model.addDefenseNodeDefinition(defense3);
		
		CountermeasureNode countermeasure1 = new CountermeasureNode("countermeasure1");
		model.addCountermeasureNodeDefinition(countermeasure1,Arrays.asList(attackroot));
		CountermeasureNode countermeasure2 = new CountermeasureNode("countermeasure2");
		model.addCountermeasureNodeDefinition(countermeasure2,Arrays.asList(attackroot));
		
		
		/////////////////
		////Relations////
		/////////////////
		LinkedHashMap<String,Set<String>> orRelations = new LinkedHashMap<>(); 
		LinkedHashMap<String,Set<String>> andRelations = new LinkedHashMap<>(); 
		LinkedHashMap<String,Set<String>> knRelations = new LinkedHashMap<>(); 
		LinkedHashMap<String,Set<String>> oandRelations = new LinkedHashMap<>(); 
		LinkedHashMap<String,List<String>> childrenMap = new LinkedHashMap<>();
		LinkedHashMap<String,Integer> knChildren = new LinkedHashMap<>();
		childrenMap.put("set0",Arrays.asList("countermeasure1","countermeasure2"));
		childrenMap.put("set1",Arrays.asList("defense1","defense2","defense3"));
		childrenMap.put("set2",Arrays.asList("attack1","attack2"));
		childrenMap.put("set3",Arrays.asList("attack3","attack4","attack5"));
		knChildren.put("set3",2);
		childrenMap.put("set4",Arrays.asList("attack6","attack7"));
		childrenMap.put("set5",Arrays.asList("attack8"));
		childrenMap.put("set6",Arrays.asList("attack9"));
		childrenMap.put("set7",Arrays.asList("attack9"));
		childrenMap.put("set8",Arrays.asList("attack9"));
		orRelations.put("defense1",new HashSet<>(Arrays.asList("set6")));
		orRelations.put("attackroot",new HashSet<>(Arrays.asList("set5","set4","set0")));
		orRelations.put("defense2",new HashSet<>(Arrays.asList("set7")));
		orRelations.put("defense3",new HashSet<>(Arrays.asList("set8")));
		andRelations.put("attackroot",new HashSet<>(Arrays.asList("set1")));
		knRelations.put("attackroot",new HashSet<>(Arrays.asList("set3")));
		oandRelations.put("attackroot",new HashSet<>(Arrays.asList("set2")));
		model.addAllRelations(orRelations,andRelations,knRelations,oandRelations,childrenMap,knChildren);
		
		//////////////////
		////Attributes////
		//////////////////
		AttributeDef AttackSteps = new AttributeDef("AttackSteps");
		model.addAttributeDef(AttackSteps);
		AttackSteps.setNodeValue(attackroot,1.0);
		AttackSteps.setNodeValue(attack1,1.0);
		AttackSteps.setNodeValue(attack2,1.0);
		AttackSteps.setNodeValue(attack3,1.0);
		AttackSteps.setNodeValue(attack4,1.0);
		AttackSteps.setNodeValue(attack5,1.0);
		AttackSteps.setNodeValue(attack6,1.0);
		AttackSteps.setNodeValue(attack7,1.0);
		AttackSteps.setNodeValue(attack8,1.0);
		AttackSteps.setNodeValue(attack9,1.0);
		
		/////////////////
		////Attackers////
		/////////////////
		Attacker testGraphAndBehaviour = new Attacker ("testGraphAndBehaviour");
		model.addAttacker(testGraphAndBehaviour);
		Attacker testOAND = new Attacker ("testOAND");
		model.addAttacker(testOAND);
		Attacker testDE = new Attacker ("testDE");
		model.addAttacker(testDE);
		
		/////////////////////////////
		////Defense Effectiveness////
		/////////////////////////////
		model.setDefenseEffectivenesss(Arrays.asList(testDE), Arrays.asList(attackroot),defense1,0.9);
		model.setDefenseEffectivenesss(Arrays.asList(testDE), Arrays.asList(attackroot),defense2,0.9);
		model.setDefenseEffectivenesss(Arrays.asList(testDE), Arrays.asList(attackroot),defense3,0.9);
		model.setDefenseEffectivenesss(model.getAttackers(), Arrays.asList(attackroot),countermeasure1,0.0);
		model.setDefenseEffectivenesss(model.getAttackers(), Arrays.asList(attackroot),countermeasure2,0.0);
		
		///////////////
		////Actions////
		///////////////
		
		//////////////////////////////
		////Attack Detection Rates////
		//////////////////////////////
		attackroot.setDetectionRate(1.0);
		
		////////////////////////////////
		////Quantitative Constraints////
		////////////////////////////////
		
		//////////////////////////
		////Action Constraints////
		//////////////////////////
		
		///////////////////////
		////Attack Diagrams////
		///////////////////////
		ProcessState initialState3 = new ProcessState("initialState3");
		model.setInitialState(initialState3);
		ProcessState finalState3 = new ProcessState("finalState3");
		initialState3.addTransition(new ProcessTransition(1.0,new AddAction(attack8),initialState3,new SideEffect[]{},new TrueConstraint()));
		initialState3.addTransition(new ProcessTransition(1.0,new FailAction(attack8),initialState3,new SideEffect[]{},new TrueConstraint()));
		initialState3.addTransition(new ProcessTransition(1.0,new AddAction(attackroot),finalState3,new SideEffect[]{},new TrueConstraint()));
		initialState3.addTransition(new ProcessTransition(1.0,new FailAction(attackroot),initialState3,new SideEffect[]{},new TrueConstraint()));
		
		///////////////////////
		////Initial Attacks////
		///////////////////////
		model.init(testDE,Arrays.asList());
		
		return model;
	}
}
