/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.padlocksoftware.ui;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import net.padlocksoftware.padlocktemplates.LicenseTemplateDefinition;

public final class LicenseTemplateTableModel extends AbstractTableModel {

   ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

   private final static String[] COLUMN_NAMES = {"Template Name"};

   public final static String EDIT_TEXT = "<Create a template>";

   ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

   //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

   private final PadlockModel model;

   private final Map<String, LicenseTemplateDefinition> templateMap;

   /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

   public LicenseTemplateTableModel(PadlockModel model) {
      this.model = model;
      templateMap = new ConcurrentSkipListMap<String, LicenseTemplateDefinition>(String.CASE_INSENSITIVE_ORDER);


      model.addModelListener(new AbstractPadlockModelListener() {

         @Override
         public void templateAdded(String name) {
            updateTable();
         };

         @Override
         public void templateRemoved(String name) {
            updateTable();
         };
      });

      updateTable();

   }

   ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

   private final void updateTable() {
      SwingUtilities.invokeLater(new Runnable(){
         public void run() {
            templateMap.clear();
            templateMap.putAll(model.getTemplates());
            fireTableDataChanged();
         }
      });

   }

   //------------------------ Implements:

   //------------------------ Overrides: AbstractTableModel

   @Override
   public void setValueAt(Object value, int row, int col) {
      String name = value.toString();

      if (name.length() > 0 && !name.equals(EDIT_TEXT)) {
         LicenseTemplateDefinition template = new LicenseTemplateDefinition();
         model.addTempate(name, template);
      }
   }

   @Override
   public boolean isCellEditable(int row, int col) {
      return row == 0;
   }

   @Override
   public String getColumnName(int col) {
      return COLUMN_NAMES[col];
   }

   public int getRowCount() {
      return templateMap.size() + 1;
   }

   public int getColumnCount() {
      return COLUMN_NAMES.length;
   }

   public Object getValueAt(int rowIndex, int columnIndex) {
      if (rowIndex == 0) {
         return EDIT_TEXT;
      } else {
         rowIndex--;
      }

      return templateMap.keySet().toArray()[rowIndex];
   }

   //---------------------------- Abstract Methods -----------------------------

   //---------------------------- Utility Methods ------------------------------

   //---------------------------- Property Methods -----------------------------
   public String getTemplateAtRow(int row) {
     if (row == 0) {
       return null;
     } else return templateMap.keySet().toArray(new String[0])[row-1];
   }
}
