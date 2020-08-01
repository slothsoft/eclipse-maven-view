package de.slothsoft.mavenview.testplan;

import org.eclipse.core.resources.IProject;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.slothsoft.mavenview.testplan.constants.MavenView;

@RunWith(SWTBotJunit4ClassRunner.class)
public class RefreshTest extends AbstractMavenViewTest {

	@Test
	public void testR01_RefreshView() throws Exception {
		final SWTBotView view = openMavenViewWithShowViewDialog();

		final IProject project = createMavenProject("groupId", "artifactId", "1.2.3");

		view.toolbarButton(MavenView.COMMAND_REFRESH).click();

		final SWTBotTree mavenProjectTree = view.bot().tree();
		Assert.assertEquals(1, mavenProjectTree.getAllItems().length);
		Assert.assertNotNull(mavenProjectTree.getTreeItem(project.getName()));
	}

}