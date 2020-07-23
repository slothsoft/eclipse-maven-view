package de.slothsoft.mavenview;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import de.slothsoft.mavenview.internal.tree.ProjectTreeContentProvider;
import de.slothsoft.mavenview.internal.tree.ProjectTreeLabelProvider;

public class MavenView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "de.slothsoft.mavenview.MavenView";

	private TreeViewer viewer;

	@Override
	public void createPartControl(Composite parent) {
		this.viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		this.viewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);
		this.viewer.setContentProvider(new ProjectTreeContentProvider());
		this.viewer.setInput(ProjectTreeContentProvider.fetchMavenProjects());
		this.viewer.setLabelProvider(new ProjectTreeLabelProvider());
		this.viewer.expandAll();

		getSite().setSelectionProvider(this.viewer);
	}

	@Override
	public void setFocus() {
		this.viewer.getControl().setFocus();
	}
}
