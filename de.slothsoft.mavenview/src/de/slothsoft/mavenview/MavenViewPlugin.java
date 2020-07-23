package de.slothsoft.mavenview;

import java.util.Objects;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class MavenViewPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "de.slothsoft.mavenview"; //$NON-NLS-1$

	private static MavenViewPlugin plugin;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static MavenViewPlugin getDefault() {
		return plugin;
	}

	public static Image getImage(String imagePath) {
		final ImageRegistry imageRegistry = JFaceResources.getImageRegistry();

		final String key = PLUGIN_ID + '/' + imagePath;
		Image result = imageRegistry.get(key);
		if (result == null) {
			imageRegistry.put(key, imageDescriptorFromPlugin(PLUGIN_ID, imagePath));
			result = imageRegistry.get(key);
		}
		return Objects.requireNonNull(result, "Image '" + imagePath + "' was not found!");
	}
}
