package org.yawlfoundation.yawl.editor.actions.sensor.template;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.yawlfoundation.yawl.editor.swing.AbstractDoneDialog;
import org.yawlfoundation.yawl.editor.swing.JUtilities;

public class CreateTaskNetDialog extends AbstractDoneDialog {
    private static final long serialVersionUID = 1L;
    private EditTemplateDialog etd = null;
    private JLabel nameL, typeL, descriptionL;
    private JTextField nameTF;
    private JTextArea descriptionTA;
    private JScrollPane descriptionJSP;
    private JRadioButton taskRB, netRB;
    private ButtonGroup buttonGroup = new ButtonGroup();
    private int row = -1;
    
    public CreateTaskNetDialog() {
        super("Create Task/Net", true);
        setContentPanel(getConfigurationPanel());
        getDoneButton().setEnabled(false);
        getDoneButton().addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
            	String name = nameTF.getText();
            	String description = descriptionTA.getText();
            	Boolean type = null;
            	if(taskRB.isSelected()) type = true;
            	else if(netRB.isSelected()) type = false;
            	etd.addTask(name, type, description, row);
            	nameTF.setText("");
            	descriptionTA.setText("");
            	taskRB.setSelected(true);
            	netRB.setSelected(false);
            	getDoneButton().setEnabled(false);
            }
        });
        
        getCancelButton().addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
            	nameTF.setText("");
            	descriptionTA.setText("");
            	taskRB.setSelected(true);
            	netRB.setSelected(false);
            	getDoneButton().setEnabled(false);
            }
        });

        getRootPane().setDefaultButton(getDoneButton());
    }

	public void update(String name, Boolean type, String description, int row) {
		this.row = row;
		if(name!=null) nameTF.setText(name);
		if(description!=null) descriptionTA.setText(description);
		if(type!=null) {
			if(type) {
				taskRB.setSelected(true);
				netRB.setSelected(false);
			}else {
				taskRB.setSelected(false);
				netRB.setSelected(true);
			}
		}
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
		p.add(getTaskRB());
		p.add(getNetRB());
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
					if(descriptionTA.getText().isEmpty()) getDoneButton().setEnabled(false);
					else getDoneButton().setEnabled(true);
				}else if(arg0.getKeyCode()==8 || arg0.getKeyCode()==46){
					if(nameTF.getText().length()<2 || descriptionTA.getText().isEmpty()) getDoneButton().setEnabled(false);
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
		typeL.setBounds(12, 68, 100, 22);
		return typeL;
	}
	    
    private JRadioButton getTaskRB() {
    	taskRB = new JRadioButton("Task");
    	taskRB.setBounds(124, 68, 100, 22);
    	taskRB.setSelected(true);
    	buttonGroup.add(taskRB);
		return taskRB;
    }
    
    private JRadioButton getNetRB() {
    	netRB = new JRadioButton("Net");
    	netRB.setBounds(236, 68, 100, 22);
    	buttonGroup.add(netRB);
		return netRB;
    }
    
    private JLabel getDescriptionL() {
		descriptionL = new JLabel("Description");
		descriptionL.setBounds(12, 96, 300, 15);
		return descriptionL;
	}
    
    private JScrollPane getDescriptionJSP() {
    	descriptionTA = new JTextArea();
    	descriptionTA.setBounds(12, 117, 300, 400);
    	descriptionTA.setLineWrap(true);
    	descriptionTA.setWrapStyleWord(true);
    	descriptionTA.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode()>50 && arg0.getKeyCode()<91) {
					if(nameTF.getText().isEmpty()) getDoneButton().setEnabled(false);
					else getDoneButton().setEnabled(true);
				}else if(arg0.getKeyCode()==8 || arg0.getKeyCode()==46){
					if(descriptionTA.getText().length()<2 || nameTF.getText().isEmpty()) getDoneButton().setEnabled(false);
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
		descriptionJSP.setBounds(12, 117, 300, 100);
		return descriptionJSP;
	}
    
    protected void makeLastAdjustments() {
        setSize(324, 300);
        JUtilities.setMinSizeToCurrent(this);
    }
}
