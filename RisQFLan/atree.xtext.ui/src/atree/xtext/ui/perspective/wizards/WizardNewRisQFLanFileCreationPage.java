package atree.xtext.ui.perspective.wizards;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

public class WizardNewRisQFLanFileCreationPage extends WizardNewFileCreationPage {

	private Combo combo;
	public static final String YESSimple = "Simple model";
	public static final String YESEmpty = "Empty model";
	public static final String NO = "No";
	private static final String extension = "bbt";
	
	public WizardNewRisQFLanFileCreationPage(IStructuredSelection selection) {
		super("New RisQFLan file", selection);
		setTitle("RisQFLan file");
		setDescription("Create a new RisQFLan file");
        setFileExtension(extension);
	}
	
	@Override
	protected void createAdvancedControls(Composite parent) {
		Label label1 = new Label(parent, SWT.NONE);
		label1.setText("Would you like an initial template file?");
		
		combo = new Combo(parent, SWT.READ_ONLY);
		combo.add(NO);
		combo.add(YESEmpty);
		combo.add(YESSimple);
		combo.select(0);
		combo.setSize(2, combo.getSize().y);
		super.createAdvancedControls(new Composite(parent, 1));
	}
	
	public String getType(){
		return combo.getText();
	}
		
	@Override
	protected InputStream getInitialContents() {
		//this.setFileName(getFileName().substring(0, 1).toUpperCase() + getFileName().substring(1));
		String modelName = getFileName();

		String ext="."+extension;
		if(modelName.endsWith(ext)){
				modelName=modelName.substring(0, modelName.length()-ext.length());
		}
		modelName= getOnlyAlphaNumeric(modelName);//.replace('-', '_').replace("(", "").replace(")", "");
		if(!getType().equals(WizardNewRisQFLanFileCreationPage.NO)){
			String preDefModel=null;
			try {
				preDefModel = getPredefModel(getType(), modelName);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(preDefModel==null){
				preDefModel="";
			}
			return new ByteArrayInputStream(preDefModel.getBytes());
		}
		else{
			return null;
		}
	}

	public static String getOnlyAlphaNumeric(String s) {
	    Pattern pattern = Pattern.compile("[^0-9 a-z A-Z _]");
	    Matcher matcher = pattern.matcher(s.replace('-', '_'));
	    String number = matcher.replaceAll("");
	    return number;
	 }
	
	private String getPredefModel(String type, String modelName) throws IOException {
		StringBuilder sb = new StringBuilder();
		
		System.out.println(getContainerFullPath());
		
		String file = null;

		if(type.equals(YESSimple)){
			file="SimpleModel.txt";
		}
		else if(type.equals(YESEmpty)){
			file="EmptyModel.txt";
		} 
		/*
		else if(type.equals(YESSimpleDiagram)){
			file="SimpleDiagram.txt";
		}
		*/
		
		if(file!=null){
			InputStream is = getClass().getResourceAsStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line=br.readLine();
			sb.append("begin model "+modelName+"\n");
			while(line!=null){
				sb.append(line);
				sb.append("\n");
				line=br.readLine();
			}
			br.close();
		}
		
		return sb.toString();
	}

}
