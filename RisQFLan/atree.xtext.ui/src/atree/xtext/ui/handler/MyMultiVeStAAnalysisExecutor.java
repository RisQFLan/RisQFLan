package atree.xtext.ui.handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

//import atree.core.processes.actions.*;
import atree.core.processes.constraints.*;
//import atree.core.processes.interfaces.*;
//import atree.core.nodes.*;
import atree.core.nodes.AttackNode;
import atree.core.nodes.CountermeasureNode;
import atree.core.nodes.DefenseNode;
//import atree.core.nodes.Node;
//import atree.core.nodes.interfaces.*;
import atree.core.attributes.*;
import atree.core.attributes.interfaces.*;
//import atree.core.attacker.*;
import atree.core.attacker.Attacker;
//import atree.core.model.*;
//import atree.core.processes.*;
import atree.core.variables.*;

import atree.core.dialogs.IMessageDialogShower;
import atree.core.dtmc.DTMCState;
import atree.core.dtmc.DTMCTransitionToDistribution;
import atree.core.dtmc.Format;
import atree.core.dtmc.TargetAndRate;
import atree.core.model.AtreeModel;
import atree.core.multivesta.AtreeState;
import atree.core.multivesta.SimulationStateInformation;
import atree.core.processes.Commitment;
import atree.core.processes.actions.NormalAction;
import atree.core.processes.interfaces.IAction;
import atree.core.processes.interfaces.IConstraint;
import atree.core.simulation.output.IDataOutputHandler;
import atree.xtext.MyParserUtil;
import atree.xtext.bbt.*;
import atree.xtext.bbt.Attribute;
import atree.xtext.ui.MyConsoleUtil;
import atree.xtext.ui.perspective.dialogs.MessageDialogShower;
import atree.xtext.ui.perspective.plot.GUIDataOutputHandler;
import vesta.NewVesta;
import vesta.mc.InfoMultiQuery;

/**
 * 
 * @author Andrea Vandin
 *
 */
public class MyMultiVeStAAnalysisExecutor {

	private static boolean librariesPresent=false;//private static boolean librariesPresent=false;
	protected static String jarPath="";
	private static final String libraryFileName="multivestaRisQFLan.jar";
	public static final String FILEWITHLIBRARYFILELOCATION="pathOfMultivestaRisQFLan.txt";
	public static final String LinkMultiVeStaRisQFLan = 
		  "https://drive.google.com/u/0/uc?id=1tbQmmzCkltHrWimUbhfj17qlESnSUGxI&export=download";
		//"https://drive.google.com/file/d/1tbQmmzCkltHrWimUbhfj17qlESnSUGxI/view?usp=sharing&export=download"; 
	    //"https://drive.google.com/uc?id=1xdcDrNsTilNy3bxjfd1KLbGDwAX0Wv4f&export=download"; //"https://dtudk-my.sharepoint.com/:u:/g/personal/anvan_win_dtu_dk/EfGDAwGVX81Fi-UcetisBVABq5F7Sne7dbHLI546lpRCtQ?e=Ly1Mxc"; //"https://www.dropbox.com/s/yj46t7c9urq8n2q/multivestaQFLan.jar?dl=1";
	private static final int IR = 10; 
	private static final int BLOCKSIZE = 80;//60;//30; //20;

	public void readAndExecuteMultiThreaded(ModelDefinition modelDef, boolean canSynchEditor, IProject project, IPath fullPathOfParent) {
		readAndExecute(modelDef,canSynchEditor,true,project,fullPathOfParent);
	}
	public void readAndExecute(ModelDefinition modelDef, boolean canSynchEditor, IProject project, IPath fullPathOfParent) {
		readAndExecute(modelDef,canSynchEditor,false,project,fullPathOfParent);
	}

	private void readAndExecute(ModelDefinition modelDef, boolean canSynchEditor,boolean multithreaded, IProject project,IPath fullPathOfParent) {
		MessageConsole console = MyConsoleUtil.generateConsole();
		MessageConsoleStream consoleOut = console.newMessageStream();
		consoleOut.println(MyConsoleUtil.computeWelcome(consoleOut));

		//		String workspacePath = project.getParent().getLocationURI().getPath();
		String workspacePath = project.getParent().getLocation().toString().replace("/", File.separator);
		String parentPath = fullPathOfParent.toFile().getPath();
		String absoluteParentPath = workspacePath+parentPath;

		while(!librariesPresent){
			try {
				checkLibraries();
			} catch (IOException e) {
				NewVesta.printStackTrace(consoleOut,e);
			}
		}

		//	     URI projectURI = project.getLocationURI();
		//	     String projectPath=projectURI.getPath();

		String projectPath = project.getLocation().toString().replace("/", File.separator);

		GUIDataOutputHandler guidog = new GUIDataOutputHandler(console);


		String name = modelDef.getName();
		String firstChar = name.substring(0, 1);
		name = name.substring(1,name.length());
		name = firstChar.toUpperCase()+name;
		String fileAbsPath = projectPath+File.separator+name+".bbt";
		
		/*
		try {
			AtreeState s = new AtreeState(new ParametersForState(fileAbsPath, ""));
		}catch(Exception e) {
			IMessageDialogShower msgVisualizer = new MessageDialogShower(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
			//String msg = "The initial status does not satisfy the hierarchical, cross-tree or quantitative constraints.";
			String msg = "Problems in compiling the .java file generated from the RisQFLan model. Try to rename to model (and accordingly the file containing it).\n"+e.getMessage();
			msgVisualizer.openSimpleDialog(msg);//, DialogType.Error);
			return;
		}
		*/
		

		Analysis analysis = null;
		Simulate simulate = null;
		ExportDTMC exportDTMC=null;
		EList<Label> labelsDTMCXTEXT=null;
		ArrayList<String> labelsDTMCNames=new ArrayList<>();
		for (EObject element : modelDef.getElements()) {
			if(element instanceof Analysis){
				analysis=(Analysis) element;
				break;
			}
			else if(element instanceof Simulate){
				simulate=(Simulate) element;
				break;
			}
			else if(element instanceof ExportDTMC){
				exportDTMC=(ExportDTMC) element;
				labelsDTMCXTEXT= exportDTMC.getLabels();
				if(labelsDTMCXTEXT!=null) {
					for(Label l:labelsDTMCXTEXT) {
						labelsDTMCNames.add(l.getName());
					}
				}
				break;
			} 
		}

		//DataFromPopulateModel data = new DataFromPopulateModel();
		ArrayList<IConstraint> labelsDTMCGuard= new ArrayList<>();
		AtreeModel model = populateAtreeModel(modelDef,consoleOut,labelsDTMCGuard,labelsDTMCXTEXT); //Try to reuse on the fly compilation!

		//AtreeModel clone = model.clone();
		
		IConstraint unsat = model.checkSATCurrentState();
		if(unsat!=null){
			IMessageDialogShower msgVisualizer = new MessageDialogShower(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
			//String msg = "The initial status does not satisfy the hierarchical, cross-tree or quantitative constraints.";
			String msg = "The initial state does not satisfy the constraint:\n"+unsat.toString();
			msgVisualizer.openSimpleDialog(msg);//, DialogType.Error);
			return;
		}


		String outputPath = projectPath+File.separator;
		if(analysis!=null){
			String queryFile = null;
			String delta=null;
			if(analysis.getQueryFile()!=null){
				queryFile=analysis.getQueryFile();
				delta=analysis.getDelta();
			}
			else{
				MathEval math = new MathEval();
				String defaultDelta=String.valueOf(math.evaluate(MyParserUtil.visitExpr(analysis.getDefaultDelta())));
				queryFile="src-gen" + File.separator + "query"+modelDef.getName()+".quatex";
				//math.eval(MyParserUtil.visitExpr(analysis.getDefaultDelta()));
				List<String> deltas = MyParserUtil.parseDeltas(analysis.getQuery(), defaultDelta);
				//deltas=MyParserUtil.parseDeltas(analysis.getQuery(), defaultDelta,math);
				StringBuilder sb = new StringBuilder("[");
				for (String d : deltas) {
					sb.append(math.evaluate(d));
					sb.append(',');
				}
				sb.replace(sb.length()-1, sb.length(), "]");
				delta=sb.toString();
			}
			//TODO: FIX paths seperators around drive specification
			String query = MyParserUtil.computeFileName(queryFile, absoluteParentPath);

			String alpha = String.valueOf(analysis.getAlpha());//"0.1";
			// String delta = analysis.getDelta(); //"[20.0,1.0,5.0,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1]";

			String parallelismOrServers = null;
			if(analysis.getServersFile()!=null) {
				parallelismOrServers = MyParserUtil.computeFileName(analysis.getServersFile(), absoluteParentPath);
			}
			if(parallelismOrServers==null) {
				parallelismOrServers = String.valueOf(analysis.getParallelism());
			}
			else {
				consoleOut.println("The address(es) of the server(s) location is: "+parallelismOrServers);
			}
			//int parallelism = analysis.getParallelism(); // 1;
			int ir = MyMultiVeStAAnalysisExecutor.IR;
			int blockSize= MyMultiVeStAAnalysisExecutor.BLOCKSIZE;
			if(analysis.getBlockSize()!=0){
				blockSize=analysis.getBlockSize();
			}
			if(analysis.getIr()!=0){
				ir=analysis.getIr();
			}

			if(ir<0){
				//No intermediate results
				ir=0;
			}

			if(multithreaded){
				MyMultiVeStAAnalysisExecutorWorker worker = new MyMultiVeStAAnalysisExecutorWorker(query,alpha,delta,parallelismOrServers,ir,guidog,consoleOut,project,fileAbsPath,getJarPath(),projectPath+File.separator,blockSize);
				worker.start();
			}
			else{
				/*InfoMultiQuery result = vesta.NewVesta.invokeClient(new String[]{
							"-sm", "false", "-sd", "atree.xtext.bbt.core.multivesta.QFlanJavaState", 
							"-m", fileAbsPath, "-l", "1", "-ir", "20", 
							"-op" , projectPath+File.separator,
							"-f", query,
							"-vp", "false",
							"-jn", getJarPath(), //ImportBNGWizard.getPossibleJarPaths(),
							"-bs", "20", "-a", String.valueOf(alpha), "-ds", delta, "-osws", "ONESTEP", "-sots", "12343"
					});*/

				InfoMultiQuery result = invokeMultiVeStA(fileAbsPath, query, alpha, delta, parallelismOrServers,ir,getJarPath(),outputPath,blockSize,consoleOut);
				visualizePlot(guidog,result, fileAbsPath, query, alpha, delta,consoleOut);
				consoleOut.println(MyConsoleUtil.computeGoodbye(consoleOut));
			}

		}
		else if(simulate!=null) {
				if(model!=null){
					String fileOut = MyParserUtil.computeFileName(simulate.getFile(), absoluteParentPath);
					int seed = simulate.getSeed();
					if(multithreaded){
						MyMultiVeStAAnalysisExecutorWorker worker = new MyMultiVeStAAnalysisExecutorWorker(simulate.getSteps(),fileOut,model,seed, guidog, consoleOut, project, fileAbsPath, getJarPath(),projectPath+File.separator);
						worker.start();
					}
					else{
						try {
							performASimulation(simulate.getSteps(),fileOut,model,fileAbsPath, seed, consoleOut,getJarPath(),outputPath);
						} catch (IOException e) {
							NewVesta.printStackTrace(consoleOut, e);
						}
					}
				}
			}
		else {
			//exportDTMC
			String fileOut = MyParserUtil.computeFileName(exportDTMC.getFile(), absoluteParentPath);
			//IConstraint guard = visitConstraint(exportDTMC.getCond(), data.getAttackNodes(), data.getVariables(), data.getAttributes());
			if(multithreaded){
				MyMultiVeStAAnalysisExecutorWorker worker = new MyMultiVeStAAnalysisExecutorWorker(fileOut,labelsDTMCNames,labelsDTMCGuard,/*exportDTMC.getFormat(),*/"PRISM",model, guidog, consoleOut, project, fileAbsPath, getJarPath(),projectPath+File.separator);
				worker.start();
			}
			else{
				try {
					exportDTMC(fileOut,labelsDTMCNames,labelsDTMCGuard,"PRISM"/*exportDTMC.getFormat()*/,model,fileAbsPath, consoleOut,getJarPath(),outputPath);
				} catch (IOException e) {
					NewVesta.printStackTrace(consoleOut, e);
				}
			}
		}
	}

	public static InfoMultiQuery invokeMultiVeStA(String modelFullPath, String query, String alpha, String delta, String parallelismOrServers, int ir, String jarPath,String outputPath, int blockSize, MessageConsoleStream out){
		/*String query = "/MultiQuaTEx/FeaturesInstalledAndPriceWeightLoadAtStep.quatex";
		String alpha = "0.1";
		String delta = "[20.0,1.0,5.0,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1]";*/
		InfoMultiQuery result = vesta.NewVesta.invokeClient(new String[]{
				"-sm", "false", "-sd", "atree.core.multivesta.AtreeState", "-jn", jarPath, "-vp", "false", 
				"-osws", "ONESTEP", /*"-sots", "12343",*/
				"-bs", String.valueOf(blockSize),
				"-m", modelFullPath, 
				//"-l", "/Users/anvan/TOOLS/Bbt/sourcecode/Thesis/serverslists/oneLocal",
				//"-l", String.valueOf(parallelism), 
				"-l", parallelismOrServers,
				"-ir", String.valueOf(ir), 
				"-op" , outputPath,
				"-f", query,
				"-a", alpha, 
				"-ds", delta
		}
		, out);

		return result;
	}
	
	public static void exportDTMC(String fileOut, ArrayList<String> labelsDTMCNames, ArrayList<IConstraint> labelsDTMCGuard, String format,AtreeModel initialState,String modelFullPath, MessageConsoleStream out, String jarPath_param,String outputPath) throws IOException
	{	
			
		
		initialState=initialState.clone();
		//We throw away the randomgenerator. We don't need it,
		initialState.dropRandomGenerator();
		

		
		if(format.equals(Format.PRISM.name())) {
			if((!fileOut.endsWith(".txt"))&&(!fileOut.endsWith(".pm"))) {
				fileOut=fileOut+".pm";
			}
		}
		BufferedWriter bwOut = new BufferedWriter(new FileWriter(fileOut));
		//AtreeState state = new AtreeState(initialState);
		//int step=1;

		//ArrayList<DTMCTransition> transitions = new ArrayList<>();
		ArrayList<DTMCTransitionToDistribution> outGoingTransitions = new ArrayList<>();
		//ArrayList<AtreeModel> states = new ArrayList<>();
		LinkedHashMap<AtreeModel,DTMCState> states = new LinkedHashMap<>();
		//int stateId=0;
		int transitions=0;
		
		
		String message = "DTMC generation starting from:";
		NewVesta.println(out, message);
		prinitInitialStateInfo(initialState, out);
		NewVesta.println(out, " Generating states and transitions...");
		
		int interval = 10000;
		int nextPrint=interval;
		Stack<DTMCState> statesToVisit = new Stack<>();
		DTMCState initialDTMCState = new DTMCState(initialState, states.size()); 
		states.put(initialState,initialDTMCState);
		statesToVisit.push(initialDTMCState);
		
		//Creating labels and checking if the first states should get one of the labels
		ArrayList<ArrayList<DTMCState>> labellingFUnction = new ArrayList<>(labelsDTMCGuard.size());
		for(IConstraint labelGuard:labelsDTMCGuard) {
			ArrayList<DTMCState> statesLabeled = new ArrayList<>();
			labellingFUnction.add(statesLabeled);
			if(labelGuard.eval(initialDTMCState.getState())) {
				statesLabeled.add(initialDTMCState);
			}
		}
		
		while(!statesToVisit.isEmpty()) {
			if(states.size()>=nextPrint){
				message = "\t"+states.size()+" states, "+transitions+" transitions";
				NewVesta.println(out, message);
				//printlnInBWOut(bwOut,message);
				nextPrint+=interval;
			}
			DTMCState currentStateDTMC = statesToVisit.pop();
			AtreeModel currentState = currentStateDTMC.getState();
			SimulationStateInformation stateInfo=new SimulationStateInformation();
			currentState.computeAllowedTransitions(stateInfo);
			if(stateInfo.getAllowedCommitments().size()>0 && stateInfo.getCumulativeRate()>0) {
				DTMCTransitionToDistribution outGoingTransitionsFromCurrent = new DTMCTransitionToDistribution(currentStateDTMC, stateInfo.getAllowedCommitments().size());
				outGoingTransitions.add(outGoingTransitionsFromCurrent);
				String totOutgoingRate = String.valueOf(stateInfo.getCumulativeRate());
				for(Commitment commitment : stateInfo.getAllowedCommitments()) {
					String rate = String.valueOf(commitment.getActualRate());
					//Detection rate of the node that can activate a countermeasure (or -1 if no countermeasure can be activated) 
					double detectionRate=AtreeModel.mightActivateCountermeasure(commitment.getTransition().getAction());
					//String rateStr = String.valueOf(commitment.getActualRate());
					transitions++;
					
					if(detectionRate>0) {
						AtreeModel onestepNextStateWithCountermeasure = currentState.clone();
						onestepNextStateWithCountermeasure.apply(commitment, false, true);
						String rateCountermeasure=(detectionRate<1)?rate+"*(  "+detectionRate+")":rate;
						DTMCState onestepNextStateDTMCWithCountermeasure = addOrGetPreviousCopy(states, statesToVisit, onestepNextStateWithCountermeasure,labellingFUnction,labelsDTMCGuard);
						outGoingTransitionsFromCurrent.addTarget(onestepNextStateDTMCWithCountermeasure, rateCountermeasure+"/"+totOutgoingRate);
						if(detectionRate<1) {
							transitions++;
							AtreeModel onestepNextStateWithoutCountermeasure = currentState.clone();
							onestepNextStateWithoutCountermeasure.apply(commitment, false, false);
							String rateNoCountermeasure = rate+"*(1-"+detectionRate+")";
							DTMCState onestepNextStateDTMCWithoutCountermeasure = addOrGetPreviousCopy(states, statesToVisit, onestepNextStateWithoutCountermeasure,labellingFUnction,labelsDTMCGuard);
							outGoingTransitionsFromCurrent.addTarget(onestepNextStateDTMCWithoutCountermeasure, rateNoCountermeasure+"/"+totOutgoingRate);
						}
					}
					else {
						AtreeModel onestepNextState = currentState.clone();
						onestepNextState.apply(commitment);
						DTMCState onestepNextStateDTMC = addOrGetPreviousCopy(states, statesToVisit, onestepNextState,labellingFUnction,labelsDTMCGuard);
						outGoingTransitionsFromCurrent.addTarget(onestepNextStateDTMC, rate+"/"+totOutgoingRate);
					}
				}
			}
		}
		message =" Generation completed.\n"+"\t"+states.size()+" states, "+transitions+" transitions";
		NewVesta.println(out, message);
		
		
		if(format.equals(Format.PRISM.name())) {
			/*PRISM
			dtmc

			const double p;
			const N = 10;

			module walk 
			state : [0..N] init 0;
			[] state > 0 & state < N -> p : (state'=state-1) + (1-p) : (state'=state+1);
			[] state = 0 -> p : (state'=state+2) + (1-p) : (state'=state+1);
			[] state = N -> p : (state'=state-2) + (1-p) : (state'=state-1);
			endmodule
			*/
			int lastState=states.size()-1;
			printlnInBWOut(bwOut,"dtmc\n");
			printlnInBWOut(bwOut,"module explicitstatespace");
			printlnInBWOut(bwOut,"s : [0.."+lastState+"] init 0;");
			for(DTMCTransitionToDistribution transitionDistr : outGoingTransitions) {
				printlnInBWOut(bwOut,transitionDistr.toPRISMFormat());
			}
			printlnInBWOut(bwOut,"endmodule\n");
			
			//label "safe" = temp<=100 | alarm=true;
			printlnInBWOut(bwOut,"label \"initalstate\" = s = 0;");
			if(labellingFUnction.size()>0) {
				NewVesta.println(out, "Labels: ");
			}
			for(int i=0; i<labellingFUnction.size();i++) {
				ArrayList<DTMCState> statesToLabel = labellingFUnction.get(i);
				if(statesToLabel.size()>0) {
					printInBWOut(bwOut,"label \""+labelsDTMCNames.get(i)+"\" = ");
					for(int s=0;s<statesToLabel.size();s++) {
						DTMCState state = statesToLabel.get(s);
						printInBWOut(bwOut,"s = "+state.getId());
						if(s<statesToLabel.size()-1) {
							printInBWOut(bwOut," | ");
						}
					}
					printlnInBWOut(bwOut,";");
					NewVesta.println(out, "Label \""+labelsDTMCNames.get(i)+"\" is satisfied by "+statesToLabel.size()+" states");
				}
				else {
					NewVesta.println(out, "Label \""+labelsDTMCNames.get(i)+"\" is satisfied by 0 states");
				}
			}
		}
		else if(format.equals(Format.STORM.name())) {
			/* STORM
			  dtmc
			  0 1 0.3
			  0 4 0.7
			  1 0 0.5
			  1 1 0.5
			 */
			printlnInBWOut(bwOut,"dtmc");
			for(DTMCTransitionToDistribution transitionDistr : outGoingTransitions) {
				for(TargetAndRate target :transitionDistr.getTargets()) {
				printlnInBWOut(bwOut,
						transitionDistr.getSource().getId()+ " " +
						target.getTargetState().getId()+ " " +
						target.getRateToPrint());
				}
			}
		} 
		
		//printlnInBWOut(bwOut,message);
		
//		state.setLastStateAlreadyComputed(false);
//		state.setNumberOfSteps(0);
//		state.setSimulatorForNewSimulation(-1);
//		
//		state.performOneStepOfSimulation();
//		
//		AtreeModel cloned=model.clone();
//		boolean equal = model.equals(cloned);
//		AttackNode att = cloned.getAttackNodes().iterator().next();
//		cloned.add(att);
//		equal = model.equals(cloned);
//		
		//state.computeAllowedTransitions();
		
		bwOut.close();
	}

/*
	private static void addTransitionToDTMC(DTMCTransitionToDistribution transitions,
			LinkedHashMap<AtreeModel, DTMCState> states, int stateId, Stack<DTMCState> statesToVisit,
			String rate,String totOutgoingRate, AtreeModel onestepNextState,DTMCState onestepNextStateDTMC) {
		//AtreeModel previouslyGeneratedCopy = states.get(onestepNextState);
		transitions.addTarget(onestepNextStateDTMC, rate+"/"+totOutgoingRate);
//		double probability = rate/totOutgoingRate;
//		DTMCTransition transition = new DTMCTransition(currentStateDTMC, onestepNextStateDTMC, probability);
//		transitions.add(transition);
	}
	*/
	private static DTMCState addOrGetPreviousCopy(LinkedHashMap<AtreeModel, DTMCState> states, 
			Stack<DTMCState> statesToVisit, AtreeModel onestepNextState, ArrayList<ArrayList<DTMCState>> labellingFUnction, ArrayList<IConstraint> labelsDTMCGuard) {
		DTMCState previouslyGeneratedCopy = states.get(onestepNextState);
		DTMCState onestepNextStateDTMC;
		if(previouslyGeneratedCopy!=null) {
			//State already visited
			onestepNextState = previouslyGeneratedCopy.getState();
			onestepNextStateDTMC=previouslyGeneratedCopy;
			//System.out.println("Same state.");
			//System.out.println("States "+states.size()+", Trans "+transitions.size());
			
		}
		else {
			//Newly generated state
			onestepNextStateDTMC=new DTMCState(onestepNextState, states.size());
			states.put(onestepNextState,onestepNextStateDTMC);
			statesToVisit.push(onestepNextStateDTMC);
			
			for(int i=0;i<labelsDTMCGuard.size();i++) {
				IConstraint labelGuard=labelsDTMCGuard.get(i);
				if(labelGuard.eval(onestepNextStateDTMC.getState())) {
					labellingFUnction.get(i).add(onestepNextStateDTMC);
				}
			}
			//System.out.println("States "+states.size()+", Trans "+transitions.size());
			//System.out.println(onestepNextState.getCurrentState().getName()+"\n"+onestepNextState.getInstalledAttackNodes()+"\n"+onestepNextState.getActivatedCountermeasures()+"\n"+onestepNextState.getInstalledCountermeasureNodes()+"\n");
		}
		return onestepNextStateDTMC;
	}

	/*
	private static void addTransitionToDTMC(ArrayList<DTMCTransition> transitions,
			LinkedHashMap<AtreeModel, DTMCState> states, int stateId, Stack<DTMCState> statesToVisit,
			DTMCState currentStateDTMC, double rate,double totOutgoingRate, Commitment commitment, AtreeModel onestepNextState) {
		DTMCState previouslyGeneratedCopy = states.get(onestepNextState);
		DTMCState onestepNextStateDTMC = null;
		//AtreeModel previouslyGeneratedCopy = states.get(onestepNextState);
		if(previouslyGeneratedCopy!=null) {
			//State already visited
			onestepNextState = previouslyGeneratedCopy.getState();
			onestepNextStateDTMC=previouslyGeneratedCopy;
			//System.out.println("Same state.");
			//System.out.println("States "+states.size()+", Trans "+transitions.size());
			
		}
		else {
			//Newly generated state
			onestepNextStateDTMC=new DTMCState(onestepNextState, stateId);
			states.put(onestepNextState,onestepNextStateDTMC);
			statesToVisit.push(onestepNextStateDTMC);
			//System.out.println("States "+states.size()+", Trans "+transitions.size());
			//System.out.println(onestepNextState.getCurrentState().getName()+"\n"+onestepNextState.getInstalledAttackNodes()+"\n"+onestepNextState.getActivatedCountermeasures()+"\n"+onestepNextState.getInstalledCountermeasureNodes()+"\n");
		}
		double probability = rate/totOutgoingRate;
		DTMCTransition transition = new DTMCTransition(currentStateDTMC, onestepNextStateDTMC, probability);
		transitions.add(transition);
	}
	*/

	private static void prinitInitialStateInfo(AtreeModel initialState, MessageConsoleStream out) {
		StringBuffer retString = new StringBuffer(" Variables:\n");
		retString.append("\t"+initialState.printVariables());
		retString.append(" Present nodes:\n ");
		//retString.append("\t"+initialState.getAllInstalledNodes());
		retString.append("\tAttack nodes:"+initialState.getInstalledAttackNodes().keySet());
		retString.append("\tDefense nodes:"+initialState.getInstalledDefenseNodes().keySet());
		retString.append("\tCountermeasure nodes:"+initialState.getInstalledCountermeasureNodes().keySet());
		retString.append("\tActivatede countermeasure nodes:"+initialState.getActivatedCountermeasures());
		retString.append("\n");
		retString.append(" Attacker and state:\n ");
		retString.append("\t"+initialState.getCurrentAttacker().getName()+"."+initialState.getCurrentState().getName());
		retString.append("\n");
		
		NewVesta.println(out, retString.toString());
	}
	
	//TODO: Implement needs print information about each simulation step
	public static void performASimulation(int steps,String fileOut, AtreeModel model,String modelFullPath, int seed, MessageConsoleStream out, String jarPath_param,String outputPath) throws IOException
	{	
		BufferedWriter bwOut = new BufferedWriter(new FileWriter(fileOut));

		AtreeState state = new AtreeState(model);

		int step=1;

		state.setLastStateAlreadyComputed(false);
		state.setNumberOfSteps(0);
		state.setSimulatorForNewSimulation(seed);

		String message="Complete initial system specification:";
		NewVesta.println(out, message);
		printlnInBWOut(bwOut,message);
		message =  state.printCurrentStateInfo(false);
		NewVesta.println(out, message);
		printlnInBWOut(bwOut,message);

		message =  "Current state: " + model.getCurrentState().toString();
		NewVesta.println(out, message);
		printlnInBWOut(bwOut,message);

		NewVesta.println(out, "\n\n");
		printlnInBWOut(bwOut,"\n\n");

		boolean completed=false;
		while(!completed && step<=steps){
			message = "###################################\nSTEP "+step+"\n###################################\n";
			NewVesta.println(out, message);
			printlnInBWOut(bwOut,message);

			state.performOneStepOfSimulation();

			message =  state.printCurrentStateInfo(); //Prints forbidden and allowed commitments
			NewVesta.println(out, message);
			printlnInBWOut(bwOut,message);

			message =  "Resulting state: " + model.getCurrentState().toString();
			NewVesta.println(out, message);
			printlnInBWOut(bwOut,message);

			message = "Succesful attacks: ";
			Set<String> attacks = state.getModel().getInstalledAttackNodes().keySet();
			int i = 0;
			for(String s : attacks)
			{
				i++;
				message = message + s;
				if(attacks.size() > i)
				{
					message = message + ", ";
				}
			}
			message = message + "\n";
			NewVesta.println(out, message);
			printlnInBWOut(bwOut,message);

			NewVesta.println(out, "\n\n");
			printlnInBWOut(bwOut,"\n\n");

			message = "Added defenses: ";
			Set<String> addedDefenses = state.getModel().getInstalledDefenseNodes().keySet();
			i = 0;
			for(String s : addedDefenses)
			{
				i++;
				message = message + s;
				if(addedDefenses.size() > i)
				{
					message = message + ", ";
				}
			}
			message = message + "\n";
			NewVesta.println(out, message);
			printlnInBWOut(bwOut,message);

			NewVesta.println(out, "\n\n");
			printlnInBWOut(bwOut,"\n\n");

			message = "Active countermeasures: ";
			Set<CountermeasureNode> countermeasures = state.getModel().getActivatedCountermeasures();
			i = 0;
			for(CountermeasureNode c : countermeasures)
			{
				i++;
				message = message + c.getName();
				if(countermeasures.size() > i)
				{
					message = message + ", ";
				}
			}
			message = message + "\n";
			NewVesta.println(out, message);
			printlnInBWOut(bwOut,message);

			NewVesta.println(out, "\n\n");
			printlnInBWOut(bwOut,"\n\n");

			message = "Added countermeasures: ";
			Set<String> addedCountermeasures = state.getModel().getInstalledCountermeasureNodes().keySet();
			i = 0;
			for(String s : addedCountermeasures)
			{
				i++;
				message = message + s;
				if(addedCountermeasures.size() > i)
				{
					message = message + ", ";
				}
			}
			message = message + "\n";
			NewVesta.println(out, message);
			printlnInBWOut(bwOut,message);

			NewVesta.println(out, "\n\n");
			printlnInBWOut(bwOut,"\n\n");

			step++;

			if(!state.nextStateAvailable())
			{
				completed = true;
				message = "No action can be executed at "+step+ ".\n";
				NewVesta.println(out, message);
				printlnInBWOut(bwOut,message);

				message = "Simulation completed.\n";
				NewVesta.println(out, message);
				printlnInBWOut(bwOut,message);
			}
		}		
		bwOut.close();
	}

	private static void printlnInBWOut(BufferedWriter bwOut, String message) {
		printInBWOut(bwOut, message+"\n");
	}
	private static void printInBWOut(BufferedWriter bwOut, String message) {
		if(bwOut!=null){
			try {
				bwOut.write(message);
				bwOut.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	public static void visualizePlot(IDataOutputHandler dog,InfoMultiQuery result, String fileAbsPath, String query, String alpha, String delta, MessageConsoleStream out){
		String fileAbsPathToShow=fileAbsPath;
		int lastSep = fileAbsPathToShow.lastIndexOf(File.separator);
		if(lastSep>0){
			fileAbsPathToShow=fileAbsPathToShow.substring(lastSep+1);
		}
		String command = "MultiVeStA analysis of " + fileAbsPathToShow;
		String queryToShow=query;
		lastSep = queryToShow.lastIndexOf(File.separator);
		if(lastSep>0){
			queryToShow=queryToShow.substring(lastSep+1);
		}
		String minimalDescription = "SMC of "+queryToShow;//
		boolean showLabels=true;
		boolean visualizePlot=true;

		//dog = new DataOutputHandler(minimalDescription,crn, result,alpha,delta);
		//IDataOutputHandler dog = new GUIDataOutputHandler(null);
		dog.setData(/*fileAbsPathToShow+" - "+*/minimalDescription, result,alpha,delta,command);

		dog.setShowLabels(showLabels);

		if(visualizePlot){
			if(result.getNumberOfX()<=3){
				NewVesta.println(out,"The used graphical library does not allow to draw lines basing on less than four points.");
			}                                 
			else{
				dog.showPlots(false,null);
			}
		}
	}


	private void checkLibraries() throws IOException {
		/*
    	String osName = System.getProperty("os.name");
    	boolean win=false;
    	boolean lin=false;
    	boolean mac=false;*/
		/*if(osName.contains("Windows")||osName.contains("Linux")||osName.contains("Mac")){
    		if(osName.contains("Windows")){
    			win=true;
    		}
    		else if(osName.contains("Linux")){
    			lin=true;
    		}
    		else{
    			mac=true;
    		}
    	}*/
		boolean weHaveJar = false;
		File f = new File(FILEWITHLIBRARYFILELOCATION);
		//String v = f.getAbsolutePath();
		// /Applications/Eclipse-SDK-4.7.1a.app/Contents/MacOS/pathOfMultivestaBbt
		if(f.exists()){
			BufferedReader br = new BufferedReader(new FileReader(f));
			String path = br.readLine();
			br.close();
			if(path!=null && !path.equals("")) {
				File jar = new File(path);
				if(jar.exists()){
					weHaveJar=true;
					jarPath=path;
					librariesPresent=true;
				}
			}
		}
		/*String property = System.getProperty("java.library.path");
			//System.out.println("\n"+property+"\n");
			//CRNReducerCommandLine.println(out, "\n"+property+"\n");
			StringTokenizer st = new StringTokenizer(property);
			ArrayList<String> paths = new ArrayList<>();
			while(st.hasMoreTokens()){
				String sep =File.pathSeparator;
				String path = st.nextToken(sep);
				paths.add(path);
			}
			StringBuilder possibleJarPathsSB = new StringBuilder();
			boolean exists=false;
			for (String path : paths) {
				if(possibleJarPathsSB.length()>0){
					possibleJarPathsSB.append(":");
				}
				String fileName = path+File.separator+libraryFileName;
				possibleJarPathsSB.append(fileName);
				File f = new File(fileName);
				if(f.exists()){
					exists=true;
					break;
				}
			}*/
		if(!weHaveJar){
			//possibleJarPaths=possibleJarPathsSB.toString();
			String link="";
			String linkShort="";
			//link= "https://dl.dropboxusercontent.com/u/18840437/qflan/multivestaQFLan.jar?dl=1";
			//link = "https://www.dropbox.com/s/kodox600mqyg7pv/multivestaQFLan.jar?dl=1";
			link = LinkMultiVeStaRisQFLan;
			linkShort=libraryFileName; 
			/*String OS = "";
    			if(mac){
    				OS="Mac";
    			}
    			else if(win){
    				OS="Windows";
    			}
    			else{
    				OS = "Linux";
    			}*/
			//msgVisualizer.showMessage(msg,"and add its files to one of the following locations:",paths);
			new MessageDialogShower(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()).openMissingQFLanCoreLibraryDialog(link, linkShort /*, paths, OS*/);
		}
		else{
			librariesPresent=true;
		}
	}

	public static String getJarPath() {
		return jarPath;
	}

	private AtreeModel populateAtreeModel(ModelDefinition modelDef, MessageConsoleStream out, ArrayList<IConstraint> labelsDTMC, EList<Label> labelsDTMCXTEXT) {
		AtreeModel model = new AtreeModel();

		LinkedHashMap<String,AtreeVariable> variables = new LinkedHashMap<>();
		LinkedHashMap<String,atree.core.nodes.AttackNode> attackNodes = new LinkedHashMap<>();
		LinkedHashMap<String,atree.core.nodes.DefenseNode> defenseNodes = new LinkedHashMap<>();
		LinkedHashMap<String,atree.core.nodes.CountermeasureNode> countermeasureNodes = new LinkedHashMap<>();
		LinkedHashMap<String,IAttributeDef> attributes = new LinkedHashMap<>();
		LinkedHashMap<String,NormalAction> actions = new LinkedHashMap<>();
		LinkedHashMap<String,atree.core.attacker.Attacker> attackers = new LinkedHashMap<>();

		//		LinkedHashMap<String,AttackNode> attackNodes,LinkedHashMap<String,DefenseNode> defenseNodes,LinkedHashMap<String,CountermeasureNode> countermeasureNodes

		EList<Variable> allVars = null;
		EList<atree.xtext.bbt.AttackNode> allAttackNodes = null;
		EList<atree.xtext.bbt.DefenseNode> allDefenseNodes = null;
		EList<atree.xtext.bbt.CountermeasureNode> allCountermeasureNodes = null;
		EList<atree.xtext.bbt.EdgeType> allEdges = null;
		EList<atree.xtext.bbt.Attribute> allAttributes = null;
		EList<atree.xtext.bbt.Attacker> allAttackers = null;
		EList<atree.xtext.bbt.DefenseEffectivenessValues> allDefenseEffectiveness = null;
		EList<atree.xtext.bbt.Action> allActions = null;
		EList<atree.xtext.bbt.AttackDetectionRateValue> allAttackDetectionRates = null;
		EList<atree.xtext.bbt.ActionRequires> allActionConstraints = null;
		EList<atree.xtext.bbt.BoolExpr> allQuantitativeConstrains = null;
		EList<atree.xtext.bbt.AttacksOfDiagram> allAttackDiagrams = null;

		String attacker = null;
		List<String> initialAttacks = new ArrayList<String>();

		EList<EObject> contents = modelDef.eContents();
		for (EObject element : contents)
		{	
			if (element instanceof atree.xtext.bbt.Variables) {
				allVars = ((atree.xtext.bbt.Variables)element).getVariables();
			}
			else if (element instanceof atree.xtext.bbt.AttackNodes) {
				allAttackNodes = ((atree.xtext.bbt.AttackNodes)element).getAttackNodes();
			}
			else if (element instanceof atree.xtext.bbt.DefenseNodes) {
				allDefenseNodes = ((atree.xtext.bbt.DefenseNodes)element).getDefenseNodes();
			}
			else if (element instanceof atree.xtext.bbt.CountermeasureNodes) {
				allCountermeasureNodes = ((atree.xtext.bbt.CountermeasureNodes)element).getCountermeasureNodes();
			}
			else if (element instanceof atree.xtext.bbt.Edges) {
				allEdges = ((atree.xtext.bbt.Edges)element).getEdges();
			}
			else if (element instanceof atree.xtext.bbt.Attributes) {
				allAttributes = ((atree.xtext.bbt.Attributes)element).getAttributes();
			}
			else if (element instanceof atree.xtext.bbt.Attackers) {
				allAttackers = ((atree.xtext.bbt.Attackers)element).getAttackers();
			}
			else if (element instanceof atree.xtext.bbt.AttackDetectionRates) {
				allAttackDetectionRates = ((atree.xtext.bbt.AttackDetectionRates)element).getValues();
			}
			else if (element instanceof atree.xtext.bbt.DefenseEffectiveness) {
				allDefenseEffectiveness = ((atree.xtext.bbt.DefenseEffectiveness)element).getDefenseEffectiveness();
			}
			else if (element instanceof atree.xtext.bbt.Actions) {
				allActions = ((atree.xtext.bbt.Actions)element).getActions();
			}
			else if (element instanceof atree.xtext.bbt.ActionConstraints) {
				allActionConstraints = ((atree.xtext.bbt.ActionConstraints)element).getActionConstraints();
			}
			else if (element instanceof atree.xtext.bbt.QuantitativeConstraints) {
				allQuantitativeConstrains = ((atree.xtext.bbt.QuantitativeConstraints)element).getQuantitativeConstraints();
			}
			else if (element instanceof atree.xtext.bbt.AttackDiagram) {
				allAttackDiagrams = ((atree.xtext.bbt.AttackDiagram)element).getAttacks();
			}
			else if (element instanceof atree.xtext.bbt.InitialAttacks) {
				InitialAttack init = ((atree.xtext.bbt.InitialAttacks)element).getValue();

				attacker = init.getAttacker().getName();
				for (atree.xtext.bbt.AttackNode an : init.getInitialAttacks()) {
					initialAttacks.add(an.getName());
				}
			}
		}
		if (allVars != null) {
			variables = populateVariables(allVars,model,variables, attributes);
		}
		if (allAttackNodes != null){
			attackNodes = populateAttackNodes(allAttackNodes,attackNodes,model);
		}
		if (allDefenseNodes != null){
			defenseNodes = populateDefenseNodes(allDefenseNodes,defenseNodes,model);
		}
		if(allCountermeasureNodes != null) {
			countermeasureNodes = populateCountermeasureNodes(allCountermeasureNodes,countermeasureNodes,attackNodes,model);
		}
		if (allEdges != null) {
			populateEdges(allEdges,model);
		}
		if (allAttributes != null) {
			attributes = populateAttributes(allAttributes,attributes,attackNodes,defenseNodes,countermeasureNodes,model);
		}
		if (allAttackers != null) {
			attackers = populateAttackers(allAttackers,attackers,model);
		}
		if (allDefenseEffectiveness != null) {
			populateDefenseEffectiveness(allDefenseEffectiveness,attackers,attackNodes,defenseNodes,countermeasureNodes,model);
		}
		if (allActions != null) {
			actions = populateActions(allActions,actions,model);
		}
		if (allAttackDetectionRates != null) {
			populateAttackDetectionRates(allAttackDetectionRates,attackNodes);
		}
		if (allActionConstraints != null) {
			populateActionConstraints(allActionConstraints,attackNodes,defenseNodes,countermeasureNodes,actions,variables,attributes,model);
		}
		if(allQuantitativeConstrains != null) {
			populateQuantitativeConstraints(allQuantitativeConstrains,attackNodes,defenseNodes,countermeasureNodes,attributes,variables,model);
		}
		if (allAttackDiagrams != null) {
			populateAttackDiagrams(allAttackDiagrams,attacker,attackNodes,defenseNodes,countermeasureNodes,variables,attributes,actions,model);
		}

		Collection<AttackNode> aNodes = new ArrayList<>();
		if (!initialAttacks.isEmpty()) {
			for (String s : initialAttacks) {
				aNodes.add(attackNodes.get(s));
			}
		}
		model.init(attackers.get(attacker), aNodes);

//		data.setAttackNodes(attackNodes);
//		data.setAttributes(attributes);
//		data.setVariables(variables);
		if(labelsDTMCXTEXT!=null) {
			for(Label label: labelsDTMCXTEXT) {
				labelsDTMC.add(visitConstraint(label.getCond(), attackNodes, variables, attributes));
			}
		}

		return model;
	}

	private void populateAttackDiagrams(EList<AttacksOfDiagram> allAttackDiagrams, String attacker,
			LinkedHashMap<String, AttackNode> attackNodes, LinkedHashMap<String, DefenseNode> defenseNodes,
			LinkedHashMap<String, CountermeasureNode> countermeasureNodes,
			LinkedHashMap<String, AtreeVariable> variables, LinkedHashMap<String, IAttributeDef> attributes,
			LinkedHashMap<String, NormalAction> actions, AtreeModel model) {
		MathEval math = new MathEval();
		for (atree.xtext.bbt.AttacksOfDiagram ad : allAttackDiagrams) {
			if (attacker != null && ad.getAttacker().getName().equals(attacker)) {
				boolean initialState = true;
				LinkedHashMap<String,atree.core.processes.ProcessState> states = new LinkedHashMap<>();
				for (atree.xtext.bbt.ProcessState state : ad.getStates().getStates()) {
					String name = state.getName();
					atree.core.processes.ProcessState s = new atree.core.processes.ProcessState(name);
					states.put(name, s);
					if (initialState) {
						initialState = false;
						model.setInitialState(s);
					}
				}
				for (atree.xtext.bbt.ProcessTransition transition : ad.getTransitions().getTransitions()) 
				{
					double rate = math.evaluate(MyParserUtil.visitExpr(transition.getRate()));
					atree.core.processes.ProcessState source = states.get(transition.getSource().getName());
					atree.core.processes.ProcessState target = states.get(transition.getTarget().getName());
					IConstraint actionGuard = visitActionGuard(transition.getActionGuard(), attackNodes, variables, attributes);
					IAction action = computeActionIncludingAskOrStoreModifierOrFeature(transition.getAction(),actions,attackNodes,defenseNodes,countermeasureNodes,attributes,variables);
					source.addTransition(new atree.core.processes.ProcessTransition(rate, action, target, visitListOfSideEffects(transition.getSideEffects(), variables, attributes), actionGuard));
				}
			}
		}
	}
	private IConstraint visitActionGuard(BoolExpr actionGuard, LinkedHashMap<String, AttackNode> attackNodes,
			LinkedHashMap<String, AtreeVariable> variables, LinkedHashMap<String, IAttributeDef> attributes) {
		if (actionGuard != null){
			return visitConstraint(actionGuard,attackNodes,variables,attributes);
		}
		else {
			return new atree.core.processes.constraints.TrueConstraint();
		}
	}
	private void populateQuantitativeConstraints(EList<BoolExpr> allQuantitativeConstrains,
			LinkedHashMap<String, AttackNode> attackNodes, LinkedHashMap<String, DefenseNode> defenseNodes,
			LinkedHashMap<String, CountermeasureNode> countermeasureNodes,
			LinkedHashMap<String, IAttributeDef> attributes, LinkedHashMap<String, AtreeVariable> variables,
			AtreeModel model) {
		for (atree.xtext.bbt.BoolExpr constraint : allQuantitativeConstrains) {
			model.addConstraint(visitConstraint(constraint, attackNodes, variables, attributes));
		}

	}
	private void populateActionConstraints(EList<ActionRequires> allActionConstraints, LinkedHashMap<String, AttackNode> attackNodes, LinkedHashMap<String, DefenseNode> defenseNodes, LinkedHashMap<String, CountermeasureNode> countermeasureNodes, LinkedHashMap<String, NormalAction> actions, LinkedHashMap<String, AtreeVariable> variables, LinkedHashMap<String, IAttributeDef> attributes, AtreeModel model) {
		for (atree.xtext.bbt.ActionRequires constraint : allActionConstraints) {
			model.addActionConstraint(visitActionRequiresConstraint(constraint, actions, attackNodes, defenseNodes, countermeasureNodes, variables, attributes));
		}

	}
	private void populateAttackDetectionRates(EList<AttackDetectionRateValue> allAttackDetectionRates,
			LinkedHashMap<String, AttackNode> attackNodes) {
		MathEval math = new MathEval();
		for (atree.xtext.bbt.AttackDetectionRateValue value : allAttackDetectionRates) {
			attackNodes.get(value.getAttackNode().getName()).setDetectionRate(math.evaluate(MyParserUtil.visitExpr(value.getValue())));
		}
	}
	private LinkedHashMap<String, NormalAction> populateActions(EList<Action> allActions,
			LinkedHashMap<String, NormalAction> actions, AtreeModel model) {
		for (atree.xtext.bbt.Action a : allActions) {
			String name = a.getName();
			NormalAction act = new NormalAction(name);
			actions.put(name, act);
			model.addNormalAction(act);
		}
		return actions;
	}
	private void populateDefenseEffectiveness(EList<DefenseEffectivenessValues> allDefenseEffectiveness,
			LinkedHashMap<String, Attacker> attackers, LinkedHashMap<String, AttackNode> attackNodes,
			LinkedHashMap<String, DefenseNode> defenseNodes,
			LinkedHashMap<String, CountermeasureNode> countermeasureNodes, AtreeModel model) {
		MathEval math = new MathEval();
		for (atree.xtext.bbt.DefenseEffectivenessValues deVal : allDefenseEffectiveness) {
			Collection<atree.core.attacker.Attacker> defAttacker = new ArrayList<>();
			Collection<atree.core.nodes.AttackNode> aNodes = new ArrayList<>();

			if (deVal.getAttackNodes().getNodes().isEmpty()) {
				aNodes = attackNodes.values();
			}
			else {
				for (atree.xtext.bbt.AttackNode node : deVal.getAttackNodes().getNodes()) {
					aNodes.add(attackNodes.get(node.getName()));
				}
			}
			if (deVal.getDefenseEffectivenessAttackers().getAttackers().isEmpty()) {
				defAttacker = attackers.values();
			}
			else {
				for (atree.xtext.bbt.Attacker a : deVal.getDefenseEffectivenessAttackers().getAttackers()) {
					defAttacker.add(attackers.get(a.getName()));
				}
			}
			if (deVal.getDefenseNode() instanceof atree.xtext.bbt.DefenseNode) {
				model.setDefenseEffectivenesss(defAttacker, aNodes, defenseNodes.get(getNodeName(deVal.getDefenseNode())), math.evaluate(MyParserUtil.visitExpr(deVal.getValue())));
			}
			else if (deVal.getDefenseNode() instanceof atree.xtext.bbt.CountermeasureNode) {
				model.setDefenseEffectivenesss(defAttacker, aNodes, countermeasureNodes.get(getNodeName(deVal.getDefenseNode())), math.evaluate(MyParserUtil.visitExpr(deVal.getValue())));
			}
		}
	}
	private LinkedHashMap<String, Attacker> populateAttackers(EList<atree.xtext.bbt.Attacker> allAttackers,
			LinkedHashMap<String, Attacker> attackers, AtreeModel model) {
		for (atree.xtext.bbt.Attacker a : allAttackers) {
			String name = a.getName();
			atree.core.attacker.Attacker attacker = new Attacker(name);
			model.addAttacker(attacker);
			attackers.put(name, attacker);
		}

		return attackers;
	}
	private LinkedHashMap<String, IAttributeDef> populateAttributes(EList<Attribute> allAttributes,
			LinkedHashMap<String, IAttributeDef> attributes, LinkedHashMap<String, AttackNode> attackNodes,
			LinkedHashMap<String, DefenseNode> defenseNodes,
			LinkedHashMap<String, CountermeasureNode> countermeasureNodes, AtreeModel model) 
	{
		MathEval math = new MathEval();
		for (atree.xtext.bbt.Attribute att : allAttributes) {
			String name = att.getName();
			IAttributeDef attDef = new AttributeDef(name);
			attributes.put(name, attDef);
			model.addAttributeDef(attDef);
			for (atree.xtext.bbt.AttributeValue value : att.getValues()) {
				if (value.getAttribute() instanceof atree.xtext.bbt.AttackNode) {
					attDef.setNodeValue(attackNodes.get(((atree.xtext.bbt.AttackNode)value.getAttribute()).getName()), math.evaluate(MyParserUtil.visitExpr(value.getValue())));
				}
				else if (value.getAttribute() instanceof atree.xtext.bbt.DefenseNode) {
					attDef.setNodeValue(defenseNodes.get(((atree.xtext.bbt.DefenseNode)value.getAttribute()).getName()), math.evaluate(MyParserUtil.visitExpr(value.getValue())));
				}
				else if (value.getAttribute() instanceof atree.xtext.bbt.CountermeasureNode) {
					attDef.setNodeValue(countermeasureNodes.get(((atree.xtext.bbt.CountermeasureNode)value.getAttribute()).getName()), math.evaluate(MyParserUtil.visitExpr(value.getValue())));
				}
			}
		}
		return attributes;
	}
	private void populateEdges(EList<EdgeType> allEdges, AtreeModel model) {

		int number = 0;
		LinkedHashMap<String,Set<String>> orRelations = new LinkedHashMap<>(); 
		LinkedHashMap<String,Set<String>> andRelations = new LinkedHashMap<>(); 
		LinkedHashMap<String,Set<String>> knRelations = new LinkedHashMap<>(); 
		LinkedHashMap<String,Set<String>> oandRelations = new LinkedHashMap<>(); 
		LinkedHashMap<String,List<String>> childrenMap = new LinkedHashMap<>();
		LinkedHashMap<String,Integer> knChildren = new LinkedHashMap<>();

		for (atree.xtext.bbt.EdgeType e : allEdges) {
			String setID = "set"+number;
			number++;
			String key = getNodeName(e.getParent());
			List<String> childLst = new ArrayList<>();
			for (atree.xtext.bbt.Node n : getRelationChildren(e)) {
				childLst.add(getNodeName(n));
			}
			childrenMap.put(setID, childLst);

			if (e instanceof ORRelation) {
				if (orRelations.containsKey(key)){
					orRelations.get(key).add(setID);
				}
				else{
					Set<String> setOfID = new HashSet<>();
					setOfID.add(setID);
					orRelations.put(key,setOfID);
				}
			}
			else if (e instanceof ANDRelation) {
				if (andRelations.containsKey(key)){
					andRelations.get(key).add(setID);
				}
				else{
					Set<String> setOfID = new HashSet<>();
					setOfID.add(setID);
					andRelations.put(key,setOfID);
				}
			}
			else if (e instanceof KNRelation) {
				knChildren.put(setID, ((KNRelation)e).getValue());
				if (knRelations.containsKey(key)){
					knRelations.get(key).add(setID);
				}
				else{
					Set<String> setOfID = new HashSet<>();
					setOfID.add(setID);
					knRelations.put(key,setOfID);
				}
			}
			else if (e instanceof OANDRelation) {
				if (oandRelations.containsKey(key)){
					oandRelations.get(key).add(setID);
				}
				else{
					Set<String> setOfID = new HashSet<>();
					setOfID.add(setID);
					oandRelations.put(key,setOfID);
				}
			}
		}
		model.addAllRelations(orRelations, andRelations, knRelations, oandRelations, childrenMap, knChildren);
	}
	private LinkedHashMap<String, CountermeasureNode> populateCountermeasureNodes(
			EList<atree.xtext.bbt.CountermeasureNode> allCountermeasureNodes,
			LinkedHashMap<String, CountermeasureNode> countermeasureNodes,
			LinkedHashMap<String, AttackNode> attackNodes, AtreeModel model) {
		for (atree.xtext.bbt.CountermeasureNode cn : allCountermeasureNodes) {
			String name = cn.getName();
			atree.core.nodes.CountermeasureNode n = new CountermeasureNode(name);
			countermeasureNodes.put(name, n);

			List<atree.core.nodes.AttackNode> aNodes = new ArrayList<>();
			for (atree.xtext.bbt.AttackNode an : cn.getTriggers()) {
				aNodes.add(attackNodes.get(an.getName()));
			}
			model.addCountermeasureNodeDefinition(n, aNodes);
		}
		return countermeasureNodes;
	}
	private LinkedHashMap<String, DefenseNode> populateDefenseNodes(EList<atree.xtext.bbt.DefenseNode> allDefenseNodes,
			LinkedHashMap<String, DefenseNode> defenseNodes, AtreeModel model) {
		for (atree.xtext.bbt.DefenseNode dn : allDefenseNodes) {
			String name = dn.getName();
			atree.core.nodes.DefenseNode n = new DefenseNode(name);
			defenseNodes.put(name, n);
			model.addDefenseNodeDefinition(n);
		}
		return defenseNodes;
	}
	private LinkedHashMap<String, AttackNode> populateAttackNodes(EList<atree.xtext.bbt.AttackNode> allAttackNodes,
			LinkedHashMap<String, AttackNode> attackNodes, AtreeModel model) {
		for (atree.xtext.bbt.AttackNode an : allAttackNodes) {
			String name = an.getName();
			atree.core.nodes.AttackNode n = new AttackNode(name);
			attackNodes.put(name, n);
			model.addAttackNodeDefinition(n);
		}
		return attackNodes;
	}
	private LinkedHashMap<String, AtreeVariable> populateVariables(EList<Variable> variables, AtreeModel model, LinkedHashMap<String, AtreeVariable> variableNameToVariable, LinkedHashMap<String, IAttributeDef> attributes) {
		for (atree.xtext.bbt.Variable var : variables) {
			IAttributeExpr expr = writeExpr(var.getValue(), variableNameToVariable, attributes);
			AtreeVariable v = model.addVariable(var.getName(), expr);
			variableNameToVariable.put(v.getName(), v);
		}
		return variableNameToVariable;
	}


	private ActionRequiresConstraint visitActionRequiresConstraint(ActionRequires element,LinkedHashMap<String, NormalAction> actionNameToAction,LinkedHashMap<String,AttackNode> attackNodes,LinkedHashMap<String,DefenseNode> defenseNodes,LinkedHashMap<String,CountermeasureNode> countermeasureNodes, LinkedHashMap<String,AtreeVariable> variableNameToVariable, LinkedHashMap<String, IAttributeDef> predicateNameToPredicateDef) {
		IAction action=computeActionIncludingSpecialOrFeature(element.getAction(),actionNameToAction,attackNodes,defenseNodes,countermeasureNodes, predicateNameToPredicateDef, variableNameToVariable);
		IConstraint constraint = visitConstraint(element.getConstraint(),attackNodes,variableNameToVariable,predicateNameToPredicateDef				
				);
		return new ActionRequiresConstraint(action, constraint);
	}

	private IAction computeActionIncludingAskOrStoreModifierOrFeature(AskOrStoreModifierActionOrReferenceToAction action,LinkedHashMap<String, NormalAction> actionNameToAction,LinkedHashMap<String,AttackNode> attackNodes,LinkedHashMap<String,DefenseNode> defenseNodes,LinkedHashMap<String,CountermeasureNode> countermeasureNodes, LinkedHashMap<String, IAttributeDef> predicateNameToPredicateDef, LinkedHashMap<String, AtreeVariable> variableNameToVariable) {
		if(action instanceof StoreModifierActionOrReferenceToAction){
			return computeActionIncludingSpecialOrFeature((StoreModifierActionOrReferenceToAction)action,actionNameToAction,attackNodes,defenseNodes,countermeasureNodes,predicateNameToPredicateDef,variableNameToVariable);
		}
		else{
			throw new UnsupportedOperationException("Unsupported action: " + action);
		} 
	}

	private IAction computeActionIncludingSpecialOrFeature(StoreModifierActionOrReferenceToAction action,LinkedHashMap<String, NormalAction> actionNameToAction,LinkedHashMap<String,AttackNode> attackNodes,LinkedHashMap<String,DefenseNode> defenseNodes,LinkedHashMap<String,CountermeasureNode> countermeasureNodes, LinkedHashMap<String, IAttributeDef> predicateNameToPredicateDef, LinkedHashMap<String, AtreeVariable> variableNameToVariable) {
		if(action instanceof atree.xtext.bbt.Action){
			return actionNameToAction.get(((atree.xtext.bbt.Action) action).getName());
		}
		else if(action instanceof atree.xtext.bbt.ReferenceToAction){
			return actionNameToAction.get(((atree.xtext.bbt.ReferenceToAction)action).getValue().getName());
		}
		else if(action instanceof atree.xtext.bbt.StoreModifierActions){
			if(action instanceof atree.xtext.bbt.AddAction){
				return new atree.core.processes.actions.AddAction(attackNodes.get(getNodeName(((atree.xtext.bbt.AddAction)action).getNode())));
			}
			else if(action instanceof atree.xtext.bbt.RemoveAction){
				return new atree.core.processes.actions.RemoveAction(attackNodes.get(getNodeName(((atree.xtext.bbt.RemoveAction)action).getNode())));
			}
			else if(action instanceof atree.xtext.bbt.FailAction){
				return new atree.core.processes.actions.FailAction(attackNodes.get(getNodeName(((atree.xtext.bbt.FailAction)action).getNode())));
			}
			else if(action instanceof atree.xtext.bbt.QueryAction){
				atree.xtext.bbt.Node node = ((atree.xtext.bbt.QueryAction)action).getNode();
				String key = getNodeName(node);
				if (node instanceof atree.xtext.bbt.AttackNode) {
					return new atree.core.processes.actions.QueryAction(attackNodes.get(key));
				}
				else if (node instanceof atree.xtext.bbt.DefenseNode) {
					return new atree.core.processes.actions.QueryAction(defenseNodes.get(key));
				}
				else if (node instanceof atree.xtext.bbt.CountermeasureNode) {
					return new atree.core.processes.actions.QueryAction(countermeasureNodes.get(key));
				}
			}
		}

		return null;
	}
	private String getNodeName(atree.xtext.bbt.Node n)
	{
		if(n instanceof atree.xtext.bbt.AttackNode)
		{
			return ((atree.xtext.bbt.AttackNode)n).getName();
		}
		else if(n instanceof atree.xtext.bbt.DefenseNode)
		{
			return ((atree.xtext.bbt.DefenseNode)n).getName();
		} 
		else if(n instanceof atree.xtext.bbt.CountermeasureNode)
		{
			return ((atree.xtext.bbt.CountermeasureNode)n).getName();
		}
		return null;
	}
	private Collection<atree.xtext.bbt.Node> getRelationChildren(EdgeType e)
	{
		if (e instanceof ORRelation)
		{
			return ((ORRelation)e).getChildrenSet().getChildren();
		}
		if (e instanceof ANDRelation)
		{
			return ((ANDRelation)e).getChildrenSet().getChildren();
		}
		if (e instanceof OANDRelation)
		{
			return ((OANDRelation)e).getChildrenSeq().getChildren();
		}
		if (e instanceof KNRelation)
		{
			return ((KNRelation)e).getChildrenSet().getChildren();
		}
		return null;
	}
	private IConstraint visitConstraint(BoolExpr constraint,LinkedHashMap<String,AttackNode> attackNodes,
			LinkedHashMap<String, AtreeVariable> variableNameToVariable,LinkedHashMap<String, IAttributeDef> predicateNameToPredicateDef
			) {
		if(constraint instanceof atree.xtext.bbt.HasNode){
			atree.xtext.bbt.Node f = ((atree.xtext.bbt.HasNode) constraint).getNode();
			return new HasNodeConstraint(attackNodes.get(getNodeName(f)));
		} 
		else if(constraint instanceof atree.xtext.bbt.AllowedNode){
			atree.xtext.bbt.Node f = ((atree.xtext.bbt.AllowedNode) constraint).getNode();
			return new AllowedNodeConstraint(attackNodes.get(getNodeName(f)));
		} 
		else if(constraint instanceof atree.xtext.bbt.FalseConstraint){
			return new atree.core.processes.constraints.FalseConstraint(); 
		} 
		else if(constraint instanceof atree.xtext.bbt.TrueConstraint){
			return new atree.core.processes.constraints.TrueConstraint(); 
		} 
		else if(constraint instanceof atree.xtext.bbt.NotConstraintExpr){
			return new atree.core.processes.constraints.NotConstraintExpr(visitConstraint(((atree.xtext.bbt.NotConstraintExpr) constraint).getLeft(), attackNodes,variableNameToVariable,predicateNameToPredicateDef));
		} 
		else if(constraint instanceof atree.xtext.bbt.AndBoolConstraintExpr){
			return new BooleanConstraintExpr(visitConstraint(((atree.xtext.bbt.AndBoolConstraintExpr) constraint).getLeft(),attackNodes,variableNameToVariable,predicateNameToPredicateDef), 
					visitConstraint(((atree.xtext.bbt.AndBoolConstraintExpr) constraint).getRight(),attackNodes,variableNameToVariable,predicateNameToPredicateDef), BooleanConnector.AND);
		} 
		else if(constraint instanceof atree.xtext.bbt.OrBoolConstraintExpr){
			return new BooleanConstraintExpr(visitConstraint(((atree.xtext.bbt.OrBoolConstraintExpr) constraint).getLeft(),attackNodes,variableNameToVariable,predicateNameToPredicateDef), 
					visitConstraint(((atree.xtext.bbt.OrBoolConstraintExpr) constraint).getRight(),attackNodes,variableNameToVariable,predicateNameToPredicateDef), BooleanConnector.OR);
		}
		else if(constraint instanceof atree.xtext.bbt.ImpliesBoolConstraintExpr){
			return new BooleanConstraintExpr(visitConstraint(((atree.xtext.bbt.ImpliesBoolConstraintExpr) constraint).getLeft(),attackNodes,variableNameToVariable,predicateNameToPredicateDef), 
					visitConstraint(((atree.xtext.bbt.ImpliesBoolConstraintExpr) constraint).getRight(),attackNodes,variableNameToVariable,predicateNameToPredicateDef), 
					BooleanConnector.IMPLIES);
		}
		else if(constraint instanceof atree.xtext.bbt.DisequationOfPredicateExpr){
			IAttributeExpr lhs = writeExpr(((atree.xtext.bbt.DisequationOfPredicateExpr) constraint).getLhs(),variableNameToVariable,predicateNameToPredicateDef);
			IAttributeExpr rhs = writeExpr(((atree.xtext.bbt.DisequationOfPredicateExpr) constraint).getRhs(),variableNameToVariable,predicateNameToPredicateDef);
			return new DisequationOfAttributeExpressions(lhs, rhs, computeComparator(((atree.xtext.bbt.DisequationOfPredicateExpr) constraint).getComp()));
		}
		return null;
	}
	private IAttributeExpr writeExpr(Expression expr,LinkedHashMap<String, AtreeVariable> variableNameToVariable,LinkedHashMap<String, IAttributeDef> predicateNameToPredicateDef) {
		IAttributeExpr rightVisited=null;
		IAttributeExpr leftVisited=null;

		if(expr instanceof atree.xtext.bbt.NumberLiteral){
			return new Constant(((NumberLiteral)expr).getValue());
		}
		else if(expr instanceof atree.xtext.bbt.RefToVariable){
			return variableNameToVariable.get(((RefToVariable)expr).getVarname().getName());
		}
		else if(expr instanceof atree.xtext.bbt.Predicate){
			return new atree.core.attributes.Attribute(predicateNameToPredicateDef.get(((atree.xtext.bbt.Predicate) expr).getPredicate().getName()));
		}
		else if(expr instanceof atree.xtext.bbt.Addition || expr instanceof atree.xtext.bbt.AdditionWithPredicates){
			if(expr instanceof atree.xtext.bbt.Addition){
				leftVisited = writeExpr(((atree.xtext.bbt.Addition) expr).getLeft(),variableNameToVariable,predicateNameToPredicateDef);
				rightVisited = writeExpr(((atree.xtext.bbt.Addition) expr).getRight(),variableNameToVariable,predicateNameToPredicateDef);
			}
			else if(expr instanceof atree.xtext.bbt.AdditionWithPredicates){
				leftVisited = writeExpr(((atree.xtext.bbt.AdditionWithPredicates) expr).getLeft(),variableNameToVariable,predicateNameToPredicateDef);
				rightVisited = writeExpr(((atree.xtext.bbt.AdditionWithPredicates) expr).getRight(),variableNameToVariable,predicateNameToPredicateDef);
			}
			return new ArithmeticAttributeExpression(leftVisited,rightVisited,ArithmeticOperation.SUM);
		}
		else if(expr instanceof atree.xtext.bbt.Subtraction || expr instanceof atree.xtext.bbt.SubtractionWithPredicates){
			if(expr instanceof atree.xtext.bbt.Subtraction){
				leftVisited = writeExpr(((atree.xtext.bbt.Subtraction) expr).getLeft(),variableNameToVariable,predicateNameToPredicateDef);
				rightVisited = writeExpr(((atree.xtext.bbt.Subtraction) expr).getRight(),variableNameToVariable,predicateNameToPredicateDef);
			}
			else if(expr instanceof atree.xtext.bbt.SubtractionWithPredicates){
				leftVisited = writeExpr(((atree.xtext.bbt.SubtractionWithPredicates) expr).getLeft(),variableNameToVariable,predicateNameToPredicateDef);
				rightVisited = writeExpr(((atree.xtext.bbt.SubtractionWithPredicates) expr).getRight(),variableNameToVariable,predicateNameToPredicateDef);
			}
			return new ArithmeticAttributeExpression(leftVisited,rightVisited,ArithmeticOperation.SUB);
		}
		else if(expr instanceof atree.xtext.bbt.Multiplication || expr instanceof atree.xtext.bbt.MultiplicationWithPredicates){
			if(expr instanceof atree.xtext.bbt.Multiplication){
				leftVisited = writeExpr(((atree.xtext.bbt.Multiplication) expr).getLeft(),variableNameToVariable,predicateNameToPredicateDef);
				rightVisited = writeExpr(((atree.xtext.bbt.Multiplication) expr).getRight(),variableNameToVariable,predicateNameToPredicateDef);
			}
			else if(expr instanceof atree.xtext.bbt.MultiplicationWithPredicates){
				leftVisited = writeExpr(((atree.xtext.bbt.MultiplicationWithPredicates) expr).getLeft(),variableNameToVariable,predicateNameToPredicateDef);
				rightVisited = writeExpr(((atree.xtext.bbt.MultiplicationWithPredicates) expr).getRight(),variableNameToVariable,predicateNameToPredicateDef);
			}
			return new ArithmeticAttributeExpression(leftVisited,rightVisited,ArithmeticOperation.MULT);
		}
		else if(expr instanceof atree.xtext.bbt.MinusPrimary || expr instanceof atree.xtext.bbt.MinusPrimaryWithPredicates){
			if(expr instanceof atree.xtext.bbt.MinusPrimary){
				leftVisited = writeExpr(((atree.xtext.bbt.MinusPrimary) expr).getLeft(),variableNameToVariable,predicateNameToPredicateDef);
			}
			else if(expr instanceof atree.xtext.bbt.MinusPrimaryWithPredicates){
				leftVisited = writeExpr(((atree.xtext.bbt.MinusPrimaryWithPredicates) expr).getLeft(),variableNameToVariable,predicateNameToPredicateDef);
			}
			return new ArithmeticAttributeExpression(new Constant(0),leftVisited,ArithmeticOperation.SUB);
		}
		else{
			throw new UnsupportedOperationException("Unsupported expression: " + expr.toString());
		}
	}
	private AttributeExprComparator computeComparator(String comp) {
		if(comp.equals(">")){
			return AttributeExprComparator.GE;
		}
		else if(comp.equals("<")){
			return AttributeExprComparator.LE;
		}
		else if(comp.equals(">=")){
			return AttributeExprComparator.GEQ;
		}
		else if(comp.equals("<=")){
			return AttributeExprComparator.LEQ;
		}
		else if(comp.equals("==")){
			return AttributeExprComparator.EQ;
		} 
		else if(comp.equals("!=")){
			return AttributeExprComparator.NOTEQ;
		}
		else{
			throw new UnsupportedOperationException("Unsupported comparator: " + comp);
		}
	}

	private atree.core.variables.SideEffect[] visitListOfSideEffects(SideEffects sideEffects,
			LinkedHashMap<String, AtreeVariable> variableNameToVariable,
			LinkedHashMap<String, IAttributeDef> predicateNameToPredicateDef
			) {

		if(sideEffects==null || sideEffects.getEffects()==null || sideEffects.getEffects().size()==0){
			return new atree.core.variables.SideEffect[0];
		}

		EList<atree.xtext.bbt.SideEffect> listOfEffects = sideEffects.getEffects();
		atree.core.variables.SideEffect[] readSideEffects = new atree.core.variables.SideEffect[listOfEffects.size()];

		int i=0;
		for(atree.xtext.bbt.SideEffect eff : listOfEffects){
			IAttributeExpr expr = writeExpr(eff.getValue(), variableNameToVariable, predicateNameToPredicateDef);
			readSideEffects[i]=new atree.core.variables.SideEffect(variableNameToVariable.get(eff.getRefToVar().getVarname().getName()),expr);
			i++;
		}

		return readSideEffects;
	}

}
