package org.yawlfoundation.yawl.editor.actions.sensor.template;

import java.awt.event.ActionEvent;

import javax.swing.*;

import org.yawlfoundation.yawl.editor.YAWLEditor;
import org.yawlfoundation.yawl.editor.actions.YAWLBaseAction;
import org.yawlfoundation.yawl.editor.swing.TooltipTogglingWidget;
import org.yawlfoundation.yawl.editor.swing.menu.MenuUtilities;

public class CreateTemplateMenuBotton extends YAWLBaseAction implements TooltipTogglingWidget {
	
    /**
    * Author: Raffaele Conforti
    * Creation Date: 22/07/2010
    */

    private static final long serialVersionUID = 1L;
//    private static final CreateTemplateDialog dialog = new CreateTemplateDialog();
    private static final EditTemplateDialog dialog = new EditTemplateDialog();
    private boolean invokedAtLeastOnce = false;

    {
        putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
        putValue(Action.NAME, "Create Template");
        putValue(Action.LONG_DESCRIPTION, "Create Template");
        putValue(Action.SMALL_ICON, getPNGIcon("application_delete"));
        putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_T));
        putValue(Action.ACCELERATOR_KEY, MenuUtilities.getAcceleratorKeyStroke("T"));
    }

    public void actionPerformed(ActionEvent event) {
        if (!invokedAtLeastOnce) {
            invokedAtLeastOnce = true;
            dialog.setLocationRelativeTo(YAWLEditor.getInstance());
        }
        dialog.setVisible(true);
    }

    public String getEnabledTooltipText() {
        return " Edit a sensor ";
    }

    public String getDisabledTooltipText() {
        return " You must have a sensor (other than the starting net) to edit it ";
    }
    
}

