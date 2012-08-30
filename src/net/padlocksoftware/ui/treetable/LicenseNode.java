package net.padlocksoftware.ui.treetable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedSet;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import net.padlocksoftware.padlock.license.License;
import net.padlocksoftware.padlock.license.LicenseState;
import net.padlocksoftware.ui.MoveToCategoryDialog;
import net.padlocksoftware.ui.PadlockModel;
import net.padlocksoftware.ui.PadlockUI;
import net.padlocksoftware.ui.plugins.CategoryPlugin;
import net.padlocksoftware.ui.plugins.StatusPlugin;

/**
 *
 * @author Jason Nichols
 */
public class LicenseNode extends AbstractTreeNode {

  ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
  ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
  //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
  private final PadlockModel model;
  private final String name;
  private License license;

  /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
  public LicenseNode(String name, License license, PadlockModel model) {
    this.model = model;
    this.name = name;
    this.license = license;
  }

  ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
  //------------------------ Implements:
  //------------------------ Overrides: Object
  @Override
  public String toString() {
    return name;
  }

  //------------------------ Overrides: AbstractTreeNode
  @Override
  public JPopupMenu getPopup() {
    final CategoryPlugin catPlugin = (CategoryPlugin) model.getPlugin("net.padlocksoftware.ui.plugins.CategoryPlugin");

    SortedSet<String> categories = catPlugin.getLicenseCategories();
    categories.add("(Default Category)");
    
    JPopupMenu menu = new JPopupMenu();

    JMenu catMenu = new JMenu("Move to category");

    for (final String cat : categories) {
      JMenuItem catItem = new JMenuItem(cat);

      catItem.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent e) {
          catPlugin.setLicenseCategory(getName(), cat);
        }
      });

      catMenu.add(catItem);
    }
    menu.add(catMenu);

    JMenuItem item = new JMenuItem("Move to new category");
    item.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        MoveToCategoryDialog dialog = new MoveToCategoryDialog(PadlockUI.padlockUI, model, getName());
        dialog.setLocationRelativeTo(PadlockUI.padlockUI);
        dialog.pack();
        dialog.setVisible(true);
      }
    });
    menu.add(item);
    return menu;
  }

  public int getChildCount() {
    return 0;
  }

  public Object getChild(int index) {
    return null;
  }

  public Object getValueAt(int index) {
    switch (index) {
      case 0:  // Name
        return name;
      case 1: // Status
        return getLicenseStatus();
      case 2:
        return formatDate(license.getCreationDate());
      case 3:
        return formatDate(license.getStartDate());
      case 4:
        return formatDate(license.getExpirationDate());
      case 5:
        boolean locked = license.getHardwareAddresses().size() != 0;
        return locked ? "Yes" : "No";
      case 6:
        StatusPlugin statusPlugin = (StatusPlugin) model.getPlugin("net.padlocksoftware.ui.plugins.StatusPlugin");
        return statusPlugin.getLicenseKey(name, "");
      default:
        return "";
    }
  }

  public int getIndexOfChild(Object obj) {
    return 0;
  }

  //---------------------------- Abstract Methods -----------------------------
  //---------------------------- Utility Methods ------------------------------
  private String formatDate(Date d) {
    if (d == null) {
      return null;
    }

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    return format.format(d);
  }

  //---------------------------- Property Methods -----------------------------
  public String getName() {
    return name;
  }

  public License getLicense() {
    return license;
  }

  public void updateLicense(License l) {
    this.license = l;
  }

  public String getLicenseStatus() {
    if (license.isSigned()) {
      StatusPlugin statusPlugin = (StatusPlugin) model.getPlugin("net.padlocksoftware.ui.plugins.StatusPlugin");
      LicenseState state = statusPlugin.getState(name);
      return (state.isValid() ? "Valid" : "Invalid");
    } else {
      return "Unsigned";
    }
  }
}
