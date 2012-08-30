package net.padlocksoftware.ui.treetable;

import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.Icon;

/**
 *
 * @author Jason Nichols
 */
public class RootNode extends AbstractTreeNode {

  ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
  
  private final CopyOnWriteArrayList<CategoryNode> nodes;

  private Comparator<LicenseNode> licenseComparator;

  /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  public RootNode() {
    licenseComparator = LicenseComparators.NAME.getComparator();

    nodes = new CopyOnWriteArrayList<CategoryNode>();
  }

  ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
  
  public void addCategory(CategoryNode node) {
    nodes.add(node);
    node.setComparator(licenseComparator);
  }

  public void removeCategory(CategoryNode node) {
    nodes.remove(node);
  }
  
  //------------------------ Implements:
  
  //------------------------ Overrides: AbstractTreeNode

  @Override
  public Object getValueAt(int column) {
    if (column == 0) {
      return "Licenses";
    } else {
      return null;
    }
  }

  @Override
  public Object getChild(int index) {
    return nodes.get(index);
  }

  @Override
  public int getChildCount() {
    return nodes.size();
  }

  @Override
  public int getIndexOfChild(Object child) {
    return nodes.indexOf(child);
  }

  @Override
  public Icon getDefaultIcon() {
    return null;
  }

  @Override
  public Icon getSelectedIcon() {
    return null;
  }

  //---------------------------- Abstract Methods -----------------------------

  //---------------------------- Utility Methods ------------------------------

  //---------------------------- Property Methods -----------------------------
  
  public void setLicenseComparator(Comparator<LicenseNode> comparator) {
    licenseComparator = comparator;
    for (CategoryNode node : nodes) {
      node.setComparator(comparator);
    }
  }
}
