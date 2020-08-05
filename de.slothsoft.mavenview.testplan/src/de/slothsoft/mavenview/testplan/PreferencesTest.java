package de.slothsoft.mavenview.testplan;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.eclipse.core.resources.IProject;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.slothsoft.mavenview.MavenViewPreferences;
import de.slothsoft.mavenview.Phase;
import de.slothsoft.mavenview.testplan.constants.CommonConstants;
import de.slothsoft.mavenview.testplan.constants.PreferencesConstants;

@RunWith(SWTBotJunit4ClassRunner.class)
public class PreferencesTest extends AbstractMavenViewTest {

	@Test
	public void testP01A_InitialProjectSelectionAll() throws Exception {

		final IProject[] projects = createMavenProjectWithModules(new MavenGav(), UUID.randomUUID().toString());

		final SWTBotShell preferenceShell = openPreferences();

		final SWTBotCombo combo = preferenceShell.bot().comboBoxWithId(MavenViewPreferences.INITIAL_PROJECT_SELECTION);
		combo.setSelection(PreferencesConstants.INITIAL_PROJECT_SELECTION_ALL);

		preferenceShell.bot().button(PreferencesConstants.BUTTON_APPLY_AND_CLOSE).click();

		final SWTBotView view = openMavenViewWithShowViewDialog();
		final SWTBotTree viewTree = view.bot().tree();

		Assert.assertEquals(2, viewTree.getAllItems().length);
		viewTree.getTreeItem(projects[0].getName()).expand();
		viewTree.getTreeItem(projects[1].getName()).expand();
	}

	@Test
	public void testP01B_InitialProjectSelectionRoot() throws Exception {

		final IProject[] projects = createMavenProjectWithModules(new MavenGav(), UUID.randomUUID().toString());

		final SWTBotView view = openMavenViewWithShowViewDialog();
		final SWTBotTree viewTree = view.bot().tree();

		final SWTBotShell preferenceShell = openPreferences();

		final SWTBotCombo combo = preferenceShell.bot().comboBoxWithId(MavenViewPreferences.INITIAL_PROJECT_SELECTION);
		combo.setSelection(PreferencesConstants.INITIAL_PROJECT_SELECTION_ROOT);

		preferenceShell.bot().button(CommonConstants.BUTTON_APPLY).click();
		preferenceShell.bot().button(CommonConstants.BUTTON_CANCEL).click();

		Assert.assertEquals(1, viewTree.getAllItems().length);
		viewTree.getTreeItem(projects[0].getName()).expand();
	}

	@Test
	public void testP02A_DisplayedPhases1() throws Exception {

		final IProject project = createMavenProject(new MavenGav());

		final SWTBotShell preferenceShell = openPreferences();

		final SWTBotTable table = preferenceShell.bot().tableWithId(MavenViewPreferences.DISPLAYED_PHASES);
		checkOnlyTableItems(table, "install");

		preferenceShell.bot().button(PreferencesConstants.BUTTON_APPLY_AND_CLOSE).click();

		final SWTBotView view = openMavenViewWithShowViewDialog();
		final SWTBotTree viewTree = view.bot().tree();

		Assert.assertEquals(1, viewTree.getAllItems().length);
		final SWTBotTreeItem projectItem = viewTree.getTreeItem(project.getName());
		projectItem.expand();

		Assert.assertEquals(1, projectItem.getItems().length);
		Assert.assertEquals("install", projectItem.getItems()[0].getText());
	}

	private static void checkOnlyTableItems(SWTBotTable table, String... items) {
		final Set<String> itemsAsSet = new TreeSet<>(Arrays.asList(items));

		table.widget.getDisplay().syncExec(() -> {
			final int rowCount = table.widget.getItemCount();
			for (int i = 0; i < rowCount; i++) {
				final SWTBotTableItem item = table.getTableItem(i);
				if (itemsAsSet.contains(item.getText())) {
					item.check();
				} else {
					item.uncheck();
				}
			}
		});
	}

	@Test
	public void testP02B_DisplayedPhases1() throws Exception {

		final IProject project = createMavenProject(new MavenGav());

		final SWTBotView view = openMavenViewWithShowViewDialog();
		final SWTBotTree viewTree = view.bot().tree();

		final SWTBotShell preferenceShell = openPreferences();

		final SWTBotTable table = preferenceShell.bot().tableWithId(MavenViewPreferences.DISPLAYED_PHASES);
		checkOnlyTableItems(table, "site", "site-deploy");

		preferenceShell.bot().button(CommonConstants.BUTTON_APPLY).click();
		preferenceShell.bot().button(CommonConstants.BUTTON_CANCEL).click();

		Assert.assertEquals(1, viewTree.getAllItems().length);
		final SWTBotTreeItem projectItem = viewTree.getTreeItem(project.getName());
		projectItem.expand();

		Assert.assertEquals(2, projectItem.getItems().length);
		Assert.assertEquals("site", projectItem.getItems()[0].getText());
		Assert.assertEquals("site-deploy", projectItem.getItems()[1].getText());
	}

	@Test
	public void testP09_PreferencesDefault() throws Exception {

		final IProject[] projects = createMavenProjectWithModules(new MavenGav(), UUID.randomUUID().toString());

		final SWTBotView view = openMavenViewWithShowViewDialog();
		final SWTBotTree viewTree = view.bot().tree();

		final SWTBotShell preferenceShell = openPreferences();

		preferenceShell.bot().button(PreferencesConstants.BUTTON_RESTORE_DEFAULTS).click();
		preferenceShell.bot().button(PreferencesConstants.BUTTON_APPLY_AND_CLOSE).click();

		Assert.assertEquals(1, viewTree.getAllItems().length);
		final SWTBotTreeItem projectItem = viewTree.getTreeItem(projects[0].getName());
		projectItem.expand();

		final Phase[] phases = Phase.values();
		Assert.assertEquals(phases.length, projectItem.getItems().length);

		for (int i = 0; i < phases.length; i++) {
			Assert.assertEquals(phases[i].getDisplayName(), projectItem.getItems()[i].getText());
		}
	}
}