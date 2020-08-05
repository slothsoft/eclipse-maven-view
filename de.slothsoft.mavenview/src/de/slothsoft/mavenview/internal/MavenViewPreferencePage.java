package de.slothsoft.mavenview.internal;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.slothsoft.mavenview.Displayable;
import de.slothsoft.mavenview.InitialProjectSelection;
import de.slothsoft.mavenview.MavenView;
import de.slothsoft.mavenview.MavenViewPlugin;
import de.slothsoft.mavenview.MavenViewPreferences;
import de.slothsoft.mavenview.Phase;
import de.slothsoft.mavenview.internal.common.CheckTableFieldEditor;
import de.slothsoft.mavenview.internal.common.CheckTableFieldEditor.PreferenceLabelProvider;

public class MavenViewPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	static class PhaseLabelProvider extends DisplayableLabelProvider implements PreferenceLabelProvider {

		@Override
		public String getPreference(Object element) {
			return ((Phase) element).name();
		}
	}

	private IWorkbench workbench;

	public MavenViewPreferencePage() {
		super(GRID);
	}

	@Override
	public void init(IWorkbench newWorkbench) {
		this.workbench = newWorkbench;
		setPreferenceStore(MavenViewPlugin.getDefault().getPreferenceStore());
	}

	@Override
	public void createFieldEditors() {
		final Composite fieldEditorParent = getFieldEditorParent();

		addField(new ComboFieldEditor(MavenViewPreferences.INITIAL_PROJECT_SELECTION,
				Messages.getString("InitialProjectSelection") + ':',
				createEntryNamesAndValues(InitialProjectSelection.values()), fieldEditorParent));
		fieldEditorParent.getChildren()[1].setData(CheckTableFieldEditor.DATA_ID,
				MavenViewPreferences.INITIAL_PROJECT_SELECTION);

		addField(new CheckTableFieldEditor(MavenViewPreferences.DISPLAYED_PHASES, getFieldEditorParent(),
				Messages.getString("DisplayedPhases") + ':').labelProvider(new PhaseLabelProvider())
						.input(Phase.values()));
	}

	static <E extends Enum<E> & Displayable> String[][] createEntryNamesAndValues(E[] enumValues) {
		final String[][] result = new String[enumValues.length][2];
		for (int i = 0; i < result.length; i++) {
			result[i][0] = enumValues[i].getDisplayName();
			result[i][1] = enumValues[i].name();
		}
		return result;
	}

	@Override
	public boolean performOk() {
		final boolean result = super.performOk();
		if (result) {
			refreshView();
		}
		return result;
	}

	private void refreshView() {
		for (final IViewPart view : this.workbench.getActiveWorkbenchWindow().getActivePage().getViews()) {
			if (view instanceof MavenView) {
				((MavenView) view).refresh();
			}
		}
	}

	@Override
	protected void performApply() {
		super.performApply();
		refreshView();
	}

}