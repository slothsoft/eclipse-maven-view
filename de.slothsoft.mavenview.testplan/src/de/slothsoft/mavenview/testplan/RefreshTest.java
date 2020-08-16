package de.slothsoft.mavenview.testplan;

import java.util.UUID;

import org.eclipse.core.resources.IProject;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.slothsoft.mavenview.MavenRunConfig;
import de.slothsoft.mavenview.Phase;
import de.slothsoft.mavenview.testplan.constants.MavenViewConstants;
import de.slothsoft.mavenview.testplan.data.MavenGav;
import de.slothsoft.mavenview.testplan.data.ProjectFactory;

@RunWith(SWTBotJunit4ClassRunner.class)
public class RefreshTest extends AbstractMavenViewTest {

	private ProjectFactory projectFactory;

	@Before
	public void setUp() {
		this.projectFactory = new ProjectFactory(this.bot);
		addToTearDown(this.projectFactory::dispose);
	}

	@Test
	public void testR01_RefreshView() throws Exception {
		final SWTBotView view = openMavenViewViaDialog();

		final IProject project = this.projectFactory.createMavenProjectViaDialog(new MavenGav());

		view.show();
		view.toolbarButton(MavenViewConstants.COMMAND_REFRESH).click();

		final SWTBotTree mavenProjectTree = view.bot().tree();
		Assert.assertEquals(1, mavenProjectTree.getAllItems().length);
		Assert.assertNotNull(mavenProjectTree.getTreeItem(project.getName()));
	}

	@Test
	public void testR02_RefreshViewForLaunchConfig() throws Exception {
		final SWTBotView view = openMavenViewViaDialog();

		final IProject project = this.projectFactory.createMavenProjectViaDialog(new MavenGav());
		final String mavenLaunchConfigName = UUID.randomUUID().toString();
		this.projectFactory.createMavenLaunchConfig(project, mavenLaunchConfigName,
				new MavenRunConfig().phases(Phase.CLEAN, Phase.INSTALL));

		view.show();
		view.toolbarButton(MavenViewConstants.COMMAND_REFRESH).click();

		final SWTBotTree mavenProjectTree = view.bot().tree();
		Assert.assertEquals(1, mavenProjectTree.getAllItems().length);

		final SWTBotTreeItem projectItem = mavenProjectTree.getTreeItem(project.getName());
		Assert.assertNotNull(projectItem);
		Assert.assertEquals(2, projectItem.getNodes().size());
		Assert.assertNotNull(projectItem.getNode(mavenLaunchConfigName));
	}

}