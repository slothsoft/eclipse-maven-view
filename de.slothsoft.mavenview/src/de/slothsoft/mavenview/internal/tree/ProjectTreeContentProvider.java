package de.slothsoft.mavenview.internal.tree;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeContentProvider;

import de.slothsoft.mavenview.InitialProjectSelection;

public class ProjectTreeContentProvider implements ITreeContentProvider {

	public static ProjectNode[] fetchMavenProjects() {
		final IProject[] projects = InitialProjectSelection.ROOT_PROJECTS.fetchMavenProjects();
		final ProjectNode[] result = new ProjectNode[projects.length];

		for (int i = 0; i < projects.length; i++) {
			result[i] = new ProjectNode(projects[i]);
		}
		return result;
	}

	@Override
	public Object[] getElements(Object parent) {
		return (ProjectNode[]) parent;
	}

	@Override
	public Object[] getChildren(Object parent) {
		return parent instanceof Parentable ? ((Parentable) parent).getChildren() : new Object[0];
	}

	@Override
	public boolean hasChildren(Object parent) {
		return parent instanceof Parentable;
	}

	@Override
	public Object getParent(Object child) {
		return null;
	}

}