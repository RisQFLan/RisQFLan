package atree.core.tests;

import atree.core.multivesta.EntryPointMultiVeStARisQFLan;

public class TestRunMultiVeStA {

	public static void main(String[] args) {
//		EntryPointMultiVeStAAndBbt.main(new String[]{
//    			"-c", "-sm", "false", "-sd", "atree.core.multivesta.AtreeState", 
//    			"-m", "RobBank", "-l", "1", "-ir", "20", 
//    			"-f", "MultiQuaTEx/stepAfterXSteps.quatex", 
//    			"-bs", "20", "-a", "0.1", "-ds", "[5.0]", "-osws", "ONESTEP", "-sots", "12343"
//    	});
		
//		EntryPointMultiVeStAAndBbt.main(new String[]{
//    			"-c", "-sm", "false", "-sd", "atree.core.multivesta.AtreeState", 
//    			"-m", "RobBank", "-l", "1", "-ir", "20", 
//    			"-f", "MultiQuaTEx/queryRobBank.quatex", 
//    			"-bs", "20", "-a", "0.1", "-ds", "[5.0]", "-osws", "ONESTEP", "-sots", "12343"
//    	});
		
		
		EntryPointMultiVeStARisQFLan.main(new String[]{
    			"-c", "-sm", "false", "-sd", "atree.core.multivesta.AtreeState", 
    			"-m", "RobBank", "-l", "1", "-ir", "20", 
    			"-f", "/C:/runtime-EclipseApplication\\testproject2\\src-gen\\queryRobBank.quatex", 
    			"-bs", "20", "-a", "0.1", "-ds", "[5.0]", "-osws", "ONESTEP", "-sots", "12343"
    	});
		
		
		
		
		/*EntryPointMultiVeStAAndQFLanJava.main(new String[]{
    			"-c", "-sm", "false", "-sd", "it.imt.qflan.core.multivesta.QFlanJavaState", 
    			"-m", "BikesIDE.java", "-l", "1", "-ir", "20", 
    			"-f", "MultiQuaTEx/FeaturesInstalledAndPriceWeightLoadAtStep.quatex", 
    			"-bs", "20", "-a", "0.1", "-ds", "[20.0,1.0,5.0,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1]", "-osws", "ONESTEP", "-sots", "12343"
    	});*/
		/*EntryPointMultiVeStAAndQFLanJava.main(new String[]{
    			"-c", "-sm", "false", "-sd", "it.imt.qflan.core.multivesta.QFlanJavaState", 
    			"-m", "BikesIDE.java", "-l", "1", "-ir", "20", 
    			"-f", "MultiQuaTEx/FeaturesInstalledAndPriceWeightLoadAtStep.quatex", 
    			"-bs", "20", "-a", "0.1", "-ds", "[20.0,1.0,5.0,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1]", "-osws", "ONESTEP", "-sots", "12343"
    	});*/
	}
}
