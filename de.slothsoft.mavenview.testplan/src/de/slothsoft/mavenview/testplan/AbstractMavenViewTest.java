package de.slothsoft.mavenview.testplan;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import de.slothsoft.mavenview.MavenView;
import de.slothsoft.mavenview.testplan.constants.CommonConstants;
import de.slothsoft.mavenview.testplan.constants.MavenViewConstants;
import de.slothsoft.mavenview.testplan.constants.NewProjectConstants;
import de.slothsoft.mavenview.testplan.constants.PreferencesConstants;
import de.slothsoft.mavenview.testplan.constants.WorkbenchConstants;

public abstract class AbstractMavenViewTest {

	private static String PROJECT_VIEW = null;

	static {
		System.setProperty("org.eclipse.swtbot.search.defaultKey", CommonConstants.DATA_ID);
	}

	protected final SWTWorkbenchBot bot = new SWTWorkbenchBot();
	private final List<Runnable> tearDowns = new ArrayList<>();

	@Before
	public final void closeAllMavenViews() {
		Display.getDefault().syncExec(() -> {
			final IWorkbench workbench = PlatformUI.getWorkbench();
			final IWorkbenchPage activePage = workbench.getActiveWorkbenchWindow().getActivePage();
			for (final IViewReference view : activePage.getViewReferences()) {
				if (view.getId().equals(MavenView.ID)) {
					activePage.hideView(view);
				}
			}

			if (PROJECT_VIEW == null) {
				final String shellText = workbench.getActiveWorkbenchWindow().getShell().getText();
				if (!"data".equals(shellText)) {
					// Maven IT tests
					PROJECT_VIEW = WorkbenchConstants.VIEW_PACKAGE_EXPLORER;
				} else {
					// Eclipse IT tests
					PROJECT_VIEW = WorkbenchConstants.VIEW_PROJECT_EXPLORER;
				}
			}
		});

		Assert.assertNotNull("Could not initialize PROJECT_VIEW!", PROJECT_VIEW);
	}

	@After
	public final void tearDownRunnables() {
		this.tearDowns.forEach(Runnable::run);
		this.tearDowns.clear();
	}

	protected void addToTearDown(Runnable runnable) {
		this.tearDowns.add(runnable);
	}

	// general code snippets

	protected SWTBotView openMavenViewViaDialog() {
		addToTearDown(this::clearShowViewDialog); // just in case

		final SWTBotView result = openViewViaDialog(MavenViewConstants.VIEW_GROUP, MavenViewConstants.VIEW_TITLE);
		addToTearDown(result::close);
		return result;
	}

	protected SWTBotView openViewViaDialog(String viewGroup, String viewTitle) {
		this.bot.menu(WorkbenchConstants.MENU_WINDOW).menu(WorkbenchConstants.SUB_MENU_SHOW_VIEW)
				.menu(WorkbenchConstants.COMMAND_OTHER).click();

		this.bot.waitUntil(Conditions.shellIsActive(WorkbenchConstants.SHOW_VIEW_TITLE));

		this.bot.text().setText(MavenViewConstants.VIEW_TITLE);

		// dunno why we have to expand, but it's necessary else the title is not found
		this.bot.tree().getTreeItem(viewGroup).expand();
		this.bot.tree().getTreeItem(viewGroup).getNode(viewTitle).select();

		this.bot.button(CommonConstants.BUTTON_OPEN).click();

		return this.bot.viewByTitle(viewTitle);
	}

	private void clearShowViewDialog() {
		final SWTBotShell activeShell = this.bot.activeShell();
		if (WorkbenchConstants.SHOW_VIEW_TITLE.equals(activeShell.getText())) {
			activeShell.close();
		}
	}

	protected IProject createMavenProjectViaDialog(MavenGav gav) {
		addToTearDown(this::clearNewProjectDialog); // just in case

		this.bot.menu(WorkbenchConstants.MENU_FILE).menu(WorkbenchConstants.SUB_MENU_NEW)
				.menu(WorkbenchConstants.COMMAND_OTHER).click();
		this.bot.waitUntil(Conditions.shellIsActive(NewProjectConstants.DIALOG_TITLE));

		this.bot.tree().getTreeItem(NewProjectConstants.GROUP_MAVEN).expand();
		this.bot.tree().getTreeItem(NewProjectConstants.GROUP_MAVEN).getNode(NewProjectConstants.PROJECT_MAVEN)
				.select();

		this.bot.button(CommonConstants.BUTTON_NEXT).click();

		this.bot.checkBox(NewProjectConstants.MAVEN_SIMPLE_PROJECT).select();

		this.bot.button(CommonConstants.BUTTON_NEXT).click();

		this.bot.comboBoxWithLabelInGroup(NewProjectConstants.MAVEN_GROUP_ID, NewProjectConstants.MAVEN_GROUP_ARTIFACT)
				.setText(gav.groupId);
		this.bot.comboBoxWithLabelInGroup(NewProjectConstants.MAVEN_ARTIFACT_ID,
				NewProjectConstants.MAVEN_GROUP_ARTIFACT).setText(gav.artifactId);
		this.bot.comboBoxWithLabelInGroup(NewProjectConstants.MAVEN_VERSION, NewProjectConstants.MAVEN_GROUP_ARTIFACT)
				.setText(gav.version);
		this.bot.comboBoxWithLabelInGroup(NewProjectConstants.MAVEN_TYPE, NewProjectConstants.MAVEN_GROUP_ARTIFACT)
				.setText(gav.type);

		this.bot.button(CommonConstants.BUTTON_FINISH).click();

		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IProject project = root.getProject(gav.artifactId);
		addProjectDeletionToTearDown(project);

		clearDiscoverM2ConnectorsDialogIfNecessary();

		return project;
	}

	protected IProject[] createMavenProjectWithModulesViaDialog(MavenGav gav, String... modules) {
		final List<IProject> result = new ArrayList<>();

		final IProject parentProject = createMavenProjectViaDialog(gav.type("pom"));
		result.add(parentProject);

		final SWTBotView projectExplorer = getProjectExplorerView();

		for (final String module : modules) {
			projectExplorer.bot().tree().getTreeItem(parentProject.getName()).select();

			this.bot.menu(WorkbenchConstants.MENU_FILE).menu(WorkbenchConstants.SUB_MENU_NEW)
					.menu(WorkbenchConstants.COMMAND_OTHER).click();
			this.bot.waitUntil(Conditions.shellIsActive(NewProjectConstants.DIALOG_TITLE));

			this.bot.tree().getTreeItem(NewProjectConstants.GROUP_MAVEN).expand();
			this.bot.tree().getTreeItem(NewProjectConstants.GROUP_MAVEN)
					.getNode(NewProjectConstants.PROJECT_MAVEN_MODULE).select();

			this.bot.button(CommonConstants.BUTTON_NEXT).click();

			this.bot.checkBox(NewProjectConstants.MAVEN_SIMPLE_PROJECT).select();
			this.bot.comboBoxWithLabel(NewProjectConstants.MAVEN_MODULE_NAME).setText(module);

			this.bot.button(CommonConstants.BUTTON_FINISH).click();

			final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			final IProject project = root.getProject(module);
			addProjectDeletionToTearDown(project);
			result.add(project);

			clearDiscoverM2ConnectorsDialogIfNecessary();
		}

		return result.toArray(new IProject[result.size()]);
	}

	private void addProjectDeletionToTearDown(final IProject project) {
		addToTearDown(() -> {
			try {
				project.delete(false, false, null);
			} catch (final CoreException e) {
				System.err.println(e.getMessage()); // ignore
			}
		});
	}

	protected SWTBotView openConsoleView() {
		return openViewProgrammatically(WorkbenchConstants.VIEW_CONSOLE_ID);
	}

	protected SWTBotView getProjectExplorerView() {
		try {
			return this.bot.viewByTitle(PROJECT_VIEW);
		} catch (final TimeoutException ignoredException) {
			return openViewViaDialog(WorkbenchConstants.GROUP_GENERAL, PROJECT_VIEW);
		}
	}

	protected SWTBotView openViewProgrammatically(String viewId) {
		final IViewReference[] view = {null};
		Display.getDefault().syncExec(() -> {
			try {
				final IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				activePage.showView(viewId);
				view[0] = activePage.findViewReference(viewId);
			} catch (final PartInitException e) {
				Assert.fail(e.getMessage());
			}
		});
		Assert.assertNotNull("Could not open view " + viewId);
		return new SWTBotView(view[0], this.bot);
	}

	private void clearNewProjectDialog() {
		final SWTBotShell activeShell = this.bot.activeShell();
		if (NewProjectConstants.MAVEN_DISCOVER_M2E_CONNECTORS.equals(activeShell.getText())) {
			activeShell.close();
		}
	}

	private void clearDiscoverM2ConnectorsDialogIfNecessary() {
		final long originalTimeout = SWTBotPreferences.TIMEOUT;
		SWTBotPreferences.TIMEOUT = 2000;
		try {
			this.bot.waitUntil(Conditions.shellIsActive(NewProjectConstants.MAVEN_DISCOVER_M2E_CONNECTORS));
			clearDiscoverM2ConnectorsDialog();
		} catch (final TimeoutException ignoredException) {
			// ignore
		} finally {
			SWTBotPreferences.TIMEOUT = originalTimeout;
		}
	}

	private void clearDiscoverM2ConnectorsDialog() {
		final SWTBotShell activeShell = this.bot.activeShell();
		if (NewProjectConstants.MAVEN_DISCOVER_M2E_CONNECTORS.equals(activeShell.getText())) {
			activeShell.close();
		}
	}

	protected SWTBotShell openPreferences() {
		addToTearDown(this::clearPreferences);

		this.bot.menu(WorkbenchConstants.MENU_WINDOW).menu(WorkbenchConstants.COMMAND_PREFERENCES).click();
		this.bot.waitUntil(Conditions.shellIsActive(PreferencesConstants.TITLE));

		this.bot.tree().getTreeItem(PreferencesConstants.PREFERENCES_MAVEN).expand();
		this.bot.tree().getTreeItem(PreferencesConstants.PREFERENCES_MAVEN)
				.getNode(PreferencesConstants.PREFERENCES_RUNS_VIEW).select();

		return this.bot.activeShell();
	}

	private void clearPreferences() {
		final SWTBotShell activeShell = this.bot.activeShell();
		if (PreferencesConstants.TITLE.equals(activeShell.getText())) {
			activeShell.close();
		}
	}

	protected void printControls(Composite parent, Predicate<Widget> tester) {
		printControls(parent, tester, 0);
	}

	protected void printControls(Composite parent, Predicate<Widget> tester, int indent) {
		for (final Control child : parent.getChildren()) {
			if (child instanceof Composite) {
				printControls((Composite) child, tester, indent + 1);
			} else if (tester.test(child)) {
				final String indentString = new String(new char[indent]).replace("\0", "  ");
				System.out.println(indentString + child);
			}
		}
	}

}