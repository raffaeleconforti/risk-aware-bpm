package org.yawlfoundation.yawl.editor.analyser;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.yawlfoundation.yawl.editor.YAWLEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;


public class AnalysisDialog extends JDialog {
    private JPanel contentPane;
    private JButton btnClose;
    private JTextPane txtOutput;
    private JCheckBox cbxKeepOpen;
    private JLabel lblHeader;
    private JScrollPane scrollpane;

    private final Preferences prefs = Preferences.userNodeForPackage(YAWLEditor.class);
    public static final String KEEP_OPEN_PREFERENCE = "keepAnalysisDialogOpenWhenDone";

    public AnalysisDialog(String title) {
        setContentPane(contentPane);
        lblHeader.setText("Analysing " + title + ":");
        setModal(false);
        setAlwaysOnTop(true);
        getRootPane().setDefaultButton(btnClose);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setKeepOpenCheckbox();

        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onClose();
            }
        });

        pack();
        position();
        setVisible(true);
    }

    public void write(String msg) {
        setText(txtOutput.getText() + msg + '\n');
    }

    public void setText(String msg) {
        txtOutput.setText(msg);
        txtOutput.setCaretPosition(msg.length());
    }

    public String getText() {
        return txtOutput.getText();
    }


    public void finished() {
        lblHeader.setText(lblHeader.getText() + " Completed.");
        if (!cbxKeepOpen.isSelected())
            onClose();
        else
            btnClose.setEnabled(true);
    }


    private void setKeepOpenCheckbox() {
        cbxKeepOpen.setSelected(prefs.getBoolean(KEEP_OPEN_PREFERENCE, true));
    }

    private void setKeepOpenPreference() {
        prefs.putBoolean(KEEP_OPEN_PREFERENCE, cbxKeepOpen.isSelected());
    }

    // put the dialog in bottom right of editor frame
    private void position() {
        Point parentLocation = YAWLEditor.getInstance().getLocationOnScreen();
        Dimension parentSize = YAWLEditor.getInstance().getSize();
        Dimension dialogSize = this.getSize();
        int x = parentLocation.x + parentSize.width - dialogSize.width;
        int y = parentLocation.y + parentSize.height - dialogSize.height;
        setLocation(x, y);
    }


    private void onClose() {
        setKeepOpenPreference();
        dispose();
    }


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 10, 10, 10), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        cbxKeepOpen = new JCheckBox();
        cbxKeepOpen.setSelected(true);
        cbxKeepOpen.setText("Keep open when analysis completes");
        cbxKeepOpen.setMnemonic('K');
        cbxKeepOpen.setDisplayedMnemonicIndex(0);
        panel1.add(cbxKeepOpen, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnClose = new JButton();
        btnClose.setEnabled(false);
        btnClose.setText("Close");
        panel2.add(btnClose, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 0, 10), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(500, 200), null, 0, false));
        lblHeader = new JLabel();
        lblHeader.setText("");
        panel3.add(lblHeader, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(151, 29), null, 0, false));
        scrollpane = new JScrollPane();
        panel3.add(scrollpane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(500, 200), null, 0, false));
        txtOutput = new JTextPane();
        txtOutput.setEditable(false);
        txtOutput.setEnabled(true);
        txtOutput.setText("");
        scrollpane.setViewportView(txtOutput);
        lblHeader.setLabelFor(scrollpane);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
