package net.padlocksoftware.ui.treetable;

import javax.swing.Icon;

/**
 *
 * @author Jason Nichols
 */
public class LicensePropertyNode extends AbstractTreeNode implements Comparable {

  ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  private final String key;

  private final String value;

  /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  public LicensePropertyNode(String key, String value) {
    this.key = key;
    this.value = value;
  }

  ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //------------------------ Implements: Comparable
  public int compareTo(Object o) {
    LicensePropertyNode node = (LicensePropertyNode)o;

    return toString().compareTo(node.toString());
  }
  
  //------------------------ Overrides: AbstractTreeNode

  @Override
  public Object getValueAt(int column) {
    if (column == 0) {
      return key + " : " + value;
    } else return null;
  }

  @Override
  public Object getChild(int index) {
    return null;
  }

  @Override
  public int getChildCount() {
    return 0;
  }

  @Override
  public int getIndexOfChild(Object child) {
    return 0;
  }

  public Icon getIcon() {
    return null;
  }
  
  //---------------------------- Abstract Methods -----------------------------

  //---------------------------- Utility Methods ------------------------------

  //---------------------------- Property Methods -----------------------------
}
