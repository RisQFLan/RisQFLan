package atree.xtext.ui.handler;

import java.io.IOException;
import java.util.ArrayList;

//import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
//import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.console.MessageConsoleStream;

import atree.xtext.ui.perspective.plot.GUIDataOutputHandler;
import atree.core.model.AtreeModel;
import atree.core.processes.interfaces.IConstraint;
import atree.xtext.ui.MyConsoleUtil;
import vesta.NewVesta;
import vesta.mc.InfoMultiQuery;

public class MyMultiVeStAAnalysisExecutorWorker extends Thread {

	MessageConsoleStream out;
	private IProject project;
	private String modelFullPath;
	private String jarPath;
	private String projectPath;
	private GUIDataOutputHandler guidog;
	private String query;
	private String alpha;
	private String delta;
	String parallelismOrServers;
	int ir;
	int blockSize;
	private int seed;
	private MultiVeStAOrSimulation whattodo;
	private AtreeModel model;
	private String fileOut;
	private int steps;
	private String format;
	private ArrayList<String> labelsDTMCNames;
	private ArrayList<IConstraint> labelsDTMCGuard;
	
	public MyMultiVeStAAnalysisExecutorWorker(String query,String alpha,String delta,String parallelismOrServers,int ir,GUIDataOutputHandler guidog, MessageConsoleStream out, IProject project, String modelFullPath, String jarPath,String projectPath, int blockSize) {
		this.guidog=guidog;
		this.out=out;
		this.project=project;
		this.modelFullPath=modelFullPath;
		this.jarPath=jarPath;
		this.projectPath=projectPath;
		this.query=query;
		this.alpha=alpha;
		this.delta=delta;
		this.parallelismOrServers=parallelismOrServers;
		this.ir=ir;
		this.blockSize=blockSize;
		this.whattodo=MultiVeStAOrSimulation.MULTIVESTA;
	}
	
	public MyMultiVeStAAnalysisExecutorWorker(int steps,String fileOut,AtreeModel model, int seed,GUIDataOutputHandler guidog, MessageConsoleStream out, IProject project, String modelFullPath, String jarPath, String projectPath) {
		this.steps=steps;
		this.fileOut=fileOut;
		this.model=model;
		this.guidog=guidog;
		this.out=out;
		this.project=project;
		this.modelFullPath=modelFullPath;
		this.jarPath=jarPath;
		this.projectPath=projectPath;
		this.seed=seed;
		this.whattodo=MultiVeStAOrSimulation.SIMULATE;
	}
	
	public MyMultiVeStAAnalysisExecutorWorker(String fileOut,ArrayList<String> labelsDTMCNames, ArrayList<IConstraint> labelsDTMCGuard, String format,AtreeModel model, GUIDataOutputHandler guidog, MessageConsoleStream out, IProject project, String modelFullPath, String jarPath, String projectPath) {
		this.fileOut=fileOut;
		this.model=model;
		this.guidog=guidog;
		this.out=out;
		this.project=project;
		this.modelFullPath=modelFullPath;
		this.jarPath=jarPath;
		this.projectPath=projectPath;
		this.whattodo=MultiVeStAOrSimulation.EXPORTDTMC;
		this.format=format;
		this.labelsDTMCNames=labelsDTMCNames;
		this.labelsDTMCGuard=labelsDTMCGuard;
	}

	@Override
	public void run() {
		/*String query = "/MultiQuaTEx/FeaturesInstalledAndPriceWeightLoadAtStep.quatex";
		String alpha = "0.1";
		String delta = "[20.0,1.0,5.0,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1]";
		InfoMultiQuery result = vesta.NewVesta.invokeClient(new String[]{
				"-sm", "false", "-sd", "atree.core.multivesta.AtreeState", 
				"-m", modelFullPath, "-l", "1", "-ir", "20", 
				"-f", query,
				"-op" , projectPath,
				"-vp", "false",
				"-jn", jarPath, //ImportBNGWizard.getPossibleJarPaths(),
				"-bs", "20", "-a", String.valueOf(alpha), "-ds", delta, "-osws", "ONESTEP", "-sots", "12343"
		},out);*/
		
		
		/*int parallelism = 1;
		int ir = 20;
		String outputPath = projectPath;*/

		final int REPEAT = 1;
		for(int i=0;i<REPEAT;i++){
			if(whattodo.equals(MultiVeStAOrSimulation.MULTIVESTA)){
				InfoMultiQuery result = MyMultiVeStAAnalysisExecutor.invokeMultiVeStA(modelFullPath, query, alpha, delta, parallelismOrServers,ir,jarPath,projectPath,blockSize,out);
				if(result.isParametric()){
					MyMultiVeStAAnalysisExecutor.visualizePlot(guidog,result, modelFullPath, query, alpha, delta,out);
				}
			}
			else if(whattodo.equals(MultiVeStAOrSimulation.SIMULATE)){
				try {
					MyMultiVeStAAnalysisExecutor.performASimulation(steps,fileOut,model,modelFullPath,seed,out,jarPath,projectPath);
				} catch (IOException e) {
					NewVesta.printStackTrace(out, e);
				}
			}
			else if(whattodo.equals(MultiVeStAOrSimulation.EXPORTDTMC)){
				try {
					MyMultiVeStAAnalysisExecutor.exportDTMC(fileOut,labelsDTMCNames,labelsDTMCGuard,format,model,modelFullPath,out,jarPath,projectPath);
				} catch (IOException e) {
					NewVesta.printStackTrace(out, e);
				}
			}
		}
		
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.println(MyConsoleUtil.computeGoodbye(out));
	}

	
}