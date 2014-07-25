package org.yawlfoundation.yawl.editor.actions.sensor.template;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.jdom.Element;
import org.yawlfoundation.yawl.editor.YAWLEditor;
import org.yawlfoundation.yawl.editor.swing.AbstractDoneDialog;
import org.yawlfoundation.yawl.editor.swing.JUtilities;
import org.yawlfoundation.yawl.util.JDOMUtil;

public class EditTemplateDialog extends AbstractDoneDialog {
    private static final long serialVersionUID = 1L;
    
    private static final EditVariableTemplateDialog dialog = new EditVariableTemplateDialog();
    private static final CreateTaskNetDialog taskNetDialog = new CreateTaskNetDialog();
    private static final CreateVariableTaskNetDialog variableTaskNetdialog = new CreateVariableTaskNetDialog();
    private boolean invokedAtLeastOnce = false;
    private boolean invokedAtLeastOnceTask = false;
    private boolean invokedAtLeastOnceVariableTask = false;
    private JLabel sensorNameLabel, sensorTypeLabel, riskProbabilityLabel, riskThresholdLabel, consequenceLabel, variableLabel, riskMessageLabel, faultMessageLabel, riskOperator;
	private JScrollPane variablesJSP;
	private JTextField riskProbabilityTF, riskThresholdTF, riskMessageTF, faultConditionTF, faultMessageTF, consequenceTF;
	private JButton addVariableB, editVariableB, removeVariableB;
	private JTable variablesTable;
	private JTextField sensorNameTF, sensorTypeTF,timeTF, eventTF;
	private JRadioButton timeRB, eventRB;
	private JCheckBox riskCB, faultCB, triggerCB;
	private ButtonGroup rbgroup = new ButtonGroup();
	private TableModel variablesTableModel;
	private JComboBox typeBox, timeBox, eventBox;
    private String[] times = new String[]{"", "Seconds", "Minutes", "Hours", "Days", "Weeks", "Years", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private String[] events = new String[]{"", "Offered", "Allocated", "Started", "Completed"};
    private String[] types = null;
    private LinkedList<String[]> variables = new LinkedList<String[]>();
    private LinkedList<String[]> taskNet = new LinkedList<String[]>();
    private HashMap<String, LinkedList<String[]>> variablesTaskNet = new HashMap<String, LinkedList<String[]>>();
    private JFileChooser jfc = new JFileChooser();
    private JButton addTaskB, editTaskB, delTaskB;
    private JButton addVarB, editVarB, delVarB;
    private JLabel taskLabel, variableTaskLabel;
    private JScrollPane taskJSP, variableTaskJSP;
    private JTable taskTable, variableTaskTable;
    private TableModel taskTableModel, variableTaskTableModel;
	
    public static void main(String[] args) {
    	EditTemplateDialog f = new EditTemplateDialog();
    	f.setVisible(true);
    }
    
    private void updateTypes() {
    	String template = null;
		try {
			File f = new File("template.properties");
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
			types = template.split("\n");
			typeBox.setModel(new DefaultComboBoxModel(types));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void setPatter(String template) {
    	updateTypes();
    	Element templateElem = JDOMUtil.stringToDocument(template).getRootElement();
		sensorNameTF.setText(templateElem.getAttributeValue("name"));
		String typeSen = templateElem.getAttributeValue("riskType");
		for(int j=0; j<types.length; j++) {
			if(types[j].equals(typeSen)) {
				typeBox.setSelectedIndex(j);
			}
		}
		if (templateElem.getAttribute("notifyTime") != null) {
			String time = templateElem.getAttributeValue("notifyTime");
			if(!time.isEmpty()) {
				if(time.startsWith("Event")) {
					time = time.substring(6);
					String name = time.substring(0, time.lastIndexOf("_"));
					String eve = time.substring(time.lastIndexOf("_")+1);
					
					triggerCB.setSelected(true);
					eventRB.setEnabled(true);
					timeRB.setEnabled(true);
					
					eventRB.setSelected(true);
					eventTF.setText(name);
					eventTF.setEnabled(true);
					eventTF.setBackground(Color.white);
					int i = 0;
					for(int j=0; j<events.length; j++) {
						if(eve.equals(events[j])) {
							i = j;
							break;
						}
					}
					eventBox.setSelectedIndex(i);
					eventBox.setEnabled(true);

					timeRB.setSelected(false);
					timeTF.setEnabled(false);
					timeTF.setBackground(Color.white);
					timeBox.setSelectedIndex(0);
					timeBox.setEnabled(false);
				}else {
					String num = time.substring(0, time.lastIndexOf("_"));
					String type = time.substring(time.lastIndexOf("_")+1);
					
					triggerCB.setSelected(true);
					eventRB.setEnabled(true);
					timeRB.setEnabled(true);
					
					timeRB.setSelected(true);
					timeTF.setText(num);
					timeTF.setEnabled(true);
					timeTF.setBackground(Color.white);
					int i = 0;
					for(int j=0; j<times.length; j++) {
						if(type.equals(times[j])) {
							i = j;
							break;
						}
					}
					timeBox.setSelectedIndex(i);
					timeBox.setEnabled(true);

					eventRB.setSelected(false);
					eventTF.setBackground(Color.white);
					eventTF.setEnabled(false);
					eventBox.setSelectedIndex(0);
					eventBox.setEnabled(false);
				}
			}else {
				triggerCB.setEnabled(false);
				eventRB.setSelected(false);
				eventTF.setEnabled(false);
				eventTF.setBackground(Color.LIGHT_GRAY);
				eventBox.setSelectedIndex(0);
				eventBox.setEnabled(false);

				timeRB.setSelected(false);
				timeTF.setEnabled(false);
				timeTF.setBackground(Color.LIGHT_GRAY);
				timeBox.setSelectedIndex(0);
				timeBox.setEnabled(false);
			}
			timeRB.getActionListeners()[0].actionPerformed(null);
		}
		Element taskNets = templateElem.getChild("taskNets");
		for (Object ele2 : taskNets.getChildren()) {
			Element taskNetElem = (Element) ele2;
			String taskNetName = taskNetElem.getAttributeValue("name");
			String taskNetType = taskNetElem.getAttributeValue("type");
			String taskNetDescription = taskNetElem.getChild("description").getText();
			taskNet.add(new String[] { taskNetName, taskNetType,taskNetDescription });
			
			LinkedList<String[]> varList = new LinkedList<String[]>();
			Element varTaskNets = taskNetElem.getChild("varTaskNets");
			for (Object ele3 : varTaskNets.getChildren()) {
				Element varTaskNet = (Element) ele3;
				String varTaskNetName = varTaskNet.getAttributeValue("name");
				String varTaskNetType = varTaskNet.getAttributeValue("type");
				String varTaskNetDescription = varTaskNet.getChild("varTaskNetDescription").getText();
				varList.add(new String[] { varTaskNetName, varTaskNetType, varTaskNetDescription });
			}
			variablesTaskNet.put(taskNetName, varList);
		}
		updateTaskJSP();
		updateVariableTaskNetJSP(-1);
		
		Element vars = templateElem.getChild("vars");
		for (Object ele3 : vars.getChildren()) {
			Element var = (Element) ele3;
			String varName = var.getAttributeValue("name");
			String varMapping = var.getAttributeValue("mapping");
			String varType = var.getAttributeValue("type");
			String varDescription = var.getChild("varDescription").getText();
			variables.add(new String[] { varName, varMapping, varType,
					varDescription });
		}
		showVariable();
		
		String riskCondition = templateElem.getChildText("riskCondition");
		String riskProbability = templateElem.getChildText("riskProbability");
		String riskThreshold = templateElem.getChildText("riskThreshold");
		String riskMessage = templateElem.getChildText("riskMessage");
		String faultCondition = templateElem.getChildText("faultCondition");
		String faultMessage = templateElem.getChildText("faultMessage");
		
		if(!riskCondition.isEmpty()) {
			riskCB.setSelected(true);

			riskProbabilityTF.setEnabled(true);
			riskProbabilityTF.setText(riskProbability);
			riskProbabilityTF.setBackground(Color.white);

			riskThresholdTF.setEnabled(true);
			riskThresholdTF.setText(riskThreshold);
			riskThresholdTF.setBackground(Color.white);
			
			riskMessageTF.setEnabled(true);
			riskMessageTF.setText(riskMessage);
			riskMessageTF.setBackground(Color.white);
		}else {
			riskCB.setSelected(false);
			
			riskProbabilityTF.setEnabled(false);
			riskProbabilityTF.setText("");
			riskProbabilityTF.setBackground(Color.lightGray);

			riskThresholdTF.setEnabled(false);
			riskThresholdTF.setText("");
			riskThresholdTF.setBackground(Color.lightGray);
			
			riskMessageTF.setEnabled(false);
			riskMessageTF.setText("");
			riskMessageTF.setBackground(Color.lightGray);
		}
		if(!faultCondition.isEmpty()) {
			faultCB.setSelected(true);
			faultConditionTF.setEnabled(true);
			faultConditionTF.setText(faultCondition);
			faultConditionTF.setBackground(Color.white);
			
			faultMessageTF.setEnabled(true);
			faultMessageTF.setText(faultMessage);
			faultMessageTF.setBackground(Color.white);
		}else {
			faultCB.setSelected(false);
			faultConditionTF.setEnabled(false);
			faultConditionTF.setText("");
			faultConditionTF.setBackground(Color.lightGray);
			
			faultMessageTF.setEnabled(false);
			faultMessageTF.setText("");
			faultMessageTF.setBackground(Color.lightGray);
		}
	}

	public EditTemplateDialog() {
        super("Create Template", true);
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
        setContentPanel(getConfigurationPanel());
        getDoneButton().setEnabled(false);
        getDoneButton().addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
            	getDoneButton().setEnabled(false);
            	Element sensor = new Element("sensor");
        		sensor.setAttribute("name", sensorNameTF.getText());
        		if(typeBox.getSelectedIndex()>0) {
        			sensor.setAttribute("riskType", types[typeBox.getSelectedIndex()]);
        		}else {
        			sensor.setAttribute("riskType", sensorTypeTF.getText());
        		}
        		if(triggerCB.isSelected()) {
        			if(timeRB.isSelected()) {
        				String time = null;
        				if(timeBox.getSelectedIndex()!=0 && !timeTF.getText().equals("")) time = timeTF.getText()+"_"+times[timeBox.getSelectedIndex()];
	            		else if(timeBox.getSelectedIndex()>7) time = 1+"_"+times[timeBox.getSelectedIndex()];
        				sensor.setAttribute("notifyTime", time);
        			}else if(eventRB.isSelected()) {
        				String time = null;
        				if(eventBox.getSelectedIndex()!=0 && !eventTF.getText().equals("")) time = "Event_"+eventTF.getText()+"_"+events[eventBox.getSelectedIndex()];
        				sensor.setAttribute("notifyTime", time);
        			}
        		}
        		Element taskNetsElem = new Element("taskNets");
        		if(taskNet.size()>0){
        			for(String[] t: taskNet) {
        				Element taskNetElem = new Element("taskNet");
        				
        				taskNetElem.setAttribute("name", t[0]);
        				taskNetElem.setAttribute("type", t[1]);
        				
        				Element description = new Element("description");
        				description.setText(t[2]);
        				
        				Element varsTaskNetElem = new Element("varTaskNets");
        				LinkedList<String[]> varList = variablesTaskNet.get(t[0]);
        				for(String[] t1: varList) {
        					Element varTaskNetElem = new Element("varTaskNet");
        					
            				varTaskNetElem.setAttribute("name", t1[0]);
            				varTaskNetElem.setAttribute("type", t1[1]);
            				
            				Element varDescription = new Element("varTaskNetDescription");
            				varDescription.setText(t1[2]);
            				
            				varTaskNetElem.addContent(varDescription);
            				
            				varsTaskNetElem.addContent(varTaskNetElem);
        				}
        				
        				taskNetElem.addContent(varsTaskNetElem);
        				taskNetElem.addContent(description);
        				
        				taskNetsElem.addContent(taskNetElem);
        			}
        		}
        		sensor.addContent(taskNetsElem);
        		Element vars = new Element("vars");
        		if(variables.size()>0){
        			for(String[] variable : variables) {
        				Element var = new Element("var");
        				var.setAttribute("name", variable[0]);
        				var.setAttribute("mapping", variable[1]);
        				var.setAttribute("type", variable[2]); 
        				
        				Element varDescription = new Element("varDescription");
        				varDescription.setText(variable[3]);
        				var.addContent(varDescription);
        				
        				vars.addContent(var);
        			}
        		}
        		sensor.addContent(vars);
        		
        		Element riskCondition = new Element("riskCondition");
        		Element riskProbability = new Element("riskProbability");
        		Element riskThreshold = new Element("riskThreshold");
        		Element riskMessage = new Element("riskMessage");
        		Element faultCondition = new Element("faultCondition");
        		Element faultProbability = new Element("faultProbability");
        		Element faultThreshold = new Element("faultThreshold");
        		Element faultMessage = new Element("faultMessage");
        		Element consequence = new Element("consequence");

        		riskProbability.setText(riskProbabilityTF.getText());
        		riskThreshold.setText(riskThresholdTF.getText());
        		riskMessage.setText(riskMessageTF.getText());
        		faultCondition.setText(faultConditionTF.getText());
        		faultMessage.setText(faultMessageTF.getText());
        		consequence.setText(consequenceTF.getText());
        		
        		sensor.addContent(riskCondition);
        		sensor.addContent(riskProbability);
        		sensor.addContent(riskThreshold);
        		sensor.addContent(riskMessage);
        		sensor.addContent(faultCondition);
        		sensor.addContent(faultProbability);
        		sensor.addContent(faultThreshold);
        		sensor.addContent(faultMessage);
        		sensor.addContent(consequence);
        		
        		int res = jfc.showSaveDialog(null);
        		if(res == 0) {
        			File f = jfc.getSelectedFile();
        			if(!f.getPath().endsWith(".stemplate")) {
        				f = new File(f.getAbsolutePath()+".stemplate");
        			}
					try {
						FileWriter fw = new FileWriter(f);
	        			fw.write(JDOMUtil.elementToString(sensor));
	        			fw.flush();
	        			fw.close();
					} catch (IOException e1) {
						System.out.println("error");
						e1.printStackTrace();
					}
	            }
        		
				sensorNameTF.setText("");
            	
            	riskProbabilityTF.setText("");
            	riskThresholdTF.setText("");
            	riskMessageTF.setText("");
            	faultConditionTF.setText("");
            	faultMessageTF.setText("");
            	consequenceTF.setText("");
            	
            	timeTF.setText("");
            	timeBox.setSelectedIndex(0);
            	variables = new LinkedList<String[]>();
            	taskNet = new LinkedList<String[]>();
            	variablesTaskNet = new HashMap<String, LinkedList<String[]>>();
            	updateTaskJSP();
            	updateVariableTaskNetJSP(-1);
            	updateVariableJSP();
//        		EditTemplateDialog.this.repaint();
        		        		
            }
        });
        getCancelButton().addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
            	getDoneButton().setEnabled(false);
            	sensorNameTF.setText("");

            	riskProbabilityTF.setText("");
            	riskThresholdTF.setText("");
            	riskMessageTF.setText("");
            	faultConditionTF.setText("");
            	faultMessageTF.setText("");
            	consequenceTF.setText("");
            	
            	timeTF.setText("");
            	timeBox.setSelectedIndex(0);
            	variables = new LinkedList<String[]>();
            	taskNet = new LinkedList<String[]>();
            	variablesTaskNet = new HashMap<String, LinkedList<String[]>>();
            	updateTaskJSP();
            	updateVariableTaskNetJSP(-1);
            	updateVariableJSP();
//            	EditTemplateDialog.this.repaint();
            }
        });

        getRootPane().setDefaultButton(getDoneButton());
    }
    
    private JLabel getSensorNameLabel() {
    	sensorNameLabel = new JLabel();
		sensorNameLabel.setText("Name");
		sensorNameLabel.setBounds(12, 12, 260, 15);
		return sensorNameLabel;
    }
    
    @SuppressWarnings("serial")
	private JTextField getSensorNameTF(){
    	sensorNameTF = new JTextField();
    	sensorNameTF.setDocument(new PlainDocument() {
			@Override
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		        if(str!=null && str.matches("[\\w]*")) {
		        	super.insertString(offs, str, a);
		        }
		    }
		});
		sensorNameTF.setBounds(12, 33, 260, 22);
		sensorNameTF.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent evt) {
				getDoneButton().setEnabled(!sensorNameTF.getText().equals(""));
			}
		});
		return sensorNameTF;
    }
    
    private JLabel getSensorTypeLabel() {
    	sensorTypeLabel = new JLabel();
    	sensorTypeLabel.setText("Risk Type");
    	sensorTypeLabel.setBounds(281, 12, 274, 15);
		return sensorTypeLabel;
    }
    
    @SuppressWarnings("serial")
	private JTextField getSensorTypeTF(){
    	sensorTypeTF = new JTextField();
    	sensorTypeTF.setDocument(new PlainDocument() {
			@Override
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		        if(str!=null && str.matches("[\\w]*")) {
		        	super.insertString(offs, str, a);
		        }
		    }
		});
    	sensorTypeTF.setBounds(281, 33, 146, 22);
    	sensorTypeTF.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent evt) {
				getDoneButton().setEnabled(!sensorNameTF.getText().isEmpty() && !(sensorTypeTF.getText().isEmpty() && sensorTypeTF.isEnabled()));
			}
		});
		return sensorTypeTF;
    }
    
    private JComboBox getTypeComboBox(){
    	typeBox = new JComboBox();
    	typeBox.setEnabled(true);
    	updateTypes();
    	typeBox.setModel(new DefaultComboBoxModel(types));
    	typeBox.setBounds(433, 33, 155, 22);
    	typeBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(EditTemplateDialog.this.typeBox.getSelectedIndex()==0) {
					EditTemplateDialog.this.sensorTypeTF.setText("");
					EditTemplateDialog.this.sensorTypeTF.setEnabled(true);
				}else {
					EditTemplateDialog.this.sensorTypeTF.setText("");
					EditTemplateDialog.this.sensorTypeTF.setEnabled(false);
				}
			}
		});
		return typeBox;
    }
    
    private JLabel getTaskLabel() {
	    taskLabel = new JLabel();
		taskLabel.setText("Task / Net");
		taskLabel.setBounds(12, 61, 274, 15);
		return taskLabel;
    }
    
    private JButton getAddTaskNetButton() {
	    addTaskB = new JButton();
	    addTaskB.setText("Add Task/Net");
	    addTaskB.setBounds(145, 82, 127, 22);
	    addTaskB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (!invokedAtLeastOnceTask) {
		          invokedAtLeastOnceTask = true;
		          taskNetDialog.setLocationRelativeTo(YAWLEditor.getInstance());
		          taskNetDialog.setEditTemplateDialog(EditTemplateDialog.this);
		        }
				taskNetDialog.update(null, null, null, -1);
				taskNetDialog.setVisible(true);
			}
		});
		return addTaskB;
    }
    
    private JButton getEditTaskNetButton() {
    	editTaskB = new JButton();
    	editTaskB.setText("Edit Task/Net");
    	editTaskB.setBounds(145, 110, 127, 22);
    	editTaskB.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent evt) {
				int row = taskTable.getSelectedRow();
				if(row>=0) {
					if (!invokedAtLeastOnceTask) {
			          invokedAtLeastOnceTask = true;
			          taskNetDialog.setLocationRelativeTo(YAWLEditor.getInstance());
			          taskNetDialog.setEditTemplateDialog(EditTemplateDialog.this);
			        }
					String[] tmp = taskNet.get(row);
					taskNetDialog.update(tmp[0], new Boolean(tmp[1]), tmp[2], row);
					taskNetDialog.setVisible(true);
				}
			}
		});
		return editTaskB;
    }
    
    private JButton getRemoveTaskNetButton() {
    	delTaskB = new JButton();
    	delTaskB.setText("Del Task/Net");
    	delTaskB.setBounds(145, 138, 127, 22);
    	delTaskB.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent evt) {
    			int row = taskTable.getSelectedRow();
				if(row>=0) {
					String taskName = taskNet.get(row)[0];
					taskNet.remove(row);
					variablesTaskNet.remove(taskName);
					updateTaskJSP();
					updateVariableTaskNetJSP(-1);
				}
			}
		});
		return delTaskB;
    }
    
    private JScrollPane getTaskJScrollPane() {
    	taskJSP = new JScrollPane();
    	taskJSP.setBounds(12, 82, 125, 79);
    	taskTableModel = new DefaultTableModel(getMatrixTask(), new String[] { "Name", "T/N" });
    	taskTable = new JTable();
    	taskJSP.setViewportView(taskTable);
    	taskTable.setModel(taskTableModel);
    	taskTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int row = taskTable.getSelectedRow();
				if(row>=0) {
					updateVariableTaskNetJSP(row);
				}
			}
		});
		return taskJSP;
	}
    
    private JLabel getVariableTasksLabel() {
	    variableTaskLabel = new JLabel();
		variableTaskLabel.setText("Task / Net Variables");
		variableTaskLabel.setBounds(281, 61, 274, 15);
		return variableTaskLabel;
    }
    
    private JButton getAddVarTaskNetButton() {
	    addVarB = new JButton();
	    addVarB.setText("Add Task/Net Var");
	    addVarB.setBounds(433, 82, 155, 22);
	    addVarB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				int taskRow = taskTable.getSelectedRow();
				if(taskRow >= 0) {
					if (!invokedAtLeastOnceVariableTask) {
			          invokedAtLeastOnceVariableTask = true;
			          variableTaskNetdialog.setLocationRelativeTo(YAWLEditor.getInstance());
			          variableTaskNetdialog.setEditTemplateDialog(EditTemplateDialog.this);
			        }
					variableTaskNetdialog.update(null, null, null, taskRow, -1);
					variableTaskNetdialog.setVisible(true);
				}
			}
		});
		return addVarB;
    }
    
    private JButton getEditVarTaskNetButton() {
    	editVarB = new JButton();
    	editVarB.setText("Edit Task/Net Var");
    	editVarB.setBounds(433, 110, 155, 22);
    	editVarB.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent evt) {
    			int taskRow = taskTable.getSelectedRow();
				if(taskRow >= 0) {
					int variableTaskRow = variableTaskTable.getSelectedRow();
					if(variableTaskRow >= 0) {
						if (!invokedAtLeastOnceVariableTask) {
				          invokedAtLeastOnceVariableTask = true;
				          variableTaskNetdialog.setLocationRelativeTo(YAWLEditor.getInstance());
				          variableTaskNetdialog.setEditTemplateDialog(EditTemplateDialog.this);
				        }
						String[] var= variablesTaskNet.get(taskNet.get(taskRow)[0]).get(variableTaskRow);
						variableTaskNetdialog.update(var[0], var[1], var[2], taskRow, variableTaskRow);
						variableTaskNetdialog.setVisible(true);
					}
				}
			}
		});
		return editVarB;
    }
    
    private JButton getRemoveVarTaskNetButton() {
    	delVarB = new JButton();
    	delVarB.setText("Del Task/Net Var");
    	delVarB.setBounds(433, 138, 155, 22);
    	delVarB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				int taskRow = taskTable.getSelectedRow();
				if(taskRow >= 0) {
					int variableTaskRow = variableTaskTable.getSelectedRow();
					if(variableTaskRow >= 0) {
						variablesTaskNet.get(taskNet.get(taskRow)[0]).remove(variableTaskRow);
						updateVariableTaskNetJSP(taskRow);
					}
				}
			}
		});
		return delVarB;
    }
    
    private JScrollPane getVariableTaskJScrollPane() {
    	variableTaskJSP = new JScrollPane();
    	variableTaskJSP.setBounds(281, 82, 146, 79);
    	variableTaskTableModel = new DefaultTableModel(getMatrixVariableTask(-1), new String[] { "Name" });
    	variableTaskTable = new JTable();
    	variableTaskJSP.setViewportView(variableTaskTable);
    	variableTaskTable.setModel(variableTaskTableModel);
    	taskTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    	taskTable.getColumnModel().getColumn(0).setPreferredWidth(98);
    	taskTable.getColumnModel().getColumn(1).setPreferredWidth(24);
		return variableTaskJSP;
	}
    
    private JLabel getVariableLabel() {
	    variableLabel = new JLabel();
		variableLabel.setText("Sensor Variables");
		variableLabel.setBounds(12, 166, 576, 15);
		return variableLabel;
    }
    
    private JButton getAddVariableButton() {
	    addVariableB = new JButton();
		addVariableB.setText("Add Var");
		addVariableB.setBounds(499, 187, 89, 22);
		addVariableB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (!invokedAtLeastOnce) {
					invokedAtLeastOnce = true;
					dialog.setLocationRelativeTo(YAWLEditor.getInstance());
					dialog.setAddEditTemplate(EditTemplateDialog.this);
		        }
				dialog.setListVar(variables);
				dialog.setTaskNet(taskNet);
				dialog.setVarTaskNet(variablesTaskNet);
				dialog.updateComboBox();
		        dialog.setVisible(true);
			}
		});
		return addVariableB;
    }
    
    private JButton getEditVariableButton() {
    	editVariableB = new JButton();
		editVariableB.setText("Edit Var");
		editVariableB.setBounds(499, 215, 89, 22);
		editVariableB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				int row = variablesTable.getSelectedRow();
				if(row>=0) {
					if (!invokedAtLeastOnce) {
			          invokedAtLeastOnce = true;
			          dialog.setLocationRelativeTo(YAWLEditor.getInstance());
			          dialog.setAddEditTemplate(EditTemplateDialog.this);
			        }
					dialog.setListVar(variables);
			        dialog.setTaskNet(taskNet);
			        dialog.setVarTaskNet(variablesTaskNet);
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
		removeVariableB.setBounds(499, 243, 89, 22);
		removeVariableB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if(variablesTable.getSelectedRow()>=0) {
					variables.remove(variablesTable.getSelectedRow());
					EditTemplateDialog.this.showVariable();
				}
			}
		});
		return removeVariableB;
    }
    
    private JScrollPane getVariableJScrollPane() {
	    variablesJSP = new JScrollPane();
		variablesJSP.setBounds(12, 187, 478, 79);
		variablesTableModel = new DefaultTableModel(getMatrixVar(), new String[] { "Variables" , "Mapping" , "Type" });
		variablesTable = new JTable();
		variablesJSP.setViewportView(variablesTable);
		variablesTable.setModel(variablesTableModel);
		return variablesJSP;
	}
    
    private JCheckBox getRiskCB() {
    	riskCB = new JCheckBox();
    	riskCB.setSelected(false);
		riskProbabilityTF.setEnabled(false);
		riskThresholdTF.setEnabled(false);
		riskMessageTF.setEnabled(false);
		riskProbabilityTF.setBackground(Color.lightGray);
		riskThresholdTF.setBackground(Color.lightGray);
		riskMessageTF.setBackground(Color.lightGray);
    	riskCB.setText("Risk Condition");
    	riskCB.setBounds(12, 271, 576, 15);
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
	    riskProbabilityLabel.setBounds(12, 292, 275, 15);
		return riskProbabilityLabel;
    }
    
    private JLabel getRiskThresholdLabel() {
	    riskThresholdLabel = new JLabel("Risk Threshold");
	    riskThresholdLabel.setBounds(314, 292, 275, 15);
		return riskThresholdLabel;
    }
    
    private JTextField getRiskProbabilityJTextField() {
		riskProbabilityTF = new JTextField();
		riskProbabilityTF.setBounds(12, 313, 275, 21);
		riskProbabilityTF.setEditable(false);
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
			    	area.setText(EditTemplateDialog.this.riskProbabilityTF.getText());
			    	area.setWrapStyleWord(true);
			    	area.setLineWrap(true);
			    	JScrollPane pane = new JScrollPane(area);
			    	int result = JOptionPane.showOptionDialog(EditTemplateDialog.this, new Object[] {"Insert Risk Probability:", pane}, "Risk Probability", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			    	if(result == 0) {
			    		EditTemplateDialog.this.riskProbabilityTF.setText(area.getText());
			    	}
			    }
			  }
		});
		return riskProbabilityTF;
	}

	private JLabel getRiskOperatorLabel() {
		riskOperator = new JLabel();
		riskOperator.setBounds(296, 316, 15, 15);
		riskOperator.setText(">");
		return riskOperator;
	}

	private JTextField getRiskThresholdJTextField() {
		riskThresholdTF = new JTextField();
		riskThresholdTF.setBounds(314, 313, 275, 21);
		riskThresholdTF.setEditable(false);
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
			    	area.setText(EditTemplateDialog.this.riskThresholdTF.getText());
			    	area.setWrapStyleWord(true);
			    	area.setLineWrap(true);
			    	JScrollPane pane = new JScrollPane(area);
			    	int result = JOptionPane.showOptionDialog(EditTemplateDialog.this, new Object[] {"Insert Risk Threshold:", pane}, "Risk Threshold", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			    	if(result == 0) {
			    		EditTemplateDialog.this.riskThresholdTF.setText(area.getText());
			    	}
			    }
			  }
		});
		return riskThresholdTF;
	}	
    
    private JCheckBox getFaultCB() {
    	faultCB = new JCheckBox();
    	faultCB.setSelected(false);
    	faultConditionTF.setEnabled(false);
		faultMessageTF.setEnabled(false);
		faultConditionTF.setBackground(Color.lightGray);
		faultMessageTF.setBackground(Color.lightGray);
    	faultCB.setText("Fault Condition");
    	faultCB.setBounds(12, 337, 576, 15);
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
	    faultConditionTF.setBounds(12, 358, 578, 21);
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
			    	area.setText(EditTemplateDialog.this.faultConditionTF.getText());
			    	area.setWrapStyleWord(true);
			    	area.setLineWrap(true);
			    	JScrollPane pane = new JScrollPane(area);
			    	int result = JOptionPane.showOptionDialog(EditTemplateDialog.this, new Object[] {"Insert Fault Condition:", pane}, "Fault Condition", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			    	if(result == 0) {
			    		EditTemplateDialog.this.faultConditionTF.setText(area.getText());
			    	}
			    }
			  }
		});
		return faultConditionTF;
    }
    
    private JCheckBox getTriggerCB() {
    	triggerCB = new JCheckBox();
    	triggerCB.setSelected(false);
    	triggerCB.setText("Trigger");
    	triggerCB.setBounds(12, 433, 576, 15);
		timeRB.setEnabled(false);
		timeTF.setEnabled(false);
		timeBox.setEnabled(false);
		eventRB.setEnabled(false);
		eventTF.setEnabled(false);
		eventBox.setEnabled(false);
		timeTF.setBackground(Color.lightGray);
		eventTF.setBackground(Color.lightGray);
    	triggerCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(triggerCB.isSelected()) {
					timeRB.setEnabled(true);
					eventRB.setEnabled(true);
//    					timeRB.setSelected(true);
					if(timeRB.isSelected()) {
						timeTF.setEnabled(true);
						timeBox.setEnabled(true);
					}else if (eventRB.isSelected()) {
						eventTF.setEnabled(true);
						eventBox.setEnabled(true);
    				}
					timeTF.setBackground(Color.white);
					eventTF.setBackground(Color.white);
				}else {
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
    	timeRB.setSelected(false);
    	timeRB.setBounds(12, 457, 114, 15);
    	timeRB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(timeRB.isSelected()) {
					timeTF.setEnabled(true);
					timeBox.setEnabled(true);
					eventTF.setEnabled(false);
					eventBox.setEnabled(false);
				}else {
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
    
    @SuppressWarnings("serial")
	private JTextField getTimeTF(){
		timeTF = new JTextField();
		timeTF.setEnabled(false);
		timeTF.setDocument(new PlainDocument() {
			@Override
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		        if(str!=null && str.matches("[\\d]*") && str.length() + getLength() <= 64) {
		        	super.insertString(offs, str, a);
		        }
		    }
		});
    	timeTF.setBounds(127, 454, 300, 22);
    	
		return timeTF;
    }
    
    private JComboBox getTimeComboBox(){
    	timeBox = new JComboBox();
		timeBox.setModel(new DefaultComboBoxModel(times));
		timeBox.setBounds(433, 454, 155, 22);
		timeBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(EditTemplateDialog.this.timeBox.getSelectedIndex()>6) {
					EditTemplateDialog.this.timeTF.setText("");
					EditTemplateDialog.this.timeTF.setEnabled(false);
				}else {
					EditTemplateDialog.this.timeTF.setEnabled(true);
				}
			}
		});
		return timeBox;
    }
    
    private JRadioButton getEventRB() {
    	eventRB = new JRadioButton();
    	eventRB.setText("Event");
    	eventRB.setBounds(12, 485, 114, 15);
    	eventRB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(eventRB.isSelected()) {
					eventTF.setEnabled(true);
					eventBox.setEnabled(true);
					timeTF.setEnabled(false);
					timeBox.setEnabled(false);
				}else {
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
    
    @SuppressWarnings("serial")
	private JTextField getEventTF(){
		eventTF = new JTextField();
		eventTF.setEnabled(false);
		eventTF.setDocument(new PlainDocument() {
			@Override
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		        if(str!=null && str.matches("[\\d\\w]*") && str.length() + getLength() <= 64) {
		        	super.insertString(offs, str, a);
		        }
		    }
		});
    	eventTF.setBounds(127, 482, 300, 22);
    	return eventTF;
    }
    
    private JComboBox getEventComboBox(){
    	eventBox = new JComboBox();
    	eventBox.setEnabled(false);
    	eventBox.setModel(new DefaultComboBoxModel(events));
    	eventBox.setBounds(433, 482, 155, 22);
    	eventBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(EditTemplateDialog.this.eventBox.getSelectedIndex()>6) {
					EditTemplateDialog.this.eventTF.setText("");
					EditTemplateDialog.this.eventTF.setEnabled(false);
				}else {
					EditTemplateDialog.this.eventTF.setEnabled(true);
				}
			}
		});
		return eventBox;
    }
    
    private JLabel getRiskMessageLabel() {
    	riskMessageLabel = new JLabel();
    	riskMessageLabel.setText("Risk Message");
    	riskMessageLabel.setBounds(12, 510, 576, 15);
		return riskMessageLabel;
    }
    
    private JTextField getRiskMessageJTextField() {
	    riskMessageTF = new JTextField();
		riskMessageTF.setBounds(12, 531, 578, 21);
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
			    if (e.getClickCount() >= 2 && riskCB.isSelected()) {
			    	JTextArea area = new JTextArea(20, 40);
			    	area.setText(EditTemplateDialog.this.riskMessageTF.getText());
			    	area.setWrapStyleWord(true);
			    	area.setLineWrap(true);
			    	JScrollPane pane = new JScrollPane(area);
			    	int result = JOptionPane.showOptionDialog(EditTemplateDialog.this, new Object[] {"Insert Risk Message:", pane}, "Risk Message", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			    	if(result == 0) {
			    		EditTemplateDialog.this.riskMessageTF.setText(area.getText());
			    	}
			    }
			  }
		});
		return riskMessageTF;
    }
    
    private JLabel getFaultMessageLabel() {
    	faultMessageLabel = new JLabel();
    	faultMessageLabel.setText("Fault Message");
    	faultMessageLabel.setBounds(12, 558, 576, 15);
		return faultMessageLabel;
    }
    
    private JTextField getFaultMessageJTextField() {
    	faultMessageTF = new JTextField();
    	faultMessageTF.setBounds(12, 579, 578, 21);
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
			    if (e.getClickCount() >= 2 && faultCB.isSelected()) {
			    	JTextArea area = new JTextArea(20, 40);
			    	area.setText(EditTemplateDialog.this.faultMessageTF.getText());
			    	area.setWrapStyleWord(true);
			    	area.setLineWrap(true);
			    	JScrollPane pane = new JScrollPane(area);
			    	int result = JOptionPane.showOptionDialog(EditTemplateDialog.this, new Object[] {"Insert Fault Message:", pane}, "Fault Message", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			    	if(result == 0) {
			    		EditTemplateDialog.this.faultMessageTF.setText(area.getText());
			    	}
			    }
			  }
		});
		return faultMessageTF;
    }
    
    private JLabel getConsequenceLabel() {
    	consequenceLabel = new JLabel();
    	consequenceLabel.setText("Consequence");
    	consequenceLabel.setBounds(12, 385, 576, 15);
		return consequenceLabel;
    }
    
    private JTextField getConsequenceJTextField() {
    	consequenceTF = new JTextField();
    	consequenceTF.setBounds(12, 406, 578, 21);
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
			    	area.setText(EditTemplateDialog.this.consequenceTF.getText());
			    	area.setWrapStyleWord(true);
			    	area.setLineWrap(true);
			    	JScrollPane pane = new JScrollPane(area);
			    	int result = JOptionPane.showOptionDialog(EditTemplateDialog.this, new Object[] {"Insert Consequence:", pane}, "consequence", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			    	if(result == 0) {
			    		EditTemplateDialog.this.consequenceTF.setText(area.getText());
			    	}
			    }
			  }
		});
		return consequenceTF;
    }
    
    private boolean check(String s) {
    	return true;
    }
    
    private JPanel getConfigurationPanel() {
    	JPanel panel = new JPanel();
    	setPreferredSize(new Dimension(600, 300));
    	panel.setLayout(null);
    	panel.add(getSensorNameLabel());
    	panel.add(getSensorNameTF());
    	panel.add(getSensorTypeLabel());
    	panel.add(getSensorTypeTF());
    	panel.add(getTypeComboBox());
    	
    	panel.add(getTaskLabel());
    	panel.add(getVariableTasksLabel());
    	panel.add(getAddTaskNetButton());
    	panel.add(getEditTaskNetButton());
    	panel.add(getRemoveTaskNetButton());
    	panel.add(getTaskJScrollPane());
    	
    	panel.add(getAddVarTaskNetButton());
    	panel.add(getEditVarTaskNetButton());
    	panel.add(getRemoveVarTaskNetButton());
    	panel.add(getVariableTaskJScrollPane());
    	
    	panel.add(getVariableLabel());
    	panel.add(getAddVariableButton());
    	panel.add(getEditVariableButton());
    	panel.add(getRemoveVariableButton());
    	panel.add(getVariableJScrollPane());
    	
    	panel.add(getRiskProbabilityLabel());
    	panel.add(getRiskThresholdLabel());
		panel.add(getRiskProbabilityJTextField());
		panel.add(getRiskOperatorLabel());
		panel.add(getRiskThresholdJTextField());
		panel.add(getRiskMessageLabel());
		panel.add(getRiskMessageJTextField());
		
		panel.add(getFaultConditionJTextField());
		panel.add(getFaultMessageLabel());
		panel.add(getFaultMessageJTextField());
		
		panel.add(getConsequenceLabel());
		panel.add(getConsequenceJTextField());
		
		panel.add(getRiskCB());
		panel.add(getFaultCB());
		
		panel.add(getTimeRB());
    	panel.add(getTimeTF());
    	panel.add(getTimeComboBox());
    	
    	panel.add(getEventRB());
    	panel.add(getEventTF());
    	panel.add(getEventComboBox());
    	
    	panel.add(getTriggerCB());
    	
		return panel;
    }
    
	public String[][] getMatrixVar(){
		String[][] matrix = new String[variables.size()][3];
		int i = 0;
		for(String[] line : variables) {
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
		variablesTableModel = 
			new DefaultTableModel(
					getMatrixVar(),
					new String[] { "Variables" , "Mapping", "Type" });
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
        //pack();
        setSize(600, 710);
        JUtilities.setMinSizeToCurrent(this);
    }

	private void updateTaskJSP() {
		taskTableModel = new DefaultTableModel(getMatrixTask(), new String[] { "Name", "T/N"});
    	taskTable.setModel(taskTableModel);
    	taskTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    	taskTable.getColumnModel().getColumn(0).setPreferredWidth(98);
    	taskTable.getColumnModel().getColumn(1).setPreferredWidth(24);
    	DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
    	dtcr.setHorizontalAlignment(SwingConstants.CENTER);
    	taskTable.getColumnModel().getColumn(1).setCellRenderer(dtcr);
	}
	
	public String[][] getMatrixTask(){
		String[][] matrix = new String[taskNet.size()][2];
		int i = 0;
		for(String[] line : taskNet) {
			matrix[i][0] = line[0];
			if(new Boolean(line[1])) {
				matrix[i][1] = "T";
			}else {
				matrix[i][1] = "N";
			}
			i++;
		}
		return matrix;
	}

	private void updateVariableTaskNetJSP(int taskRow) {
		variableTaskTableModel = new DefaultTableModel(getMatrixVariableTask(taskRow), new String[] { "Name" });
    	variableTaskTable.setModel(variableTaskTableModel);
	}
	
	private void updateVariableJSP() {
		variablesTableModel = new DefaultTableModel(getMatrixVar(), new String[] { "Variables" , "Mapping" , "Type" });
		variablesTable.setModel(variablesTableModel);
	}
	
	public String[][] getMatrixVariableTask(int taskRow){
		if(taskRow >= 0) {
			String taskName = taskNet.get(taskRow)[0];
			if(variablesTaskNet.containsKey(taskName)) {
				String[][] matrix = new String[variablesTaskNet.get(taskName).size()][1];
				int i = 0;
				for(String[] line : variablesTaskNet.get(taskName)) {
					matrix[i][0] = line[0];
					i++;
				}
				return matrix;
			}else {
				String[][] matrix = new String[0][1];
				return matrix;
			}
		}else {
			String[][] matrix = new String[0][1];
			return matrix;
		}
	}
	

    
    public void addTask(String name, Boolean type, String description, int row) {
    	if(row>-1) {
    		String oldName = taskNet.get(row)[0];
    		LinkedList<String[]> list = variablesTaskNet.get(oldName);
    		taskNet.set(row, new String[]{name, type.toString(), description});
    		variablesTaskNet.remove(oldName);
    		variablesTaskNet.put(name, list);
    	}else {
    		taskNet.add(new String[]{name, type.toString(), description});
    		variablesTaskNet.put(name, new LinkedList<String[]>());
    	}
    	updateTaskJSP();
    }

	public void addVarTask(String name, String type, String description, int taskRow, int varTaskRow) {
		String oldName = taskNet.get(taskRow)[0];
		LinkedList<String[]> list = variablesTaskNet.get(oldName);
		if(varTaskRow>-1) {
			list.set(varTaskRow, new String[]{name, type, description});
    	}else {
    		list.add(new String[]{name, type, description});
    	}
    	updateVariableTaskNetJSP(taskRow);
	}
    
}
