package de.slothsoft.mavenview.internal.tree;

import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.junit.Before;

import de.slothsoft.mavenview.InitialProjectSelection;
import de.slothsoft.mavenview.InitialProjectSelectionAllProjectsTest;
import de.slothsoft.mavenview.MavenViewPreferences;

public class ProjectTreeContentProviderAllProjectsTest extends InitialProjectSelectionAllProjectsTest {

	@Before
	@Override
	public void setUp() throws CoreException {
		super.setUp();
		MavenViewPreferences.setInitialProjectSelection(InitialProjectSelection.ALL_PROJECTS);
	}

	@Override
	protected IProject[] fetchMavenProjects() {
		return Arrays.stream(ProjectTreeContentProvider.fetchMavenProjects()).map(ProjectNode::getProjectResource)
				.toArray(IProject[]::new);
	}

}
