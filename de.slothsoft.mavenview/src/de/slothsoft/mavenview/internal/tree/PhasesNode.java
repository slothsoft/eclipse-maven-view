package de.slothsoft.mavenview.internal.tree;

import java.util.Objects;

import org.eclipse.swt.graphics.Image;

import de.slothsoft.mavenview.Displayable;
import de.slothsoft.mavenview.MavenViewImages;
import de.slothsoft.mavenview.MavenViewPlugin;
import de.slothsoft.mavenview.Parentable;

public class PhasesNode implements Displayable, Parentable {

	private final ProjectNode mavenProject;

	public PhasesNode(ProjectNode mavenProject) {
		this.mavenProject = Objects.requireNonNull(mavenProject);
	}

	@Override
	public String getDisplayName() {
		return Messages.getString("Phases");
	}

	@Override
	public Image getImage() {
		return MavenViewPlugin.getImage(MavenViewImages.OBJ_PHASES);
	}

	@Override
	public Object[] getChildren() {
		return PhaseNode.createAll(this.mavenProject);
	}
}
