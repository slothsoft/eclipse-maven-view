package de.slothsoft.mavenview.testplan;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarDropDownButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.slothsoft.mavenview.Phase;
import de.slothsoft.mavenview.testplan.constants.MavenViewConstants;

@RunWith(SWTBotJunit4ClassRunner.class)
public class MavenTest extends AbstractMavenViewTest {

	private IProject project1;
	private IProject project2;
	private SWTBotView view;

	@Before
	public void setUp() {
		this.project1 = createMavenProjectViaDialog(new MavenGav());
		this.project2 = createMavenProjectViaDialog(new MavenGav());
		this.view = openMavenViewViaDialog();
	}

	@Test
	public void testM01_RunMavenBuild() throws Exception {
		final SWTBotTree mavenProjectTree = this.view.bot().tree();

		mavenProjectTree.getTreeItem(this.project1.getName()).expand();
		mavenProjectTree.getTreeItem(this.project1.getName()).getNode(Phase.CLEAN.getDisplayName()).select();

		this.view.toolbarButton(MavenViewConstants.COMMAND_RUN_MAVEN_BUILD).click();

		final SWTBotView consoleView = openConsoleView();
		final String consoleText = consoleView.bot().styledText().getText();

		Assert.assertTrue("Missing working directory: " + consoleText,
				consoleText.contains("Working Directory: " + this.project1.getLocation().toOSString()));
		Assert.assertTrue("Missing phases: " + consoleText, consoleText.contains("Phases: clean"));
	}

	@Test
	public void testM02_RunMavenBuildWithMultiplePhases() throws Exception {
		final SWTBotTree mavenProjectTree = this.view.bot().tree();

		mavenProjectTree.getTreeItem(this.project1.getName()).expand();

		final SWTBotTreeItem installItem = mavenProjectTree.getTreeItem(this.project1.getName())
				.getNode(Phase.INSTALL.getDisplayName());
		final SWTBotTreeItem testItem = mavenProjectTree.getTreeItem(this.project1.getName())
				.getNode(Phase.TEST.getDisplayName());
		mavenProjectTree.select(installItem, testItem);

		this.view.toolbarButton(MavenViewConstants.COMMAND_RUN_MAVEN_BUILD).click();

		final SWTBotView consoleView = openConsoleView();
		final String consoleText = consoleView.bot().styledText().getText();

		Assert.assertTrue("Missing phases: " + consoleText, consoleText.contains("Phases: test install"));
	}

	@Test
	public void testM03_RunMavenBuildWithMultipleProjects() throws Exception {
		final SWTBotTree mavenProjectTree = this.view.bot().tree();

		mavenProjectTree.getTreeItem(this.project1.getName()).expand();
		mavenProjectTree.getTreeItem(this.project2.getName()).expand();

		final SWTBotTreeItem compileItem = mavenProjectTree.getTreeItem(this.project1.getName())
				.getNode(Phase.COMPILE.getDisplayName());
		final SWTBotTreeItem verifyItem = mavenProjectTree.getTreeItem(this.project2.getName())
				.getNode(Phase.VERIFY.getDisplayName());
		mavenProjectTree.select(compileItem, verifyItem);

		this.view.toolbarButton(MavenViewConstants.COMMAND_RUN_MAVEN_BUILD).click();

		final SWTBotView consoleView = openConsoleView();
		String consoleText = consoleView.bot().styledText().getText();

		final String phasePattern = "(?s)(.*)Phases: (verify|compile)(.*)";
		Assert.assertTrue("Missing phases: " + consoleText, consoleText.matches(phasePattern));

		final SWTBotToolbarDropDownButton button = consoleView
				.toolbarDropDownButton(MavenViewConstants.COMMAND_DISPLAY_SELECTED_CONSOLE);

		button.menuItem(new BaseMatcher<MenuItem>() {

			@Override
			public boolean matches(Object item) {
				return !((MenuItem) item).getSelection();
			}

			@Override
			public void describeTo(Description description) {
				// nothing to do?
			}
		}).click();

		consoleText = consoleView.bot().styledText().getText();
		Assert.assertTrue("Missing phases: " + consoleText, consoleText.matches(phasePattern));
	}

}