package org.abapgit.adt.ui.internal.repositories.actions;

import org.abapgit.adt.backend.IExternalRepositoryInfoService;
import org.abapgit.adt.backend.IRepositoryService;
import org.abapgit.adt.backend.RepositoryServiceFactory;
import org.abapgit.adt.backend.model.abapgitrepositories.IRepository;
import org.abapgit.adt.ui.AbapGitUIPlugin;
import org.abapgit.adt.ui.internal.i18n.Messages;
import org.abapgit.adt.ui.internal.repositories.AbapGitView;
import org.abapgit.adt.ui.internal.repositories.exceptions.PackageRefNotFoundException;
import org.abapgit.adt.ui.internal.wizards.AbapGitWizardSwitchBranch;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.sap.adt.communication.resources.ResourceException;
import com.sap.adt.tools.core.project.AdtProjectServiceFactory;
import com.sap.adt.tools.core.ui.packages.AdtPackageServiceUIFactory;
import com.sap.adt.tools.core.ui.packages.IAdtPackageServiceUI;

public class SwitchbranchAction extends Action {

	private IRepository selRepo;
	private final AbapGitView AbapGitView;
	private IProject project;

	public SwitchbranchAction(AbapGitView view) {
		super(Messages.AbapGitView_action_switch_branch);
		setToolTipText(Messages.AbapGitView_action_switch_branch);
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(AbapGitUIPlugin.PLUGIN_ID, "/icons/etool/branches_obj.png")); //$NON-NLS-1$
		this.AbapGitView = view;
	}

	@Override
	public void run() {
		this.project = getProject();
		this.selRepo = getRepository();
		if (this.selRepo != null) {
			String destination = AdtProjectServiceFactory.createProjectService().getDestinationId(this.project);
			try {
				IAdtPackageServiceUI packageServiceUI = AdtPackageServiceUIFactory.getOrCreateAdtPackageServiceUI();
				IExternalRepositoryInfoService externalRepoInfoService = RepositoryServiceFactory
						.createExternalRepositoryInfoService(destination, null);
				IRepositoryService repoService = RepositoryServiceFactory.createRepositoryService(destination, new NullProgressMonitor());
				WizardDialog dialog = new WizardDialog(this.AbapGitView.getViewSite().getShell(),
						new AbapGitWizardSwitchBranch(this.project, this.selRepo, destination, packageServiceUI, externalRepoInfoService,
								repoService));
				dialog.open();
			} catch (PackageRefNotFoundException | ResourceException e) {
				MessageDialog.openError(this.AbapGitView.getViewSite().getShell(), Messages.AbapGitWizardSwitch_branch_wizard_title,
						e.getLocalizedMessage());
			}
		}
		this.AbapGitView.refresh();

	}

	private IRepository getRepository() {
		return this.AbapGitView.getRepositorySelection();
	}

	private IProject getProject() {
		return this.AbapGitView.getProject();
	}

}
