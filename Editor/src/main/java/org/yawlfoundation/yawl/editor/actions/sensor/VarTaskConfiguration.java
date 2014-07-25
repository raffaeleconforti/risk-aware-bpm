package org.yawlfoundation.yawl.editor.actions.sensor;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class VarTaskConfiguration {

	private JLabel nameL, typeL;
	private JTextArea descriptionTA;
	private JComboBox selectionCB;

	public VarTaskConfiguration(String name, String type, String description, String[] selections) {
		nameL = new JLabel("Name: "+name);
		typeL = new JLabel("Type: "+type);
		descriptionTA = new JTextArea(description);
		descriptionTA.setEditable(false);
		descriptionTA.setLineWrap(true);
		selectionCB = new JComboBox(new DefaultComboBoxModel(selections));
	}

	public JLabel getNameL() {
		return nameL;
	}
	
	public JLabel getTypeL() {
		return typeL;
	}

	public JTextArea getDescriptionTA() {
		return descriptionTA;
	}

	public JComboBox getSelectionCB() {
		return selectionCB;
	}
}