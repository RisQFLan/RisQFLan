package atree.core.models;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import atree.core.attacker.Attacker;
import atree.core.attributes.ArithmeticAttributeExpression;
import atree.core.attributes.ArithmeticOperation;
import atree.core.attributes.Attribute;
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
import atree.core.processes.actions.NormalAction;
import atree.core.processes.constraints.ActionRequiresConstraint;
import atree.core.processes.constraints.AllowedNodeConstraint;
import atree.core.processes.constraints.AttributeExprComparator;
import atree.core.processes.constraints.BooleanConnector;
import atree.core.processes.constraints.BooleanConstraintExpr;
import atree.core.processes.constraints.DisequationOfAttributeExpressions;
import atree.core.processes.constraints.HasNodeConstraint;
import atree.core.processes.constraints.NotConstraintExpr;
import atree.core.processes.constraints.TrueConstraint;
import atree.core.variables.AtreeVariable;
import atree.core.variables.SideEffect;

public class RobBank implements IAtreeModelBuilder {
	
	public RobBank(){
		System.out.println("Model builder instantiated");
	}
	public AtreeModel createModel(){
		
		AtreeModel model = new AtreeModel();		
		
		//////////////////
		/////Variables////
		//////////////////
		AtreeVariable testvar = model.addVariable("testvar", new Constant(2.0));
		AtreeVariable testvartwo = model.addVariable("testvartwo", new Constant(3.0));
		
		
		/////////////
		////Nodes////
		/////////////
		AttackNode RobBank = new AttackNode("RobBank");
		model.addAttackNodeDefinition(RobBank);
		AttackNode GetToVault = new AttackNode("GetToVault");
		model.addAttackNodeDefinition(GetToVault);
		AttackNode OpenVault = new AttackNode("OpenVault");
		model.addAttackNodeDefinition(OpenVault);
		AttackNode BlowUpVault = new AttackNode("BlowUpVault");
		model.addAttackNodeDefinition(BlowUpVault);
		AttackNode LearnCombo = new AttackNode("LearnCombo");
		model.addAttackNodeDefinition(LearnCombo);
		AttackNode CutOpenVault = new AttackNode("CutOpenVault");
		model.addAttackNodeDefinition(CutOpenVault);
		AttackNode Combo1 = new AttackNode("Combo1");
		model.addAttackNodeDefinition(Combo1);
		AttackNode Combo2 = new AttackNode("Combo2");
		model.addAttackNodeDefinition(Combo2);
		AttackNode Combo3 = new AttackNode("Combo3");
		model.addAttackNodeDefinition(Combo3);
		AttackNode Force1 = new AttackNode("Force1");
		model.addAttackNodeDefinition(Force1);
		AttackNode Force2 = new AttackNode("Force2");
		model.addAttackNodeDefinition(Force2);
		AttackNode Force3 = new AttackNode("Force3");
		model.addAttackNodeDefinition(Force3);
		AttackNode Eavesdrop1 = new AttackNode("Eavesdrop1");
		model.addAttackNodeDefinition(Eavesdrop1);
		AttackNode Eavesdrop2 = new AttackNode("Eavesdrop2");
		model.addAttackNodeDefinition(Eavesdrop2);
		AttackNode Eavesdrop3 = new AttackNode("Eavesdrop3");
		model.addAttackNodeDefinition(Eavesdrop3);
		AttackNode FindNote1 = new AttackNode("FindNote1");
		model.addAttackNodeDefinition(FindNote1);
		AttackNode FindNote2 = new AttackNode("FindNote2");
		model.addAttackNodeDefinition(FindNote2);
		AttackNode FindNote3 = new AttackNode("FindNote3");
		model.addAttackNodeDefinition(FindNote3);
		AttackNode ForceGuard = new AttackNode("ForceGuard");
		model.addAttackNodeDefinition(ForceGuard);
		AttackNode BribeGuard = new AttackNode("BribeGuard");
		model.addAttackNodeDefinition(BribeGuard);
		AttackNode HackCamera = new AttackNode("HackCamera");
		model.addAttackNodeDefinition(HackCamera);
		AttackNode PlayCameraLoop = new AttackNode("PlayCameraLoop");
		model.addAttackNodeDefinition(PlayCameraLoop);
		AttackNode CutPower = new AttackNode("CutPower");
		model.addAttackNodeDefinition(CutPower);
		AttackNode ShootCops = new AttackNode("ShootCops");
		model.addAttackNodeDefinition(ShootCops);
		AttackNode GetAway = new AttackNode("GetAway");
		model.addAttackNodeDefinition(GetAway);
		AttackNode HireGetAwayCar = new AttackNode("HireGetAwayCar");
		model.addAttackNodeDefinition(HireGetAwayCar);
		AttackNode EnterBank = new AttackNode("EnterBank");
		model.addAttackNodeDefinition(EnterBank);
		
		DefenseNode Surveilance = new DefenseNode("Surveilance");
		model.addDefenseNodeDefinition(Surveilance);
		DefenseNode Memorize1 = new DefenseNode("Memorize1");
		model.addDefenseNodeDefinition(Memorize1);
		DefenseNode Memorize2 = new DefenseNode("Memorize2");
		model.addDefenseNodeDefinition(Memorize2);
		DefenseNode Guard = new DefenseNode("Guard");
		model.addDefenseNodeDefinition(Guard);
		DefenseNode Camera = new DefenseNode("Camera");
		model.addDefenseNodeDefinition(Camera);
		DefenseNode LaserSensor = new DefenseNode("LaserSensor");
		model.addDefenseNodeDefinition(LaserSensor);
		
		CountermeasureNode CallCops = new CountermeasureNode("CallCops");
		model.addCountermeasureNodeDefinition(CallCops,Arrays.asList(CutPower,HackCamera,PlayCameraLoop,ForceGuard,EnterBank,CutOpenVault));
		CountermeasureNode LockDownVault = new CountermeasureNode("LockDownVault");
		model.addCountermeasureNodeDefinition(LockDownVault,Arrays.asList(CutPower,HackCamera,PlayCameraLoop,ForceGuard,EnterBank,ShootCops,BlowUpVault,GetToVault,Force1,Force2,Force3,Eavesdrop1,Eavesdrop3));
		
		
		/////////////////
		////Relations////
		/////////////////
		LinkedHashMap<String,Set<String>> orRelations = new LinkedHashMap<>(); 
		LinkedHashMap<String,Set<String>> andRelations = new LinkedHashMap<>(); 
		LinkedHashMap<String,Set<String>> knRelations = new LinkedHashMap<>(); 
		LinkedHashMap<String,Set<String>> oandRelations = new LinkedHashMap<>(); 
		LinkedHashMap<String,List<String>> childrenMap = new LinkedHashMap<>();
		LinkedHashMap<String,Integer> knChildren = new LinkedHashMap<>();
		childrenMap.put("set0",Arrays.asList("OpenVault","GetAway"));
		childrenMap.put("set1",Arrays.asList("HireGetAwayCar"));
		childrenMap.put("set2",Arrays.asList("LockDownVault"));
		childrenMap.put("set3",Arrays.asList("BlowUpVault"));
		childrenMap.put("set4",Arrays.asList("GetToVault"));
		childrenMap.put("set5",Arrays.asList("GetToVault","LearnCombo"));
		childrenMap.put("set6",Arrays.asList("CallCops"));
		childrenMap.put("set7",Arrays.asList("Surveilance"));
		childrenMap.put("set8",Arrays.asList("EnterBank"));
		childrenMap.put("set9",Arrays.asList("ShootCops"));
		childrenMap.put("set10",Arrays.asList("Guard","Camera"));
		childrenMap.put("set11",Arrays.asList("ForceGuard","BribeGuard"));
		childrenMap.put("set12",Arrays.asList("HackCamera","PlayCameraLoop"));
		childrenMap.put("set13",Arrays.asList("CutPower"));
		childrenMap.put("set14",Arrays.asList("LaserSensor"));
		childrenMap.put("set15",Arrays.asList("CutPower"));
		childrenMap.put("set16",Arrays.asList("CutOpenVault"));
		childrenMap.put("set17",Arrays.asList("Combo1","Combo2","Combo3"));
		knChildren.put("set17",2);
		childrenMap.put("set18",Arrays.asList("Force1","Eavesdrop1","FindNote1"));
		childrenMap.put("set19",Arrays.asList("Memorize1"));
		childrenMap.put("set20",Arrays.asList("Force2","Eavesdrop2","FindNote2"));
		childrenMap.put("set21",Arrays.asList("Memorize2"));
		childrenMap.put("set22",Arrays.asList("Force3","Eavesdrop3","FindNote3"));
		orRelations.put("Combo1",new HashSet<>(Arrays.asList("set18")));
		orRelations.put("BlowUpVault",new HashSet<>(Arrays.asList("set4")));
		orRelations.put("LockDownVault",new HashSet<>(Arrays.asList("set16")));
		orRelations.put("GetToVault",new HashSet<>(Arrays.asList("set7","set6","set8")));
		orRelations.put("FindNote2",new HashSet<>(Arrays.asList("set21")));
		orRelations.put("CallCops",new HashSet<>(Arrays.asList("set9")));
		orRelations.put("FindNote1",new HashSet<>(Arrays.asList("set19")));
		orRelations.put("Combo3",new HashSet<>(Arrays.asList("set22")));
		orRelations.put("Combo2",new HashSet<>(Arrays.asList("set20")));
		orRelations.put("Surveilance",new HashSet<>(Arrays.asList("set10")));
		orRelations.put("GetAway",new HashSet<>(Arrays.asList("set1")));
		orRelations.put("EnterBank",new HashSet<>(Arrays.asList("set14")));
		orRelations.put("Guard",new HashSet<>(Arrays.asList("set11")));
		orRelations.put("Camera",new HashSet<>(Arrays.asList("set13")));
		orRelations.put("LaserSensor",new HashSet<>(Arrays.asList("set15")));
		orRelations.put("OpenVault",new HashSet<>(Arrays.asList("set3","set2")));
		andRelations.put("OpenVault",new HashSet<>(Arrays.asList("set5")));
		knRelations.put("LearnCombo",new HashSet<>(Arrays.asList("set17")));
		oandRelations.put("Camera",new HashSet<>(Arrays.asList("set12")));
		oandRelations.put("RobBank",new HashSet<>(Arrays.asList("set0")));
		model.addAllRelations(orRelations,andRelations,knRelations,oandRelations,childrenMap,knChildren);
		
		//////////////////
		////Attributes////
		//////////////////
		AttributeDef Cost = new AttributeDef("Cost");
		model.addAttributeDef(Cost);
		Cost.setNodeValue(CutOpenVault,10000.0);
		Cost.setNodeValue(HireGetAwayCar,2000.0);
		Cost.setNodeValue(ShootCops,200.0);
		Cost.setNodeValue(BribeGuard,2500.0);
		Cost.setNodeValue(HackCamera,1000.0);
		Cost.setNodeValue(ForceGuard,100.0);
		Cost.setNodeValue(CutPower,2500.0);
		Cost.setNodeValue(BlowUpVault,10000.0);
		Cost.setNodeValue(Force1,200.0);
		Cost.setNodeValue(Force2,200.0);
		Cost.setNodeValue(Force3,200.0);
		Cost.setNodeValue(Eavesdrop1,500.0);
		Cost.setNodeValue(Eavesdrop2,500.0);
		Cost.setNodeValue(Eavesdrop3,500.0);
		Cost.setNodeValue(LockDownVault,7500.0);
		Cost.setNodeValue(CallCops,5000.0);
		Cost.setNodeValue(Guard,1000.0);
		Cost.setNodeValue(LaserSensor,1000.0);
		Cost.setNodeValue(Camera,500.0);
		
		/////////////////
		////Attackers////
		/////////////////
		Attacker SneakyThief = new Attacker ("SneakyThief");
		model.addAttacker(SneakyThief);
		Attacker NotSoSneakyThief = new Attacker ("NotSoSneakyThief");
		model.addAttacker(NotSoSneakyThief);
		
		/////////////////////////////
		////Defense Effectiveness////
		/////////////////////////////
		model.setDefenseEffectivenesss(model.getAttackers(), Arrays.asList(GetToVault),Surveilance,1.0);
		model.setDefenseEffectivenesss(model.getAttackers(), Arrays.asList(FindNote1,FindNote2,FindNote3),Memorize1,1.0);
		model.setDefenseEffectivenesss(model.getAttackers(), Arrays.asList(FindNote1,FindNote2,FindNote3),Memorize2,1.0);
		model.setDefenseEffectivenesss(Arrays.asList(SneakyThief), Arrays.asList(EnterBank),LaserSensor,1.0);
		model.setDefenseEffectivenesss(Arrays.asList(NotSoSneakyThief), Arrays.asList(EnterBank),LaserSensor,0.2);
		model.setDefenseEffectivenesss(Arrays.asList(SneakyThief), Arrays.asList(GetToVault),CallCops,1.0);
		model.setDefenseEffectivenesss(Arrays.asList(NotSoSneakyThief), Arrays.asList(GetToVault),CallCops,0.1);
		model.setDefenseEffectivenesss(model.getAttackers(), Arrays.asList(OpenVault),LockDownVault,1.0);
		
		///////////////
		////Actions////
		///////////////
		NormalAction tryAction = new NormalAction("tryAction");
		model.addNormalAction(tryAction);
		
		//////////////////////////////
		////Attack Detection Rates////
		//////////////////////////////
		BlowUpVault.setDetectionRate(1.0);
		Force1.setDetectionRate(0.8);
		Force2.setDetectionRate(0.8);
		Force3.setDetectionRate(0.8);
		ForceGuard.setDetectionRate(0.7);
		BribeGuard.setDetectionRate(0.1);
		HackCamera.setDetectionRate(0.1);
		PlayCameraLoop.setDetectionRate(0.05);
		CutPower.setDetectionRate(0.3);
		ShootCops.setDetectionRate(0.9);
		
		////////////////////////////////
		////Quantitative Constraints////
		////////////////////////////////
		model.addConstraint(new DisequationOfAttributeExpressions(new Attribute(Cost),new Constant(25000.0),AttributeExprComparator.LE));
		
		//////////////////////////
		////Action Constraints////
		//////////////////////////
		model.addActionConstraint(new ActionRequiresConstraint(new AddAction(Eavesdrop1), new NotConstraintExpr(new HasNodeConstraint(LearnCombo))));
		model.addActionConstraint(new ActionRequiresConstraint(new AddAction(Eavesdrop2), new NotConstraintExpr(new HasNodeConstraint(LearnCombo))));
		model.addActionConstraint(new ActionRequiresConstraint(new AddAction(Eavesdrop3), new NotConstraintExpr(new HasNodeConstraint(LearnCombo))));
		
		///////////////////////
		////Attack Diagrams////
		///////////////////////
		ProcessState STIdle = new ProcessState("STIdle");
		model.setInitialState(STIdle);
		ProcessState TryED1 = new ProcessState("TryED1");
		ProcessState TryED2 = new ProcessState("TryED2");
		ProcessState TryED3 = new ProcessState("TryED3");
		ProcessState TryFN1 = new ProcessState("TryFN1");
		ProcessState TryFN2 = new ProcessState("TryFN2");
		ProcessState TryFN3 = new ProcessState("TryFN3");
		ProcessState STLearnCombo = new ProcessState("STLearnCombo");
		ProcessState STIdle2 = new ProcessState("STIdle2");
		ProcessState TryBribe = new ProcessState("TryBribe");
		ProcessState TryHack = new ProcessState("TryHack");
		ProcessState TryLoop = new ProcessState("TryLoop");
		ProcessState TryCutPower = new ProcessState("TryCutPower");
		ProcessState TryOpenVault = new ProcessState("TryOpenVault");
		ProcessState TryRobBank = new ProcessState("TryRobBank");
		STIdle.addTransition(new ProcessTransition(1.0,tryAction,TryED1,new SideEffect[]{new SideEffect(testvartwo,new Constant(1.0)),new SideEffect(testvar,new ArithmeticAttributeExpression(testvar,new Constant(3.0),ArithmeticOperation.SUM))},new NotConstraintExpr(new BooleanConstraintExpr(new BooleanConstraintExpr(new AllowedNodeConstraint(Combo1),new AllowedNodeConstraint(LearnCombo),BooleanConnector.OR),new HasNodeConstraint(Eavesdrop1),BooleanConnector.OR))));
		STIdle.addTransition(new ProcessTransition(1.0,tryAction,TryED2,new SideEffect[]{new SideEffect(testvar,new ArithmeticAttributeExpression(testvar,new Constant(1.0),ArithmeticOperation.SUM))},new NotConstraintExpr(new BooleanConstraintExpr(new BooleanConstraintExpr(new AllowedNodeConstraint(Combo2),new AllowedNodeConstraint(LearnCombo),BooleanConnector.OR),new HasNodeConstraint(Eavesdrop2),BooleanConnector.OR))));
		STIdle.addTransition(new ProcessTransition(1.0,tryAction,TryED3,new SideEffect[]{},new NotConstraintExpr(new BooleanConstraintExpr(new BooleanConstraintExpr(new AllowedNodeConstraint(Combo3),new AllowedNodeConstraint(LearnCombo),BooleanConnector.OR),new HasNodeConstraint(Eavesdrop3),BooleanConnector.OR))));
		STIdle.addTransition(new ProcessTransition(1.0,tryAction,TryFN1,new SideEffect[]{},new NotConstraintExpr(new BooleanConstraintExpr(new BooleanConstraintExpr(new AllowedNodeConstraint(Combo1),new AllowedNodeConstraint(LearnCombo),BooleanConnector.OR),new HasNodeConstraint(FindNote1),BooleanConnector.OR))));
		STIdle.addTransition(new ProcessTransition(1.0,tryAction,TryFN2,new SideEffect[]{},new NotConstraintExpr(new BooleanConstraintExpr(new BooleanConstraintExpr(new AllowedNodeConstraint(Combo2),new AllowedNodeConstraint(LearnCombo),BooleanConnector.OR),new HasNodeConstraint(FindNote2),BooleanConnector.OR))));
		STIdle.addTransition(new ProcessTransition(1.0,tryAction,TryFN3,new SideEffect[]{},new NotConstraintExpr(new BooleanConstraintExpr(new BooleanConstraintExpr(new AllowedNodeConstraint(Combo3),new AllowedNodeConstraint(LearnCombo),BooleanConnector.OR),new HasNodeConstraint(FindNote3),BooleanConnector.OR))));
		STIdle.addTransition(new ProcessTransition(1.0,new AddAction(Combo1),STIdle,new SideEffect[]{},new NotConstraintExpr(new AllowedNodeConstraint(LearnCombo))));
		STIdle.addTransition(new ProcessTransition(1.0,new AddAction(Combo2),STIdle,new SideEffect[]{},new NotConstraintExpr(new AllowedNodeConstraint(LearnCombo))));
		STIdle.addTransition(new ProcessTransition(1.0,new AddAction(Combo3),STIdle,new SideEffect[]{},new NotConstraintExpr(new AllowedNodeConstraint(LearnCombo))));
		STIdle.addTransition(new ProcessTransition(1.0,new AddAction(LearnCombo),STLearnCombo,new SideEffect[]{},new TrueConstraint()));
		TryED1.addTransition(new ProcessTransition(1.0,new AddAction(Eavesdrop1),STIdle,new SideEffect[]{new SideEffect(testvar,new ArithmeticAttributeExpression(testvar,new Constant(1.0),ArithmeticOperation.SUM))},new TrueConstraint()));
		TryED1.addTransition(new ProcessTransition(1.0,new FailAction(Eavesdrop1),STIdle,new SideEffect[]{},new DisequationOfAttributeExpressions(testvar,new Constant(0.0),AttributeExprComparator.GE)));
		TryED2.addTransition(new ProcessTransition(1.0,new AddAction(Eavesdrop2),STIdle,new SideEffect[]{},new DisequationOfAttributeExpressions(new Attribute(Cost),new Constant(0.0),AttributeExprComparator.GE)));
		TryED2.addTransition(new ProcessTransition(1.0,new FailAction(Eavesdrop2),STIdle,new SideEffect[]{},new TrueConstraint()));
		TryED3.addTransition(new ProcessTransition(1.0,new AddAction(Eavesdrop3),STIdle,new SideEffect[]{},new TrueConstraint()));
		TryED3.addTransition(new ProcessTransition(1.0,new FailAction(Eavesdrop3),STIdle,new SideEffect[]{},new TrueConstraint()));
		TryFN1.addTransition(new ProcessTransition(1.0,new AddAction(FindNote1),STIdle,new SideEffect[]{},new TrueConstraint()));
		TryFN1.addTransition(new ProcessTransition(1.0,new FailAction(FindNote1),STIdle,new SideEffect[]{},new TrueConstraint()));
		TryFN2.addTransition(new ProcessTransition(1.0,new AddAction(FindNote2),STIdle,new SideEffect[]{},new TrueConstraint()));
		TryFN2.addTransition(new ProcessTransition(1.0,new FailAction(FindNote2),STIdle,new SideEffect[]{},new TrueConstraint()));
		TryFN3.addTransition(new ProcessTransition(1.0,new AddAction(FindNote3),STIdle,new SideEffect[]{},new TrueConstraint()));
		TryFN3.addTransition(new ProcessTransition(1.0,new FailAction(FindNote3),STIdle,new SideEffect[]{},new TrueConstraint()));
		STLearnCombo.addTransition(new ProcessTransition(1.0,new AddAction(HireGetAwayCar),STIdle2,new SideEffect[]{},new TrueConstraint()));
		STLearnCombo.addTransition(new ProcessTransition(1.0,tryAction,STIdle2,new SideEffect[]{},new HasNodeConstraint(HireGetAwayCar)));
		STIdle2.addTransition(new ProcessTransition(1.0,tryAction,TryBribe,new SideEffect[]{},new NotConstraintExpr(new HasNodeConstraint(BribeGuard))));
		STIdle2.addTransition(new ProcessTransition(1.0,tryAction,TryHack,new SideEffect[]{},new NotConstraintExpr(new HasNodeConstraint(HackCamera))));
		STIdle2.addTransition(new ProcessTransition(1.0,tryAction,TryCutPower,new SideEffect[]{},new NotConstraintExpr(new HasNodeConstraint(CutPower))));
		STIdle2.addTransition(new ProcessTransition(1.0,new AddAction(EnterBank),TryOpenVault,new SideEffect[]{},new TrueConstraint()));
		TryBribe.addTransition(new ProcessTransition(3.0,new AddAction(BribeGuard),STIdle2,new SideEffect[]{},new TrueConstraint()));
		TryBribe.addTransition(new ProcessTransition(1.0,new FailAction(BribeGuard),STIdle2,new SideEffect[]{},new TrueConstraint()));
		TryCutPower.addTransition(new ProcessTransition(3.0,new AddAction(CutPower),STIdle2,new SideEffect[]{},new TrueConstraint()));
		TryCutPower.addTransition(new ProcessTransition(1.0,new FailAction(CutPower),STIdle2,new SideEffect[]{},new TrueConstraint()));
		TryHack.addTransition(new ProcessTransition(2.0,new AddAction(HackCamera),TryLoop,new SideEffect[]{},new TrueConstraint()));
		TryHack.addTransition(new ProcessTransition(1.0,new FailAction(HackCamera),STIdle2,new SideEffect[]{},new TrueConstraint()));
		TryLoop.addTransition(new ProcessTransition(1.0,new AddAction(PlayCameraLoop),STIdle2,new SideEffect[]{},new TrueConstraint()));
		TryOpenVault.addTransition(new ProcessTransition(1.0,new AddAction(GetToVault),TryOpenVault,new SideEffect[]{},new TrueConstraint()));
		TryOpenVault.addTransition(new ProcessTransition(1.0,new AddAction(OpenVault),TryRobBank,new SideEffect[]{},new TrueConstraint()));
		TryRobBank.addTransition(new ProcessTransition(1.0,new AddAction(GetAway),TryRobBank,new SideEffect[]{},new TrueConstraint()));
		TryRobBank.addTransition(new ProcessTransition(1.0,new AddAction(RobBank),TryRobBank,new SideEffect[]{},new TrueConstraint()));
		
		///////////////////////
		////Initial Attacks////
		///////////////////////
		model.init(SneakyThief,Arrays.asList(HireGetAwayCar));
		
		return model;
	}
}
