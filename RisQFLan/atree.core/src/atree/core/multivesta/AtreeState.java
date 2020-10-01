package atree.core.multivesta;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import atree.core.attributes.interfaces.IAttributeDef;
import atree.core.model.AtreeModel;
import atree.core.model.IAtreeModelBuilder;
//import atree.core.models.OpenSafe;
import atree.core.models.RobBank;
import atree.core.nodes.interfaces.INode;
import atree.core.processes.Commitment;
import atree.core.variables.AtreeVariable;
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
	
	
	private SimulationStateInformation stateInfo;
	
	private RandomEngineFacilities randomGenerator;
	
	public SimulationStateInformation getStateInfo() {
		return stateInfo;
	}

	private Commitment latestSelectedCommitment;
	
	public AtreeState(ParametersForState parameters) {
		super(parameters);
		
		stateInfo = new SimulationStateInformation();
		IAtreeModelBuilder modelBuilder = loadModel(getModelName());
		loadedModel = modelBuilder.createModel();
	}
	
	public AtreeState(AtreeModel model) {
		super(new ParametersForState("provided",""));
		stateInfo = new SimulationStateInformation();
		loadedModel = model;
	}
	
	private IAtreeModelBuilder loadModel(String modelAbsolutePath) {
		//Used to load models
		if(modelAbsolutePath.equals("RobBank")){
			//return new TestBikesWithoutFakeFeatures2();
			return new RobBank();
		}
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
		
		compiler = ToolProvider.getSystemJavaCompiler();
		int res = compiler.run(null, null, stream, sourceFile.getPath()/*,"-cp",cp*/);
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
			//URL jarURL = new File(jarPath).toURI().toURL();
			classLoader = URLClassLoader.newInstance(new URL[] { /*jarURL ,*/rootUrl });
			String className = sourceFile.getName().replace(".java", "");
			Class<?> cls = Class.forName(className, true, classLoader);
			Object inst = cls.newInstance();
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
			}
		}
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
		return 0;
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
		whichObservation = whichObservation.trim();
		if(whichObservation.equalsIgnoreCase(DEFAULTOBSERVATIONS.STEPS.toString())){
			return (double)this.getNumberOfSteps();
		}
		else if(whichObservation.equalsIgnoreCase(DEFAULTOBSERVATIONS.TERMINATED.toString())||
				whichObservation.equalsIgnoreCase(DEFAULTOBSERVATIONS.DEADLOCK.toString())){
			if(isLastStateAlreadyComputed()){
				return 1.0;
			}
			else {
				return 0.0;
			}
		}
		INode toSearch = loadedModel.getNode(whichObservation);
		if(toSearch!=null){
			if(loadedModel.isInstalled(toSearch)){
				return 1;
			}
			else{
				return 0;
			}
		}
		AtreeVariable variable = loadedModel.getVariable(whichObservation);
		if(variable!=null){
			return loadedModel.eval(variable);
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
				return val;
			}
		}
		else{
			IAttributeDef attToSearch = loadedModel.getAttributeDef(whichObservation);
			if(attToSearch!=null){
				double val = attToSearch.getAttackerVal(loadedModel.getAttributeEvaluator());//loadedModel.totalValOfInstalledNodes(attToSearch);
				return val;
			}
		}
		throw new UnsupportedOperationException("Unsupported observation "+whichObservation);
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
