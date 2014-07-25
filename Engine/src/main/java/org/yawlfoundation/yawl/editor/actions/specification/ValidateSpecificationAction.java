/*
 * Created on 9/10/2003
 * YAWLEditor v1.0 
 *
 * @author Lindsay Bradford
 * 
 * 
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package org.yawlfoundation.yawl.editor.actions.specification;

import org.yawlfoundation.yawl.editor.specification.ArchivingThread;
import org.yawlfoundation.yawl.editor.specification.SpecificationModel;
import org.yawlfoundation.yawl.editor.swing.TooltipTogglingWidget;
import org.yawlfoundation.yawl.editor.swing.menu.MenuUtilities;

import java.awt.event.ActionEvent;

public class ValidateSpecificationAction extends YAWLOpenSpecificationAction
        implements TooltipTogglingWidget {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  {
    putValue(SHORT_DESCRIPTION, getDisabledTooltipText());
    putValue(NAME, "Validate Specification");
    putValue(LONG_DESCRIPTION, "Validate this specification.");
    putValue(SMALL_ICON, getIconByName("Validate"));
    putValue(MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_V));
    putValue(ACCELERATOR_KEY, MenuUtilities.getAcceleratorKeyStroke("shift V"));
  }
  
  public void actionPerformed(ActionEvent event) {
    ArchivingThread.getInstance().validate(
      SpecificationModel.getInstance()    
    );
  }
  
  public String getEnabledTooltipText() {
    return " Validate this specification ";
  }
  
  public String getDisabledTooltipText() {
    return " You must have an open specification" + 
           " to validate it ";
  }
}
