package de.slothsoft.mavenview;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

public enum InitialProjectSelection {
	ALL_PROJECTS {

		@Override
		public IProject[] fetchMavenProjects() {
			return collectMavenProjects(project -> true);
		}

	},

	ROOT_PROJECTS {

		@Override
		public IProject[] fetchMavenProjects() {
			final List<IPath> roots = fetchAllRoots();
			return collectMavenProjects(project -> {
				return roots.contains(project.getProject().getLocation());
			});
		}

		List<IPath> fetchAllRoots() {
			final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
			final IProject[] projects = workspaceRoot.getProjects();

			final List<IPath> roots = new ArrayList<>(projects.length);
			for (int i = 0; i < projects.length; i++) {
				final IPath possibleRoot = projects[i].getLocation();

				boolean consumed = false;
				int rootSize = roots.size();
				for (int rootIndex = 0; rootIndex < rootSize; rootIndex++) {
					final IPath root = roots.get(rootIndex);

					if (possibleRoot.isPrefixOf(root)) {
						// possibleRoot is below the root
						roots.remove(rootIndex);
						if (!consumed) {
							// possibleRoot can be the root of other paths as well
							roots.add(rootIndex, possibleRoot);
							consumed = true;
						} else {
							rootIndex--;
							rootSize--;
						}
					} else if (root.isPrefixOf(possibleRoot)) {
						// root is below the possibleRoot
						consumed = true;
						break;
					}
				}

				if (!consumed) {
					// possibleRoot is completely new
					roots.add(possibleRoot);
				}
			}

			return roots;
		}
	};

	static final String MAVEN_NATURE = "org.eclipse.m2e.core.maven2Nature";

	public abstract IProject[] fetchMavenProjects();

	static IProject[] collectMavenProjects(Predicate<IProject> tester) {
		final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		final IProject[] projects = workspaceRoot.getProjects();

		final List<IProject> result = new ArrayList<>();
		for (int i = 0; i < projects.length; i++) {
			final IProject project = projects[i];
			try {
				if (isMavenProject(project) && tester.test(project)) {
					result.add(project);
				}
			} catch (final CoreException e) {
				// we'll ignore this case
			}
		}
		return result.toArray(new IProject[result.size()]);
	}

	static boolean isMavenProject(IProject project) throws CoreException {
		return project.isOpen() && project.hasNature(MAVEN_NATURE);
	}

}