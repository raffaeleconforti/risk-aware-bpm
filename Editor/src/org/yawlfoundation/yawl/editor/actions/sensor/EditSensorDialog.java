package org.yawlfoundation.yawl.editor.actions.sensor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.yawlfoundation.yawl.editor.YAWLEditor;
import org.yawlfoundation.yawl.editor.swing.AbstractDoneDialog;
import org.yawlfoundation.yawl.editor.swing.JUtilities;

public class EditSensorDialog extends AbstractDoneDialog {
	private static final long serialVersionUID = 1L;

	private static final EditVariableDialog dialog = new EditVariableDialog();
	private boolean invokedAtLeastOnce = false;

	private SensorDialog editRemove;
	private String sensorNameOld = null;
	private boolean edit = false;
	private JLabel sensorNameLabel, variableLabel, riskProbabilityLabel, riskThresholdLabel, riskMessageLabel, riskOperator, faultMessageLabel, consequenceLabel;
	private JScrollPane variablesJSP;
	private JTextField riskProbabilityTF, riskThresholdTF, riskMessageTF, faultConditionTF, faultMessageTF, consequenceTF;
	private JButton removeVariableB;
	private JButton editVariableB;
	private JButton addVariableB;
	private JTable variablesTable;
	public JTextField sensorNameTF;
	private JTextField timeTF;
	private JTextField eventTF;
	private JRadioButton timeRB;
	private JRadioButton eventRB;
	private JCheckBox riskCB;
	private JCheckBox faultCB;
	private JCheckBox triggerCB;
	private ButtonGroup rbgroup = new ButtonGroup();
	private TableModel variablesTableModel;
	private JComboBox timeBox;
	private JComboBox eventBox;
	private String[] times = new String[] { "", "Seconds", "Minutes", "Hours",
			"Days", "Weeks", "Years", "Sunday", "Monday", "Tuesday",
			"Wednesday", "Thursday", "Friday", "Saturday" };
	private String[] events = new String[] { "", "Offered", "Allocated",
			"Started", "Completed" };
	private LinkedList<String[]> variables = new LinkedList<String[]>();

	public static void main(String[] args) {
		EditSensorDialog f = new EditSensorDialog();
		f.setVisible(true);
	}

	public EditSensorDialog() {
		super("Edit Sensor", true);
		setContentPanel(getConfigurationPanel());
		getDoneButton().setEnabled(false);
		getDoneButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getDoneButton().setEnabled(false);
				if (!edit) {
					if (!editRemove.getYSensorEditor().contain(sensorNameTF.getText())) {
						String time = null;
						if (triggerCB.isSelected()) {
							if (timeRB.isSelected()) {
								if (timeBox.getSelectedIndex() != 0
										&& !timeTF.getText().equals(""))
									time = timeTF.getText() + "_"
											+ times[timeBox.getSelectedIndex()];
								else if (timeBox.getSelectedIndex() > 7)
									time = 1 + "_"
											+ times[timeBox.getSelectedIndex()];
							} else if (eventRB.isSelected()) {
								if (eventBox.getSelectedIndex() != 0
										&& !eventTF.getText().equals(""))
									time = "Event_"
											+ eventTF.getText()
											+ "_"
											+ events[eventBox
													.getSelectedIndex()];
							}
						}

						String riskProbability = null;
						String riskThreshold = null;
						String riskMessage = null;
						if (riskCB.isSelected()) {
							riskProbability = riskProbabilityTF.getText().replace(" ", "");
							riskThreshold = riskThresholdTF.getText().replace(" ", "");
							if(!riskThresholdTF.getText().isEmpty()) riskMessage = riskMessageTF.getText();
						}
						
						String faultCondition = null;
						String faultMessage =null;
						if (faultCB.isSelected()) {
							faultCondition = faultConditionTF.getText().replace(" ", "");
							if(!faultMessageTF.getText().isEmpty()) faultMessage = faultMessageTF.getText();
						}
						
						String consequence = consequenceTF.getText().replace(" ", "");
						if(consequence.isEmpty()) consequence = "1";
						
						editRemove.getYSensorEditor().add(
								sensorNameTF.getText(), riskProbability, riskThreshold, riskMessage,
								faultCondition, faultMessage, consequence, time, variables);
					}
				} else {
					String time = null;
					if (timeRB.isSelected()) {
						if (timeBox.getSelectedIndex() != 0
								&& !timeTF.getText().equals(""))
							time = timeTF.getText() + "_"
									+ times[timeBox.getSelectedIndex()];
						else if (timeBox.getSelectedIndex() > 7)
							time = 1 + "_" + times[timeBox.getSelectedIndex()];
					} else if (eventRB.isSelected()) {
						if (eventBox.getSelectedIndex() != 0
								&& !eventTF.getText().equals(""))
							time = eventTF.getText() + "_"
									+ events[eventBox.getSelectedIndex()];
					}

					String riskProbability = null;
					String riskThreshold = null;
					String riskMessage = null;
					if (riskCB.isSelected()) {
						if(!riskProbabilityTF.getText().isEmpty()) riskProbability = riskProbabilityTF.getText().replace(" ", "");
						if(!riskThresholdTF.getText().isEmpty()) riskThreshold = riskThresholdTF.getText().replace(" ", "");
						if(!riskMessageTF.getText().isEmpty()) riskMessage = riskMessageTF.getText();
					} 
					
					String faultCondition = null;
					String faultMessage = null;
					if (faultCB.isSelected()) {
						if(!faultConditionTF.getText().isEmpty()) faultCondition = faultConditionTF.getText().replace(" ", "");
						if(!faultMessageTF.getText().isEmpty()) faultMessage = faultMessageTF.getText();
					} 
					
					String consequence = consequenceTF.getText().replace(" ", "");
					if(consequence.isEmpty()) consequence = "1";
					
					if (sensorNameTF.getText().equals(sensorNameOld)) {
						editRemove.getYSensorEditor().update(sensorNameTF.getText(), riskProbability, riskThreshold, riskMessage, faultCondition,  faultMessage, consequence, time, variables);
					}
					if (!editRemove.getYSensorEditor().contain(sensorNameTF.getText())) {
						editRemove.getYSensorEditor().remove(sensorNameOld);
						editRemove.getYSensorEditor().add(sensorNameTF.getText(), riskProbability, riskThreshold, riskMessage, faultCondition, faultMessage, consequence, time, variables);
					}
				}
				reset();
				editRemove.update();
			}
		});
		getCancelButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getDoneButton().setEnabled(false);
				reset();
				editRemove.update();
			}
		});

		getRootPane().setDefaultButton(getDoneButton());
	}
	
	private void reset() {
		sensorNameTF.setText("");
		riskProbabilityTF.setText("");
		riskThresholdTF.setText("");
		faultConditionTF.setText("");
		triggerCB.setSelected(false);
		timeRB.setSelected(false);
		timeTF.setText("");
		timeBox.setSelectedIndex(0);
		eventRB.setSelected(false);
		eventTF.setText("");
		eventBox.setSelectedIndex(0);
		riskMessageTF.setText("");
		faultMessageTF.setText("");
		consequenceTF.setText("");
		variables = new LinkedList<String[]>();
		edit = false;
	}

	public void addEditRemoveDialog(SensorDialog ersd) {
		this.editRemove = ersd;
	}

	public void setLine(String sensorName) {
		reset();
		edit = true;
		sensorNameOld = sensorName;
		sensorNameTF.setText(sensorName);
		if (editRemove.getYSensorEditor().getTime(sensorName) != null) {
			String specificTime = editRemove.getYSensorEditor().getTime(
					sensorName);
			if (!specificTime.equals("")) {
				triggerCB.setSelected(true);
				triggerCB.getActionListeners()[0].actionPerformed(null);
				if (!(specificTime.contains("Offered")
						| specificTime.contains("Allocated")
						| specificTime.contains("Started") | specificTime
						.contains("Completed"))) {
					timeTF.setText(specificTime.substring(0,
							specificTime.indexOf("_")));
					String period = specificTime.substring(specificTime
							.indexOf("_") + 1);
					int index = 0;
					while (!period.equals(times[index]) && index < times.length) {
						index++;
					}
					if (index > 7) {
						timeTF.setText("");
						timeTF.setEnabled(false);
					}
					timeBox.setSelectedIndex(index);
					timeRB.setSelected(true);
					timeRB.getActionListeners()[0].actionPerformed(null);
					eventTF.setText("");
					eventBox.setSelectedIndex(0);
				} else {
					String time = specificTime.substring(6);
					eventTF.setText(time.substring(0, time.lastIndexOf("_")));
					String period = time.substring(time.lastIndexOf("_") + 1);
					int index = 0;
					while (!period.equals(events[index])
							&& index < events.length) {
						index++;
					}
					if (index > 7) {
						eventTF.setText("");
						eventTF.setEnabled(false);
					}
					eventBox.setSelectedIndex(index);
					eventRB.setSelected(true);
					eventRB.getActionListeners()[0].actionPerformed(null);
					timeTF.setText("");
					timeBox.setSelectedIndex(0);
				}
			} else {
				triggerCB.setSelected(false);
				eventTF.setText("");
				timeTF.setText("");
				eventBox.setSelectedIndex(0);
				timeBox.setSelectedIndex(0);
			}
		}
		
		
		if (editRemove.getYSensorEditor().getRiskProbability(sensorName) != null) {
			riskProbabilityTF.setText(editRemove.getYSensorEditor().getRiskProbability(sensorName));
			riskThresholdTF.setText(editRemove.getYSensorEditor().getRiskThreshold(sensorName));
			riskCB.setSelected(true);
			
			if (editRemove.getYSensorEditor().getRiskMessage(sensorName) != null) {
				riskMessageTF.setText(editRemove.getYSensorEditor().getRiskMessage(sensorName));
			}
		} else riskCB.setSelected(false);
		
		if (editRemove.getYSensorEditor().getFaultCondition(sensorName) != null) {
			faultConditionTF.setText(editRemove.getYSensorEditor().getFaultCondition(sensorName));
			faultCB.setSelected(true);
			
			if (editRemove.getYSensorEditor().getFaultMessage(sensorName) != null) {
				faultMessageTF.setText(editRemove.getYSensorEditor().getFaultMessage(sensorName));
			}
		} else faultCB.setSelected(false);
		
		String consequence = editRemove.getYSensorEditor().getConsequence(sensorName);
		if(consequence != null && !consequence.isEmpty()) {
			consequenceTF.setText(editRemove.getYSensorEditor().getConsequence(sensorName));
		}else consequenceTF.setText("1");
		
		riskCB.getActionListeners()[0].actionPerformed(null);
		faultCB.getActionListeners()[0].actionPerformed(null);
		variables = editRemove.getYSensorEditor().getVariable(sensorName);
	}

	private JLabel getSensorNameLabel() {
		sensorNameLabel = new JLabel();
		sensorNameLabel.setText("Name");
		sensorNameLabel.setBounds(12, 12, 576, 15);
		return sensorNameLabel;
	}

	private JTextField getSensorNameTF() {
		sensorNameTF = new JTextField();
		sensorNameTF.setDocument(new PlainDocument() {
			@Override
			public void insertString(int offs, String str, AttributeSet a)
					throws BadLocationException {
				if (str != null && str.matches("[\\w]*")) {
					super.insertString(offs, str, a);
				}
			}
		});
		sensorNameTF.setBounds(12, 33, 576, 22);
		sensorNameTF.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent evt) {
//				if (sensorNameTF.getText().equals(sensorNameOld)) {
//					single = true;
//				} else {
//					single = (!sensorNameTF.getText().equals("") && !editRemove
//							.getYSensorEditor().contain(sensorNameTF.getText()));
//					getDoneButton().setEnabled(
//							check(riskConditionText.getText()));
//				}
			}
		});
		return sensorNameTF;
	}

	private JCheckBox getTriggerCB() {
		triggerCB = new JCheckBox();
		triggerCB.setSelected(false);
		triggerCB.setText("Trigger");
		triggerCB.setBounds(12, 340, 576, 15);
		triggerCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (triggerCB.isSelected()) {
					timeRB.setEnabled(true);
					eventRB.setEnabled(true);
					// timeRB.setSelected(true);
					if (timeRB.isSelected()) {
						timeTF.setEnabled(true);
						timeBox.setEnabled(true);
					} else if (eventRB.isSelected()) {
						eventTF.setEnabled(true);
						eventBox.setEnabled(true);
					}
					timeTF.setBackground(Color.white);
					eventTF.setBackground(Color.white);
				} else {
					timeRB.setEnabled(false);
					timeTF.setEnabled(false);
					timeBox.setEnabled(false);
					eventRB.setEnabled(false);
					eventTF.setEnabled(false);
					eventBox.setEnabled(false);
					timeTF.setBackground(Color.lightGray);
					eventTF.setBackground(Color.lightGray);
				}
			}
		});
		triggerCB.getActionListeners()[0].actionPerformed(null);
		return triggerCB;
	}

	private JRadioButton getTimeRB() {
		timeRB = new JRadioButton();
		timeRB.setText("Sample Time");
		timeRB.setSelected(true);
		timeRB.setBounds(12, 361, 114, 15);
		timeRB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (timeRB.isSelected()) {
					timeTF.setEnabled(true);
					timeBox.setEnabled(true);
					eventTF.setEnabled(false);
					eventBox.setEnabled(false);
				} else {
					timeTF.setEnabled(false);
					timeBox.setEnabled(false);
					eventTF.setEnabled(true);
					eventBox.setEnabled(true);
				}
			}
		});
		rbgroup.add(timeRB);
		return timeRB;
	}

	private JTextField getTimeTF() {
		timeTF = new JTextField();
		timeTF.setDocument(new PlainDocument() {
			@Override
			public void insertString(int offs, String str, AttributeSet a)
					throws BadLocationException {
				if (str != null && str.matches("[\\d]*")
						&& str.length() + getLength() <= 64) {
					super.insertString(offs, str, a);
				}
			}
		});
		timeTF.setBounds(127, 358, 300, 22);

		return timeTF;
	}

	private JComboBox getTimeComboBox() {
		timeBox = new JComboBox();
		timeBox.setModel(new DefaultComboBoxModel(times));
		timeBox.setBounds(433, 358, 155, 21);
		timeBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (EditSensorDialog.this.timeBox.getSelectedIndex() > 6) {
					EditSensorDialog.this.timeTF.setText("");
					EditSensorDialog.this.timeTF.setEnabled(false);
				} else {
					EditSensorDialog.this.timeTF.setEnabled(true);
				}
			}
		});
		return timeBox;
	}

	private JRadioButton getEventRB() {
		eventRB = new JRadioButton();
		eventRB.setText("Event");
		eventRB.setBounds(12, 388, 114, 15);
		eventRB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (eventRB.isSelected()) {
					eventTF.setEnabled(true);
					eventBox.setEnabled(true);
					timeTF.setEnabled(false);
					timeBox.setEnabled(false);
				} else {
					eventTF.setEnabled(false);
					eventBox.setEnabled(false);
					timeTF.setEnabled(true);
					timeBox.setEnabled(true);
				}
			}
		});
		rbgroup.add(eventRB);
		return eventRB;
	}

	private JTextField getEventTF() {
		eventTF = new JTextField();
		eventTF.setEnabled(false);
		eventTF.setDocument(new PlainDocument() {
			@Override
			public void insertString(int offs, String str, AttributeSet a)
					throws BadLocationException {
				if (str != null && str.matches("[\\d\\w]*")
						&& str.length() + getLength() <= 64) {
					super.insertString(offs, str, a);
				}
			}
		});
		eventTF.setBounds(127, 385, 300, 22);
		return eventTF;
	}

	private JComboBox getEventComboBox() {
		eventBox = new JComboBox();
		eventBox.setEnabled(false);
		eventBox.setModel(new DefaultComboBoxModel(events));
		eventBox.setBounds(433, 385, 155, 21);
		eventBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (EditSensorDialog.this.eventBox.getSelectedIndex() > 6) {
					EditSensorDialog.this.eventTF.setText("");
					EditSensorDialog.this.eventTF.setEnabled(false);
				} else {
					EditSensorDialog.this.eventTF.setEnabled(true);
				}
			}
		});
		return eventBox;
	}

	private JLabel getVariableLabel() {
		variableLabel = new JLabel();
		variableLabel.setText("Variables");
		variableLabel.setBounds(12, 61, 576, 15);
		return variableLabel;
	}

	private JScrollPane getVariableJScrollPane() {
		variablesJSP = new JScrollPane();
		variablesJSP.setBounds(12, 82, 478, 89);
		variablesTableModel = new DefaultTableModel(getMatrixVar(),
				new String[] { "Variables", "Mapping", "Type" });
		variablesTable = new JTable();
		variablesJSP.setViewportView(variablesTable);
		variablesTable.setModel(variablesTableModel);
		return variablesJSP;
	}

	private JButton getAddVariableButton() {
		addVariableB = new JButton();
		addVariableB.setText("Add Var");
		addVariableB.setBounds(499, 82, 89, 22);
		addVariableB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (!invokedAtLeastOnce) {
					invokedAtLeastOnce = true;
					dialog.setLocationRelativeTo(YAWLEditor.getInstance());
					dialog.setAddSensor(EditSensorDialog.this);
				}
				dialog.setListVar(variables);
				dialog.updateComboBox();
				dialog.setVisible(true);
			}
		});
		return addVariableB;
	}

	private JLabel getRiskMessageLabel() {
		riskMessageLabel = new JLabel();
		riskMessageLabel.setText("Risk Message");
		riskMessageLabel.setBounds(12, 413, 576, 15);
		return riskMessageLabel;
	}

	private JTextField getRiskMessageJTextField() {
		riskMessageTF = new JTextField();
		riskMessageTF.setBounds(12, 434, 578, 21);
		if(riskCB.isSelected()) riskMessageTF.setEditable(true);
		else riskMessageTF.setEditable(false);
		riskMessageTF.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
			    if (e.getClickCount() >= 2) {
			    	if(riskCB.isSelected()) {
				    	JTextArea area = new JTextArea(20, 40);
				    	area.setText(EditSensorDialog.this.riskMessageTF.getText());
				    	area.setWrapStyleWord(true);
				    	area.setLineWrap(true);
				    	JScrollPane pane = new JScrollPane(area);
				    	int result = JOptionPane.showOptionDialog(EditSensorDialog.this, new Object[] {"Insert Risk Message:", pane}, "Risk Message", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				    	if(result == 0) {
				    		EditSensorDialog.this.riskMessageTF.setText(area.getText());
				    	}
			    	}
			    }
			  }
		});
		return riskMessageTF;
	}

	private JLabel getFaultMessageLabel() {
		faultMessageLabel = new JLabel();
		faultMessageLabel.setText("Fault Message");
		faultMessageLabel.setBounds(12, 461, 578, 15);
		return faultMessageLabel;
	}

	private JTextField getFaultMessageJTextField() {
		faultMessageTF = new JTextField();
		faultMessageTF.setBounds(12, 482, 578, 21);
		if(faultCB.isSelected()) faultMessageTF.setEditable(true);
		else faultMessageTF.setEditable(true);
		faultMessageTF.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
			    if (e.getClickCount() >= 2) {
			    	if(faultCB.isSelected()) {
				    	JTextArea area = new JTextArea(20, 40);
				    	area.setText(EditSensorDialog.this.faultMessageTF.getText());
				    	area.setWrapStyleWord(true);
				    	area.setLineWrap(true);
				    	JScrollPane pane = new JScrollPane(area);
				    	int result = JOptionPane.showOptionDialog(EditSensorDialog.this, new Object[] {"Insert Fault Message:", pane}, "Fault Message", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				    	if(result == 0) {
				    		EditSensorDialog.this.faultMessageTF.setText(area.getText());
				    	}
			    	}
			    }
			  }
		});
		return faultMessageTF;
	}
	
	private JLabel getConsequenceLabel() {
		consequenceLabel = new JLabel();
		consequenceLabel.setText("Consequence");
		consequenceLabel.setBounds(12, 292, 578, 15);
		return consequenceLabel;
	}

	private JTextField getConsequenceJTextField() {
		consequenceTF = new JTextField();
		consequenceTF.setBounds(12, 313, 578, 21);
		consequenceTF.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
			    if (e.getClickCount() >= 2) {
			    	JTextArea area = new JTextArea(20, 40);
			    	area.setText(EditSensorDialog.this.consequenceTF.getText());
			    	area.setWrapStyleWord(true);
			    	area.setLineWrap(true);
			    	JScrollPane pane = new JScrollPane(area);
			    	int result = JOptionPane.showOptionDialog(EditSensorDialog.this, new Object[] {"Insert Consequence:", pane}, "Consequence", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			    	if(result == 0) {
			    		EditSensorDialog.this.consequenceTF.setText(area.getText());
			    	}
			    }
			  }
		});
		return consequenceTF;
	}

	private JButton getEditVariableButton() {
		editVariableB = new JButton();
		editVariableB.setText("Edit Var");
		editVariableB.setBounds(499, 115, 89, 22);
		editVariableB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				int row = variablesTable.getSelectedRow();
				if (row >= 0) {
					if (!invokedAtLeastOnce) {
						invokedAtLeastOnce = true;
						dialog.setLocationRelativeTo(YAWLEditor.getInstance());
						dialog.setAddSensor(EditSensorDialog.this);
					}
					dialog.setListVar(variables);
					dialog.updateComboBox();
					dialog.setLine(row);
					dialog.setVisible(true);
				}
			}
		});
		return editVariableB;
	}

	private JButton getRemoveVariableButton() {
		removeVariableB = new JButton();
		removeVariableB.setText("Del Var");
		removeVariableB.setBounds(499, 148, 89, 22);
		removeVariableB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (variablesTable.getSelectedRow() >= 0) {
					variables.remove(variablesTable.getSelectedRow());
					EditSensorDialog.this.showVariable();
				}
			}
		});
		return removeVariableB;
	}

	private JCheckBox getRiskCB() {
		riskCB = new JCheckBox();
		riskCB.setSelected(true);
		riskCB.setText("Risk Condition");
		riskCB.setBounds(12, 177, 576, 15);
		riskCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (riskCB.isSelected()) {
					riskProbabilityTF.setEnabled(true);
					riskThresholdTF.setEnabled(true);
					riskMessageTF.setEnabled(true);
					riskProbabilityTF.setBackground(Color.white);
					riskThresholdTF.setBackground(Color.white);
					riskMessageTF.setBackground(Color.white);
				} else {
					riskProbabilityTF.setEnabled(false);
					riskThresholdTF.setEnabled(false);
					riskMessageTF.setEnabled(false);
					riskProbabilityTF.setBackground(Color.lightGray);
					riskThresholdTF.setBackground(Color.lightGray);
					riskMessageTF.setBackground(Color.lightGray);
				}
			}
		});
		return riskCB;
	}

	private JLabel getRiskProbabilityLabel() {
		riskProbabilityLabel = new JLabel("Risk Probability");
		riskProbabilityLabel.setBounds(12, 198, 275, 15);
		return riskProbabilityLabel;
	}
	
	private JLabel getRiskThresholdLabel() {
		riskThresholdLabel = new JLabel("Risk Threshold");
		riskThresholdLabel.setBounds(314, 198, 275, 15);
		return riskThresholdLabel;
	}
	
	private JTextField getRiskProbabilityJTextField() {
		riskProbabilityTF = new JTextField();
		riskProbabilityTF.setBounds(12, 219, 275, 21);
		riskProbabilityTF.setEditable(false);
		riskProbabilityTF.setBackground(Color.white);
		riskProbabilityTF.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
			    if (e.getClickCount() >= 2 && riskCB.isSelected()) {
			    	JTextArea area = new JTextArea(20, 40);
			    	area.setText(EditSensorDialog.this.riskProbabilityTF.getText());
			    	area.setWrapStyleWord(true);
			    	area.setLineWrap(true);
			    	JScrollPane pane = new JScrollPane(area);
			    	int result = JOptionPane.showOptionDialog(EditSensorDialog.this, new Object[] {"Insert Risk Probability:", pane}, "Risk Probability", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			    	if(result == 0) {
			    		EditSensorDialog.this.riskProbabilityTF.setText(area.getText());
			    	}
			    }
			  }
		});
		return riskProbabilityTF;
	}

	private JLabel getRiskOperatorLabel() {
		riskOperator = new JLabel();
		riskOperator.setBounds(296, 222, 15, 15);
		riskOperator.setText(">");
		return riskOperator;
	}

	private JTextField getRiskThresholdJTextField() {
		riskThresholdTF = new JTextField();
		riskThresholdTF.setBounds(314, 219, 275, 21);
		riskThresholdTF.setEditable(false);
		riskThresholdTF.setBackground(Color.white);
		riskThresholdTF.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
			    if (e.getClickCount() >= 2 && riskCB.isSelected()) {
			    	JTextArea area = new JTextArea(20, 40);
			    	area.setText(EditSensorDialog.this.riskThresholdTF.getText());
			    	area.setWrapStyleWord(true);
			    	area.setLineWrap(true);
			    	JScrollPane pane = new JScrollPane(area);
			    	int result = JOptionPane.showOptionDialog(EditSensorDialog.this, new Object[] {"Insert Risk Threshold:", pane}, "Risk Threshold", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			    	if(result == 0) {
			    		EditSensorDialog.this.riskThresholdTF.setText(area.getText());
			    	}
			    }
			  }
		});
		return riskThresholdTF;
	}	

	private JCheckBox getFaultCB() {
		faultCB = new JCheckBox();
		faultCB.setSelected(true);
		faultCB.setText("Fault Condition");
		faultCB.setBounds(12, 244, 576, 15);
		faultCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (faultCB.isSelected()) {
					faultConditionTF.setEnabled(true);
					faultMessageTF.setEnabled(true);
					faultConditionTF.setBackground(Color.white);
					faultMessageTF.setBackground(Color.white);
				} else {
					faultConditionTF.setEnabled(false);
					faultMessageTF.setEnabled(false);
					faultConditionTF.setBackground(Color.lightGray);
					faultMessageTF.setBackground(Color.lightGray);
				}
			}
		});
		return faultCB;
	}

	private JTextField getFaultConditionJTextField() {
		faultConditionTF = new JTextField();
		faultConditionTF.setBounds(12, 265, 578, 21);
		faultConditionTF.setEditable(false);
		faultConditionTF.setBackground(Color.white);
		faultConditionTF.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
			    if (e.getClickCount() >= 2 && faultCB.isSelected()) {
			    	JTextArea area = new JTextArea(20, 40);
			    	area.setText(EditSensorDialog.this.faultConditionTF.getText());
			    	area.setWrapStyleWord(true);
			    	area.setLineWrap(true);
			    	JScrollPane pane = new JScrollPane(area);
			    	int result = JOptionPane.showOptionDialog(EditSensorDialog.this, new Object[] {"Insert Fault Condition:", pane}, "Fault Condition", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			    	if(result == 0) {
			    		EditSensorDialog.this.faultConditionTF.setText(area.getText());
			    	}
			    }
			  }
		});
		return faultConditionTF;
	}

	private boolean check(String s) {
		return true;
	}

	/*
	 * private boolean check(String s) { s = s.replace(" ", ""); StringTokenizer
	 * st = new StringTokenizer(s, "()", true); int par = 0; int varPre = 0; int
	 * varPost = 0; while(st.hasMoreTokens()) { String token = st.nextToken();
	 * if(token.equals("(")) par++; else if(token.equals(")")) par--; }
	 * if(par!=0) return false; st = new StringTokenizer(s, "{(-*+/^&|!<=>)}",
	 * true); boolean enable = true; String oldToken = null; String token =
	 * null; boolean jump = false; boolean cycle = false;
	 * while(st.hasMoreTokens() || jump) { cycle = false; if(!jump) { oldToken =
	 * token; token = st.nextToken(); }else { jump = false; } for(int i=0;
	 * i<variablesTable.getRowCount(); i++) {
	 * if(token.equals(variablesTable.getValueAt(i, 0)) || token.contains("."))
	 * { cycle = true; varPost+=2; break; } } if(!cycle) { cycle = true;
	 * if(token.equals("+") || token.equals("-") || token.equals("*") ||
	 * token.equals("/") || token.equals("^") || token.equals("&") ||
	 * token.equals("|") || token.equals("!") || token.equals("(") ||
	 * token.equals(")") || token.equals(">") || token.equals("=") ||
	 * token.equals("<") || token.equals("{") || token.equals("}")){
	 * if((oldToken==null && !(token.equals("(")||token.equals("!"))) ||
	 * (!st.hasMoreTokens() && !token.equals(")"))) { cycle = false; //
	 * System.out.println(2); break; }else if(token.equals("+") ||
	 * token.equals("-") || token.equals("*") || token.equals("/") ||
	 * token.equals("^")) { try { if(!")".equals(oldToken)) { new
	 * Double(oldToken); } }catch(NumberFormatException nfe) { try {
	 * StringTokenizer stoken = new StringTokenizer(oldToken,"[:]",true);
	 * stoken.nextToken(); Integer year = null; Integer month = null; Integer
	 * day = null; Integer hour = null; Integer min = null; Integer sec = null;
	 * String value = stoken.nextToken(); if(value.equals(":")) year = new
	 * Integer(0); else { year = new Integer(value); stoken.nextToken(); } value
	 * = stoken.nextToken(); if(value.equals(":")) month = new Integer(0); else
	 * { month = new Integer(value); stoken.nextToken(); } value =
	 * stoken.nextToken(); if(value.equals(":")) day = new Integer(0); else {
	 * day = new Integer(value); stoken.nextToken(); } value =
	 * stoken.nextToken(); if(value.equals(":")) hour = new Integer(0); else {
	 * hour = new Integer(value); stoken.nextToken(); } value =
	 * stoken.nextToken(); if(value.equals(":")) min = new Integer(0); else {
	 * min = new Integer(value); stoken.nextToken(); } value =
	 * stoken.nextToken(); if(value.equals(":")) sec = new Integer(0); else {
	 * sec = new Integer(value); stoken.nextToken(); } }catch(RuntimeException
	 * rte) { boolean internalCycle = false; for(int i=0;
	 * i<variablesTable.getRowCount(); i++) {
	 * if(oldToken.equals(variablesTable.getValueAt(i, 0)) ||
	 * oldToken.contains(".")) { internalCycle = true; break; }
	 * }if(!internalCycle) { cycle = false; // System.out.println(oldToken); //
	 * System.out.println(3); break; } } } }else if(token.equals("&") ||
	 * token.equals("|") || token.equals("!")) { try { new Double(oldToken);
	 * StringTokenizer stoken = new StringTokenizer(oldToken,"[:]",true);
	 * stoken.nextToken(); Integer year = null; Integer month = null; Integer
	 * day = null; Integer hour = null; Integer min = null; Integer sec = null;
	 * String value = stoken.nextToken(); if(value.equals(":")) year = new
	 * Integer(0); else { year = new Integer(value); stoken.nextToken(); } value
	 * = stoken.nextToken(); if(value.equals(":")) month = new Integer(0); else
	 * { month = new Integer(value); stoken.nextToken(); } value =
	 * stoken.nextToken(); if(value.equals(":")) day = new Integer(0); else {
	 * day = new Integer(value); stoken.nextToken(); } value =
	 * stoken.nextToken(); if(value.equals(":")) hour = new Integer(0); else {
	 * hour = new Integer(value); stoken.nextToken(); } value =
	 * stoken.nextToken(); if(value.equals(":")) min = new Integer(0); else {
	 * min = new Integer(value); stoken.nextToken(); } value =
	 * stoken.nextToken(); if(value.equals(":")) sec = new Integer(0); else {
	 * sec = new Integer(value); stoken.nextToken(); } cycle = false; //
	 * System.out.println(4); break; }catch(RuntimeException rte) {
	 * if(token.equals("!") && oldToken==null) {} else if(token.equals("!") &&
	 * !("&".equals(oldToken) || "|".equals(oldToken) || "(".equals(oldToken))){
	 * oldToken = token; token = st.nextToken(); boolean found = false; for(int
	 * i=0; i<variablesTable.getRowCount(); i++) {
	 * if(token.equals(variablesTable.getValueAt(i, 0))) { found = true; //
	 * System.out.println(5); break; } } jump = true; if(!found) { cycle =
	 * false; // System.out.println(6); break; } } } }else if(token.equals("("))
	 * { if(!("+".equals(oldToken) || "-".equals(oldToken) ||
	 * "*".equals(oldToken) || "/".equals(oldToken) || "^".equals(oldToken) ||
	 * "&".equals(oldToken) || "|".equals(oldToken) || "!".equals(oldToken) ||
	 * "(".equals(oldToken) || "<".equals(oldToken) || ">".equals(oldToken) ||
	 * "=".equals(oldToken) || oldToken==null)) { cycle = false; //
	 * System.out.println(7); break; } }else if(token.equals(")") ||
	 * token.equals("<")) { if("+".equals(oldToken) || "-".equals(oldToken) ||
	 * "*".equals(oldToken) || "/".equals(oldToken) || "^".equals(oldToken) ||
	 * "&".equals(oldToken) || "|".equals(oldToken) || "!".equals(oldToken) ||
	 * "(".equals(oldToken) || "<".equals(oldToken) || ">".equals(oldToken) ||
	 * "=".equals(oldToken) || "{".equals(oldToken) || "}".equals(oldToken)) {
	 * cycle = false; // System.out.println(8); break; } }else
	 * if(token.equals(">")) { if("+".equals(oldToken) || "-".equals(oldToken)
	 * || "*".equals(oldToken) || "/".equals(oldToken) || "^".equals(oldToken)
	 * || "&".equals(oldToken) || "|".equals(oldToken) || "!".equals(oldToken)
	 * || "(".equals(oldToken) || "<".equals(oldToken) || ">".equals(oldToken)
	 * || "{".equals(oldToken) || "}".equals(oldToken)) { cycle = false; //
	 * System.out.println(9); break; } }else if(token.equals("=")) {
	 * if("+".equals(oldToken) || "-".equals(oldToken) || "*".equals(oldToken)
	 * || "/".equals(oldToken) || "^".equals(oldToken) || "&".equals(oldToken)
	 * || "|".equals(oldToken) || "!".equals(oldToken) || "(".equals(oldToken)
	 * || "{".equals(oldToken) || "}".equals(oldToken)) { cycle = false; //
	 * System.out.println(10); break; } else if(!("<".equals(oldToken) ||
	 * ">".equals(oldToken) || "=".equals(oldToken))) { oldToken = token; token
	 * = st.nextToken(); if(!(token.equals("=") ||token.equals(">"))) { cycle =
	 * false; // System.out.println(11); break; } if(token.equals(">")) { varPre
	 * = varPost; varPost = 0; } jump = true; } } }else if(token.equals("{") ||
	 * token.equals("}")) { oldToken = token; token = st.nextToken(); jump =
	 * true; boolean subCycle = false; for(int i=0;
	 * i<variablesTable.getRowCount(); i++) {
	 * if(oldToken.equals(variablesTable.getValueAt(i, 0)) &&
	 * variablesTable.getValueAt(i, 2).equals(true)) { subCycle = true; } }
	 * if(subCycle) { subCycle = false; for(int i=0;
	 * i<variablesTable.getRowCount(); i++) {
	 * if(token.equals(variablesTable.getValueAt(i, 0)) &&
	 * variablesTable.getValueAt(i, 2).equals(true)) { subCycle = true; } } }
	 * if(!subCycle) { cycle = false; // System.out.println(12); break; } }else
	 * { varPost++; if(token.equals("true") || token.equals("false")) varPost++;
	 * else { try { new Double(token); if("&".equals(oldToken) ||
	 * "|".equals(oldToken) || "!".equals(oldToken) || ")".equals(oldToken)){
	 * cycle = false; // System.out.println(13); break; }
	 * }catch(NumberFormatException nfe) { try { StringTokenizer stoken = new
	 * StringTokenizer(oldToken,"[:]",true); stoken.nextToken(); Integer year =
	 * null; Integer month = null; Integer day = null; Integer hour = null;
	 * Integer min = null; Integer sec = null; String value =
	 * stoken.nextToken(); if(value.equals(":")) year = new Integer(0); else {
	 * year = new Integer(value); stoken.nextToken(); } value =
	 * stoken.nextToken(); if(value.equals(":")) month = new Integer(0); else {
	 * month = new Integer(value); stoken.nextToken(); } value =
	 * stoken.nextToken(); if(value.equals(":")) day = new Integer(0); else {
	 * day = new Integer(value); stoken.nextToken(); } value =
	 * stoken.nextToken(); if(value.equals(":")) hour = new Integer(0); else {
	 * hour = new Integer(value); stoken.nextToken(); } value =
	 * stoken.nextToken(); if(value.equals(":")) min = new Integer(0); else {
	 * min = new Integer(value); stoken.nextToken(); } value =
	 * stoken.nextToken(); if(value.equals(":")) sec = new Integer(0); else {
	 * sec = new Integer(value); stoken.nextToken(); } if(!("-".equals(oldToken)
	 * || "+".equals(oldToken) || "(".equals(oldToken))){ cycle = false; //
	 * System.out.println(14); break; } }catch(RuntimeException rte) {
	 * if("-".equals(oldToken) || "+".equals(oldToken) || "*".equals(oldToken)
	 * || "/".equals(oldToken) || "^".equals(oldToken) || ")".equals(oldToken))
	 * { cycle = false; // System.out.println(15); break; } } } } } } }
	 * if(!cycle) enable = false; if(varPre<2 || varPost<2) return false;
	 * if(!s.contains("=>")) return false; else {
	 * if(s.indexOf("=>")!=s.lastIndexOf("=>")) return false; } if(!single)
	 * return false; st = new StringTokenizer(s, "(-*+/^&|!<=>)", true);
	 * while(st.hasMoreTokens()) { String tok = st.nextToken(); //
	 * System.out.println(tok); if(tok.contains(".")) { try { new Double(tok);
	 * }catch(NumberFormatException nfe) { boolean check = false; String var =
	 * tok.substring(0, tok.indexOf(".")); for(int i=0;
	 * i<variablesTable.getRowCount(); i++) {
	 * if(var.equals(variablesTable.getValueAt(i, 0))) { check = true;
	 * varPost+=2; break; } } if(!check) return false; String method =
	 * tok.substring(tok.indexOf(".")+1); if(method.contains(".")) { method =
	 * method.substring(0, method.indexOf("."));
	 * if(!(method.equals("offeredContains") ||
	 * method.equals("offeredMinNumberExcept") ||
	 * method.equals("allocatedContains") ||
	 * method.equals("allocatedMinNumberExcept") ||
	 * method.equals("startedContains") ||
	 * method.equals("startedMinNumberExcept"))) { return false; } }else {
	 * if(!(method.equals("offeredList") || method.equals("offeredNumber") ||
	 * method.equals("offeredMinNumber") || method.equals("allocatedList") ||
	 * method.equals("allocatedNumber") || method.equals("allocatedMinNumber")
	 * || method.equals("startedList") || method.equals("startedNumber") ||
	 * method.equals("startedMinNumber"))) { return false; } } } } } return
	 * enable; }
	 */

	private JPanel getConfigurationPanel() {
		JPanel panel = new JPanel();
		setPreferredSize(new Dimension(600, 300));
		panel.setLayout(null);
		panel.add(getSensorNameLabel());
		panel.add(getSensorNameTF());
		panel.add(getVariableLabel());
		panel.add(getVariableJScrollPane());
		panel.add(getAddVariableButton());
		panel.add(getEditVariableButton());
		panel.add(getRemoveVariableButton());
		panel.add(getRiskCB());
		panel.add(getRiskProbabilityLabel());
		panel.add(getRiskThresholdLabel());
		panel.add(getRiskProbabilityJTextField());
		panel.add(getRiskThresholdJTextField());
		panel.add(getRiskOperatorLabel());
		panel.add(getFaultCB());
		panel.add(getFaultConditionJTextField());
		panel.add(getTimeRB());
		panel.add(getTimeTF());
		panel.add(getTimeComboBox());
		panel.add(getEventRB());
		panel.add(getEventTF());
		panel.add(getEventComboBox());
		panel.add(getTriggerCB());
		panel.add(getRiskMessageLabel());
		panel.add(getRiskMessageJTextField());
		panel.add(getFaultMessageLabel());
		panel.add(getFaultMessageJTextField());
		panel.add(getConsequenceLabel());
		panel.add(getConsequenceJTextField());
		return panel;
	}

	

	public String[][] getMatrixVar() {
		String[][] matrix = new String[variables.size()][3];
		int i = 0;
		for (String[] line : variables) {
			matrix[i][0] = line[0];
			matrix[i][1] = line[1];
			if(line[2].isEmpty()) {
				matrix[i][2] = "data";
			}else {
				matrix[i][2] = line[2];
			}
			
			i++;
		}
		return matrix;
	}

	public void showVariable() {
		variablesTableModel = new DefaultTableModel(getMatrixVar(),
				new String[] { "Variables", "Mapping", "Type" });
		variablesTable.setModel(variablesTableModel);
		variablesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		variablesTable.getColumnModel().getColumn(0).setPreferredWidth(188);
		variablesTable.getColumnModel().getColumn(1).setPreferredWidth(208);
		variablesTable.getColumnModel().getColumn(2).setPreferredWidth(79);
    	DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
    	dtcr.setHorizontalAlignment(SwingConstants.CENTER);
    	variablesTable.getColumnModel().getColumn(2).setCellRenderer(dtcr);
		getDoneButton().setEnabled(true);
		this.repaint();
	}

	protected void makeLastAdjustments() {
		// pack();
		setSize(600, 621);
		JUtilities.setMinSizeToCurrent(this);
	}

}
