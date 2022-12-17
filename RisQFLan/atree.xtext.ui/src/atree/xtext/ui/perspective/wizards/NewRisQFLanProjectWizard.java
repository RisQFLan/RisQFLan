package atree.xtext.ui.perspective.wizards;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

public class NewRisQFLanProjectWizard extends BasicNewProjectResourceWizard{
	
	public NewRisQFLanProjectWizard(){
		super();
		setWindowTitle("RisQFLan Project");
	}
	
	@Override
	public void addPages() {
		super.addPages();
		this.getPages()[0].setDescription("Create a new RisQFLan project");
		this.getPages()[0].setTitle("RisQFLan project");
	}
}
