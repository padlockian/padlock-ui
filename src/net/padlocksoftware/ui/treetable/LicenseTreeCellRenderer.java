package net.padlocksoftware.ui.treetable;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author Jason Nichols
 */
public class LicenseTreeCellRenderer extends DefaultTreeCellRenderer {

  ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\


  /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //------------------------ Implements:

  //------------------------ Overrides:

  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value,
          boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

    AbstractTreeNode node = (AbstractTreeNode)value;

    JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value,
            selected, expanded, leaf, row, hasFocus);
    
    label.setText(node.getValueAt(0).toString());
    Icon icon;

    if (selected) {
      icon = node.getSelectedIcon();
    } else icon = node.getDefaultIcon();
    
    label.setIcon(icon);
    
    return label;
  }

  //---------------------------- Abstract Methods -----------------------------

  //---------------------------- Utility Methods ------------------------------

  //---------------------------- Property Methods -----------------------------
}

