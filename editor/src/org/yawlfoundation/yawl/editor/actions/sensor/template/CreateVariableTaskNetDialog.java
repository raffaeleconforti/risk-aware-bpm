package org.yawlfoundation.yawl.editor.actions.sensor.template;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.yawlfoundation.yawl.editor.swing.AbstractDoneDialog;
import org.yawlfoundation.yawl.editor.swing.JUtilities;

public class CreateVariableTaskNetDialog extends AbstractDoneDialog {
	private static final long serialVersionUID = 1L;
    private EditTemplateDialog etd = null;
    private JLabel nameL, typeL, descriptionL;
    private JTextField nameTF;
    private JComboBox typeCB;
    private String[] types = new String[]{"", "Integer, Long, Float, Double", "String, ComplexType", "DataTime", "Boolean"};
    private JTextArea descriptionTA;
    private JScrollPane descriptionJSP;
    private int taskRow = -1;
    private int varTaskRow = -1;
    
    public CreateVariableTaskNetDialog() {
        super("Create Task/Net Variable", true);
        setContentPanel(getConfigurationPanel());
        getDoneButton().setEnabled(false);
        getDoneButton().addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
            	String name = nameTF.getText();
            	String description = descriptionTA.getText();
            	String type = types[typeCB.getSelectedIndex()];
            	etd.addVarTask(name, type, description, taskRow, varTaskRow);
            	nameTF.setText("");
            	typeCB.setSelectedIndex(0);
            	descriptionTA.setText("");
            	getDoneButton().setEnabled(false);
            }
        });
        
        getCancelButton().addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
            	nameTF.setText("");
            	typeCB.setSelectedIndex(0);          	
            	descriptionTA.setText("");
            	getDoneButton().setEnabled(false);
            }
        });

        getRootPane().setDefaultButton(getDoneButton());
    }

	public void update(String name, String type, String description, int taskRow, int varTaskRow) {
		this.taskRow = taskRow;
		this.varTaskRow = varTaskRow;
		if(name!=null) nameTF.setText(name);
		if(type!=null) {
			for(int i=0; i<types.length; i++) {
				if(types[i].equals(type)) {
					typeCB.setSelectedIndex(i);
					break;
				}
			}
		}
		if(description!=null) descriptionTA.setText(description);
	}

	public void setEditTemplateDialog(EditTemplateDialog editTemplateDialog) {
		etd = editTemplateDialog;
	}

	private JPanel getConfigurationPanel() {
		JPanel p = new JPanel();
    	p.setLayout(null);
		p.add(getNameL());
		p.add(getNameTF());
		p.add(getTypeL());
		p.add(getTypeCB());
		p.add(getDescriptionL());
		p.add(getDescriptionJSP());
		return p;
	}
	
	private JLabel getNameL() {
		nameL = new JLabel("Name");
		nameL.setBounds(12, 12, 300, 22);
		return nameL;
	}
	
	private JTextField getNameTF() {
		nameTF = new JTextField();
		nameTF.setBounds(12, 40, 300, 22);
		nameTF.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode()>50 && arg0.getKeyCode()<91) {
					if(typeCB.getSelectedIndex()==0 || descriptionTA.getText().isEmpty()) getDoneButton().setEnabled(false);
					else getDoneButton().setEnabled(true);
				}else if(arg0.getKeyCode()==8 || arg0.getKeyCode()==46){
					if(nameTF.getText().length()<2 || typeCB.getSelectedIndex()==0 || descriptionTA.getText().isEmpty()) getDoneButton().setEnabled(false);
					else getDoneButton().setEnabled(true);
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {}

			@Override
			public void keyTyped(KeyEvent arg0) {}
		});
		return nameTF;
	}
	
	private JLabel getTypeL() {
		typeL = new JLabel("Type");
		typeL.setBounds(12, 68, 300, 15);
		return 		typeL;
	}
	
	private JComboBox getTypeCB() {
		typeCB = new JComboBox();
		typeCB.setModel(new DefaultComboBoxModel(types));
		typeCB.setBounds(12, 96, 300, 22);
		typeCB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(typeCB.getSelectedIndex()==0 || nameTF.getText().isEmpty() || descriptionTA.getText().isEmpty()) getDoneButton().setEnabled(false);
				else getDoneButton().setEnabled(true);
			}
		});
		return typeCB;
	}
    
    private JLabel getDescriptionL() {
		descriptionL = new JLabel("Description");
		descriptionL.setBounds(12, 128, 300, 15);
		return descriptionL;
	}
    
    private JScrollPane getDescriptionJSP() {
    	descriptionTA = new JTextArea();
    	descriptionTA.setBounds(12, 154, 300, 400);
    	descriptionTA.setLineWrap(true);
    	descriptionTA.setWrapStyleWord(true);
    	descriptionTA.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode()>50 && arg0.getKeyCode()<91) {
					if(typeCB.getSelectedIndex()==0 || nameTF.getText().isEmpty()) getDoneButton().setEnabled(false);
					else getDoneButton().setEnabled(true);
				}else if(arg0.getKeyCode()==8 || arg0.getKeyCode()==46){
					if(descriptionTA.getText().length()<2 || typeCB.getSelectedIndex()==0 || nameTF.getText().isEmpty()) getDoneButton().setEnabled(false);
					else getDoneButton().setEnabled(true);
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {}

			@Override
			public void keyTyped(KeyEvent arg0) {}
		});
    	descriptionJSP = new JScrollPane();
    	descriptionJSP.setViewportView(descriptionTA);
		descriptionJSP.setBounds(12, 154, 300, 100);
		return descriptionJSP;
	}
    
    protected void makeLastAdjustments() {
        setSize(324, 335);
        JUtilities.setMinSizeToCurrent(this);
    }
}
