package de.slothsoft.mavenview.internal.tree;

import java.util.Objects;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.swt.graphics.Image;

import de.slothsoft.mavenview.Displayable;
import de.slothsoft.mavenview.MavenViewImages;
import de.slothsoft.mavenview.MavenViewPlugin;

public class LaunchConfigNode implements Displayable {

	private final ILaunchConfiguration launchConfig;

	public LaunchConfigNode(ILaunchConfiguration launchConfig) {
		this.launchConfig = Objects.requireNonNull(launchConfig);
	}

	public ILaunchConfiguration getLaunchConfig() {
		return this.launchConfig;
	}

	@Override
	public String getDisplayName() {
		return this.launchConfig.getName();
	}

	@Override
	public Image getImage() {
		return MavenViewPlugin.getImage(MavenViewImages.OBJ_MAVEN);
	}

}
