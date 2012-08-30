package net.padlocksoftware.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import net.padlocksoftware.padlock.license.License;

/**
 *
 * @author Jason Nichols
 */
public class HardwareTableModel extends AbstractTableModel {

  ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
  private static final String[] COLUMNS = {"Hardware Locking"};
  public static final String ADD_VALUE = "<Add an address>";
  ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
  //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
  private final PadlockModel model;
  private String licenseName;
  private License license;
  private final List<String> addresses;

  /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
  public HardwareTableModel(PadlockModel model) {
    this.model = model;

    licenseName = null;

    license = null;

    addresses = new ArrayList<String>();

    model.addModelListener(new AbstractPadlockModelListener() {

      @Override
      public void licenseUpdated(final String name) {
        SwingUtilities.invokeLater(new Runnable() {

          public void run() {
            if (name.equals(licenseName)) {
              setLicense(name);
            }
          }
        });
      }
    });
  }

  ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //------------------------ Implements:

  //------------------------ Overrides: AbstractTableModel

  @Override
  public void setValueAt(Object value, int row, int column) {

    // Todo: Strip and format this String
    String mac = ((String) value).replaceAll(":", "").toLowerCase();

    if (row != 0) {
      // We're updating the address list
      String oldMac = addresses.get(row-1);
      license.removeHardwareAddress(oldMac);
    }

    // Add the new value
    license.addHardwareAddress(mac);

    // Update license
    model.updateLicense(licenseName, license);
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return (!license.isSigned());
  }

  public int getRowCount() {
    if (license == null) {
      return 0;
    } else {
      return addresses.size() + (license.isSigned() ? 0 : 1);
    }
  }

  public int getColumnCount() {
    return COLUMNS.length;
  }

  @Override
  public String getColumnName(int column) {
    return COLUMNS[column];
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    if (license.isSigned()) {
      return formatMac(addresses.get(rowIndex));
    } else {
      if (rowIndex != 0) {
        return formatMac(addresses.get(rowIndex - 1));
      } else {
        return ADD_VALUE;
      }
    }

  }

  //---------------------------- Abstract Methods -----------------------------

  //---------------------------- Utility Methods ------------------------------

  private void updateModel() {
    addresses.clear();

    if (licenseName != null) {
      License l = model.getLicense(licenseName);
      if (licenseName == null || l == null) {
      } else {
        addresses.addAll(l.getHardwareAddresses());
        Collections.sort(addresses, String.CASE_INSENSITIVE_ORDER);
      }
    }

    Iterator<String> iter = addresses.iterator();
    while (iter.hasNext()) {
      String str = iter.next();
      if (str.length() != 12) {
        iter.remove();
      }
    }

    fireTableDataChanged();

  }

  private String formatMac(String mac) {
    String str = "";

    for (int x = 0; x < 6; x++) {
      if (x > 0) {
        str += ":";
      }
      str += mac.substring(2 * x, 2 * x + 2);
    }
    return str;
  }

  //---------------------------- Property Methods -----------------------------

  public String getMacAt(int row) {
    return addresses.get(row);
  }
  
  public void setLicense(String licenseName) {
    this.licenseName = licenseName;
    if (licenseName != null) {
      this.license = model.getLicense(licenseName);
    } else {
      license = null;
    }

    updateModel();
  }
}
