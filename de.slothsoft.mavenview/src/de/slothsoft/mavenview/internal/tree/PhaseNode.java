package de.slothsoft.mavenview.internal.tree;

import java.util.Arrays;
import java.util.Objects;

import org.eclipse.swt.graphics.Image;

import de.slothsoft.mavenview.Displayable;
import de.slothsoft.mavenview.MavenViewImages;
import de.slothsoft.mavenview.MavenViewPlugin;
import de.slothsoft.mavenview.Phase;

public class PhaseNode implements Displayable {

	static PhaseNode[] createAll(ProjectNode mavenProject) {
		return Arrays.stream(Phase.values()).map(phase -> new PhaseNode(mavenProject, phase)).toArray(PhaseNode[]::new);
	}

	private final ProjectNode mavenProject;
	private final Phase phase;

	public PhaseNode(ProjectNode mavenProject, Phase phase) {
		this.mavenProject = Objects.requireNonNull(mavenProject);
		this.phase = Objects.requireNonNull(phase);
	}

	@Override
	public String getDisplayName() {
		return this.phase.getDisplayName();
	}

	@Override
	public Image getImage() {
		return MavenViewPlugin.getImage(MavenViewImages.OBJ_PHASE);
	}

}
