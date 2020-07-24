package de.slothsoft.mavenview.internal.tree;

import java.util.Objects;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.slothsoft.mavenview.Displayable;

public class ProjectNode implements Displayable, Parentable {

	private final IProject project;

	public ProjectNode(IProject project) {
		this.project = Objects.requireNonNull(project);
	}

	@Override
	public String getDisplayName() {
		return this.project.getName();
	}

	public IProject getProjectResource() {
		return this.project;
	}

	@Override
	@SuppressWarnings("deprecation")
	public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_PROJECT);
	}

	@Override
	public Object[] getChildren() {
		return PhaseNode.createAll(this);
	}

	@Override
	public int hashCode() {
		return 7 * Objects.hash(this.project);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;

		final ProjectNode that = (ProjectNode) obj;
		if (!Objects.equals(this.project.getName(), that.project.getName())) return false;
		return true;
	}
}
