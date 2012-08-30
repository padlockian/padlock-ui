package net.padlocksoftware.ui.treetable;

import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;

/**
 *
 * @author Jason Nichols
 */
public abstract class AbstractTreeNode {
  ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  private static final URL url = AbstractTreeNode.class.getResource("/net/padlocksoftware/ui/resources/padlock-20.png");
  private static final URL selectedUrl = AbstractTreeNode.class.getResource("/net/padlocksoftware/ui/resources/padlock-20-inverted.png");
  
  private static final ImageIcon DEFAULT_ICON = new ImageIcon(url);
  private static final ImageIcon SELECTED_ICON = new ImageIcon(selectedUrl);

  ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //------------------------ Implements:

  //------------------------ Overrides:

  //---------------------------- Abstract Methods -----------------------------

  public abstract Object getValueAt(int column);

  public abstract Object getChild(int index);

  public abstract int getChildCount();

  public abstract int getIndexOfChild(Object child);

  //---------------------------- Utility Methods ------------------------------

  //---------------------------- Property Methods -----------------------------
  public Icon getDefaultIcon() {
    return DEFAULT_ICON;
  }

  public Icon getSelectedIcon() {
    return SELECTED_ICON;
  }

  public JPopupMenu getPopup() {
    return null;
  }
}
