package de.slothsoft.mavenview.internal;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.slothsoft.mavenview.MavenView;

public class RefreshHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final MavenView mavenView = findMavenView(event);
		if (mavenView != null) {
			mavenView.refresh();
		}
		return null;
	}

	private static MavenView findMavenView(ExecutionEvent event) {
		final IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (activePart instanceof MavenView) return (MavenView) activePart;

		for (final IViewReference viewReference : HandlerUtil.getActiveWorkbenchWindow(event).getActivePage()
				.getViewReferences()) {
			if (viewReference.getId().equals(MavenView.ID)) return (MavenView) viewReference.getView(false);
		}
		throw new IllegalArgumentException("Cannot refresh view " + activePart + "!");
	}

}
