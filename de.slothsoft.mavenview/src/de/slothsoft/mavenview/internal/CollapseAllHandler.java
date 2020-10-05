package de.slothsoft.mavenview.internal;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.slothsoft.mavenview.MavenView;

public class CollapseAllHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final MavenView mavenView = RefreshHandler.findMavenView(event);
		if (mavenView != null) {
			mavenView.collapseAll();
		}
		return null;
	}

}
