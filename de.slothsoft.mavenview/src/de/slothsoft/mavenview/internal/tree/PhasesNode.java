package de.slothsoft.mavenview.internal.tree;

import java.util.Objects;

import org.eclipse.swt.graphics.Image;

import de.slothsoft.mavenview.Displayable;
import de.slothsoft.mavenview.MavenViewImages;
import de.slothsoft.mavenview.MavenViewPlugin;

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

	@Override
	public int hashCode() {
		return 5 * Objects.hash(this.mavenProject);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;

		final PhasesNode that = (PhasesNode) obj;
		if (!Objects.equals(this.mavenProject, that.mavenProject)) return false;
		return true;
	}
}
