package atree.core.tests;

//import atree.core.models.OpenSafe;
import atree.core.multivesta.AtreeState;
import vesta.mc.ParametersForState;

public class TestLoadModel {
	public static void main(String[] args) {
		AtreeState s=null;
		if(args!=null && args.length>0) {
			s = new AtreeState(new ParametersForState(args[0], ""));
		}
		else {
			//		AtreeState s = new AtreeState(new RobBank().createModel());
			//		AtreeState s = new AtreeState(new ParametersForState("RobBank", ""));
			//s = new AtreeState(new ParametersForState("C:\\runtime-EclipseApplication\\testproject\\RobBank.bbt", ""));
			//s = new AtreeState(new ParametersForState("/Users/anvan/OneDrive/OneDrive - Danmarks Tekniske Universitet/TOOLS/RisQFLan/GBarRepository/runtime-RisQFLan.product/models/TestA.bbt", ""));
			//s = new AtreeState(new ParametersForState("/Users/anvan/OneDrive/OneDrive - Danmarks Tekniske Universitet/TOOLS/RisQFLan/GBarRepository/runtime-RisQFLan.product/models/Test.bbt", ""));
			//s = new AtreeState(new ParametersForState("/Users/anvan/OneDrive/OneDrive - Danmarks Tekniske Universitet/TOOLS/RisQFLan/GBarRepository/runtime-RisQFLan.product/models/Pino.bbt", ""));
			//s = new AtreeState(new ParametersForState("/Users/anvan/OneDrive/OneDrive - Danmarks Tekniske Universitet/TOOLS/RisQFLan/GBarRepository/runtime-RisQFLan.product/models/Piero.bbt", ""));
			//s = new AtreeState(new ParametersForState("/Users/andrea/OneDrive - Danmarks Tekniske Universitet/TOOLSSecurity/RisQFLan/GBarRepository/runtime-RisQFLan.product/modelsPaper/CatBurglarDebug.bbt", ""));
			//s = new AtreeState(new ParametersForState("/Users/andrea/OneDrive - Danmarks Tekniske Universitet/TOOLSSecurity/RisQFLan/runtime-RisQFLan.product/modelsMainPaper/OpenSafe.bbt", ""));
			s = new AtreeState(new ParametersForState("/Users/andrea/Library/CloudStorage/OneDrive-ScuolaSuperioreSant'Anna/runtimes/runtime-RisQFLan.product/modelsToolPaper/OpenSafe.bbt", "-logFile ciao.csv"));
			
		}
		
		
		int numberOfSimulations = 4;
		int numberOfSimulationSteps = 3;
		
		for(int i=0; i < numberOfSimulations; i++)
		{
			System.out.println("\n");
			s.setSimulatorForNewSimulation(i+10);
//			while(s.rval("STEPS")<=10)
			for(int j=0; j<numberOfSimulationSteps; j++)
			{
				// FIX ADD AUTOMATIC ! GHOST ATTACKS ARE NOT ADDED -> ADD(OPENVAULT) IS NOT ALLOWED!?
				System.out.println("step:"+j);
				System.out.println("current state: "+s.getModel().getCurrentState().getName());
				System.out.println("Number of transitions: "+s.getModel().getCurrentState().getTransitions().size());
//				System.out.println(s.getModel().getCurrentState().getTransitions().iterator().next().isAllowed(s.getModel()));
				//s.performOneStepOfSimulation();
				s.next();
				//System.out.println(s.rval("Noticeability"));
				System.out.println(s.rval("LearnPIN"));
//				s.next();
				System.out.println("Forbidden: "+s.getStateInfo().getForbiddenCommitments().size()+" - Allowed: "+s.getStateInfo().getAllowedCommitments().size());
//				System.out.println("Attack attempts is " + s.rval("attackAttempts"));
			}
			System.out.println("Iteration "+i+": Done.");
		}
		System.out.println("");
	}
}
