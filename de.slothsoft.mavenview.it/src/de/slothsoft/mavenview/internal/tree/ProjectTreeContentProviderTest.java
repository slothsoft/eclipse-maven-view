package de.slothsoft.mavenview.internal.tree;

import java.util.Arrays;

import org.eclipse.core.resources.IProject;

import de.slothsoft.mavenview.AbstractFetchMavenProjectsTest;

public class ProjectTreeContentProviderTest extends AbstractFetchMavenProjectsTest {

	@Override
	protected IProject[] fetchMavenProjects() {
		return Arrays.stream(ProjectTreeContentProvider.fetchMavenProjects()).map(ProjectNode::getProjectResource)
				.toArray(IProject[]::new);
	}

	@Override
	protected boolean areNestedProjectsFound() {
		return false;
	}

}
