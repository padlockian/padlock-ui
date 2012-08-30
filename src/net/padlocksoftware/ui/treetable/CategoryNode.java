package net.padlocksoftware.ui.treetable;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author Jason Nichols
 */
public class CategoryNode extends AbstractTreeNode {

  ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  private static final URL url = AbstractTreeNode.class.getResource("/net/padlocksoftware/ui/resources/folder-20.png");
  private static final ImageIcon DEFAULT_ICON = new ImageIcon(url);

  ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  private final String categoryName;

  private final List<LicenseNode> nodeList;

  private Comparator<LicenseNode> currentComparator;

  /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  public CategoryNode(String categoryName) {

    this.categoryName = categoryName;

    nodeList = new ArrayList<LicenseNode>();

    currentComparator = LicenseComparators.NAME.getComparator();
  }
  
  ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //------------------------ Implements:
  
  //------------------------ Overrides: AbstractTreeNode

  @Override
  public Object getValueAt(int column) {
    if (column == 0) {
      return categoryName;
    } else return null;
  }

  @Override
  public Object getChild(int index) {
    return nodeList.get(index);
  }

  @Override
  public int getChildCount() {
    return nodeList.size();
  }

  @Override
  public int getIndexOfChild(Object child) {
    return nodeList.indexOf(child);
  }

  @Override
  public Icon getDefaultIcon() {
    return DEFAULT_ICON;
  }

  @Override
  public Icon getSelectedIcon() {
    return DEFAULT_ICON;
  }
  
  //---------------------------- Abstract Methods -----------------------------

  //---------------------------- Utility Methods ------------------------------

  //---------------------------- Property Methods -----------------------------

  public void setComparator (Comparator<LicenseNode> comparator) {
    currentComparator = comparator;
    Collections.sort(nodeList, currentComparator);
  }

  public LicenseNode getLicense(String licenseName) {
    LicenseNode node = null;

    for (LicenseNode n: nodeList) {
      if (n.getName().equals(licenseName)) {
        node = n;
        break;
      }
    }
    return node;

  }

  public boolean containsChild(String licenseName) {
    return getLicense(licenseName) != null;
  }

  public String getName() {
    return categoryName;
  }

  public int getLicenseIndex(String licenseName) {
    int index = 0;
    for (int x = 0 ; x < nodeList.size() ; x++)  {
      LicenseNode n = nodeList.get(x);
      if (n.getName().equals(licenseName)) {
        index = x;
        break;
      }
    }

    return index;
  }
  
  public void addLicenseNode(LicenseNode node) {
    nodeList.add(node);
    Collections.sort(nodeList, currentComparator);
  }

  public LicenseNode removeLicense(String licenseName) {
    LicenseNode dNode = null;
    for (LicenseNode node : nodeList) {
      if (node.getName().equals(licenseName)) {
        nodeList.remove(node);
        dNode = node;
        break;
      }
    }

    return dNode;
  }

  public void updateLicenseNode(LicenseNode node) {
    Collections.sort(nodeList, currentComparator);
  }
  
}
