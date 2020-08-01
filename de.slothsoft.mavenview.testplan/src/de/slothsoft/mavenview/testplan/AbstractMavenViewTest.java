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
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;

import de.slothsoft.mavenview.MavenView;
import de.slothsoft.mavenview.testplan.constants.Common;
import de.slothsoft.mavenview.testplan.constants.MainWindow;
import de.slothsoft.mavenview.testplan.constants.NewProject;

public abstract class AbstractMavenViewTest {

	static {
		System.setProperty("org.eclipse.swtbot.search.defaultKey", Common.DATA_ID);
	}

	protected final SWTWorkbenchBot bot = new SWTWorkbenchBot();
	private final List<Runnable> tearDowns = new ArrayList<>();

	@Before
	public final void closeAllMavenViews() {
		Display.getDefault().asyncExec(() -> {
			final IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			for (final IViewReference view : activePage.getViewReferences()) {
				if (view.getId().equals(MavenView.ID)) {
					activePage.hideView(view);
				}
			}
		});
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

	protected SWTBotView openMavenViewWithShowViewDialog() {
		addToTearDown(this::clearShowViewDialog); // just in case

		this.bot.menu(MainWindow.MENU_WINDOW).menu(MainWindow.SUB_MENU_SHOW_VIEW).menu(MainWindow.COMMAND_OTHER)
				.click();

		this.bot.waitUntil(Conditions.shellIsActive(MainWindow.SHOW_VIEW_TITLE));

		this.bot.text().setText(MainWindow.VIEW_TITLE);

		// dunno why we have to expand, but it's necessary else the title is not found
		this.bot.tree().getTreeItem(MainWindow.VIEW_GROUP).expand();
		this.bot.tree().getTreeItem(MainWindow.VIEW_GROUP).getNode(MainWindow.VIEW_TITLE).select();

		this.bot.button(Common.BUTTON_OPEN).click();

		final SWTBotView result = this.bot.viewByTitle(MainWindow.VIEW_TITLE);
		addToTearDown(result::close);
		return result;
	}

	private void clearShowViewDialog() {
		final SWTBotShell activeShell = this.bot.activeShell();
		if (MainWindow.SHOW_VIEW_TITLE.equals(activeShell.getText())) {
			activeShell.close();
		}
	}

	protected IProject createMavenProject(String groupId, String artifactId, String version) {
		addToTearDown(this::clearNewProjectDialog); // just in case

		this.bot.menu(MainWindow.MENU_FILE).menu(MainWindow.SUB_MENU_NEW).menu(MainWindow.COMMAND_OTHER).click();
		this.bot.waitUntil(Conditions.shellIsActive(NewProject.DIALOG_TITLE));

		this.bot.tree().getTreeItem(NewProject.GROUP_MAVEN).expand();
		this.bot.tree().getTreeItem(NewProject.GROUP_MAVEN).getNode(NewProject.PROJECT_MAVEN).select();

		this.bot.button(Common.BUTTON_NEXT).click();

		this.bot.checkBox(NewProject.MAVEN_SIMPLE_PROJECT).select();

		this.bot.button(Common.BUTTON_NEXT).click();

		this.bot.comboBoxWithLabelInGroup(NewProject.MAVEN_GROUP_ID, NewProject.MAVEN_GROUP_ARTIFACT).setText(groupId);
		this.bot.comboBoxWithLabelInGroup(NewProject.MAVEN_ARTIFACT_ID, NewProject.MAVEN_GROUP_ARTIFACT)
				.setText(artifactId);
		this.bot.comboBoxWithLabelInGroup(NewProject.MAVEN_VERSION, NewProject.MAVEN_GROUP_ARTIFACT).setText(version);

		this.bot.button(Common.BUTTON_FINISH).click();

		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IProject project = root.getProject(artifactId);
		addToTearDown(() -> {
			try {
				project.delete(true, null);
			} catch (final CoreException e) {
				e.printStackTrace(); // ignore
			}
		});

		this.bot.waitUntil(Conditions.shellIsActive(NewProject.MAVEN_DISCOVER_M2E_CONNECTORS));
		clearDiscoverM2ConnectorsDialog();

		return project;
	}

	private void clearNewProjectDialog() {
		final SWTBotShell activeShell = this.bot.activeShell();
		if (NewProject.MAVEN_DISCOVER_M2E_CONNECTORS.equals(activeShell.getText())) {
			activeShell.close();
		}
	}

	private void clearDiscoverM2ConnectorsDialog() {
		final SWTBotShell activeShell = this.bot.activeShell();
		if (NewProject.MAVEN_DISCOVER_M2E_CONNECTORS.equals(activeShell.getText())) {
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