package net.padlocksoftware.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import net.padlocksoftware.ui.plugins.StatusPlugin;

/**
 *
 * @author Jason Nichols
 */
public class KeysComboBoxModel extends AbstractListModel implements ComboBoxModel {

  ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  private String selectedKey = null;

  private String selectedLicense = null;

  private List<String> keys;

  private final PadlockModel model;

  private final StatusPlugin statusPlugin;

  /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  public KeysComboBoxModel(PadlockModel model) {
    this.model = model;
    setSelectedLicense(null);
    statusPlugin = (StatusPlugin)model.getPlugin(StatusPlugin.class.getName());
    setSelectedLicense(null);

    model.addModelListener(new AbstractPadlockModelListener(){

      @Override
      public void keyPairAdded(String name) {
        setSelectedLicense(selectedLicense);
      }

      @Override
      public void keyPairRemoved(String name) {
        setSelectedLicense(selectedLicense);
      }

      @Override
      public void keyPairUpdated(String name) {
        setSelectedLicense(selectedLicense);
      }
    });

  }

  ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //------------------------ Implements:

  public void setSelectedItem(Object anItem) {
    if (keys.contains((String)anItem)) {
      selectedKey = (String)anItem;
      this.fireContentsChanged(this, 0, -1);
    }
  }

  public String getSelectedItem() {
    return selectedKey;
  }

  //------------------------ Overrides: ListModel

  public int getSize() {
    return keys.size();
  }

  public String getElementAt(int index) {
    return keys.get(index);
  }

  //---------------------------- Abstract Methods -----------------------------

  //---------------------------- Utility Methods ------------------------------

  //---------------------------- Property Methods -----------------------------
  public void setSelectedLicense(String licenseName) {
    keys = new ArrayList<String>(model.getKeys());
    Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);

    selectedLicense = licenseName;

    if (selectedLicense != null) {
      selectedKey = statusPlugin.getLicenseKey(licenseName, null);
    } else {
      selectedKey = null;
    }

    this.fireContentsChanged(this, 0, -1);
  }
}
