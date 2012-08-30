package net.padlocksoftware.ui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Jason Nichols
 */
public class LicensePropertiesTableCellRenderer extends DefaultTableCellRenderer {
  ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  private Color textColor = null;

  private final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();

  /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //------------------------ Implements:
  
  //------------------------ Overrides: DefaultTableCellRenderer
  
  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

    String text = (String) value;

    if (row == 0 && (text.equals(LicensePropertiesTableModel.KEY_TEXT) ||
            text.equals(LicensePropertiesTableModel.VALUE_TEXT)) ||
            text.equals(LicenseTemplateTableModel.EDIT_TEXT)) {

      JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      label.setForeground(isSelected ? Color.WHITE : Color.GRAY);
      return label;
    } else {
      return renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
  }

  //---------------------------- Abstract Methods -----------------------------

  //---------------------------- Utility Methods ------------------------------

  //---------------------------- Property Methods -----------------------------
}
