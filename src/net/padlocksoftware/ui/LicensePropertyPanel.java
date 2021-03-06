/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * LicensePropertyPanel.java
 *
 * Created on Jan 26, 2010, 9:54:02 AM
 */
package net.padlocksoftware.ui;

/**
 *
 * @author jason
 */
public class LicensePropertyPanel extends javax.swing.JPanel {

  private final PadlockModel model;

  private final LicensePropertiesTableModel tableModel;

  /** Creates new form LicensePropertyPanel */
  public LicensePropertyPanel() {
    initComponents();
    model = null;
    tableModel = null;
  }

  public LicensePropertyPanel(PadlockModel model) {
    initComponents();
    this.model = model;
    tableModel = new LicensePropertiesTableModel(model);
    propertiesTable.setModel(tableModel);
    propertiesTable.setDefaultRenderer(String.class, new LicensePropertiesTableCellRenderer());
  }


  public void setLicense(String name) {
    tableModel.setLicense(name);
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jPanel1 = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    propertiesTable = new javax.swing.JTable();
    jButton1 = new javax.swing.JButton();

    jPanel1.setBackground(new java.awt.Color(70, 70, 70));

    jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
    jLabel1.setForeground(new java.awt.Color(255, 255, 255));
    jLabel1.setText("License Properties");

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE))
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
    );

    jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(70, 70, 70)));

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
    jScrollPane1.setViewportView(propertiesTable);

    jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/padlocksoftware/ui/resources/x-20.png"))); // NOI18N
    jButton1.setText("Remove Selected");

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jButton1))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(0, 0, 0)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton jButton1;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JTable propertiesTable;
  // End of variables declaration//GEN-END:variables
}
