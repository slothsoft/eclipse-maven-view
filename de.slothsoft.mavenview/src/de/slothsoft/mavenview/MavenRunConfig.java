package de.slothsoft.mavenview;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class MavenRunConfig {

	private Phase[] phases = {Phase.CLEAN, Phase.INSTALL};

	public String toGoalString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(Arrays.stream(this.phases).sorted().map(Phase::getDisplayName).collect(Collectors.joining(" ")));
		return sb.toString();
	}

	public Phase[] getPhases() {
		return this.phases;
	}

	public MavenRunConfig phases(Phase... newPhases) {
		setPhases(newPhases);
		return this;
	}

	public void setPhases(Phase... phases) {
		this.phases = Objects.requireNonNull(phases);
	}

	public MavenRunConfig copy() {
		return new MavenRunConfig().phases(this.phases.clone());
	}

	@Override
	public String toString() {
		return "MavenRunConfig [" + Arrays.toString(this.phases) + "]";
	}

}
