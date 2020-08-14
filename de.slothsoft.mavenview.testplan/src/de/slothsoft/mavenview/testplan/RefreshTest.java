package de.slothsoft.mavenview.testplan;

import org.eclipse.core.resources.IProject;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.slothsoft.mavenview.testplan.constants.MavenViewConstants;

@RunWith(SWTBotJunit4ClassRunner.class)
public class RefreshTest extends AbstractMavenViewTest {

	@Test
	public void testR01_RefreshView() throws Exception {
		final SWTBotView view = openMavenViewViaDialog();

		final IProject project = createMavenProjectViaDialog(new MavenGav());

		view.toolbarButton(MavenViewConstants.COMMAND_REFRESH).click();

		final SWTBotTree mavenProjectTree = view.bot().tree();
		Assert.assertEquals(1, mavenProjectTree.getAllItems().length);
		Assert.assertNotNull(mavenProjectTree.getTreeItem(project.getName()));
	}

}