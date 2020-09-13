package de.slothsoft.mavenview.testplan;

import java.util.UUID;

import org.eclipse.core.resources.IProject;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.slothsoft.mavenview.MavenRunConfig;
import de.slothsoft.mavenview.Phase;
import de.slothsoft.mavenview.testplan.constants.MavenViewConstants;
import de.slothsoft.mavenview.testplan.data.MavenGav;
import de.slothsoft.mavenview.testplan.data.ProjectFactory;
import de.slothsoft.mavenview.testplan.data.WorkbenchView;

@RunWith(SWTBotJunit4ClassRunner.class)
public class RefreshTest extends AbstractMavenViewTest {

	private ProjectFactory projectFactory;
	private SWTBotView mavenView;

	@Before
	public void setUp() {
		this.projectFactory = new ProjectFactory(this.bot);
		addToTearDown(this.projectFactory::dispose);

		this.mavenView = WorkbenchView.MAVEN.openProgrammatically(this.bot);
	}

	@After
	public void closeMavenView() {
		this.mavenView.close();
	}

	@Test
	public void testR01_RefreshView() throws Exception {
		final IProject project = this.projectFactory.createMavenProjectViaDialog(new MavenGav());

		System.out.println("RefreshTest.testR01_RefreshView(A)");
		clickToolbarButton(this.mavenView, MavenViewConstants.COMMAND_REFRESH);
		System.out.println("RefreshTest.testR01_RefreshView(B)");

		final SWTBotTree mavenProjectTree = this.mavenView.bot().tree();
		Assert.assertEquals(1, mavenProjectTree.getAllItems().length);
		Assert.assertNotNull(mavenProjectTree.getTreeItem(project.getName()));
	}

	@Test
	public void testR02_RefreshViewForLaunchConfig() throws Exception {
		final IProject project = this.projectFactory.createMavenProjectViaDialog(new MavenGav());
		final String mavenLaunchConfigName = UUID.randomUUID().toString();
		this.projectFactory.createMavenLaunchConfig(project, mavenLaunchConfigName,
				new MavenRunConfig().phases(Phase.CLEAN, Phase.INSTALL));

		System.out.println("RefreshTest.testR01_RefreshView(A)");
		clickToolbarButton(this.mavenView, MavenViewConstants.COMMAND_REFRESH);
		System.out.println("RefreshTest.testR01_RefreshView(B)");

		final SWTBotTree mavenProjectTree = this.mavenView.bot().tree();
		Assert.assertEquals(1, mavenProjectTree.getAllItems().length);

		final SWTBotTreeItem projectItem = mavenProjectTree.getTreeItem(project.getName());
		Assert.assertNotNull(projectItem);
		Assert.assertEquals(2, projectItem.getNodes().size());
		Assert.assertNotNull(projectItem.getNode(mavenLaunchConfigName));
	}

}