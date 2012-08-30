/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * LicenseTemplateDetailPanel.java
 *
 * Created on Mar 1, 2010, 2:06:14 PM
 */

package net.padlocksoftware.ui;

import com.imagine.component.calendar.DateListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.padlocksoftware.padlocktemplates.LicenseTemplateDefinition;

/**
 *
 * @author jason
 */
public class LicenseTemplateDetailPanel extends javax.swing.JPanel {

    private static final long DAY_IN_MS = 1000L * 3600L * 24;

    private final PadlockModel model;

    private String selectedName;
    private LicenseTemplateDefinition selectedTemplate;
    private final TemplatePropertiesTableModel tableModel;

    private boolean updating;

    /** Creates new form LicenseTemplateDetailPanel */
    public LicenseTemplateDetailPanel() {
      initComponents();
      UIUtils.macifyTable(propertiesTable);
      model = null;
      tableModel = null;
      removeButton.setEnabled(false);
    }

    public LicenseTemplateDetailPanel(PadlockModel model) {
      updating = false;
      initComponents();
      removeButton.setEnabled(false);
      UIUtils.macifyTable(propertiesTable);
      this.model = model;
      tableModel = new TemplatePropertiesTableModel(model);
      propertiesTable.setModel(tableModel);
      propertiesTable.setDefaultRenderer(Object.class, new LicensePropertiesTableCellRenderer());
      propertiesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){

      public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
          selectionChanged();
        }
      }

      });

      model.addModelListener(new AbstractPadlockModelListener(){
      @Override
        public void templateUpdated(String name){
          if (name.equals(selectedName) ) {
            updatePanel();
          }
        }      
      });

      strictDatePicker.calendar.getCalendarComponent().addDateListener(new DateListener() {
        public void dateChanged(Date date) {
          if (!updating) {
            Date expirationDate = strictDatePicker.calendar.getDate();
            selectedTemplate.setExpirationDate(expirationDate);
            LicenseTemplateDetailPanel.this.model.updateTemplate(selectedName, selectedTemplate);
          }
        }
      });
    }


    public void setLicenseTemplate(String name) {
      selectedName = name;
      selectedTemplate = model.getTemplates().get(selectedName);
      updatePanel();
    }

    private void updatePanel() {
      updating = true;
      boolean enabled = selectedName != null;

      templateNameField.setEnabled(enabled);
      noneRadioButton.setEnabled(enabled);
      strictRadioButton.setEnabled(enabled);
      deltaRadioButton.setEnabled(enabled);
      strictDatePicker.calendar.setEnabled(false);
      strictDatePicker.setDate(new Date(System.currentTimeMillis() + DAY_IN_MS * 30));
      deltaTextField.setText("30");
      deltaTextField.setEnabled(false);

      floatTextField.setEnabled(enabled);
      updateFloatPeriod();
      
      propertiesTable.setEnabled(enabled);
      tableModel.setTemplate(selectedName);
      if (enabled) {

        // Name
        templateNameField.setText(selectedName);

        // Expiration Dates
        Date strictDate = selectedTemplate.getStrictExpirationDate();
        Long delta = selectedTemplate.getDeltaExpirationPeriod();

        if (strictDate == null && delta == null) {
          noneRadioButton.setSelected(true);
        } else if (strictDate != null) {
          strictRadioButton.setSelected(true);
          strictDatePicker.calendar.setEnabled(true);
          if (strictDatePicker.calendar.getDate().getTime() != strictDate.getTime()) {
            strictDatePicker.setDate(strictDate);
          }
        } else {
          Long days = delta / DAY_IN_MS;
          deltaRadioButton.setSelected(true);
          deltaTextField.setEnabled(true);
          deltaTextField.setText(days.toString());
        }
      }

      if (!templateNameField.getText().equals(selectedName)) {
        templateNameField.setText(selectedName != null ? selectedName : "");
      }
      updating = false;
    }

    private void updateFloatPeriod() {
      if (selectedTemplate == null) {
        floatTextField.setText("");
        return;
      }
      
      Long floatPeriod = selectedTemplate.getFloatPeriod();
      if (floatPeriod == null) {
        floatTextField.setText("");
      } else {
        floatTextField.setText(Long.toString(floatPeriod/DAY_IN_MS));
      }
    }
    
    private void nameUpdated() {
      if (!updating) {
        String newname = templateNameField.getText();
        if (newname.length() == 0) {
          templateNameField.setText(selectedName);
        } else if (!newname.equals(selectedName)) {
          model.renameTemplate(selectedName, newname);
        }
      }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    expirationGroup = new javax.swing.ButtonGroup();
    deltaTextField = new javax.swing.JTextField();
    noneRadioButton = new javax.swing.JRadioButton();
    jLabel5 = new javax.swing.JLabel();
    deltaRadioButton = new javax.swing.JRadioButton();
    jLabel6 = new javax.swing.JLabel();
    strictRadioButton = new javax.swing.JRadioButton();
    templateNameField = new javax.swing.JTextField();
    floatTextField = new javax.swing.JTextField();
    jLabel4 = new javax.swing.JLabel();
    strictDatePicker = new net.padlocksoftware.ui.DatePicker();
    jLabel3 = new javax.swing.JLabel();
    jScrollPane2 = new javax.swing.JScrollPane();
    propertiesTable = new javax.swing.JTable();
    jLabel2 = new javax.swing.JLabel();
    jLabel1 = new javax.swing.JLabel();
    removeButton = new javax.swing.JButton();

    deltaTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
    deltaTextField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        deltaTextFieldActionPerformed(evt);
      }
    });
    deltaTextField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        deltaTextFieldFocusLost(evt);
      }
    });

    expirationGroup.add(noneRadioButton);
    noneRadioButton.setText("None");
    noneRadioButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        noneRadioButtonActionPerformed(evt);
      }
    });

    jLabel5.setText("days from creation");

    expirationGroup.add(deltaRadioButton);
    deltaRadioButton.setText("Delta");
    deltaRadioButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        deltaRadioButtonActionPerformed(evt);
      }
    });

    jLabel6.setText("days");

    expirationGroup.add(strictRadioButton);
    strictRadioButton.setText("Strict");
    strictRadioButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        strictRadioButtonActionPerformed(evt);
      }
    });

    templateNameField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        templateNameFieldActionPerformed(evt);
      }
    });
    templateNameField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        templateNameFieldFocusLost(evt);
      }
    });

    floatTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
    floatTextField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        floatTextFieldActionPerformed(evt);
      }
    });
    floatTextField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        floatTextFieldFocusLost(evt);
      }
    });

    jLabel4.setText("License Properties:");

    jLabel3.setText("Floating Expiration:");

    propertiesTable.setModel(new javax.swing.table.DefaultTableModel(
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
    propertiesTable.setRowHeight(22);
    jScrollPane2.setViewportView(propertiesTable);

    jLabel2.setText("License Expiration:");

    jLabel1.setText("Template Name:");

    removeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/padlocksoftware/ui/resources/x-20.png"))); // NOI18N
    removeButton.setText("Remove Selected");
    removeButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        removeButtonActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
              .addComponent(jLabel3)
              .addComponent(jLabel4)
              .addComponent(jLabel1)
              .addComponent(jLabel2))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
              .addComponent(templateNameField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
              .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                .addComponent(strictRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(strictDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
              .addComponent(noneRadioButton, javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                  .addComponent(floatTextField)
                  .addComponent(deltaRadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addComponent(jLabel6)
                  .addComponent(deltaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE))))
          .addComponent(removeButton, javax.swing.GroupLayout.Alignment.TRAILING))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(templateNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(noneRadioButton))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(strictRadioButton)
          .addComponent(strictDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(deltaRadioButton)
          .addComponent(deltaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel5))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(floatTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel3)
          .addComponent(jLabel6))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(jLabel4)
        .addGap(12, 12, 12)
        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(removeButton)
        .addContainerGap(16, Short.MAX_VALUE))
    );

    layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {strictDatePicker, strictRadioButton});

    layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {deltaRadioButton, deltaTextField, jLabel5});

  }// </editor-fold>//GEN-END:initComponents

    private void disableExpirationItems() {
      strictDatePicker.calendar.setEnabled(false);
      deltaTextField.setEnabled(false);
    }

    private void templateNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_templateNameFieldActionPerformed
      nameUpdated();
    }//GEN-LAST:event_templateNameFieldActionPerformed

    private void templateNameFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_templateNameFieldFocusLost
      nameUpdated();
    }//GEN-LAST:event_templateNameFieldFocusLost

    private void noneRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noneRadioButtonActionPerformed
      if (!updating) {
        disableExpirationItems();
        selectedTemplate.setExpirationDate(null);
        model.updateTemplate(selectedName, selectedTemplate);
      }
    }//GEN-LAST:event_noneRadioButtonActionPerformed

    private void strictRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_strictRadioButtonActionPerformed
      if (!updating) {
        disableExpirationItems();
        strictDatePicker.calendar.setEnabled(true);
        Date expirationDate = strictDatePicker.calendar.getDate();
        selectedTemplate.setExpirationDate(expirationDate);
        model.updateTemplate(selectedName, selectedTemplate);
      }
    }//GEN-LAST:event_strictRadioButtonActionPerformed

    private void deltaRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deltaRadioButtonActionPerformed
      if (!updating) {
        disableExpirationItems();        
        long delta = 0;
        deltaTextField.setEnabled(true);
        try {
          delta = Long.parseLong(deltaTextField.getText());
        } catch (Throwable t) {
          deltaTextField.setText("30");
          delta = 30;
        }
        
        delta = delta * DAY_IN_MS;
        selectedTemplate.setExpirationDate(delta);
        model.updateTemplate(selectedName, selectedTemplate);
      }
    }//GEN-LAST:event_deltaRadioButtonActionPerformed

    private void deltaTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deltaTextFieldActionPerformed
      updateDeltaValue();
    }//GEN-LAST:event_deltaTextFieldActionPerformed

    private void deltaTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_deltaTextFieldFocusLost
      updateDeltaValue();
    }//GEN-LAST:event_deltaTextFieldFocusLost

    private void floatTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_floatTextFieldActionPerformed
      saveFloatPeriod();
    }//GEN-LAST:event_floatTextFieldActionPerformed

    private void floatTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_floatTextFieldFocusLost
      saveFloatPeriod();
    }//GEN-LAST:event_floatTextFieldFocusLost

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
      List<String> properties = getSelectedProperties();
      for (String key : properties) {
        selectedTemplate.setProperty(key, null);
      }

      model.updateTemplate(selectedName, selectedTemplate);
    }//GEN-LAST:event_removeButtonActionPerformed

    private List<String> getSelectedProperties() {
      int[] selected = propertiesTable.getSelectedRows();
      List<String> selectedKeys = new ArrayList<String>();

      for (int row : selected) {
        String key = tableModel.getPropertyAtRow(propertiesTable.convertRowIndexToModel(row));
        if (!key.equals(TemplatePropertiesTableModel.KEY_TEXT)) {
          selectedKeys.add(key);
        }
      }

      return selectedKeys;
    }

    private void selectionChanged() {
      removeButton.setEnabled(getSelectedProperties().size() > 0);
    }

    private void saveFloatPeriod() {
      try {
        long floatPeriod = Long.parseLong(floatTextField.getText());
        floatPeriod = floatPeriod * DAY_IN_MS;
        selectedTemplate.setFloatPeriod(floatPeriod);
        model.updateTemplate(selectedName, selectedTemplate);
      } catch (Throwable t) {
        // Revert to saved value
        updateFloatPeriod();
      }
    }
    
    private void updateDeltaValue() {
      if (!updating) {
        try {
          Long value = Long.parseLong(deltaTextField.getText());
          value = value * DAY_IN_MS;
          selectedTemplate.setExpirationDate(value);
          model.updateTemplate(selectedName, selectedTemplate);

        } catch (Throwable t) {
          // Revert to previous value
          Long delta = selectedTemplate.getDeltaExpirationPeriod();
          delta = delta / DAY_IN_MS;
          deltaTextField.setText(delta.toString());
        }
      }
    }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JRadioButton deltaRadioButton;
  private javax.swing.JTextField deltaTextField;
  private javax.swing.ButtonGroup expirationGroup;
  private javax.swing.JTextField floatTextField;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JRadioButton noneRadioButton;
  private javax.swing.JTable propertiesTable;
  private javax.swing.JButton removeButton;
  private net.padlocksoftware.ui.DatePicker strictDatePicker;
  private javax.swing.JRadioButton strictRadioButton;
  private javax.swing.JTextField templateNameField;
  // End of variables declaration//GEN-END:variables

}
