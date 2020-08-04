package de.slothsoft.mavenview.internal;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import de.slothsoft.mavenview.InitialProjectSelection;
import de.slothsoft.mavenview.MavenViewPlugin;
import de.slothsoft.mavenview.MavenViewPreferences;
import de.slothsoft.mavenview.Phase;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		final IPreferenceStore store = MavenViewPlugin.getDefault().getPreferenceStore();
		store.setDefault(MavenViewPreferences.INITIAL_PROJECT_SELECTION, InitialProjectSelection.ROOT_PROJECTS.name());
		store.setDefault(MavenViewPreferences.DISPLAYED_PHASES,
				MavenViewPreferences.getDisplayedPhasesString(Phase.values()));
	}

}
