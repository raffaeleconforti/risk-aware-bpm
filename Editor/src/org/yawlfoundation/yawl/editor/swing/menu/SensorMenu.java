package org.yawlfoundation.yawl.editor.swing.menu;

import org.yawlfoundation.yawl.editor.actions.sensor.SensorMenuBotton;
import org.yawlfoundation.yawl.editor.actions.sensor.template.CreateTemplateMenuBotton;
import org.yawlfoundation.yawl.editor.actions.sensor.template.EditTemplateMenuBotton;

import java.awt.event.KeyEvent;

import javax.swing.JMenu;

class SensorMenu extends JMenu {

	/**
	 * Author: Raffaele Conforti
	 * Creation Date: 22/07/2010
	 */
	private static final long serialVersionUID = 1L;

	public SensorMenu() {
//		super("Sensor",KeyEvent.VK_O);
		super("Sensors");
        setMnemonic(KeyEvent.VK_O);
        buildInterface();
	}
  
	protected void buildInterface() {
		add(new YAWLMenuItem(new CreateTemplateMenuBotton()));
		add(new YAWLMenuItem(new EditTemplateMenuBotton()));
		add(new YAWLMenuItem(new SensorMenuBotton()));
	}
}