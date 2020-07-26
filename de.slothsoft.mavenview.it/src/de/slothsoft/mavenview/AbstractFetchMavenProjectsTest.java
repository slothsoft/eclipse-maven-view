package de.slothsoft.mavenview;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public abstract class AbstractFetchMavenProjectsTest {

	protected static final String JAVA_NATURE = "org.eclipse.jdt.core.javanature";
	protected static final String MAVEN_NATURE = InitialProjectSelection.MAVEN_NATURE;

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

	protected static void deleteAllProjects() throws CoreException {
		final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		for (final IProject project : workspaceRoot.getProjects()) {
			project.delete(true, new NullProgressMonitor());
		}
	}

	protected IProject createProject() throws CoreException {
		return createProject(this.testName.getMethodName());
	}

	protected static IProject createProject(String projectName) throws CoreException {
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IProject project = root.getProject(projectName);
		project.create(null);
		project.open(null);
		return project;
	}

	@SuppressWarnings("deprecation")
	protected static IProject createProject(String projectName, IPath location) throws CoreException {
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IProject project = root.getProject(projectName);
		JavaCapabilityConfigurationPage.createProject(project, location, null);
		return project;
	}

	protected static void setProjectNatures(final IProject project, String... natures) throws CoreException {
		final IProjectDescription description = project.getDescription();
		description.setNatureIds(natures);
		project.setDescription(description, null);
	}

	// actual test cases

	@Test
	public void testFetchMavenProjectsNoProject() throws Exception {
		final IProject[] mavenProjects = fetchMavenProjects();

		Assert.assertArrayEquals(new IProject[0], mavenProjects);
	}

	protected abstract IProject[] fetchMavenProjects();

	@Test
	public void testFetchMavenProjectsNoMavenProject() throws Exception {
		createProject();

		final IProject[] mavenProjects = fetchMavenProjects();

		Assert.assertNotNull(mavenProjects);
		Assert.assertEquals(0, mavenProjects.length);
	}

	@Test
	public void testFetchMavenProjectsMavenProject() throws Exception {
		final IProject project = createProject();
		setProjectNatures(project, InitialProjectSelection.MAVEN_NATURE);

		final IProject[] mavenProjects = fetchMavenProjects();

		Assert.assertNotNull(mavenProjects);
		Assert.assertEquals(1, mavenProjects.length);
		Assert.assertEquals(project, mavenProjects[0]);
	}

	@Test
	public void testFetchMavenProjectsMavenAndStuffProject() throws Exception {
		final IProject project = createProject();
		setProjectNatures(project, JAVA_NATURE, InitialProjectSelection.MAVEN_NATURE);

		final IProject[] mavenProjects = fetchMavenProjects();

		Assert.assertNotNull(mavenProjects);
		Assert.assertEquals(1, mavenProjects.length);
		Assert.assertEquals(project, mavenProjects[0]);
	}

	@Test
	public void testFetchMavenProjectsMultipleMavenProjects() throws Exception {
		final IProject mavenProject1 = createProject("1");
		setProjectNatures(mavenProject1, InitialProjectSelection.MAVEN_NATURE);

		final IProject mavenProject2 = createProject("2");
		setProjectNatures(mavenProject2, InitialProjectSelection.MAVEN_NATURE);

		createProject();

		final IProject[] mavenProjects = fetchMavenProjects();

		Assert.assertNotNull(mavenProjects);
		Assert.assertEquals(2, mavenProjects.length);
		Assert.assertEquals(mavenProject1, mavenProjects[0]);
		Assert.assertEquals(mavenProject2, mavenProjects[1]);
	}

	@Test
	public void testFetchMavenProjectsClosedMavenProject() throws Exception {
		final IProject project = createProject();
		setProjectNatures(project, InitialProjectSelection.MAVEN_NATURE);
		project.close(new NullProgressMonitor());

		final IProject[] mavenProjects = fetchMavenProjects();

		Assert.assertNotNull(mavenProjects);
		Assert.assertEquals(0, mavenProjects.length);
	}

	@Test
	public void testFetchMavenProjectsNestedMavenProjects() throws Exception {
		final IProject mavenProject1 = createProject("1");
		setProjectNatures(mavenProject1, InitialProjectSelection.MAVEN_NATURE);

		final IProject mavenProject2 = createProject("2", mavenProject1.getLocation().append("2"));
		setProjectNatures(mavenProject2, InitialProjectSelection.MAVEN_NATURE);

		final IProject[] mavenProjects = fetchMavenProjects();

		Assert.assertNotNull(mavenProjects);

		if (areNestedProjectsFound()) {
			Assert.assertEquals(2, mavenProjects.length);
			Assert.assertEquals(mavenProject1, mavenProjects[0]);
			Assert.assertEquals(mavenProject2, mavenProjects[1]);
		} else {
			Assert.assertEquals(1, mavenProjects.length);
			Assert.assertEquals(mavenProject1, mavenProjects[0]);
		}
	}

	protected abstract boolean areNestedProjectsFound();

	@Test
	public void testFetchMavenProjectsNestedMavenProjectsSwitched() throws Exception {
		final IProject mavenProject1 = createProject("2");
		setProjectNatures(mavenProject1, InitialProjectSelection.MAVEN_NATURE);

		final IProject mavenProject2 = createProject("1", mavenProject1.getLocation().append("1"));
		setProjectNatures(mavenProject2, InitialProjectSelection.MAVEN_NATURE);

		final IProject[] mavenProjects = fetchMavenProjects();

		if (areNestedProjectsFound()) {
			Assert.assertEquals(2, mavenProjects.length);
			Assert.assertEquals(mavenProject2, mavenProjects[0]);
			Assert.assertEquals(mavenProject1, mavenProjects[1]);
		} else {
			Assert.assertEquals(1, mavenProjects.length);
			Assert.assertEquals(mavenProject1, mavenProjects[0]);
		}
	}

	@Test
	public void testFetchMavenProjectsMultiNestedMavenProjects() throws Exception {
		final IProject mavenProject1 = createProject("1");
		setProjectNatures(mavenProject1, InitialProjectSelection.MAVEN_NATURE);

		final IProject mavenProject2 = createProject("2", mavenProject1.getLocation().append("2"));
		setProjectNatures(mavenProject2, InitialProjectSelection.MAVEN_NATURE);

		final IProject mavenProject3 = createProject("3", mavenProject1.getLocation().append("3"));
		setProjectNatures(mavenProject3, InitialProjectSelection.MAVEN_NATURE);

		final IProject mavenProject4 = createProject("4", mavenProject3.getLocation().append("4"));
		setProjectNatures(mavenProject4, InitialProjectSelection.MAVEN_NATURE);

		final IProject[] mavenProjects = fetchMavenProjects();

		Assert.assertNotNull(mavenProjects);

		if (areNestedProjectsFound()) {
			Assert.assertEquals(4, mavenProjects.length);
			Assert.assertEquals(mavenProject1, mavenProjects[0]);
			Assert.assertEquals(mavenProject2, mavenProjects[1]);
			Assert.assertEquals(mavenProject3, mavenProjects[2]);
			Assert.assertEquals(mavenProject4, mavenProjects[3]);
		} else {
			Assert.assertEquals(1, mavenProjects.length);
			Assert.assertEquals(mavenProject1, mavenProjects[0]);
		}
	}

	@Test
	public void testFetchMavenProjectsMultiNestedMavenProjectsSwitched() throws Exception {
		final IProject mavenProject1 = createProject("4");
		setProjectNatures(mavenProject1, InitialProjectSelection.MAVEN_NATURE);

		final IProject mavenProject2 = createProject("3", mavenProject1.getLocation().append("3"));
		setProjectNatures(mavenProject2, InitialProjectSelection.MAVEN_NATURE);

		final IProject mavenProject3 = createProject("2", mavenProject1.getLocation().append("2"));
		setProjectNatures(mavenProject3, InitialProjectSelection.MAVEN_NATURE);

		final IProject mavenProject4 = createProject("1", mavenProject3.getLocation().append("1"));
		setProjectNatures(mavenProject4, InitialProjectSelection.MAVEN_NATURE);

		final IProject[] mavenProjects = fetchMavenProjects();

		Assert.assertNotNull(mavenProjects);

		if (areNestedProjectsFound()) {
			Assert.assertEquals(4, mavenProjects.length);
			Assert.assertEquals(mavenProject4, mavenProjects[0]);
			Assert.assertEquals(mavenProject3, mavenProjects[1]);
			Assert.assertEquals(mavenProject2, mavenProjects[2]);
			Assert.assertEquals(mavenProject1, mavenProjects[3]);
		} else {
			Assert.assertEquals(1, mavenProjects.length);
			Assert.assertEquals(mavenProject1, mavenProjects[0]);
		}
	}
}
