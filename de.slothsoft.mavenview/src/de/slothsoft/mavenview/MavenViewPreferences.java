package de.slothsoft.mavenview;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Constant definitions for plug-in preferences.
 */

public final class MavenViewPreferences {

	public static final String INITIAL_PROJECT_SELECTION = "initialProjectSelection";
	public static final String DISPLAYED_PHASES = "displayedPhases";

	private static final String PHASES_SEPARATOR = "\n";

	public static void setInitialProjectSelection(InitialProjectSelection value) {
		if (value == null) {
			getPreferences().setToDefault(INITIAL_PROJECT_SELECTION);
		} else {
			getPreferences().setValue(INITIAL_PROJECT_SELECTION, value.name());
		}
	}

	private static IPreferenceStore getPreferences() {
		return MavenViewPlugin.getDefault().getPreferenceStore();
	}

	public static InitialProjectSelection getInitialProjectSelection() {
		return InitialProjectSelection.valueOf(getPreferences().getString(INITIAL_PROJECT_SELECTION));
	}

	public static Phase[] getDisplayedPhases() {
		return Arrays.stream(getPreferences().getString(DISPLAYED_PHASES).split(PHASES_SEPARATOR))
				.filter(s -> !s.isEmpty()).map(Phase::valueOf).toArray(Phase[]::new);
	}

	public static void setDisplayedPhases(Phase[] displayedPhases) {
		if (displayedPhases == null || displayedPhases.length == 0) {
			getPreferences().setToDefault(DISPLAYED_PHASES);
		} else {
			getPreferences().setValue(DISPLAYED_PHASES, getDisplayedPhasesString(displayedPhases));
		}
	}

	public static String getDisplayedPhasesString(Phase[] displayedPhases) {
		return Arrays.stream(displayedPhases).map(Phase::name).collect(Collectors.joining(PHASES_SEPARATOR));
	}

	private MavenViewPreferences() {
		// hide me
	}

}
