package org.yawlfoundation.yawl.editor.actions.sensor;

import java.awt.event.ActionEvent;

import javax.swing.*;

import org.yawlfoundation.yawl.editor.YAWLEditor;
import org.yawlfoundation.yawl.editor.actions.specification.YAWLOpenSpecificationAction;
import org.yawlfoundation.yawl.editor.swing.TooltipTogglingWidget;
import org.yawlfoundation.yawl.editor.swing.menu.MenuUtilities;

public class SensorMenuBotton extends YAWLOpenSpecificationAction implements TooltipTogglingWidget {//YAWLBaseAction implements TooltipTogglingWidget {

	/**
	 * Author: Raffaele Conforti Creation Date: 22/07/2010
	 */

	private static final long serialVersionUID = 1L;
	private static final SensorDialog dialog = new SensorDialog();
	private boolean invokedAtLeastOnce = false;

	{
		putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
		putValue(Action.NAME, "Edit Sensors");
		putValue(Action.LONG_DESCRIPTION, "Edit Sensors");
		putValue(Action.SMALL_ICON, getPNGIcon("application_delete"));
		putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_S));
		putValue(Action.ACCELERATOR_KEY,
				MenuUtilities.getAcceleratorKeyStroke("S"));
	}

	public void actionPerformed(ActionEvent event) {
		if (!invokedAtLeastOnce) {
			invokedAtLeastOnce = true;
			dialog.setLocationRelativeTo(YAWLEditor.getInstance());
		}
		dialog.inizialize();
		dialog.update();
		dialog.setVisible(true);
	}

	public String getEnabledTooltipText() {
		return " Edit a sensor ";
	}

	public String getDisabledTooltipText() {
		return " You must have a sensor (other than the starting net) to edit it ";
	}
}
