package de.slothsoft.mavenview.internal.tree;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;

public class ProjectTreeContentProvider implements ITreeContentProvider {

	static final String MAVEN_NATURE = "org.eclipse.m2e.core.maven2Nature";

	public static ProjectNode[] fetchMavenProjects() {
		final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		final IProject[] projects = workspaceRoot.getProjects();

		final List<ProjectNode> result = new ArrayList<>();
		for (int i = 0; i < projects.length; i++) {
			final IProject project = projects[i];
			try {
				if (project.isOpen() && project.hasNature(MAVEN_NATURE)) {
					result.add(new ProjectNode(project));
				}
			} catch (final CoreException e) {
				// we'll ignore this case
			}
		}
		return result.toArray(new ProjectNode[result.size()]);
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