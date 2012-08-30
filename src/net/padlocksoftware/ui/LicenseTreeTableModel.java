package net.padlocksoftware.ui;

import java.util.Comparator;
import net.padlocksoftware.ui.treetable.AbstractTreeNode;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;
import net.padlocksoftware.padlock.license.License;
import net.padlocksoftware.ui.plugins.CategoryPlugin;
import net.padlocksoftware.ui.treetable.CategoryNode;
import net.padlocksoftware.ui.treetable.LicenseNode;
import net.padlocksoftware.ui.treetable.RootNode;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

/**
 *
 * @author Jason Nichols
 */
public class LicenseTreeTableModel extends AbstractTreeTableModel {

  private class ModelListener extends AbstractPadlockModelListener {

    @Override
    public void licenseAdded(String name) {
      LicenseTreeTableModel.this.licenseAdded(name);
    }

    @Override
    public void licenseRemoved(String name) {
      LicenseTreeTableModel.this.licenseRemoved(name);
    }

    @Override
    public void licenseUpdated(String name) {
      LicenseTreeTableModel.this.licenseUpdated(name);
    }
  }
  
  ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  private static final String[] COLUMNS = {"License", "Status", "Creation Date",
    "Start Date", "Expiration Date", "Hardware Locked", "Signing Key"};
  private static final String DEFAULT_CAT = "(Default Category)";

  ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  private final PadlockModel model;

  private final RootNode rootNode;

  private Map<String, CategoryNode> nodeMap;

  private final CategoryPlugin catPlugin;

  private final CategoryNode defaultCatNode;

  /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  public LicenseTreeTableModel(PadlockModel model) {
    this.model = model;

    rootNode = new RootNode();

    model.addModelListener(new ModelListener());

    nodeMap = new ConcurrentHashMap<String, CategoryNode>();

    // We always have a default map
    defaultCatNode = new CategoryNode(DEFAULT_CAT);
    nodeMap.put(DEFAULT_CAT, defaultCatNode);
    rootNode.addCategory(defaultCatNode);

    catPlugin = (CategoryPlugin) model.getPlugin(CategoryPlugin.class.getName());
  }

  ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //------------------------ Implements:

  //------------------------ Overrides: AbstractTreeTableModel

  public int getColumnCount() {
    return COLUMNS.length;
  }

  @Override
  public String getColumnName(int column) {
    return COLUMNS[column];
  }

  public Object getValueAt(Object o, int i) {
    if (o instanceof AbstractTreeNode) {
      return ((AbstractTreeNode) o).getValueAt(i);
    } else {
      throw new IllegalArgumentException("Node must be an instance of AbstractTreeNode");
    }
  }

  public Object getChild(Object parent, int index) {
    return ((AbstractTreeNode) parent).getChild(index);
  }

  public int getChildCount(Object parent) {
    return ((AbstractTreeNode) parent).getChildCount();
  }

  public int getIndexOfChild(Object parent, Object child) {
    return ((AbstractTreeNode) parent).getIndexOfChild(child);
  }

  @Override
  public Object getRoot() {
    return rootNode;
  }

  //---------------------------- Abstract Methods -----------------------------

  //---------------------------- Utility Methods ------------------------------

  private CategoryNode lookupCategory(String licenseName) {
    CategoryNode catNode = null;
    for (CategoryNode cat : nodeMap.values()) {
      if (cat.containsChild(licenseName)) {
        catNode = cat;
        break;
      }
    }
    return catNode;
  }

  private CategoryNode getOrCreateCategory(String licenseName) {
    CategoryNode catNode = defaultCatNode;

    String licenseCat = catPlugin.getLicenseCategory(licenseName);
    if (licenseCat != null) {
      // This license has an assigned category, get or create it
      catNode = nodeMap.get(licenseCat);

      if (catNode == null) {
        // This is a new category
        catNode = new CategoryNode(licenseCat);
        nodeMap.put(licenseCat, catNode);
        rootNode.addCategory(catNode);
        modelSupport.fireTreeStructureChanged(new TreePath(rootNode));
      }
    }

    return catNode;
  }

  private void licenseAdded(final String name) {
    SwingUtilities.invokeLater(new Runnable() {

      public void run() {

        // Let's get/add the current license category
        String catName = catPlugin.getLicenseCategory(name);

        if (catName == null) {
          catName = DEFAULT_CAT;
        }

        CategoryNode catNode = getOrCreateCategory(name);

        License l = model.getLicense(name);
        LicenseNode node = new LicenseNode(name, l, model);
        catNode.addLicenseNode(node);
        TreePath path = new TreePath(new Object[]{rootNode, catNode});
        modelSupport.fireChildAdded(path, catNode.getIndexOfChild(node), node);
      }
    });
  }

  private void licenseRemoved(final String name) {
    SwingUtilities.invokeLater(new Runnable() {

      public void run() {
        // Manually find the category this license exists under
        CategoryNode catNode = lookupCategory(name);

        int index = catNode.getLicenseIndex(name);
        LicenseNode node = catNode.removeLicense(name);

        TreePath path = new TreePath(new Object[]{rootNode, catNode});
        modelSupport.fireChildRemoved(path, index, node);

        // If this is the last license in the given category, remove the
        // category too
        if (catNode.getChildCount() == 0 && !catNode.getName().equals(DEFAULT_CAT)) {
          path = new TreePath(rootNode);
          nodeMap.remove(catNode.getName());
          index = rootNode.getIndexOfChild(catNode);
          rootNode.removeCategory(catNode);
          modelSupport.fireChildRemoved(path, index, catNode);
        }
      }
    });
  }

  private void licenseUpdated(final String name) {
    SwingUtilities.invokeLater(new Runnable() {

      public void run() {
        // We need to check both the license contents and the license category
        // for changes.

        // License Category
        CategoryNode currentCat = lookupCategory(name);
        CategoryNode pluginCat = getOrCreateCategory(name);

        if (!currentCat.getName().equals(pluginCat.getName())) {
          // The categories have changed, remove the node from the old
          // category
          int index = currentCat.getLicenseIndex(name);
          LicenseNode licenseNode = (LicenseNode)currentCat.getChild(index);
          TreePath path = new TreePath(new Object[]{rootNode, currentCat});
          currentCat.removeLicense(name);
          modelSupport.fireChildRemoved(path, index, licenseNode);

          pluginCat.addLicenseNode(licenseNode);
          index = pluginCat.getIndexOfChild(licenseNode);
          path = new TreePath(new Object[]{rootNode, pluginCat});
          modelSupport.fireChildAdded(path, index, licenseNode);

          // If the old category is now empty, remove it
          if (currentCat.getChildCount() == 0 && !currentCat.getName().equals(DEFAULT_CAT)) {
            path = new TreePath(rootNode);
            nodeMap.remove(currentCat.getName());
            index = rootNode.getIndexOfChild(currentCat);
            rootNode.removeCategory(currentCat);
            modelSupport.fireChildRemoved(path, index, currentCat);
          }
        }

        // Update the license contents
        LicenseNode node = pluginCat.getLicense(name);
        node.updateLicense(model.getLicense(name));
        int index = pluginCat.getLicenseIndex(name);
        TreePath path = new TreePath(new Object[]{rootNode, currentCat});
        modelSupport.fireChildChanged(path, index, node);
        //modelSupport.fireTreeStructureChanged(path);
      }
    });
  }
  
  //---------------------------- Property Methods -----------------------------

  public void setLicenseComparator(Comparator<LicenseNode> comparator) {
    rootNode.setLicenseComparator(comparator);
    for (CategoryNode node : nodeMap.values()) {
      TreePath path = new TreePath(new Object[]{rootNode, node});
      modelSupport.fireTreeStructureChanged(path);
    }
  }
}
