package org.yawlfoundation.yawl.editor.actions.sensor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.yawlfoundation.yawl.editor.YAWLEditor;
import org.yawlfoundation.yawl.editor.actions.sensor.template.TemplateWizardDialog;
import org.yawlfoundation.yawl.editor.specification.SpecificationModel;
import org.yawlfoundation.yawl.editor.swing.AbstractDoneDialog;
import org.yawlfoundation.yawl.editor.swing.JUtilities;
import org.yawlfoundation.yawl.sensors.YSensorsEditor;

public class SensorDialog extends AbstractDoneDialog {
	private static final long serialVersionUID = 1L;
	private static YSensorsEditor sensors = new YSensorsEditor();
	private JLabel timeLabel;
	private boolean timeSetted = false;
	private boolean sensorSetted = false;
	private JTextField timeTF;
	private JScrollPane jScrollPane1;
	private JButton addJB, addTemplateJB, editJB, removeJB;
	private DefaultTableModel model;
	private JTable table;
	private JComboBox timeBox;
	private String[] times = new String[] { "Seconds", "Minutes", "Hours",
			"Days", "Weeks", "Month", "Years", "Sunday", "Monday", "Tuesday",
			"Wednesday", "Thursday", "Friday", "Saturday" };
	private boolean invokedAtLeastOnce;
	private boolean invokedAtLeastOnceTemplate;
	private EditSensorDialog editSensorDialog = new EditSensorDialog();
	private TemplateWizardDialog addTemplateDialog = new TemplateWizardDialog();

	public static void main(String[] args) {
		SensorDialog f = new SensorDialog();
		f.setVisible(true);		
	}

	public SensorDialog() {
		super("Edit Sensors", true);
		setContentPanel(getConfigurationPanel());

		getDoneButton().setEnabled(false);

		getDoneButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (timeBox.getSelectedIndex() > 6)
					sensors.conferm(1 + "_" + times[timeBox.getSelectedIndex()]);
				else
					sensors.conferm(timeTF.getText() + "_"
							+ times[timeBox.getSelectedIndex()]);
				SpecificationModel.getInstance().setSensors(sensors);
				SensorDialog.this.getDoneButton().setEnabled(false);
			}
		});
		getCancelButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sensors.cancel();
			}
		});

		getRootPane().setDefaultButton(getDoneButton());
	}

	public void inizialize() {
		if (SpecificationModel.getInstance().getSensors() != null)
			sensors = SpecificationModel.getInstance().getSensors();
		else
			sensors = new YSensorsEditor();
	}

	public YSensorsEditor getYSensorEditor() {
		return sensors;
	}

	private JLabel getTimeFrequenceLabel() {
		timeLabel = new JLabel();
		timeLabel.setText("Overall Sample Time");
		timeLabel.setBounds(12, 12, 350, 15);
		return timeLabel;
	}

	private JTextField getTimeFrequenceTF() {
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
		timeTF.setBounds(12, 33, 202, 22);
		timeTF.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				if (timeTF.getText().length() > 0)
					timeSetted = true;
				else
					timeSetted = false;
				SensorDialog.this.getDoneButton().setEnabled(
						(timeSetted && sensorSetted)
								|| (!timeSetted && !sensorSetted));
			}
		});
		return timeTF;
	}

	private JComboBox getTimeFrequenceComboBox() {
		timeBox = new JComboBox();
		timeBox.setModel(new DefaultComboBoxModel(times));
		timeBox.setBounds(220, 33, 198, 22);
		timeBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (SensorDialog.this.timeBox.getSelectedIndex() > 5) {
					SensorDialog.this.timeTF.setText("");
					SensorDialog.this.timeTF.setEnabled(false);
				} else {
					SensorDialog.this.timeTF.setEnabled(true);
				}
			}
		});
		return timeBox;
	}

	private JScrollPane getJScrollPane1() {
		jScrollPane1 = new JScrollPane();
		jScrollPane1.setBounds(12, 61, 202, 107);
		model = new DefaultTableModel(getMatrixSensor(),
				new String[] { "Sensors" });
		table = new JTable();
		jScrollPane1.setViewportView(table);
		table.setModel(model);
		return jScrollPane1;
	}

	private JButton getAddJB() {
		addJB = new JButton();
		addJB.setText("Add Sensor");
		addJB.setBounds(220, 61, 198, 22);
		addJB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (!invokedAtLeastOnce) {
					invokedAtLeastOnce = true;
					editSensorDialog.setLocationRelativeTo(YAWLEditor.getInstance());
					editSensorDialog.addEditRemoveDialog(SensorDialog.this);
				}
				editSensorDialog.showVariable();
				editSensorDialog.setVisible(true);
			}
		});
		return addJB;
	}

	private JButton getAddTemplateJB() {
		addTemplateJB = new JButton();
		addTemplateJB.setText("Add from Template");
		addTemplateJB.setBounds(220, 89, 198, 22);
		addTemplateJB.addActionListener(new ActionListener() {
			JFileChooser jfc = new JFileChooser();

			public void actionPerformed(ActionEvent evt) {
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
				int select = jfc.showOpenDialog(null);
				if(select==0) {
					String template = null;
					try {
						File f = jfc.getSelectedFile();
						InputStream is = new FileInputStream(f);
						Writer writer = new StringWriter();
						char[] buffer = new char[1024];
						try {
							Reader reader = new BufferedReader(
									new InputStreamReader(is, "UTF-8"));
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
					if (!invokedAtLeastOnceTemplate) {
						invokedAtLeastOnceTemplate = true;
						addTemplateDialog.setLocationRelativeTo(YAWLEditor.getInstance());
						addTemplateDialog.addEditRemoveDialog(SensorDialog.this);
					}
					addTemplateDialog.loadTemplate(template);
					addTemplateDialog.setVisible(true);
					addTemplateDialog.repaint();
				}
			}
		});
		return addTemplateJB;
	}

	private JButton getEditJB() {
		editJB = new JButton();
		editJB.setText("Edit Sensor");
		editJB.setBounds(220, 117, 198, 22);
		editJB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				int row = table.getSelectedRow();
				if (row > -1) {
					if (!invokedAtLeastOnce) {
						invokedAtLeastOnce = true;
						editSensorDialog.setLocationRelativeTo(YAWLEditor.getInstance());
						editSensorDialog.addEditRemoveDialog(SensorDialog.this);
					}
					editSensorDialog.setLine((String) table.getValueAt(row, 0));
					editSensorDialog.showVariable();
					editSensorDialog.setVisible(true);
				}
			}
		});
		return editJB;
	}

	private JButton getRemoveJB() {
		removeJB = new JButton();
		removeJB.setText("Remove Sensor");
		removeJB.setBounds(220, 145, 198, 22);
		removeJB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				int row = table.getSelectedRow();
				if (row > -1) {
					sensors.remove((String) table.getValueAt(row, 0));
					SensorDialog.this.update();
				}
			}
		});
		return removeJB;
	}

	private JPanel getConfigurationPanel() {
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(430, 248));
		panel.setLayout(null);
		panel.add(getTimeFrequenceLabel());
		panel.add(getTimeFrequenceTF());
		panel.add(getTimeFrequenceComboBox());
		panel.add(getJScrollPane1());
		panel.add(getAddJB());
		panel.add(getAddTemplateJB());
		panel.add(getEditJB());
		panel.add(getRemoveJB());
		return panel;
	}

	public String[][] getMatrixSensor() {
		String[] sensorsArray = sensors.getNames().toArray(new String[0]);
		String[][] matrix = new String[sensorsArray.length][1];
		for (int i = 0; i < sensorsArray.length; i++) {
			matrix[i][0] = sensorsArray[i];
		}
		return matrix;
	}

	public void update() {
		model = new DefaultTableModel(getMatrixSensor(),
				new String[] { "Sensor" });
		if (model.getRowCount() > 0)
			sensorSetted = true;
		else
			sensorSetted = false;
		table.setModel(model);
		if (sensors != null && sensors.getGeneralTime() != null) {
			String generalTime = sensors.getGeneralTime();
			timeTF.setText(generalTime.substring(0, generalTime.indexOf("_")));
			String period = generalTime.substring(generalTime.indexOf("_") + 1);
			int index = 0;
			while (!period.equals(times[index]) && index < times.length) {
				index++;
			}
			timeBox.setSelectedIndex(index);
			if (index > 6) {
				timeTF.setText("");
				timeTF.setEnabled(false);
			}
			timeSetted = true;
		} else {
			timeTF.setText("");
			timeSetted = false;
			timeBox.setSelectedIndex(0);
		}
		SensorDialog.this.getDoneButton().setEnabled(
				timeSetted && sensorSetted);
		this.repaint();
	}

	protected void makeLastAdjustments() {
		setSize(430, 248);
		JUtilities.setMinSizeToCurrent(this);
	}

}
