/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * LicenseDetailPanel.java
 *
 * Created on Jan 28, 2010, 1:58:57 PM
 */
package net.padlocksoftware.ui;

import com.imagine.component.calendar.DateListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.padlocksoftware.padlock.license.License;

/**
 *
 * @author jason
 */
public class LicenseDetailPanel extends javax.swing.JPanel {

  ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  private final long TWO_WEEKS_IN_MS = 1000L * 3600L * 24L * 14L;
  
  ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  private final PadlockModel model;

  private final HardwareTableModel tableModel;

  private String selectedLicenseName;

  private License selectedLicense;

  private final KeysComboBoxModel comboBoxModel;

  private boolean updating;

  /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  public LicenseDetailPanel() {
    initComponents();
    model = null;
    selectedLicenseName = null;
    selectedLicense = null;
    tableModel = null;
    comboBoxModel = null;
  }

  public LicenseDetailPanel(PadlockModel model) {
    initComponents();
    updating = true;
    this.model = model;
    selectedLicenseName = null;
    selectedLicense = null;
    tableModel = new HardwareTableModel(model);
    hardwareTable.setModel(tableModel);
    hardwareTable.setDefaultRenderer(Object.class, new HardwareTableCellRenderer());

    hardwareTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){

      public void valueChanged(ListSelectionEvent e) {
        updateRemoveButton();
      }

    });
    
    comboBoxModel = new KeysComboBoxModel(model);
    keyComboBox.setModel(comboBoxModel);
    keyComboBox.addActionListener(new ActionListener(){

      public void actionPerformed(ActionEvent e) {
        updateSignButton();
      }

    });
    updateRemoveButton();

    updateSignButton();

    startDatePicker.calendar.getCalendarComponent().addDateListener(new DateListener() {
        public void dateChanged(Date date) {
          if (!updating) {
            selectedLicense.setStartDate(date);

            // If we're in demo mode, set the expiration date to checked and
            // move the expiration date to two weeks from the start date.
            if (!LicenseDetailPanel.this.model.getState().isValid()) {
              expirationDateCheckbox.setSelected(true);
              Date expirationDate = new Date(date.getTime() + TWO_WEEKS_IN_MS);
              expirationDatePicker.calendar.setDate(expirationDate);
              selectedLicense.setExpirationDate(expirationDate);
            }

            LicenseDetailPanel.this.model.updateLicense(selectedLicenseName, selectedLicense);
          }
        }
    });

    expirationDatePicker.calendar.getCalendarComponent().addDateListener(new DateListener() {
        public void dateChanged(Date date) {
          if (!updating) {
            selectedLicense.setExpirationDate(date);

            LicenseDetailPanel.this.model.updateLicense(selectedLicenseName, selectedLicense);
          }
        }
    });

    model.addModelListener(new AbstractPadlockModelListener(){

      @Override
      public void licenseUpdated(String name) {
        if (name.equals(selectedLicenseName)) {
          setSelectedLicense(name);
        }
      }
    });
    updating = false;

    UIUtils.macifyTable(hardwareTable);

  }

  ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //------------------------ Implements:

  //------------------------ Overrides:

  //---------------------------- Abstract Methods -----------------------------

  //---------------------------- Utility Methods ------------------------------

  private Long getFloatPeriod() {
    Long period = null;

    String floatString = floatingDateField.getText();
    try {
      period = Long.parseLong(floatString);
    } catch (Throwable t) {}
    return period;
  }

  private void updateFloatDate() {

    try {
      String strFloat = floatingDateField.getText();

      if (strFloat.length() == 0) {
        selectedLicense.setFloatingExpirationPeriod(null);
        model.updateLicense(selectedLicenseName, selectedLicense);
      } else {
        Long floatPeriod = Long.parseLong(strFloat);
        selectedLicense.setFloatingExpirationPeriod(1000L * 3600 * 24 * floatPeriod);
        model.updateLicense(selectedLicenseName, selectedLicense);
      }
    } catch (Throwable t) {
      // Show user error here!
    }
  }
  
  private void clearPanel() {
    nameField.setText("");
    nameField.setEnabled(false);

    startDatePicker.calendar.setEnabled(false);
    expirationDatePicker.calendar.setEnabled(false);

    expirationDateCheckbox.setEnabled(false);
    expirationDateCheckbox.setSelected(false);

    floatingDateCheckbox.setEnabled(false);
    floatingDateCheckbox.setSelected(false);
    floatingDateField.setText("");
    floatingDateField.setEnabled(false);

    keyComboBox.setEnabled(false);
    signButton.setEnabled(false);

    tableModel.setLicense(null);
    comboBoxModel.setSelectedLicense(null);
  }

  private void updatePanel() {

    nameField.setEnabled(true);
    nameField.setText(selectedLicenseName);

    startDatePicker.calendar.setEnabled(!selectedLicense.isSigned());
    startDatePicker.calendar.setDate(selectedLicense.getStartDate());

    Date expirationDate = selectedLicense.getExpirationDate();

    // If true, the user cannot change the expiration date.
    boolean readOnlyExpire = selectedLicense.isSigned() || !model.getState().isValid();

    expirationDateCheckbox.setEnabled(!readOnlyExpire);

    if (expirationDate != null) {
      expirationDateCheckbox.setSelected(true);
      expirationDatePicker.calendar.setDate(expirationDate);
    } else {
      expirationDateCheckbox.setSelected(false);
      expirationDatePicker.calendar.setDate(null);
    }

    expirationDatePicker.calendar.setEnabled(expirationDateCheckbox.isSelected() &&
            expirationDateCheckbox.isEnabled());

    Long floatPeriod = selectedLicense.getFloatingExpirationPeriod();
    floatingDateCheckbox.setEnabled(!selectedLicense.isSigned());


    if (floatPeriod != null) {
      int days = (int)(floatPeriod / (1000 * 3600 * 24));
      floatingDateCheckbox.setSelected(true);
      floatingDateField.setEnabled(true);
      floatingDateField.setText(Integer.toString(days));
    } else {
      floatingDateCheckbox.setSelected(false);
      floatingDateField.setEnabled(false);
      floatingDateField.setText("");
    }

    keyComboBox.setEnabled(!selectedLicense.isSigned());
    
    comboBoxModel.setSelectedLicense(selectedLicenseName);
  }

  private void updateSignButton() {

    // Only enable when:
    // 1) A license is selected (non-null)
    // 2) The license is not signed
    // 3) The combo box has a non-null value selected

    if (selectedLicense == null || selectedLicense.isSigned()) {
      signButton.setEnabled(false);
      return;
    }

    signButton.setEnabled(comboBoxModel.getSelectedItem() != null);
  }

  private void updateRemoveButton() {

    if (selectedLicense == null || selectedLicense.isSigned()) {
      removeButton.setEnabled(false);
      return;
    }
    
    // Only enable when the following are true:
    // 1) One more more licenses are selected
    // 2) The license is not signed

    int[] selectedRows = hardwareTable.getSelectedRows();
    List<Integer> rows = new ArrayList<Integer>();
    for (int selectedRow : selectedRows) {
      int row = hardwareTable.convertRowIndexToModel(selectedRow);

      // Never count row 0 in the selection since it's the edit row
      if (row != 0) rows.add(row);
    }

    removeButton.setEnabled(rows.size() > 0);
  }

  private void updateLicenseName(String newName) {
    if (!newName.equals(selectedLicenseName) && newName.length() > 0) {
      model.renameLicense(selectedLicenseName, newName);
    } else {
      nameField.setText(selectedLicenseName);
    }

  }
  
  //---------------------------- Property Methods -----------------------------

  public void setSelectedLicense(String licenseName) {
    updating = true;

    if (licenseName == null) {
      selectedLicenseName = null;
      selectedLicense = null;
      clearPanel();
      updating = false;
      return;
    }
    
    License license = model.getLicense(licenseName);
    if (license == null) {
      selectedLicenseName = null;
      clearPanel();
    } else {
      selectedLicenseName = licenseName;
      selectedLicense = license;
      updatePanel();
    }
    
    updateSignButton();

    tableModel.setLicense(licenseName);
    updating = false;
  }

  /** Creates new form LicenseDetailPanel */
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        hardwareTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        startDatePicker = new net.padlocksoftware.ui.DatePicker();
        expirationDatePicker = new net.padlocksoftware.ui.DatePicker();
        startDateLabel = new javax.swing.JLabel();
        expirationDateCheckbox = new javax.swing.JCheckBox();
        floatingDateCheckbox = new javax.swing.JCheckBox();
        floatingDateField = new javax.swing.JTextField();
        removeButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        keyComboBox = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        signButton = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(70, 70, 70)));

        hardwareTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        hardwareTable.setRowHeight(22);
        jScrollPane1.setViewportView(hardwareTable);

        jPanel1.setBackground(new java.awt.Color(70, 70, 70));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("License Details");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                .addContainerGap())
        );

        startDateLabel.setText("License Start Date:");

        expirationDateCheckbox.setText("License Expiration Date:");
        expirationDateCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expirationDateCheckboxActionPerformed(evt);
            }
        });

        floatingDateCheckbox.setText("Floating Expiration (days):");
        floatingDateCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                floatingDateCheckboxActionPerformed(evt);
            }
        });

        floatingDateField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        floatingDateField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                floatingDateFieldActionPerformed(evt);
            }
        });
        floatingDateField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                floatingDateFieldFocusLost(evt);
            }
        });

        removeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/padlocksoftware/ui/resources/x-20.png"))); // NOI18N
        removeButton.setText("Remove Selected");
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        jLabel3.setText("Signing Key:");

        keyComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel4.setText("License Name:");

        nameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameFieldActionPerformed(evt);
            }
        });
        nameField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                nameFieldFocusLost(evt);
            }
        });

        signButton.setText("Sign License");
        signButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                signButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(startDateLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(startDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(floatingDateCheckbox)
                            .addComponent(expirationDateCheckbox))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(expirationDatePicker, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(floatingDateField)))
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nameField, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(keyComboBox, 0, 277, Short.MAX_VALUE))
                            .addComponent(jSeparator3, javax.swing.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(signButton)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(removeButton)))
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(startDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(startDateLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(expirationDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(expirationDateCheckbox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(floatingDateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(floatingDateCheckbox))
                .addGap(21, 21, 21)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(removeButton)
                .addGap(18, 18, 18)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(keyComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(signButton)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {expirationDateCheckbox, expirationDatePicker, floatingDateField, startDateLabel, startDatePicker});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {removeButton, signButton});

    }// </editor-fold>//GEN-END:initComponents

  private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed

    // For each selected row get the MAC that goes with it
    int[] selectedRows = hardwareTable.getSelectedRows();
    List<String> macs = new ArrayList<String>();

    for (int selectedRow : selectedRows) {
      int row = hardwareTable.convertRowIndexToModel(selectedRow);
      if (row != 0) {
        macs.add(tableModel.getMacAt(row-1));
      }
    }

    // Remove these addresses
    for (String mac : macs) {
      selectedLicense.removeHardwareAddress(mac);
    }

    model.updateLicense(selectedLicenseName, selectedLicense);
  }//GEN-LAST:event_removeButtonActionPerformed

  private void signButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signButtonActionPerformed
    model.signLicense(selectedLicenseName, comboBoxModel.getSelectedItem());
  }//GEN-LAST:event_signButtonActionPerformed

  private void nameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameFieldActionPerformed
    updateLicenseName(nameField.getText());
  }//GEN-LAST:event_nameFieldActionPerformed

  private void nameFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nameFieldFocusLost
    updateLicenseName(nameField.getText());
  }//GEN-LAST:event_nameFieldFocusLost

  private void expirationDateCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expirationDateCheckboxActionPerformed
    if (expirationDateCheckbox.isSelected()) {      
      expirationDatePicker.calendar.setEnabled(true);
      Date d = new Date();
      expirationDatePicker.calendar.setDate(d);
      selectedLicense.setExpirationDate(d);
      model.updateLicense(selectedLicenseName, selectedLicense);

    } else {
      selectedLicense.setExpirationDate(null);
      model.updateLicense(selectedLicenseName, selectedLicense);
      expirationDatePicker.calendar.setEnabled(false);
    }
  }//GEN-LAST:event_expirationDateCheckboxActionPerformed

  private void floatingDateFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_floatingDateFieldFocusLost
    updateFloatDate();
  }//GEN-LAST:event_floatingDateFieldFocusLost

  private void floatingDateFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_floatingDateFieldActionPerformed
    updateFloatDate();
  }//GEN-LAST:event_floatingDateFieldActionPerformed

  private void floatingDateCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_floatingDateCheckboxActionPerformed

    boolean selected = floatingDateCheckbox.isSelected();
    floatingDateField.setEnabled(selected);

    // If the box is checked and the text field has a valid value, go ahead
    // and commit that.  Otherwise change focus to the textfield and let the
    // user enter a value.
    if (selected) {
      Long floatPeriod = getFloatPeriod();

      if (floatPeriod == null) {
        // Empty or bad value, set Focus and do not update license
        floatingDateField.setText("");
        floatingDateField.requestFocus();
      } else {
        selectedLicense.setFloatingExpirationPeriod(floatPeriod);
        model.updateLicense(selectedLicenseName, selectedLicense);
      }
    } else {
      selectedLicense.setFloatingExpirationPeriod(null);
      model.updateLicense(selectedLicenseName, selectedLicense);
    }

    
  }//GEN-LAST:event_floatingDateCheckboxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox expirationDateCheckbox;
    private net.padlocksoftware.ui.DatePicker expirationDatePicker;
    private javax.swing.JCheckBox floatingDateCheckbox;
    private javax.swing.JTextField floatingDateField;
    private javax.swing.JTable hardwareTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JComboBox keyComboBox;
    private javax.swing.JTextField nameField;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton signButton;
    private javax.swing.JLabel startDateLabel;
    private net.padlocksoftware.ui.DatePicker startDatePicker;
    // End of variables declaration//GEN-END:variables
}

