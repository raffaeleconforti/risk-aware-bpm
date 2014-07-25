package org.yawlfoundation.yawl.editor.actions.sensor.template;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.yawlfoundation.yawl.editor.actions.sensor.template.EditTemplateDialog;
import org.yawlfoundation.yawl.editor.actions.sensor.template.EditVariableTemplateDialog;
import org.yawlfoundation.yawl.editor.swing.AbstractDoneDialog;
import org.yawlfoundation.yawl.editor.swing.JUtilities;

public class EditVariableTemplateDialog extends AbstractDoneDialog {
	private static final long serialVersionUID = 1L;

    private boolean edit = false;
    private boolean single = false;
    private int row = -1;
    private ButtonGroup group;
    private JRadioButton role;
    private JRadioButton participant;
    private String caseID;
	private String subCaseID;
	private JLabel variableNameLabel;
	private JLabel variableLabel;
	private JLabel descriptionL;
	private JScrollPane descriptionJSP;
	private JTextArea descriptionTA;
	private JScrollPane jScrollPane1;
	private JTextArea jTextArea2;
	private JLabel Variable;
	private JLabel subCase;
	private JLabel cases;
	private JComboBox jComboBox3;
	private JComboBox jComboBox2;
	private JComboBox jComboBox1;
	private JTextField variableNameTF;
	private JCheckBox resource;
	private String[] jComboBoxString1 = new String[0];
	private String[] jComboBoxString2 = new String[0];
	private String[] jComboBoxString3 = new String[0];
	private EditTemplateDialog editTemplate = null;
	private LinkedList<String[]> variables = new LinkedList<String[]>();
	private LinkedList<String> list = new LinkedList<String>();
	private LinkedList<LinkedList<String>> listParameter = new LinkedList<LinkedList<String>>();
	private LinkedList<String[]> taskNet = new LinkedList<String[]>();
	private HashMap<String, LinkedList<String[]>> varTaskNet = new HashMap<String, LinkedList<String[]>>();


    public EditVariableTemplateDialog() {
        super("Edit Variable Template", true);
        setContentPanel(getConfigurationPanel());
        getDoneButton().setEnabled(false);
        getDoneButton().addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
            	if(row == -1) {
            		if(variableNameTF.getText().contains("@")) resource.setSelected(false);
            		String resourceVar = "";
            		String description = descriptionTA.getText();
            		if(resource.isSelected() && role.isSelected()) resourceVar = "role";
            		else if(resource.isSelected() && participant.isSelected()) resourceVar = "participant";
	                variables.addLast(new String[]{ variableNameTF.getText(), jTextArea2.getText(), resourceVar, description });
	                editTemplate.showVariable();
            	}else {
            		String[] vect = variables.get(row);
            		vect[0] = variableNameTF.getText();
            		vect[1] = jTextArea2.getText();
            		String resourceVar = "";
            		if(resource.isSelected() && role.isSelected()) resourceVar = "role";
            		else if(resource.isSelected() && participant.isSelected()) resourceVar = "participant";
            		vect[2] = resourceVar;
            		vect[3] = descriptionTA.getText();
	                editTemplate.showVariable();
            	}
            	edit=false;
            	single=false;
            	variableNameTF.setText("");
            	jTextArea2.setText("");
            	descriptionTA.setText("");
            	resource.setSelected(false);
            	role.setEnabled(false);
            	participant.setEnabled(false);
            	row = -1;
            }
        });
        
        getCancelButton().addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
            	edit=false;
            	single=false;
            	variableNameTF.setText("");
            	jTextArea2.setText("");
            	descriptionTA.setText("");
            	resource.setSelected(false);
            	role.setEnabled(false);
            	participant.setEnabled(false);
            	row = -1;
            }
        });

        getRootPane().setDefaultButton(getDoneButton());
    }
    
    private JLabel getDescriptionL() {
    	descriptionL = new JLabel("Description");
    	descriptionL.setBounds(12, 261, 544, 15);
    	return descriptionL;
    }
    
    private JScrollPane getDescriptionJSP() {
    	descriptionTA = new JTextArea();
    	descriptionTA.setLineWrap(true);
    	descriptionJSP = new JScrollPane(descriptionTA);
    	descriptionJSP.setBounds(12, 282, 576, 173);
    	return descriptionJSP;
    }
    
    private String[] getjComboBoxString2() {
    	list.clear();
    	listParameter.clear();
		list.addFirst("");
		LinkedList<String> set1 = new LinkedList<String>();
		set1.add("");
		set1.add("(TimeEstimationInMillis)");
		listParameter.add(set1);
		for(String[] taskNetElem : taskNet) {
			if(new Boolean(taskNetElem[1])) {
				Set<String> set = new HashSet<String>();
				LinkedList<String[]> varTask = varTaskNet.get(taskNetElem[0]);
				if(varTask.size()>0) {
					for(String[] varTaskElem : varTask) {
						set.add(varTaskElem[0]);
					}
				}
				LinkedList<String> l = new LinkedList<String>(set);
				l.addFirst("");
				
				l.addLast("(Count)");
				
				l.addLast("(isEnabled)");
				l.addLast("(isStarted)");
				l.addLast("(isCompleted)");
				
				l.addLast("(EnablementTime)");
				l.addLast("(StartTime)");
				l.addLast("(CompleteTime)");
				l.addLast("(PassTime)");
				l.addLast("(EnablementTimeInMillis)");
				l.addLast("(StartTimeInMillis)");
				l.addLast("(CompleteTimeInMillis)");
				l.addLast("(PassTimeInMillis)");

				l.addLast("(TimeEstimationInMillis)");
				
				l.addLast("(offerResource)");
				l.addLast("(allocateResource)");
				l.addLast("(startResource)");
				l.addLast("(completeResource)");
				
				l.addLast("(offerDistribution)");
				l.addLast("(allocateDistribution)");
				l.addLast("(startDistribution)");
				
				l.addLast("(offerInitiator)");
				l.addLast("(allocateInitiator)");
				l.addLast("(startInitiator)");
				listParameter.add(l);
			}else {
				Set<String> set = new HashSet<String>();
				LinkedList<String[]> varNet = varTaskNet.get(taskNetElem[0]);
				if(varNet.size()>0) {
					for(String[] varNetElem : varNet) {
						set.add(varNetElem[0]);
					}
				}
				LinkedList<String> l = new LinkedList<String>(set);
				l.addFirst("");
				
				l.addLast("(isEnabled)");
				l.addLast("(isStarted)");
				l.addLast("(isCompleted)");
				
				l.addLast("(EnablementTime)");
				l.addLast("(StartTime)");
				l.addLast("(CompleteTime)");
				l.addLast("(PassTime)");
				l.addLast("(EnablementTimeInMillis)");
				l.addLast("(StartTimeInMillis)");
				l.addLast("(CompleteTimeInMillis)");
				l.addLast("(PassTimeInMillis)");
				
				listParameter.add(l);
			}
			list.add(taskNetElem[0]);
		}
		return list.toArray(new String[0]);
    }
    
    public void updateComboBox() {
    	jComboBox1.setSelectedIndex(0);
    	jComboBox2.setSelectedIndex(0);
    	jComboBox3.setSelectedIndex(0);
    	jComboBoxString2 = getjComboBoxString2();
    	ComboBoxModel jComboBox2Model = new DefaultComboBoxModel(jComboBoxString2);
    	jComboBox2.setModel(jComboBox2Model);
    	EditVariableTemplateDialog.this.repaint();
    }
    
    public void setAddEditTemplate(EditTemplateDialog editTemplate) {
    	this.editTemplate = editTemplate;
    }
    
    public void setListVar(LinkedList<String[]> variables) {
    	this.variables = variables;
    }
    
    public void setTaskNet(LinkedList<String[]> taskNet) {
    	this.taskNet = taskNet;
    }
    
    public void setVarTaskNet(HashMap<String, LinkedList<String[]>> varTaskNet) {
    	this.varTaskNet = varTaskNet;
    }
    
    public void setLine(int row) {
    	this.row = row;
    	edit = true;
    	single = true;
    	variableNameTF.setText(variables.get(row)[0]);
    	jTextArea2.setText(variables.get(row)[1]);
    	if("role".equals(variables.get(row)[2])) {
    		resource.setSelected(true);
    		role.setSelected(true);
    	}else if("participant".equals(variables.get(row)[2])) {
    		resource.setSelected(true);
    		participant.setSelected(true);
    	}
    	descriptionTA.setText(variables.get(row)[3]);
    }
    
    private JLabel getVariableNameLabel() {
	    variableNameLabel = new JLabel();
		variableNameLabel.setText("Name");
		variableNameLabel.setBounds(12, 12, 576, 15);
		return variableNameLabel;
    }
    
    @SuppressWarnings("serial")
	private JTextField getVariableNameTF() {
    	variableNameTF = new JTextField();
    	variableNameTF.setDocument(new PlainDocument() {
			@Override
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		        if(str!=null && str.matches("[\\w]*") && !str.equals("_")) {
		        	super.insertString(offs, str, a);
		        }
		    }
		});
		variableNameTF.setBounds(12, 33, 576, 22);
		variableNameTF.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent evt) {
				if(!edit) {
					if(variableNameTF.getText().equals(""))	single=false;
					else {
						boolean cycle = false;
						for(int i=0; i<variables.size(); i++) {
							if(variables.get(i)[0].equals(variableNameTF.getText())) {
								cycle = true;
							}
						}
						if(cycle) single=false;
						else single=true;
					}
					getDoneButton().setEnabled(check(jTextArea2.getText()) && single);
				}else {
					if(variableNameTF.getText().equals(""))	single=false;
					else {
						boolean cycle = false;
						for(int i=0; i<variables.size(); i++) {
							if(i!=row && variables.get(i)[0].equals(variableNameTF.getText())) {
								cycle = true;
							}
						}
						if(cycle) single=false;
						else single=true;
					}
					getDoneButton().setEnabled(check(jTextArea2.getText()) && single);
				}
			}
		});
		return variableNameTF;
    }
    
    private JCheckBox getResource() {
    	resource = new JCheckBox();
    	resource.setText("Resource");
    	resource.setBounds(424, 209, 164, 22);
    	resource.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(resource.isSelected()) {
					role.setEnabled(true);
					participant.setEnabled(true);
				}else {
					role.setEnabled(false);
					participant.setEnabled(false);
				}
			}
		});
    	return resource;
    }
    
    private ButtonGroup getButtonGroup() {
    	if(group == null) {
			group = new ButtonGroup();
		}
		return group;
    }
    
    private JRadioButton getRole() {
    	role = new JRadioButton();
    	role.setText("Role");
    	role.setBounds(424, 237, 60, 19);
    	role.setEnabled(false);
    	group.add(role);
    	return role;
    }
    
    private JRadioButton getParticipant() {
    	participant = new JRadioButton();
    	participant.setText("Participant");
    	participant.setBounds(484, 237, 104, 19);
    	participant.setEnabled(false);
    	group.add(participant);
    	return participant;
    }
    
    private JLabel getVariableLabel() {
	    variableLabel = new JLabel();
		variableLabel.setText("Mapping");
		variableLabel.setBounds(12, 61, 576, 15);
		return variableLabel;
    }
    
    private JScrollPane getJScrollPane1() {
	    jScrollPane1 = new JScrollPane();
		jScrollPane1.setBounds(12, 82, 400, 173);
		jScrollPane1.setViewportView(getJTextArea2());
		return jScrollPane1;
    }

    private JPanel getConfigurationPanel() {
    	JPanel panel = new JPanel();
    	setPreferredSize(new Dimension(600, 300));
    	panel.setLayout(null);
    	panel.add(getVariableNameLabel());
    	panel.add(getVariableNameTF());
		panel.add(getVariableLabel());
		panel.add(getJScrollPane1());
		panel.add(getJComboBox1());
		panel.add(getJComboBox2());
		panel.add(getJComboBox3());
		panel.add(getCase());
		panel.add(getSubCase());
		panel.add(getVariable());
		panel.add(getResource());
		getButtonGroup();
		panel.add(getRole());
		panel.add(getParticipant());
		panel.add(getDescriptionL());
		panel.add(getDescriptionJSP());
		return panel;
    }

    protected void makeLastAdjustments() {
        setSize(600, 535);
        JUtilities.setMinSizeToCurrent(this);
    }
    
    private JComboBox getJComboBox1() {
		if(jComboBox1 == null) {
			jComboBoxString1 = new String[]{ "", "Current", "Previous"};
		    ComboBoxModel jComboBox1Model = 
				new DefaultComboBoxModel(jComboBoxString1);
			jComboBox1 = new JComboBox();
			jComboBox1.setModel(jComboBox1Model);
			jComboBox1.setBounds(424, 82, 164, 22);
			jComboBox1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					int index = jComboBox1.getSelectedIndex();
					if(index == 0) { jTextArea2.setText("");}
					else if(index == 1) { 
						jTextArea2.setText("case(current).");
						caseID = "case(current)";
					}
					else { 
						jTextArea2.setText("case()");
					}
				}
			});
		}
		return jComboBox1;
	}
	
	private JComboBox getJComboBox2() {
		if(jComboBox2 == null) {
			jComboBoxString2 = getjComboBoxString2();
			ComboBoxModel jComboBox2Model = 
				new DefaultComboBoxModel(jComboBoxString2);
			jComboBox2 = new JComboBox();
			jComboBox2.setModel(jComboBox2Model);
			jComboBox2.setBounds(424, 131, 164, 22);
			jComboBox2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					int index = jComboBox2.getSelectedIndex();
					String textArea = jTextArea2.getText();
					String txt = null;
					if(textArea.contains(".")) {
						txt = textArea.substring(0, textArea.indexOf("."));
					}else {
						txt = textArea;
					}
					jTextArea2.setText(txt+"."+jComboBoxString2[index]);
					caseID = txt;
					subCaseID = jComboBoxString2[index];
					jComboBoxString3 = listParameter.get(index).toArray(new String[0]);
					updateBox3();
					EditVariableTemplateDialog.this.repaint();
				}
			});
		}
		return jComboBox2;
	}

	private JComboBox getJComboBox3() {
		if(jComboBox3 == null) {
			ComboBoxModel jComboBox3Model = 
				new DefaultComboBoxModel(jComboBoxString3);
			jComboBox3 = new JComboBox();
			jComboBox3.setModel(jComboBox3Model);
			jComboBox3.setBounds(424, 180, 164, 22);
			jComboBox3.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					int index = jComboBox3.getSelectedIndex();
					if(index == 0) {
						if(jComboBox1.getSelectedIndex() == 0) jTextArea2.setText("");
						else jTextArea2.setText(caseID+"."+subCaseID);
					}else if(subCaseID.length()==0){ 
						jTextArea2.setText(caseID+"."+jComboBoxString3[index]);
					}else {
						if(!jComboBoxString3[index].startsWith("(")) {
							jTextArea2.setText(caseID+"."+subCaseID+"."+jComboBoxString3[index]);
						}else {
							jTextArea2.setText(caseID+"."+subCaseID+jComboBoxString3[index]);
						}
					}
				}
			});
		}
		return jComboBox3;
	}
	
	private JLabel getCase() {
		if(cases == null) {
		cases = new JLabel();
		cases.setText("Case");
		cases.setBounds(424, 61, 164, 15);
		}
		return cases;
	}
	
	private JLabel getSubCase() {
		if(subCase == null) {
			subCase = new JLabel();
			subCase.setText("SubCase");
			subCase.setBounds(424, 110, 164, 15);
		}
		return subCase;
	}
	
	private JLabel getVariable() {
		if(Variable == null) {
			Variable = new JLabel();
			Variable.setText("Variable");
			Variable.setBounds(424, 159, 164, 15);
		}
		return Variable;
	}
	
	private JTextArea getJTextArea2() {
		jTextArea2 = new JTextArea();
		jTextArea2.setLineWrap(true);
		jTextArea2.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent evt) {
				if(jTextArea2.getText().contains("@")) {
					resource.setEnabled(false);
					resource.setSelected(false);
				}
				EditVariableTemplateDialog.this.getDoneButton().setEnabled(check(jTextArea2.getText()) && single);
			}
		});
		return jTextArea2;
	}
	
	private void updateBox3() {
		ComboBoxModel jComboBox3Model = 
			new DefaultComboBoxModel(jComboBoxString3);
		jComboBox3.setModel(jComboBox3Model);
	}
	
	private boolean check(String var) {
		return true;
	}
}
