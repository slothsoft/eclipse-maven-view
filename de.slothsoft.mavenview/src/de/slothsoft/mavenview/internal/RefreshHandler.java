package de.slothsoft.mavenview.internal;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.slothsoft.mavenview.MavenView;

public class RefreshHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		System.out.println("RefreshHandler.execute(A)");
		if (activePart instanceof MavenView) {
			((MavenView) activePart).refresh();
			System.out.println("RefreshHandler.execute(B)");
			return null;
		}
		throw new IllegalArgumentException("Cannot refresh view " + activePart + "!");
	}

}
