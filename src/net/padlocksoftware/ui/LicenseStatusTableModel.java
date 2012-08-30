package net.padlocksoftware.ui;

import javax.swing.table.AbstractTableModel;
import net.padlocksoftware.padlock.license.LicenseState;
import net.padlocksoftware.padlock.license.TestResult;
import net.padlocksoftware.ui.plugins.StatusPlugin;

/**
 *
 * @author Jason Nichols
 */
public final class LicenseStatusTableModel extends AbstractTableModel {

  ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  private static final String COLUMNS[] = {"Test name", "Result", "Description"};

  private static final Class CLASSES[] = {String.class, Boolean.class, String.class};
  
  ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  private final PadlockModel model;

  private String licenseName;

  private LicenseState states;

  /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  public LicenseStatusTableModel(PadlockModel model) {
    this.model = model;

    model.addModelListener(new AbstractPadlockModelListener(){

      @Override
      public void keyPairAdded(String name) {
        setLicense(licenseName);
      }

      @Override
      public void keyPairRemoved(String name) {
        setLicense(licenseName);
      }

      @Override
      public void keyPairUpdated(String name) {
        setLicense(licenseName);
      }

      @Override
      public void licenseAdded(String name) {
        if (name.equals(licenseName)) {
          setLicense(licenseName);
        }
      }

      @Override
      public void licenseUpdated(String name) {
        if (name.equals(licenseName)) {
          setLicense(licenseName);
        }
      }

    });
  }

  ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
  
  public void setLicense(String licenseName) {
    if (licenseName == null) {
      clearTable();
    } else {
      this.licenseName = licenseName;
      updateTable();
    }
  }

  //------------------------ Implements:

  //------------------------ Overrides: AbstractTableModel

  public int getRowCount() {
    if (states == null) {
      return 0;
    } else return states.getTests().size();
  }

  public int getColumnCount() {
    return COLUMNS.length;
  }

  @Override
  public Class getColumnClass(int column) {
    return CLASSES[column];
  }

  @Override
  public String getColumnName(int column) {
    return COLUMNS[column];
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    TestResult result = states.getTests().get(rowIndex);

    switch (columnIndex) {
      case 0: // Test name
        return result.getTest().getName();
      case 1: // Test result
        return (result.passed() ? Boolean.TRUE : Boolean.FALSE);
      case 2: // Result description
        return result.getResultDescription();
      default:
        return "";
    }
  }

  //---------------------------- Abstract Methods -----------------------------

  //---------------------------- Utility Methods ------------------------------

  private void clearTable() {
    licenseName = null;
    states = null;
    fireTableDataChanged();
  }

  private void updateTable() {
    StatusPlugin statusPlugin = (StatusPlugin)model.getPlugin("net.padlocksoftware.ui.plugins.StatusPlugin");
    states = statusPlugin.getState(licenseName);
    fireTableDataChanged();
  }

  //---------------------------- Property Methods -----------------------------
}
