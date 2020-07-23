package de.slothsoft.mavenview;

import org.eclipse.swt.graphics.Image;

import de.slothsoft.mavenview.MavenViewImages;
import de.slothsoft.mavenview.MavenViewPlugin;

public enum Phase implements Displayable {
	VALIDATE,

	COMPILE,

	TEST,

	PACKAGE,

	VERIFY,

	INSTALL,

	DEPLOY,

	;

	@Override
	public String getDisplayName() {
		return name().toLowerCase();
	}

	@Override
	public Image getImage() {
		return MavenViewPlugin.getImage(MavenViewImages.OBJ_PHASE);
	}

}
