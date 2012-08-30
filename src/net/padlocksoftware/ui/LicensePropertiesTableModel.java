package net.padlocksoftware.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import net.padlocksoftware.padlock.license.License;

/**
 *
 * @author Jason Nichols
 */
public class LicensePropertiesTableModel extends AbstractTableModel {

  ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
  private static String[] COLUMNS = {"Property", "Value"};

  private static Class[] CLASSES = {String.class, String.class};

  public static final String KEY_TEXT = "<Add a property>";

  public static final String VALUE_TEXT = "<Add a value>";

  ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  private final PadlockModel model;

  private String selectedName;
  
  private License selectedLicense;

  private final ArrayList<String> keys;
  private final ArrayList<String> values;

  /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  public LicensePropertiesTableModel(PadlockModel model) {
    this.model = model;
    keys = new ArrayList<String>();
    values = new ArrayList<String>();
    model.addModelListener(new AbstractPadlockModelListener(){

      @Override
      public void licenseUpdated(final String name) {
        SwingUtilities.invokeLater(new Runnable(){
          public void run() {
            if (name.equals(selectedName)) {
              setLicense(name);
            }            
          }
        });
      }
    });
  }

  ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
  public void setLicense(String name) {
    keys.clear();
    values.clear();

    if (name != null) {
      selectedName = name;
      selectedLicense = model.getLicense(name);
      
      if (selectedLicense == null) return;

      if (!selectedLicense.isSigned()) {
        keys.add(KEY_TEXT);
        values.add(VALUE_TEXT);
      }

      Properties props = selectedLicense.getProperties();
      SortedSet<String> sorted = new TreeSet<String>(props.stringPropertyNames());
      for(String key : sorted) {
        String value = props.getProperty(key);
        keys.add(key);
        values.add(value);
      }
    }

    fireTableDataChanged();
  }
  
  //------------------------ Implements:
  
  //------------------------ Overrides: AbstractTableModel

  @Override
  public void setValueAt(Object obj, int row, int column) {

    String text = ((String)obj).trim();

    if (text.length() == 0 ) return ;

    if (row == 0) {
      // We're adding a value
      addProperty(text , column == 0);
    } else {
      // We're editing a current value
      updateProperty(text, row, column == 0);
      
    }
  }

  @Override
  public Class getColumnClass(int column) {
    return CLASSES[column];
  }

  @Override
  public String getColumnName(int column) {
    return COLUMNS[column];
  }
  
  public int getRowCount() {
    return keys.size();
  }

  public int getColumnCount() {
    return COLUMNS.length;
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return !selectedLicense.isSigned();
  }

  public Object getValueAt(int rowIndex, int columnIndex) {

    List<String> list = columnIndex == 0 ? keys : values;

    return list.get(rowIndex);
  }

  //---------------------------- Abstract Methods -----------------------------

  //---------------------------- Utility Methods ------------------------------

  private void updateProperty(String value, int row, boolean isKey) {
    String currentKey = keys.get(row);
    String currentValue = values.get(row);

    String newKey = currentKey;
    String newValue = currentValue;

    if (isKey) {
      newKey = value;
      selectedLicense.addProperty(currentKey, null);
    } else newValue = value;

    if (!currentKey.equals(newKey) || !currentValue.equals(newValue)) {
      // A change has occurred
      keys.set(row, newKey);
      values.set(row, newValue);
      fireTableRowsUpdated(row, row);

      selectedLicense.addProperty(newKey, newValue);
      model.updateLicense(selectedName, selectedLicense);
    }
  }
  
  private void addProperty(String value, boolean isKey) {

    String currentKey = keys.get(0);
    String currentValue = values.get(0);

    if (isKey) {
      currentKey = value;
    } else currentValue = value;

    if (!currentKey.equals(KEY_TEXT) && !currentValue.equals(VALUE_TEXT)) {
      // Add is complete
      keys.set(0, KEY_TEXT);
      values.set(0, VALUE_TEXT);
      keys.add(currentKey);
      values.add(currentValue);
      selectedLicense.addProperty(currentKey, currentValue);
      model.updateLicense(selectedName, selectedLicense);
      fireTableDataChanged();

    } else {
      // Still need to add the key/value
      keys.set(0, currentKey);
      values.set(0, currentValue);
      fireTableRowsUpdated(0, 0);
    }
  }

  //---------------------------- Property Methods -----------------------------
  public String getPropertyAtRow(int row) {
    return keys.get(row);
  }

  public void removeProperties(List<String> properties) {
    for (String key : properties) {
      
      if (key.equals(KEY_TEXT)) continue;

      selectedLicense.addProperty(key, null);
      int index = keys.indexOf(key);
      keys.remove(key);
      values.remove(index);
    }
    model.updateLicense(selectedName, selectedLicense);
    
    fireTableDataChanged();
  }
}
