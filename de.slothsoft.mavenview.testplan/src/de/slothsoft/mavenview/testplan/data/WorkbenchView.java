package de.slothsoft.mavenview.testplan.data;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.junit.Assert;

import de.slothsoft.mavenview.testplan.constants.CommonConstants;
import de.slothsoft.mavenview.testplan.constants.MavenViewConstants;
import de.slothsoft.mavenview.testplan.constants.WorkbenchConstants;

public enum WorkbenchView {
	CONSOLE {

		@Override
		public SWTBotView open(SWTWorkbenchBot bot) {
			return openViewProgrammatically(bot, WorkbenchConstants.VIEW_CONSOLE_ID);
		}
	},

	PROJECT_EXPLORER {
		@Override
		public SWTBotView open(SWTWorkbenchBot bot) {
			return getOrOpenViewViaDialog(bot, WorkbenchConstants.GROUP_GENERAL,
					WorkbenchConstants.VIEW_PROJECT_EXPLORER);
		}
	},

	;

	public abstract SWTBotView open(SWTWorkbenchBot bot);

	public static SWTBotView openViewProgrammatically(SWTWorkbenchBot bot, String viewId) {
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
		return new SWTBotView(view[0], bot);
	}

	public static SWTBotView getOrOpenViewViaDialog(SWTWorkbenchBot bot, String viewGroup, String viewTitle) {
		try {
			return bot.viewByTitle(viewTitle);
		} catch (final TimeoutException | WidgetNotFoundException ignoredException) {
			return openViewViaDialog(bot, viewGroup, viewTitle);
		}
	}

	public static SWTBotView openViewViaDialog(SWTWorkbenchBot bot, String viewGroup, String viewTitle) {
		bot.menu(WorkbenchConstants.MENU_WINDOW).menu(WorkbenchConstants.SUB_MENU_SHOW_VIEW)
				.menu(WorkbenchConstants.COMMAND_OTHER).click();

		bot.waitUntil(Conditions.shellIsActive(WorkbenchConstants.SHOW_VIEW_TITLE));

		bot.text().setText(MavenViewConstants.VIEW_TITLE);

		// dunno why we have to expand, but it's necessary else the title is not found
		bot.tree().getTreeItem(viewGroup).expand();
		bot.tree().getTreeItem(viewGroup).getNode(viewTitle).select();

		bot.button(CommonConstants.BUTTON_OPEN).click();

		return bot.viewByTitle(viewTitle);
	}
}
