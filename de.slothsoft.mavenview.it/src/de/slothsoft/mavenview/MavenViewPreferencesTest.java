package de.slothsoft.mavenview;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MavenViewPreferencesTest {

	private static final InitialProjectSelection INITIAL_PROJECT_SELECTION_DEFAULT = InitialProjectSelection.ROOT_PROJECTS;
	private static final InitialProjectSelection INITIAL_PROJECT_SELECTION_OTHER = InitialProjectSelection.ALL_PROJECTS;

	private static final Phase[] DISPLAYED_PHASES_DEFAULT = Phase.values();
	private static final Phase[] DISPLAYED_PHASES_OTHER = new Phase[]{Phase.CLEAN, Phase.INSTALL};

	private IPreferenceStore preferences;

	@Before
	public void setUp() throws Exception {
		this.preferences = MavenViewPlugin.getDefault().getPreferenceStore();
		this.preferences.setToDefault(MavenViewPreferences.INITIAL_PROJECT_SELECTION);
	}

	@Test
	public void testInitialProjectSelectionDefault() throws Exception {
		Assert.assertEquals(INITIAL_PROJECT_SELECTION_DEFAULT, MavenViewPreferences.getInitialProjectSelection());
	}

	@Test
	public void testGetInitialProjectSelection() throws Exception {
		this.preferences.setValue(MavenViewPreferences.INITIAL_PROJECT_SELECTION,
				INITIAL_PROJECT_SELECTION_OTHER.name());

		Assert.assertEquals(INITIAL_PROJECT_SELECTION_OTHER, MavenViewPreferences.getInitialProjectSelection());
	}

	@Test
	public void testGetInitialProjectSelectionNull() throws Exception {
		this.preferences.setToDefault(MavenViewPreferences.INITIAL_PROJECT_SELECTION);

		Assert.assertEquals(INITIAL_PROJECT_SELECTION_DEFAULT, MavenViewPreferences.getInitialProjectSelection());
	}

	@Test
	public void testSetInitialProjectSelection() throws Exception {
		MavenViewPreferences.setInitialProjectSelection(INITIAL_PROJECT_SELECTION_OTHER);

		Assert.assertEquals(INITIAL_PROJECT_SELECTION_OTHER.name(),
				this.preferences.getString(MavenViewPreferences.INITIAL_PROJECT_SELECTION));
	}

	@Test
	public void testSetInitialProjectSelectionNull() throws Exception {
		MavenViewPreferences.setInitialProjectSelection(null);

		Assert.assertEquals(INITIAL_PROJECT_SELECTION_DEFAULT.name(),
				this.preferences.getString(MavenViewPreferences.INITIAL_PROJECT_SELECTION));
	}

	@Test
	public void testDisplayedPhasesDefault() throws Exception {
		Assert.assertArrayEquals(DISPLAYED_PHASES_DEFAULT, MavenViewPreferences.getDisplayedPhases());
	}

	@Test
	public void testGetDisplayedPhases() throws Exception {
		this.preferences.setValue(MavenViewPreferences.DISPLAYED_PHASES,
				MavenViewPreferences.getDisplayedPhasesString(DISPLAYED_PHASES_OTHER));

		Assert.assertArrayEquals(DISPLAYED_PHASES_OTHER, MavenViewPreferences.getDisplayedPhases());
	}

	@Test
	public void testGetDisplayedPhasesNull() throws Exception {
		this.preferences.setToDefault(MavenViewPreferences.DISPLAYED_PHASES);

		Assert.assertArrayEquals(DISPLAYED_PHASES_DEFAULT, MavenViewPreferences.getDisplayedPhases());
	}

	@Test
	public void testSetDisplayedPhases() throws Exception {
		MavenViewPreferences.setDisplayedPhases(DISPLAYED_PHASES_OTHER);

		Assert.assertEquals(MavenViewPreferences.getDisplayedPhasesString(DISPLAYED_PHASES_OTHER),
				this.preferences.getString(MavenViewPreferences.DISPLAYED_PHASES));
	}

	@Test
	public void testSetDisplayedPhasesNull() throws Exception {
		MavenViewPreferences.setDisplayedPhases(null);

		Assert.assertEquals(MavenViewPreferences.getDisplayedPhasesString(DISPLAYED_PHASES_DEFAULT),
				this.preferences.getString(MavenViewPreferences.DISPLAYED_PHASES));
	}

	@Test
	public void testGetDisplayedPhasesString() throws Exception {
		Assert.assertEquals("CLEAN\nINSTALL", MavenViewPreferences.getDisplayedPhasesString(DISPLAYED_PHASES_OTHER));
	}
}
