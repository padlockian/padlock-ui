package net.padlocksoftware.ui;

import java.awt.Component;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Jason Nichols
 */
public class TestResultTableCellRenderer extends DefaultTableCellRenderer {
  ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\


  URL goodURL = this.getClass().getResource("/net/padlocksoftware/ui/resources/check-16.png");
  ImageIcon goodIcon = new ImageIcon(goodURL);


  URL badURL = this.getClass().getResource("/net/padlocksoftware/ui/resources/bad-16.png");
  ImageIcon badIcon = new ImageIcon(badURL);

  ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //------------------------ Implements:
  
  //------------------------ Overrides: DefaultTableCellRenderer
  
  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

    JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    label.setText("");
    label.setHorizontalAlignment(SwingConstants.CENTER);
    if (((Boolean)value) == true) {
      label.setIcon(goodIcon);
    } else {
      label.setIcon(badIcon);
    }

    return label;
  }

  //---------------------------- Abstract Methods -----------------------------

  //---------------------------- Utility Methods ------------------------------

  //---------------------------- Property Methods -----------------------------
}
