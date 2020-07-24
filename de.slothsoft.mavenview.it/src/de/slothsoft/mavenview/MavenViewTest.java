package de.slothsoft.mavenview;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MavenViewTest {

	private IWorkbenchPage activePage;

	@Before
	public void setUp() {
		this.activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		closeAllViews();
	}

	private void closeAllViews() {
		for (final IViewReference view : this.activePage.getViewReferences()) {
			this.activePage.hideView(view);
		}
	}

	@After
	public void tearDown() {
		closeAllViews();
	}

	@Test
	public void testOpenView() throws Exception {
		final IViewPart view = this.activePage.showView(MavenView.ID);

		Assert.assertTrue("Wrong view: " + view, view instanceof MavenView);
	}
}
