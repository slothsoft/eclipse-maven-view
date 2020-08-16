package de.slothsoft.mavenview.testplan;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotWorkbenchPart;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import de.slothsoft.mavenview.testplan.constants.CommonConstants;
import de.slothsoft.mavenview.testplan.constants.MavenViewConstants;
import de.slothsoft.mavenview.testplan.data.WorkbenchView;

public abstract class AbstractMavenViewTest {

	static {
		System.setProperty("org.eclipse.swtbot.search.defaultKey", CommonConstants.DATA_ID);
	}

	protected final SWTWorkbenchBot bot = new SWTWorkbenchBot();
	private final List<Runnable> tearDowns = new ArrayList<>();

	@Before
	public void setUpShell() {
		UIThreadRunnable.syncExec(new VoidResult() {

			@Override
			public void run() {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().forceActive();
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

	protected void clickToolbarButton(SWTBotWorkbenchPart part, String commandTooltip) {
		part.show();
		Assert.assertTrue("Part should be active!", part.isActive());
		part.toolbarButton(commandTooltip).click();
	}

	protected SWTBotView openMavenViewViaDialog() {
		final SWTBotView result = WorkbenchView.openViewViaDialog(this.bot, MavenViewConstants.VIEW_GROUP,
				MavenViewConstants.VIEW_TITLE);
		addToTearDown(result::close);
		return result;
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