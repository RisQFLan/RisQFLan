package atree.core.multivesta;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import atree.core.attributes.interfaces.IAttributeDef;
import atree.core.model.AtreeModel;
import atree.core.model.IAtreeModelBuilder;
import atree.core.nodes.AttackNode;
import atree.core.nodes.CountermeasureNode;
//import atree.core.models.OpenSafe;
//import atree.core.models.RobBank;
import atree.core.nodes.interfaces.INode;
import atree.core.processes.Commitment;
import atree.core.processes.interfaces.IAction;
import atree.core.variables.AtreeVariable;
import atree.core.variables.VariableAssignment;
import cern.jet.random.engine.MersenneTwister;
import it.imtlucca.util.RandomEngineFacilities;
import vesta.mc.NewState;
import vesta.mc.ParametersForState;
import vesta.quatex.DEFAULTOBSERVATIONS;



public class AtreeState extends NewState {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AtreeModel loadedModel;
	private String nameOfTheJarForBM;
	private String logFile="";
	private boolean toLog=false;
	
	private SimulationStateInformation stateInfo;
	
	private RandomEngineFacilities randomGenerator;
	
	public SimulationStateInformation getStateInfo() {
		return stateInfo;
	}

	private Commitment latestSelectedCommitment;
	
	public AtreeState(ParametersForState parameters) {
		super(parameters);
		
		this.nameOfTheJarForBM=parameters.getNameOfTheJarForBM();
		stateInfo = new SimulationStateInformation();
		IAtreeModelBuilder modelBuilder = loadModel(getModelName());
		loadedModel = modelBuilder.createModel();
		
		//"-o","-logFile "+logFile
		String other = parameters.getOtherParameters();
		other = other.replace("-logFile ", "");
		if(other.length()>0) {
			logFile=other;
			toLog=true;
		}
		
	}
	
	public AtreeState(AtreeModel model) {
		super(new ParametersForState("provided",""));
		stateInfo = new SimulationStateInformation();
		loadedModel = model;
	}
	
	private IAtreeModelBuilder loadModel(String modelAbsolutePath) {
		//Used to load models
//		if(modelAbsolutePath.equals("RobBank")){
//			//return new TestBikesWithoutFakeFeatures2();
//			return new RobBank();
//		}
//		else if(modelAbsolutePath.equals("elevator")){
//			return new Elevator();
//		}
//		else if(modelAbsolutePath.equalsIgnoreCase("bikesdiagram")){
//			return new BikesDiagram();
//		}
		
		File sourceFile = null;
		JavaCompiler compiler = null;
		URLClassLoader classLoader;
		File root;
		IAtreeModelBuilder modelBuilder=null;
		
		//root = new File("/models/abc/d-e");//new File("/Users/andrea/Dropbox/runtime-EclipseApplication/qflan2/src-gen");//new File("/models/abc");//new File("/models"); // On Windows running on C:\, this is C:\java.
		//root = new File("/Users/andrea/Dropbox/runtime-EclipseApplication/qflan2/src-gen");
				
		String rootName = modelAbsolutePath.substring(0,modelAbsolutePath.lastIndexOf(File.separator));
		rootName = rootName + File.separator + "src-gen";
		root = new File(rootName);
		String modelName = modelAbsolutePath.substring(modelAbsolutePath.lastIndexOf(File.separator)+1);
		modelName = modelName.replace(".bbt", ".java");
		sourceFile = new File(root, modelName);
		File compilationLog = new File(root,sourceFile.getName().replace(".java", ".log"));
		OutputStream stream=null;
		try {
			stream = new FileOutputStream(compilationLog);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		
		String javaclassPath = System.getProperty("java.class.path");
		if(nameOfTheJarForBM!=null&&nameOfTheJarForBM.length()>0) {
			//TODO: gli devo passare il nameofthejar
			//String nameOfTheJar="/Users/andrea/multivestaRisQFLan.jar";
			javaclassPath=javaclassPath+File.pathSeparator+nameOfTheJarForBM;
			//javaclassPath=javaclassPath+File.pathSeparator+"/Users/andrea/multivestaRisQFLan.jar";
		}
		
		compiler = ToolProvider.getSystemJavaCompiler();
		//int res = compiler.run(null, null, stream, "-cp",System.getProperty("java.class.path"),sourceFile.getPath()/*,"-cp",cp*/);
		//int res = compiler.run(null, null, stream, sourceFile.getPath()/*,"-cp",cp*/);
		int res = compiler.run(null, null, stream,"-cp",javaclassPath,sourceFile.getPath());
		///Users/andrea/Google Drive (andrea.vandin@alumni.imtlucca.it)/DISTR/RisQFLan/multivestaRisQFLan.jar
		if(res==0){
			System.out.println("The compilation succeeded: "+res);
		}
		else{
			System.out.println("Compilation failed: "+res);
			return null;
		}
		// Load and instantiate compiled class.
		try {
			URL rootUrl = root.toURI().toURL();
			if(nameOfTheJarForBM!=null&&nameOfTheJarForBM.length()>0) {
				URL jarURL = new File(nameOfTheJarForBM).toURI().toURL();
				classLoader = URLClassLoader.newInstance(new URL[] { jarURL, rootUrl });
			}
			else {
				classLoader = URLClassLoader.newInstance(new URL[] {         rootUrl });
			}
			
			//classLoader = URLClassLoader.newInstance(new URL[] {         rootUrl });
			String className = sourceFile.getName().replace(".java", "");
			Class<?> cls = Class.forName(className, true, classLoader);
			//Object inst = cls.newInstance(); //cls.getDeclaredConstructor().newInstance(null);
			Object inst=null;
			try {
				inst = cls.getConstructors()[0].newInstance();
			} catch (IllegalArgumentException | InvocationTargetException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			modelBuilder = (IAtreeModelBuilder)inst;
			//System.out.println(modelBuilder);
		} catch (MalformedURLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			System.exit(-1);
		} 
		return modelBuilder;
	}

	@Override
	public void setSimulatorForNewSimulation(int randomSeed) {
		loadedModel.resetToInitialState();
		
		randomGenerator = new RandomEngineFacilities(new MersenneTwister(this.getCurrentSeed()));		
		loadedModel.setRandomSeed(randomSeed);
		
		if(toLog) {
			LinkedHashMap<String, String> captionToValue = defaultLogInfo(true);
			captionToValue.put("activity", "reset");
			addRowToLog(captionToValue);
		}
	}

	private void addRowToLog(LinkedHashMap<String, String> captionToValue) {
		StringBuffer row = new StringBuffer();
		for(Entry<String, String> entry:captionToValue.entrySet()) {
			row.append(entry.getValue());
			row.append(",");
		}
		row.deleteCharAt(row.length()-1);
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(logFile,true));
			writer.write(row.toString());
			writer.write("\n");
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private LinkedHashMap<String, String> defaultLogInfo(boolean stepMinusOne) {
		LinkedHashMap<String, String> captionToValue=new LinkedHashMap<>();
		
		//case id
		captionToValue.put("caseID", this.getCurrentSeed()+"");
		
		//time
		int time =this.getNumberOfSteps();
		if(stepMinusOne) {
			time--;
		}
		captionToValue.put("time", time+"");
		
		
		//state
		captionToValue.put("state", loadedModel.getCurrentState().getName());
		
		//installed attack nodes
		for(AttackNode attack : loadedModel.getAttackNodes()) {
			boolean installed = loadedModel.isInstalled(attack);
			captionToValue.put(attack.getName(), String.valueOf(installed));
		}
		
		//active countermeasures
		for(CountermeasureNode countermeasure : loadedModel.getCountermeasureNodes()) {
			boolean installed = loadedModel.isInstalled(countermeasure);
			captionToValue.put(countermeasure.getName(), String.valueOf(installed));
		}
		
		//variables
		VariableAssignment va = loadedModel.getVariableAssignment();
		for(AtreeVariable var : va.getVariables()) {
			//caption.add(var.getName());
			double val = va.getValue(var);
			captionToValue.put(var.getName(), String.valueOf(val));
		}
		
		//activity is add outside
		
		return captionToValue;
	}
	public static ArrayList<String> logCaption(AtreeModel model) {
		ArrayList<String> caption=new ArrayList<>();
		caption.add("caseID");
		caption.add("time");
		caption.add("state");
		
		//attack nodes
		//model.getCurrentState();
		for(AttackNode attack : model.getAttackNodes()) {
			caption.add(attack.getName());
		}
		
		for(CountermeasureNode countermeasure : model.getCountermeasureNodes()) {
			caption.add(countermeasure.getName());
		}
		
		//variables
		VariableAssignment va = model.getVariableAssignment();
		for(AtreeVariable var : va.getVariables()) {
			caption.add(var.getName());
		}
		
		caption.add("activity");
		
		return caption;
	}

	@Override
	public void performOneStepOfSimulation() {
		stateInfo = new SimulationStateInformation();
		
		computeAllowedTransitions();
		
		if (stateInfo.getAllowedCommitments().size() == 0) {
			setLastStateAlreadyComputed(true);
		}
		else {
			double cumulativeRate = stateInfo.getCumulativeRate();
			if (cumulativeRate == 0) {
				setLastStateAlreadyComputed(true);
			}
			else {
				Commitment commitment = chooseCommitment(cumulativeRate);
				apply(commitment);
				
				if(toLog) {
					LinkedHashMap<String, String> captionToValue = defaultLogInfo(false);
					IAction executed = commitment.getTransition().getAction();
					captionToValue.put("activity", executed.getName());
					addRowToLog(captionToValue);
				}
			}
		}
		//System.out.println("Step "+getNumberOfSteps());
	}
	
	public void apply(Commitment commitment) {
		loadedModel.apply(commitment);
		
	}

	public Commitment chooseCommitment(double totalRate) {
		double sampledDouble = randomGenerator.nextDouble();
		double sampledNumber = sampledDouble * totalRate;
		Commitment comm = stateInfo.getAllowedCommitment(sampledNumber);
		latestSelectedCommitment = comm;
		return comm;
	}

	private void computeAllowedTransitions() {
		loadedModel.computeAllowedTransitions(stateInfo);
		if(stateInfo.getAllowedCommitments().size() == 0) { //loadedModel.getNumberOfComputedCommitments()==0){
			setLastStateAlreadyComputed(true);
		}
	}

	@Override
	public double getTime() {
		//return 0;
		return getNumberOfSteps();
	}

	@Override
	public void performWholeSimulation() {
		while(!isLastStateAlreadyComputed()){
			performOneStepOfSimulation();
		}
	}

	@Override
	public double rval(int observation) {
		if(observation == 1){
			//System.out.println("End rval("+r+")");
			return (double)this.getNumberOfSteps();
		}
		else{
			throw new UnsupportedOperationException("Unsupported observation "+observation);
		} 
	}

	@Override
	public double rval(String whichObservation) {
		double ret=0;
		boolean found=false;
		whichObservation = whichObservation.trim();
		if(whichObservation.equalsIgnoreCase(DEFAULTOBSERVATIONS.STEPS.toString())){
			ret=(double)this.getNumberOfSteps();
			found=true;
		}
		else if(whichObservation.equalsIgnoreCase(DEFAULTOBSERVATIONS.TERMINATED.toString())||
				whichObservation.equalsIgnoreCase(DEFAULTOBSERVATIONS.DEADLOCK.toString())){
			if(isLastStateAlreadyComputed()){
				ret=1.0;
			}
			else {
				ret=0.0;
			}
			found=true;
		}
		
		
		INode toSearch = loadedModel.getNode(whichObservation);
		
		if(toSearch!=null){
			if(loadedModel.isInstalled(toSearch)){
				ret=1;
			}
			else{
				ret=0;
			}
			found=true;
		}
		AtreeVariable variable = loadedModel.getVariable(whichObservation);
		if(variable!=null){
			ret=loadedModel.eval(variable);
			found=true;
		}
		
		whichObservation=whichObservation.replace(" ", "");
		int openPar=whichObservation.indexOf('(');
		int closedPar=whichObservation.indexOf(')');
		if(openPar>0 && closedPar>0 && closedPar>openPar && whichObservation.endsWith(")")){
			String attName=whichObservation.substring(0, openPar);
//					String nodeName= whichObservation.substring(openPar+1,closedPar);
			IAttributeDef attToSearch = loadedModel.getAttributeDef(attName);
			if(attToSearch!=null){
				double val = attToSearch.eval(loadedModel.getAttributeEvaluator());//loadedModel.getNode(nodeName), loadedModel.getInstalledNodes());
				ret=val;
				found=true;
			}
		}
		else{
			IAttributeDef attToSearch = loadedModel.getAttributeDef(whichObservation);
			if(attToSearch!=null){
				double val = attToSearch.getAttackerVal(loadedModel.getAttributeEvaluator());//loadedModel.totalValOfInstalledNodes(attToSearch);
				ret=val;
				found=true;
			}
		}
		
		
		if(!found) {
			throw new UnsupportedOperationException("Unsupported observation "+whichObservation);
		}
		double noise=0;//randomGenerator.sampleFromNormal(0, 0.1);
		return ret+noise;
		
		
	}

	public String computeSATMessage() {
		return loadedModel.computeSATMessage();
	}

	public String currentStatus() {
		if(loadedModel == null)
		{
			return "model not loaded";
		}
		return loadedModel.toString();
	}

	public String printCurrentStateInfo() {
		return printCurrentStateInfo(true);
	}
	
	public String printCurrentStateInfo(boolean printStateInfo) {
		if(stateInfo == null)
		{
			return "transitions not loaded";
		}
		StringBuffer retString = new StringBuffer("Variables:\n");
		retString.append(loadedModel.printVariables());
		retString.append("\n");
		retString.append("Relations:\n");
		retString.append(loadedModel.printRelations());
		retString.append("\n");
		if(printStateInfo) {
			retString.append(stateInfo.toString());
		}
		return retString.toString();
//		return stateInfo.toString();
	}
	
	public AtreeModel getModel()
	{
		return loadedModel;
	}
	
	public boolean nextStateAvailable()
	{
		return !stateInfo.getAllowedCommitments().isEmpty() || stateInfo.getCumulativeRate() == 0;
	}
	
	public Commitment getLatestCommitment()
	{
		return latestSelectedCommitment;
	}

}
