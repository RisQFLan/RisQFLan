package atree.xtext.ui.perspective.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewFileResourceWizard;

public class NewRisQFLanFileWizard  extends BasicNewFileResourceWizard {//extends Wizard implements INewWizard {

	private WizardNewRisQFLanFileCreationPage page;
	
	public NewRisQFLanFileWizard() {
		super();
		setWindowTitle("RisQFLan File");
	}

	@Override
    public void addPages() {
        page = new WizardNewRisQFLanFileCreationPage(selection);
        addPage(page);
    }
	 
	 /* (non-Javadoc)
     * Method declared on IWizard.
     */
    @Override
	public boolean performFinish() {
    		//Model files should start with capital name
    		page.setFileName(page.getFileName().substring(0, 1).toUpperCase() + page.getFileName().substring(1));
        IFile file = page.createNewFile();
        if (file == null) {
			return false;
		}

        selectAndReveal(file);

        // Open editor on new file.
        IWorkbenchWindow dw = getWorkbench().getActiveWorkbenchWindow();
        try {
            if (dw != null) {
                IWorkbenchPage page = dw.getActivePage();
                if (page != null) {
                    IDE.openEditor(page, file, true);
                }
            }
        } catch (PartInitException e) {
        	
            //DialogUtil.openError(dw.getShell(), ResourceMessages.FileResource_errorMessage,e.getMessage(), e);
        	e.printStackTrace();
        }

        return true;
    }



}
