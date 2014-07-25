package org.yawlfoundation.yawl.editor.actions.sensor;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class VariableConfiguration {

	private JLabel nameL, typeL;
	private JTextArea descriptionTA;
	private JTextField newNameTF;
	private JTextArea valueTA;

	public VariableConfiguration(String name, String type, String description, String value) {
		nameL = new JLabel("Name:");
		typeL = new JLabel("Type: "+(type.equals("")?(value.contains("(")?"Mapping":"Variable"):type));
		descriptionTA = new JTextArea(description);
		descriptionTA.setEditable(false);
		descriptionTA.setLineWrap(true);
		newNameTF = new JTextField(name);
		valueTA = new JTextArea(value);
		valueTA.setLineWrap(true);
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

	public JTextField getNewNameTF() {
		return newNameTF;
	}
	
	public JTextArea getValueTA() {
		return valueTA;
	}
}
