package org.yawlfoundation.yawl.editor.actions.sensor.template;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.jdom.Element;
import org.yawlfoundation.yawl.editor.actions.sensor.SensorDialog;
import org.yawlfoundation.yawl.editor.actions.sensor.TaskConfiguration;
import org.yawlfoundation.yawl.editor.actions.sensor.VarTaskConfiguration;
import org.yawlfoundation.yawl.editor.actions.sensor.VariableConfiguration;
import org.yawlfoundation.yawl.editor.data.DataVariable;
import org.yawlfoundation.yawl.editor.elements.model.YAWLTask;
import org.yawlfoundation.yawl.editor.net.NetGraph;
import org.yawlfoundation.yawl.editor.net.NetGraphModel;
import org.yawlfoundation.yawl.editor.specification.SpecificationModel;
import org.yawlfoundation.yawl.editor.swing.JUtilities;
import org.yawlfoundation.yawl.util.JDOMUtil;

public class TemplateWizardDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private SensorDialog editRemoveSensorDialog = null;
	private LinkedList<String[]> variablesOriginal = new LinkedList<String[]>();
	private LinkedList<String[]> variables = new LinkedList<String[]>();
	private LinkedList<String[]> taskNet = new LinkedList<String[]>();
	private HashMap<String, LinkedList<String[]>> variablesTaskNet = new HashMap<String, LinkedList<String[]>>();
	private String sensorName = null;
	private String riskProbabilityOriginal = null;
	private String riskThresholdOriginal = null;
	private String riskMessageOriginal = null;
	private String faultConditionOriginal = null;
	private String faultMessageOriginal = null;
	private String consequenceOriginal = null;
	private String sensorTimeOriginal = null;
	
	private String riskProbability = null;
	private String riskThreshold = null;
	private String riskMessage = null;
	private String faultCondition = null;
	private String faultMessage = null;
	private String consequence = null;
	private String sensorTime = null;
	
	private JButton next, prev, done, cancel;
	private JScrollPane taskPanel = null;
	private JScrollPane varPanel = null;
	private JScrollPane sensorPanel = null;
	private JScrollPane summaryPanel = null;
	private JScrollPane actualPanel = null;
	private int numPanel = 0;
	private LinkedList<String> listTask = new LinkedList<String>();
	private LinkedList<LinkedList<String>> listParameterTask = new LinkedList<LinkedList<String>>();
	private LinkedList<String> listNet = new LinkedList<String>();
	private LinkedList<LinkedList<String>> listParameterNet = new LinkedList<LinkedList<String>>();
	private LinkedList<TaskConfiguration> listTC = new LinkedList<TaskConfiguration>();
	private LinkedList<VarTaskConfiguration> listVTC = new LinkedList<VarTaskConfiguration>();
	private LinkedList<VariableConfiguration> listVC = new LinkedList<VariableConfiguration>();
	private JLabel sensorNameLabel, variableLabel, riskProbabilityLabel, riskThresholdLabel, riskOperator, riskMessageLabel, faultMessageLabel, consequenceLabel;
	private JScrollPane variablesJSP;
	private JTable variablesTable;
	private JTextField sensorNameTF, timeTF, eventTF, consequenceTF;
	private JTextField riskProbabilityTF, riskThresholdTF, riskMessageTF, faultConditionTF, faultMessageTF;
	private JRadioButton timeRB, eventRB;
	private JCheckBox riskCB, faultCB, triggerCB;
	private ButtonGroup rbgroup = new ButtonGroup();
	private TableModel variablesTableModel;
	private JComboBox timeBox, eventBox;
	private boolean varPresent = false;
	private String[] times = new String[] { "", "Seconds", "Minutes", "Hours",
			"Days", "Weeks", "Years", "Sunday", "Monday", "Tuesday",
			"Wednesday", "Thursday", "Friday", "Saturday" };
	private String[] events = new String[] { "", "Offered", "Allocated",
			"Started", "Completed" };

	public void loadTemplate(String template) {
		Element templateElem = JDOMUtil.stringToDocument(template)
				.getRootElement();
		sensorName = templateElem.getAttributeValue("name");
		if (templateElem.getAttribute("notifyTime") != null) {
			sensorTimeOriginal = templateElem.getAttributeValue("notifyTime");
		}
		if(taskNet != null) taskNet.clear();
		if(variablesTaskNet != null) variablesTaskNet.clear();
		if(variablesOriginal != null) variablesOriginal.clear();
		Element taskNets = templateElem.getChild("taskNets");
		for (Object ele2 : taskNets.getChildren()) {
			Element taskNetElem = (Element) ele2;
			String taskNetName = taskNetElem.getAttributeValue("name");
			String taskNetType = taskNetElem.getAttributeValue("type");
			String taskNetDescription = taskNetElem.getChild("description")
					.getText();
			taskNet.add(new String[] { taskNetName, taskNetType,
					taskNetDescription });

			LinkedList<String[]> varList = new LinkedList<String[]>();
			Element varTaskNets = taskNetElem.getChild("varTaskNets");
			for (Object ele3 : varTaskNets.getChildren()) {
				Element varTaskNet = (Element) ele3;
				String varTaskNetName = varTaskNet.getAttributeValue("name");
				String varTaskNetType = varTaskNet.getAttributeValue("type");
				String varTaskNetDescription = varTaskNet.getChild(
						"varTaskNetDescription").getText();
				varList.add(new String[] { varTaskNetName, varTaskNetType,
						varTaskNetDescription });
			}
			if(varList.size()>0) variablesTaskNet.put(taskNetName, varList);
		}
		Element vars = templateElem.getChild("vars");
		for (Object ele3 : vars.getChildren()) {
			Element var = (Element) ele3;
			String varName = var.getAttributeValue("name");
			String varMapping = var.getAttributeValue("mapping");
			String varType = var.getAttributeValue("type");
			String varDescription = var.getChild("varDescription").getText();
			variablesOriginal.add(new String[] { varName, varMapping, varType,
					varDescription });
		}
		riskProbabilityOriginal = templateElem.getChildText("riskProbability");
		riskThresholdOriginal = templateElem.getChildText("riskThreshold");
		riskMessageOriginal = templateElem.getChildText("riskMessage");
		faultConditionOriginal = templateElem.getChildText("faultCondition");
		faultMessageOriginal = templateElem.getChildText("faultMessage");
		consequenceOriginal = templateElem.getChildText("consequence");
		visualizePanel(getTaskPanel());
	}

	public void visualizePanel(JScrollPane jScrollPane) {
		if (actualPanel != null) {
			getContentPane().remove(actualPanel);
		}
		actualPanel = jScrollPane;
		getContentPane().add(actualPanel, BorderLayout.CENTER);
		makeLastAdjustments();
		pack();
	}

	public TemplateWizardDialog() {
		setTitle("Add Sensor from Template");
        setModal(true);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(getDoneButton());
		buttonPanel.add(getPrevButton());
		buttonPanel.add(getNextButton());
		buttonPanel.add(getCancelButton());
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
	}

	public JButton getDoneButton() {
		done = new JButton("Done");
		done.setEnabled(false);
		done.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				done.setEnabled(false);
				if (!editRemoveSensorDialog.getYSensorEditor().contain(sensorNameTF.getText())) {
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

					editRemoveSensorDialog.getYSensorEditor().add(
							sensorNameTF.getText(), riskProbability, riskThreshold, riskMessage,
							faultCondition, faultMessage, consequence, time, variables);
				}
				
				reset();
			}
		});
		return done;
	}

	public JButton getPrevButton() {
		prev = new JButton("Prev");
		prev.setEnabled(false);
		prev.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(numPanel == 1) {
					visualizePanel(taskPanel);
					next.setEnabled(true);
					prev.setEnabled(false);
					numPanel = 0;
				} else if(numPanel == 2) {
					if(varPresent) {
						visualizePanel(varPanel);
						next.setEnabled(true);
						prev.setEnabled(true);
						numPanel = 1;
					}else {
						visualizePanel(taskPanel);
						next.setEnabled(true);
						prev.setEnabled(false);
						numPanel = 0;
					}
				} else if(numPanel == 3) {
					visualizePanel(sensorPanel);
					next.setEnabled(true);
					prev.setEnabled(true);
					done.setEnabled(false);
					numPanel = 2;
				} 
			}
		});
		return prev;
	}

	public JButton getNextButton() {
		next = new JButton("Next");
		next.setEnabled(false);
		next.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(numPanel == 0) {
					if(varPanel==null) {
						getVarPanel();
					}
					if(varPresent) {
						visualizePanel(varPanel);
						numPanel = 1;
						next.setEnabled(check());
						prev.setEnabled(true);
					}else {
						if(sensorPanel==null) {
							getSensorPanel();
						}
						visualizePanel(sensorPanel);
						numPanel = 2;
						next.setEnabled(check());
						prev.setEnabled(true);
					}
				} else if(numPanel == 1) {
					if(sensorPanel==null) {
						getSensorPanel();
					}
					visualizePanel(sensorPanel);
					numPanel = 2;
					next.setEnabled(check());
					prev.setEnabled(true);
				} else if(numPanel == 2) {
					if(summaryPanel==null) {
						getSummaryPanel();
					}
					visualizePanel(summaryPanel);
					numPanel = 3;
					next.setEnabled(false);
					prev.setEnabled(true);
					done.setEnabled(true);
				} 
			}
		});
		return next;
	}

	public JButton getCancelButton() {
		cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				reset();
			}
		});
		return cancel;
	}
	
	private void reset() {
		done.setEnabled(false);
		
		variables.clear();
		variablesOriginal.clear();
		listTask.clear();
		listParameterTask.clear();
		listNet.clear();
		listParameterNet.clear();
		listTC.clear();
		listVTC.clear();
		listVC.clear();
		
		riskProbability = null;
		riskThreshold = null;
		riskMessage = null;
		faultCondition = null;
		faultMessage = null;
		
		riskProbabilityOriginal = null;
		riskThresholdOriginal = null;
		riskMessageOriginal = null;
		faultConditionOriginal = null;
		faultMessageOriginal = null;
		
		if(riskProbabilityTF!=null) riskProbabilityTF.setText("");
		if(riskThresholdTF!=null) riskThresholdTF.setText("");
		if(riskMessageTF!=null) riskMessageTF.setText("");
		if(faultConditionTF!=null) faultConditionTF.setText("");
		if(faultMessageTF!=null) faultMessageTF.setText("");
		if(consequenceTF!=null) consequenceTF.setText("");
		
		sensorName = null;
		getContentPane().remove(actualPanel);
		actualPanel = null;
		taskPanel = null;
		varPanel = null;
		sensorPanel = null;
		summaryPanel = null;
		numPanel = 0;
		taskNet.clear();
		variablesTaskNet.clear();
		prev.setEnabled(false);
		next.setEnabled(false);
		editRemoveSensorDialog.update();
		TemplateWizardDialog.this.setVisible(false);
	}
	
	public boolean check() {
		if(numPanel==0) {
			int fixed = 0;
			int total = taskNet.size();
			for(TaskConfiguration tc : listTC) {
				if(tc.getSelectionCB().getSelectedIndex()>0) {
					fixed++;
				}
			}
			return fixed == total;
		}else if(numPanel==1) {
			int fixed = 0;
			int total = variablesTaskNet.size();
			for(VarTaskConfiguration vtc : listVTC) {
				if(vtc.getSelectionCB().getSelectedIndex()>0) {
					fixed++;
				}
			}
			return fixed == total;
		}else if(numPanel==2) {
			int fixed = 0;
			int total = variablesOriginal.size();
			for(VariableConfiguration vc : listVC) {
				if(!vc.getNewNameTF().getText().isEmpty()) {
					if(!vc.getValueTA().getText().isEmpty()) {
						fixed++;
					}
				}
			}
			return fixed == total;
		}else return false;
	}

	public void addEditRemoveDialog(SensorDialog ersd) {
		this.editRemoveSensorDialog = ersd;
	}

	public void inizialize() {
//		if (SpecificationModel.getInstance().getSensors() != null)
//			sensors = SpecificationModel.getInstance().getSensors();
//		else
//			sensors = new YSensorsEditor();
	}
	
	private String[] getjComboBoxStringTask() {
		listTask.clear();
		listParameterTask.clear();
		SpecificationModel m = SpecificationModel.getInstance();
		Set<NetGraphModel> s = m.getNets();
		listTask.addFirst("");
		LinkedList<String> set1 = new LinkedList<String>();
		listParameterTask.add(set1);
		for (NetGraphModel ngm : s) {
			NetGraph ng = ngm.getGraph();
			for (Object o : ng.getDescendants(ng.getRoots())) {
				if (o instanceof YAWLTask) {
					Set<String> set = new HashSet<String>();
					YAWLTask task = (YAWLTask) o;
					if(task.getLabel()!=null && !task.getLabel().isEmpty()) {
						listTask.add(task.getLabel());
						if (task.getVariables() != null) {
							for (int i = 0; i < task.getVariables().size(); i++) {
								set.add(task.getVariables().getNameAt(i));
							}
						}
						String[] tmp = set.toArray(new String[0]);
						Arrays.sort(tmp);
						LinkedList<String> l = new LinkedList<String>();
						for(String str : tmp) {
							l.add(str);
						}
						
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
						listParameterTask.add(l);
					}
				}
			}
		}
		String[] res = listTask.toArray(new String[0]);
		String[] res1 = listTask.toArray(new String[0]);
		Arrays.sort(res);
		listTask.clear();
		for(String str : res) {
			listTask.add(str);
		}
		LinkedList<LinkedList<String>> listParameterTask2 = new LinkedList<LinkedList<String>>();
		for(int i=0; i<res.length; i++) {
			int pos = 0;
			for(int j=0; j<res1.length; j++) {
				if(res[i].equals(res1[j])) {
					pos = j;
					break;
				}
			}
			listParameterTask2.add(listParameterTask.get(pos));
		}
		listParameterTask = listParameterTask2;
		return res;
	}
	
	private String[] getjComboBoxStringNet() {
		listNet.clear();
		listParameterNet.clear();
		SpecificationModel m = SpecificationModel.getInstance();
		Set<NetGraphModel> s = m.getNets();
		listNet.addFirst("");
		LinkedList<String> set1 = new LinkedList<String>();
		listParameterNet.add(set1);
		for (NetGraphModel ngm : s) {
			Set<String> set = new HashSet<String>();
			for (DataVariable dv : (LinkedList<DataVariable>) ngm.getGraph()
					.getNetModel().getDecomposition().getVariables()
					.getAllVariables()) {
				set.add(dv.getName());
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

			listParameterNet.add(l);
			listNet.add(ngm.getName());
		}
		String[] res = listNet.toArray(new String[0]);
		Arrays.sort(res);
		listNet.clear();
		for(String str : res) {
			listNet.add(str);
		}
		return res;
//		return listNet.toArray(new String[0]);
	}
	
	private JScrollPane getTaskPanel() {
    	JPanel p = new JPanel();
    	int width = 0;
    	if(p.getSize().getWidth()==0) {
    		width = 600;
    	}else {
    		width = (int) p.getSize().getWidth();
    	}
		p.setLayout(null);
		int y = 12;
		String[] taskBox = getjComboBoxStringTask();
		String[] netBox = getjComboBoxStringNet();
		String[] selection = null;
		for(String[] task : taskNet) {
			if(new Boolean(task[1])) {
				selection = taskBox;
			}else {
				selection = netBox;
			}
    		TaskConfiguration tc = new TaskConfiguration(task[0], task[1], task[2], selection);
    		listTC.add(tc);
    		tc.getNameL().setBounds(12, y, (int)(width-30)/2, 15);
    		p.add(tc.getNameL());
    		tc.getTypeL().setBounds(12, y+22, (int)(width-30)/2, 15);
    		p.add(tc.getTypeL());
    		JLabel map = new JLabel("Map to:");
    		map.setBounds(12, y+44, (int)(width-30)/2, 15);
    		p.add(map);
    		JComboBox jcb = tc.getSelectionCB();
    		jcb.setBounds(12, y+65, (int)(width-30)/2, 22);
    		jcb.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					varPanel=null;
					done.setEnabled(false);
					int fixed = 0;
					int total = taskNet.size();
					for(TaskConfiguration tc : listTC) {
						if(tc.getSelectionCB().getSelectedIndex()>0) {
							fixed++;
						}
					}
					if(fixed == total) {
						next.setEnabled(true);
					}else {
						next.setEnabled(false);
					}
				}
			});
    		p.add(tc.getSelectionCB());
    		JScrollPane descriptionJSP = new JScrollPane(tc.getDescriptionTA());
    		descriptionJSP.setBounds(((int)(width-30)/2)+18, y, (int)((width-30)/2), 88);
    		p.add(descriptionJSP);
    		JSeparator jsep = new JSeparator();
    		jsep.setBounds(12, y+94, width-44, 12);
    		p.add(jsep);
    		y += 100;
    	}
		p.setSize(600, y);
		p.setPreferredSize(new Dimension(600, y));
		taskPanel = new JScrollPane(p);
		return taskPanel;
    }

	private JScrollPane getVarPanel() {
		JPanel p = new JPanel();
    	int width = 0;
    	if(p.getSize().getWidth()==0) {
    		width = 600;
    	}else {
    		width = (int) p.getSize().getWidth();
    	}
		p.setLayout(null);
		int y = 12;
		for(int i=0; i<taskNet.size(); i++) {
			String[] task = taskNet.get(i);
			LinkedList<String[]> varTaskNet = variablesTaskNet.get(task[0]);
			if(varTaskNet!=null) {
				for(String[] var : varTaskNet) {
					varPresent = true;
					String[] selection = null;
					String father = null;
					if(listTC.get(i).getTypeL().getText().equals("Type: Task")) {
						selection = listParameterTask.get(listTC.get(i).getSelectionCB().getSelectedIndex()).toArray(new String[0]);
						father = listTask.get(listTC.get(i).getSelectionCB().getSelectedIndex());
					}else {
						selection = listParameterNet.get(listTC.get(i).getSelectionCB().getSelectedIndex()).toArray(new String[0]);
						father = listNet.get(listTC.get(i).getSelectionCB().getSelectedIndex());
					} 
		    		VarTaskConfiguration vtc = new VarTaskConfiguration(var[0], var[1], var[2], selection);
		    		listVTC.add(vtc);
		    		vtc.getNameL().setBounds(12, y, (int)(width-30)/2, 15);
		    		p.add(vtc.getNameL());
		    		vtc.getTypeL().setBounds(12, y+22, (int)(width-30)/2, 15);
		    		p.add(vtc.getTypeL());
		    		JLabel map = new JLabel("Belongs to "+father+" map to:");
		    		map.setBounds(12, y+44, (int)(width-30)/2, 15);
		    		p.add(map);
		    		JComboBox jcb = vtc.getSelectionCB();
		    		jcb.setBounds(12, y+65, (int)(width-30)/2, 22);
		    		jcb.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							sensorPanel = null;
							done.setEnabled(false);
							int fixed = 0;
							int total = variablesTaskNet.size();
							for(VarTaskConfiguration vtc : listVTC) {
								if(vtc.getSelectionCB().getSelectedIndex()>0) {
									fixed++;
								}
							}
							if(fixed == total) {
								next.setEnabled(true);
							}else {
								next.setEnabled(false);
							}
						}
					});
		    		p.add(vtc.getSelectionCB());
		    		JScrollPane descriptionJSP = new JScrollPane(vtc.getDescriptionTA());
		    		descriptionJSP.setBounds((int)(((width-30)/2)+18), y, (int)((width-30)/2), 88);
		    		p.add(descriptionJSP);
		    		JSeparator jsep = new JSeparator();
		    		jsep.setBounds(12, y+94, width-44, 12);
		    		p.add(jsep);
		    		y += 100;
				}
			}
    	}
		p.setSize(600, y);
		p.setPreferredSize(new Dimension(600, y));
		varPanel = new JScrollPane(p);
		return varPanel;
	}

	private JScrollPane getSensorPanel() {
		JPanel p = new JPanel();
    	int width = 0;
    	if(p.getSize().getWidth()==0) {
    		width = 600;
    	}else {
    		width = (int) p.getSize().getWidth();
    	}
		p.setLayout(null);
		int y = 12;
		createVariables();
		for(String[] var : variables) {
			VariableConfiguration vc = new VariableConfiguration(var[0], var[2], var[3], var[1]);
    		listVC.add(vc);
    		vc.getNameL().setBounds(12, y+3, 48, 15);
    		p.add(vc.getNameL());
    		JTextField jtf1 = vc.getNewNameTF();
    		jtf1.setBounds(60, y, (int)(width-30)/2-48, 22);
    		jtf1.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					summaryPanel = null;
					int fixed = 0;
					int total = variables.size();
					for(VariableConfiguration vc : listVC) {
						if(!vc.getNewNameTF().getText().isEmpty()) {
							fixed++;
						}
					}
					if(fixed == total) {
						next.setEnabled(true);
					}else {
						next.setEnabled(false);
					}
				}
			});
    		p.add(vc.getNewNameTF());
    		vc.getTypeL().setBounds(12, y+28, (int)(width-30)/2, 15);
    		p.add(vc.getTypeL());
    		JLabel map = new JLabel("Value:");
    		map.setBounds((int)(((width-30)/2)+18), y+3, (int)((width-30)/2), 15);
    		p.add(map);
    		JScrollPane jsp1 = new JScrollPane(vc.getValueTA());
    		jsp1.setBounds((int)(((width-30)/2)+18), y+24, (int)((width-30)/2), 92);
    		vc.getValueTA().addCaretListener(new CaretListener() {
				
				@Override
				public void caretUpdate(CaretEvent arg0) {
					summaryPanel = null;
					int fixed = 0;
					int total = variables.size();
					for(VariableConfiguration vc : listVC) {
						if(!vc.getValueTA().isEnabled()) {
							fixed++;
						}else if(!vc.getValueTA().getText().isEmpty()) {
							fixed++;
						}
					}
					if(fixed == total) {
						next.setEnabled(true);
					}else {
						next.setEnabled(false);
					}
				}
			});
    		p.add(jsp1);
    		JScrollPane descriptionJSP = new JScrollPane(vc.getDescriptionTA());
    		descriptionJSP.setBounds(12, y+49, (int)((width-30)/2), 64);
    		p.add(descriptionJSP);
    		JSeparator jsep = new JSeparator();
    		jsep.setBounds(12, y+119, width-44, 12);
    		p.add(jsep);
    		y += 125;
		}
		p.setSize(600, y);
		p.setPreferredSize(new Dimension(600, y));
		sensorPanel = new JScrollPane(p);
		return sensorPanel;
	}
	
	private JScrollPane getSummaryPanel() {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		for(int i=0; i<variablesOriginal.size(); i++) {
			VariableConfiguration vc = listVC.get(i);
			variables.get(i)[0] = vc.getNewNameTF().getText();
			variables.get(i)[1] = vc.getValueTA().getText();
			hashMap.put(variablesOriginal.get(i)[0], variables.get(i)[0]);
		}
		String[] keys = hashMap.keySet().toArray(new String[0]);
		Arrays.sort(keys);
		riskProbability = riskProbabilityOriginal;
		riskThreshold = riskThresholdOriginal;
		riskMessage = riskMessageOriginal;
		faultCondition = faultConditionOriginal;
		faultMessage = faultMessageOriginal;
		consequence = consequenceOriginal;
		for(int i=keys.length-1; i>=0; i--) {
			String key = keys[i];
			riskProbability = riskProbability.replace(key, hashMap.get(key));
			riskThreshold = riskThreshold.replace(key, hashMap.get(key));
			if(riskMessage!=null) riskMessage = riskMessage.replace("$"+key+"$", "$"+hashMap.get(key)+"$");
			faultCondition = faultCondition.replace(key, hashMap.get(key));
			if(faultMessage!=null) faultMessage = faultMessage.replace("$"+key+"$", "$"+hashMap.get(key)+"$");
		}
		JPanel p = new JPanel();
    	p.setLayout(null);
		p.add(getSensorNameLabel());
		p.add(getSensorNameTF());
		p.add(getVariableLabel());
		p.add(getVariableJScrollPane());
		p.add(getRiskCB());
		p.add(getRiskProbabilityLabel());
		p.add(getRiskThresholdLabel());
		p.add(getRiskProbabilityJTextField());
		p.add(getRiskOperatorLabel());
		p.add(getRiskThresholdJTextField());
		p.add(getFaultCB());
		p.add(getFaultConditionJTextField());
		p.add(getTimeRB());
		p.add(getTimeTF());
		p.add(getTimeComboBox());
		p.add(getEventRB());
		p.add(getEventTF());
		p.add(getEventComboBox());
		p.add(getTriggerCB());
		p.add(getRiskMessageLabel());
		p.add(getRiskMessageJTextField());
		p.add(getFaultMessageLabel());
		p.add(getFaultMessageJTextField());
		p.add(getConsequenceLabel());
		p.add(getConsequenceJTextField());
		
		sensorNameTF.setText(sensorName);
		if (sensorTime != null) {
			String specificTime = sensorTime;
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
				}
			} else {
				triggerCB.setSelected(false);
			}
		}
		if(riskMessage != null) {
			riskMessageTF.setText(riskMessage);
		}
		if(faultMessage != null) {
			faultMessageTF.setText(faultMessage);
		}
		
		if(!riskProbability.isEmpty()) {
			riskCB.setSelected(true);
		}else riskCB.setSelected(false);
		if(!faultCondition.isEmpty()) {
			faultConditionTF.setText(faultCondition);
			faultCB.setSelected(true);
		}else faultCB.setSelected(false);
		
		riskCB.getActionListeners()[0].actionPerformed(null);
		faultCB.getActionListeners()[0].actionPerformed(null);

		summaryPanel = new JScrollPane(p);
		return summaryPanel;
	}

	private void createVariables() {
		variables.clear();
		sensorTime = sensorTimeOriginal;
		for(String[] variable : variablesOriginal) {
			String mapping = variable[1];
			for(int i=0; i<listTC.size(); i++) {
				TaskConfiguration tc = listTC.get(i);
				String taskOriginalName = tc.getNameL().getText().substring(6);
				String taskFinalName = null;
				boolean task = tc.getTypeL().getText().equals("Type: Task"); 
				if(task) {
					taskFinalName = listTask.get(tc.getSelectionCB().getSelectedIndex());
				}else {
					taskFinalName = listNet.get(tc.getSelectionCB().getSelectedIndex());
				}
				if(sensorTime!= null && sensorTime.contains("_"+taskOriginalName+"_")) {
					sensorTime = sensorTime.replace("_"+taskOriginalName+"_", "_"+taskFinalName+"_");
				}
				if(mapping.contains(taskOriginalName+".")) {
					HashMap<String, String> hashMap = new HashMap<String, String>();
					for(int j=0; j<listVTC.size(); j++) {
						VarTaskConfiguration vtc = listVTC.get(j);
						String varTaskOriginalName = vtc.getNameL().getText().substring(6);
						String varTaskFinalName = null;
						if(task) {
							varTaskFinalName = listParameterTask.get(tc.getSelectionCB().getSelectedIndex()).get(vtc.getSelectionCB().getSelectedIndex());
						}else {
							varTaskFinalName = listParameterNet.get(tc.getSelectionCB().getSelectedIndex()).get(vtc.getSelectionCB().getSelectedIndex());
						}
						hashMap.put(varTaskOriginalName, varTaskFinalName);
					}
					String[] keys = hashMap.keySet().toArray(new String[0]);
					Arrays.sort(keys);
					for(int j=keys.length-1; j>=0; j--) {
						String key = keys[j];
						mapping = mapping.replace(taskOriginalName+"."+key, taskFinalName+"."+hashMap.get(key));
					}
				}else if(mapping.contains(taskOriginalName+"(")) {
					mapping = mapping.replace(taskOriginalName+"(", taskFinalName+"(");
				}
			}
			variables.add(new String[] {variable[0], mapping, variable[2], variable[3]});
		}
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
		return sensorNameTF;
	}

	private JCheckBox getTriggerCB() {
		triggerCB = new JCheckBox();
		triggerCB.setSelected(false);
		triggerCB.setText("Trigger");
		triggerCB.setBounds(12, 342, 576, 15);
		triggerCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (triggerCB.isSelected()) {
					timeRB.setEnabled(true);
					eventRB.setEnabled(true);
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
		timeRB.setBounds(12, 363, 114, 15);
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
		timeTF.setBounds(127, 360, 300, 22);

		return timeTF;
	}

	private JComboBox getTimeComboBox() {
		timeBox = new JComboBox();
		timeBox.setModel(new DefaultComboBoxModel(times));
		timeBox.setBounds(433, 360, 155, 21);
		timeBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (TemplateWizardDialog.this.timeBox.getSelectedIndex() > 6) {
					TemplateWizardDialog.this.timeTF.setText("");
					TemplateWizardDialog.this.timeTF.setEnabled(false);
				} else {
					TemplateWizardDialog.this.timeTF.setEnabled(true);
				}
			}
		});
		return timeBox;
	}

	private JRadioButton getEventRB() {
		eventRB = new JRadioButton();
		eventRB.setText("Event");
		eventRB.setBounds(12, 390, 114, 15);
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
		eventTF.setBounds(127, 387, 300, 22);
		return eventTF;
	}

	private JComboBox getEventComboBox() {
		eventBox = new JComboBox();
		eventBox.setEnabled(false);
		eventBox.setModel(new DefaultComboBoxModel(events));
		eventBox.setBounds(433, 387, 155, 21);
		eventBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (TemplateWizardDialog.this.eventBox.getSelectedIndex() > 6) {
					TemplateWizardDialog.this.eventTF.setText("");
					TemplateWizardDialog.this.eventTF.setEnabled(false);
				} else {
					TemplateWizardDialog.this.eventTF.setEnabled(true);
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
		variablesJSP.setBounds(12, 82, 576, 89);
		variablesTableModel = new DefaultTableModel(getMatrixVar(),
				new String[] { "Variables", "Mapping", "Resource" });
		variablesTable = new JTable();
		variablesJSP.setViewportView(variablesTable);
		variablesTable.setModel(variablesTableModel);
		variablesTable.setEnabled(false);
		return variablesJSP;
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
			    	area.setText(TemplateWizardDialog.this.riskProbabilityTF.getText());
			    	area.setWrapStyleWord(true);
			    	area.setLineWrap(true);
			    	JScrollPane pane = new JScrollPane(area);
			    	int result = JOptionPane.showOptionDialog(TemplateWizardDialog.this, new Object[] {"Insert Risk Probability:", pane}, "Risk Probability", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			    	if(result == 0) {
			    		TemplateWizardDialog.this.riskProbabilityTF.setText(area.getText());
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
			    	area.setText(TemplateWizardDialog.this.riskThresholdTF.getText());
			    	area.setWrapStyleWord(true);
			    	area.setLineWrap(true);
			    	JScrollPane pane = new JScrollPane(area);
			    	int result = JOptionPane.showOptionDialog(TemplateWizardDialog.this, new Object[] {"Insert Risk Threshold:", pane}, "Risk Threshold", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			    	if(result == 0) {
			    		TemplateWizardDialog.this.riskThresholdTF.setText(area.getText());
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
		faultCB.setBounds(12, 246, 576, 15);
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
		faultConditionTF.setBounds(12, 267, 578, 21);
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
			    	area.setText(TemplateWizardDialog.this.faultConditionTF.getText());
			    	area.setWrapStyleWord(true);
			    	area.setLineWrap(true);
			    	JScrollPane pane = new JScrollPane(area);
			    	int result = JOptionPane.showOptionDialog(TemplateWizardDialog.this, new Object[] {"Insert Fault Condition:", pane}, "Fault Condition", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			    	if(result == 0) {
			    		TemplateWizardDialog.this.faultConditionTF.setText(area.getText());
			    	}
			    }
			  }
		});
		return faultConditionTF;
	}
	
	private JLabel getRiskMessageLabel() {
		riskMessageLabel = new JLabel();
		riskMessageLabel.setText("Risk Message");
		riskMessageLabel.setBounds(12, 415, 576, 15);
		return riskMessageLabel;
	}

	private JTextField getRiskMessageJTextField() {
		riskMessageTF = new JTextField();
		riskMessageTF.setBounds(12, 436, 578, 21);
		riskMessageTF.setEditable(false);
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
				    	area.setText(TemplateWizardDialog.this.riskMessageTF.getText());
				    	area.setWrapStyleWord(true);
				    	area.setLineWrap(true);
				    	JScrollPane pane = new JScrollPane(area);
				    	int result = JOptionPane.showOptionDialog(TemplateWizardDialog.this, new Object[] {"Insert Risk Message:", pane}, "Risk Message", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				    	if(result == 0) {
				    		TemplateWizardDialog.this.riskMessageTF.setText(area.getText());
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
		faultMessageLabel.setBounds(12, 463, 578, 15);
		return faultMessageLabel;
	}

	private JTextField getFaultMessageJTextField() {
		faultMessageTF = new JTextField();
		faultMessageTF.setBounds(12, 480, 578, 21);
		faultMessageTF.setEditable(false);
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
				    	area.setText(TemplateWizardDialog.this.faultMessageTF.getText());
				    	area.setWrapStyleWord(true);
				    	area.setLineWrap(true);
				    	JScrollPane pane = new JScrollPane(area);
				    	int result = JOptionPane.showOptionDialog(TemplateWizardDialog.this, new Object[] {"Insert Fault Message:", pane}, "Fault Message", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				    	if(result == 0) {
				    		TemplateWizardDialog.this.faultMessageTF.setText(area.getText());
				    	}
			    	}
			    }
			  }
		});
		return faultMessageTF;
	}
	
	private JLabel getConsequenceLabel() {
		consequenceLabel = new JLabel("Consequence");
		consequenceLabel.setBounds(12, 294, 578, 15);
		return consequenceLabel;
	}

	private JTextField getConsequenceJTextField() {
		consequenceTF = new JTextField();
		consequenceTF.setBounds(12, 315, 578, 21);
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
			    	area.setText(TemplateWizardDialog.this.consequenceTF.getText());
			    	area.setWrapStyleWord(true);
			    	area.setLineWrap(true);
			    	JScrollPane pane = new JScrollPane(area);
			    	int result = JOptionPane.showOptionDialog(TemplateWizardDialog.this, new Object[] {"Insert Consequence:", pane}, "Consequence", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			    	if(result == 0) {
			    		TemplateWizardDialog.this.consequenceTF.setText(area.getText());
				    	}
			    }
			  }
		});
		return consequenceTF;
	}
	
	public String[][] getMatrixVar() {
		String[][] matrix = new String[variables.size()][3];
		int i = 0;
		for (String[] line : variables) {
			matrix[i][0] = line[0];
			matrix[i][1] = line[1];
			if(line[2].isEmpty()) {
				matrix[i][2] = "false";
			}else {
				matrix[i][2] = "true";
			}
			i++;
		}
		return matrix;
	}

	protected void makeLastAdjustments() {
		// pack();
		setSize(new Dimension(600, 400));
		JUtilities.setMinSizeToCurrent(this);
	}

}