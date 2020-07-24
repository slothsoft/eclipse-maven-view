package de.slothsoft.mavenview.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import de.slothsoft.mavenview.MavenRunConfig;
import de.slothsoft.mavenview.MavenRunner;
import de.slothsoft.mavenview.MavenRunnerException;
import de.slothsoft.mavenview.Phase;
import de.slothsoft.mavenview.internal.tree.PhaseNode;

public class RunMavenPhasesHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getCurrentSelection(event);

		final PhaseNode[] selectedPhaseNodes = selection instanceof IStructuredSelection
				? Arrays.stream(((IStructuredSelection) selection).toArray()).filter(s -> s instanceof PhaseNode)
						.map(s -> (PhaseNode) s).toArray(PhaseNode[]::new)
				: new PhaseNode[0];

		if (selectedPhaseNodes.length == 0) {
			MessageDialog.openError(HandlerUtil.getActiveShell(event), Messages.getString("SelectPhasesTitle"),
					Messages.getString("SelectPhases"));
		} else {
			runMavenPhases(HandlerUtil.getActiveShell(event), selectedPhaseNodes);
		}
		return null;
	}

	private static void runMavenPhases(Shell shell, PhaseNode[] phaseNodes) {
		final Map<IProject, List<PhaseNode>> projects = Arrays.stream(phaseNodes)
				.collect(Collectors.groupingBy(PhaseNode::getProject));

		final MavenRunConfig config = new MavenRunConfig();

		for (final Entry<IProject, List<PhaseNode>> project : projects.entrySet()) {
			try {
				final MavenRunConfig projectConfig = config.copy();
				projectConfig.setPhases(project.getValue().stream().map(PhaseNode::getPhase).toArray(Phase[]::new));

				final MavenRunner runner = new MavenRunner();
				runner.runForProject(project.getKey(), projectConfig);
			} catch (final MavenRunnerException e) {
				MessageDialog.openError(shell, Messages.getString("ErrorWhileRunningMaven"), e.getLocalizedMessage());
			}
		}
	}

}
