package org.yawlfoundation.yawl.editor.actions.sensor.template;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import org.yawlfoundation.yawl.editor.YAWLEditor;
import org.yawlfoundation.yawl.editor.actions.YAWLBaseAction;
import org.yawlfoundation.yawl.editor.swing.TooltipTogglingWidget;
import org.yawlfoundation.yawl.editor.swing.menu.MenuUtilities;

public class EditTemplateMenuBotton extends YAWLBaseAction implements TooltipTogglingWidget {
	
    /**
    * Author: Raffaele Conforti
    * Creation Date: 22/07/2010
    */

    private static final long serialVersionUID = 1L;
    private static final EditTemplateDialog dialog = new EditTemplateDialog();
    private boolean invokedAtLeastOnce = false;
    private JFileChooser jfc = new JFileChooser();

    {
        putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
        putValue(Action.NAME, "Edit Template");
        putValue(Action.LONG_DESCRIPTION, "Edit Template");
        putValue(Action.SMALL_ICON, getPNGIcon("application_delete"));
        putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_E));
        putValue(Action.ACCELERATOR_KEY, MenuUtilities.getAcceleratorKeyStroke("E"));
    }

    public void actionPerformed(ActionEvent event) {
        if (!invokedAtLeastOnce) {
            invokedAtLeastOnce = true;
            dialog.setLocationRelativeTo(YAWLEditor.getInstance());
        }
        jfc.setFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				return "Template File";
			}
			
			@Override
			public boolean accept(File arg0) {
				return arg0.getPath().endsWith(".stemplate") || arg0.isDirectory();
			}
		});
        jfc.setAcceptAllFileFilterUsed(false);
		int res = jfc.showOpenDialog(null);
		if(res == 0) {
			String template = null;
			try {
				File f = jfc.getSelectedFile();
				InputStream is = new FileInputStream(f);
				Writer writer = new StringWriter();
				char[] buffer = new char[1024];
				try {
					Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
					int n;
					while ((n = reader.read(buffer)) != -1) {
						writer.write(buffer, 0, n);
					}
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					is.close();
				}
				template = writer.toString();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dialog.setPatter(template);
	        dialog.setVisible(true);
		}
    }

    public String getEnabledTooltipText() {
        return " Edit a sensor ";
    }

    public String getDisabledTooltipText() {
        return " You must have a sensor (other than the starting net) to edit it ";
    }
    
}

