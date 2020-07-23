package de.slothsoft.mavenview.internal.tree;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class ProjectTreeContentProviderTest {

	static final String JAVA_NATURE = "org.eclipse.jdt.core.javanature";

	@Rule
	public TestName testName = new TestName();

	@Before
	public void setUp() throws CoreException {
		deleteAllProjects();
	}

	@After
	public void tearDown() throws CoreException {
		deleteAllProjects();
	}

	static void deleteAllProjects() throws CoreException {
		final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		for (final IProject project : workspaceRoot.getProjects()) {
			project.delete(true, new NullProgressMonitor());
		}
	}

	@Test
	public void testFetchMavenProjectsNoProject() throws Exception {
		final ProjectNode[] mavenProjects = ProjectTreeContentProvider.fetchMavenProjects();

		Assert.assertArrayEquals(new ProjectNode[0], mavenProjects);
	}

	@Test
	public void testFetchMavenProjectsNoMavenProject() throws Exception {
		createProject();

		final ProjectNode[] mavenProjects = ProjectTreeContentProvider.fetchMavenProjects();

		Assert.assertNotNull(mavenProjects);
		Assert.assertEquals(0, mavenProjects.length);
	}

	private IProject createProject() throws CoreException {
		return createProject(this.testName.getMethodName());
	}

	private static IProject createProject(String projectName) throws CoreException {
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IProject project = root.getProject(projectName);
		project.create(null);
		project.open(null);
		return project;
	}

	@Test
	public void testFetchMavenProjectsMavenProject() throws Exception {
		final IProject project = createProject();
		setProjectNature(project, ProjectTreeContentProvider.MAVEN_NATURE);

		final ProjectNode[] mavenProjects = ProjectTreeContentProvider.fetchMavenProjects();

		Assert.assertNotNull(mavenProjects);
		Assert.assertEquals(1, mavenProjects.length);
		Assert.assertEquals(project, mavenProjects[0].getProjectResource());
		Assert.assertEquals(project.getName(), mavenProjects[0].getDisplayName());
	}

	private static void setProjectNature(final IProject project, String... natures) throws CoreException {
		final IProjectDescription description = project.getDescription();
		description.setNatureIds(natures);
		project.setDescription(description, null);
	}

	@Test
	public void testFetchMavenProjectsMavenAndStuffProject() throws Exception {
		final IProject project = createProject();
		setProjectNature(project, JAVA_NATURE, ProjectTreeContentProvider.MAVEN_NATURE);

		final ProjectNode[] mavenProjects = ProjectTreeContentProvider.fetchMavenProjects();

		Assert.assertNotNull(mavenProjects);
		Assert.assertEquals(1, mavenProjects.length);
		Assert.assertEquals(project, mavenProjects[0].getProjectResource());
		Assert.assertEquals(project.getName(), mavenProjects[0].getDisplayName());
	}

	@Test
	public void testFetchMavenProjectsMultipleMavenProjects() throws Exception {
		final IProject mavenProject1 = createProject("1");
		setProjectNature(mavenProject1, ProjectTreeContentProvider.MAVEN_NATURE);

		final IProject mavenProject2 = createProject("2");
		setProjectNature(mavenProject2, ProjectTreeContentProvider.MAVEN_NATURE);

		createProject();

		final ProjectNode[] mavenProjects = ProjectTreeContentProvider.fetchMavenProjects();

		Assert.assertNotNull(mavenProjects);
		Assert.assertEquals(2, mavenProjects.length);
		Assert.assertEquals(mavenProject1, mavenProjects[0].getProjectResource());
		Assert.assertEquals(mavenProject1.getName(), mavenProjects[0].getDisplayName());

		Assert.assertEquals(mavenProject2, mavenProjects[1].getProjectResource());
		Assert.assertEquals(mavenProject2.getName(), mavenProjects[1].getDisplayName());
	}

	@Test
	public void testFetchMavenProjectsClosedMavenProject() throws Exception {
		final IProject project = createProject();
		setProjectNature(project, ProjectTreeContentProvider.MAVEN_NATURE);
		project.close(new NullProgressMonitor());

		final ProjectNode[] mavenProjects = ProjectTreeContentProvider.fetchMavenProjects();

		Assert.assertNotNull(mavenProjects);
		Assert.assertEquals(0, mavenProjects.length);
	}

}
