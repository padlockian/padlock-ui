package net.padlocksoftware.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import net.padlocksoftware.padlocktemplates.LicenseTemplateDefinition;

/**
 *
 * @author Jason Nichols
 */
public class TemplatePropertiesTableModel extends AbstractTableModel {

  ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
  private static String[] COLUMNS = {"Property", "Value"};

  private static Class[] CLASSES = {String.class, String.class};

  public static final String KEY_TEXT = "<Add a property>";

  public static final String VALUE_TEXT = "<Add a value>";

  ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  private final PadlockModel model;

  private String selectedName;
  
  private LicenseTemplateDefinition selectedTemplate;

  private final ArrayList<String> keys;
  private final ArrayList<String> values;

  /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  public TemplatePropertiesTableModel(PadlockModel model) {
    this.model = model;
    keys = new ArrayList<String>();
    values = new ArrayList<String>();
    model.addModelListener(new AbstractPadlockModelListener(){

      @Override
      public void licenseUpdated(final String name) {
        SwingUtilities.invokeLater(new Runnable(){
          public void run() {
            if (name.equals(selectedName)) {
              setTemplate(name);
            }            
          }
        });
      }
    });
  }

  ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
  public void setTemplate(String name) {
    keys.clear();
    values.clear();

    if (name != null) {
      selectedName = name;
      selectedTemplate = model.getTemplates().get(selectedName);
      
      if (selectedTemplate == null) return;

      keys.add(KEY_TEXT);
      values.add(VALUE_TEXT);

      TreeMap<String, String> props = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
      props.putAll(selectedTemplate.getProperties());
      for(Entry<String, String> entry : props.entrySet()) {
        keys.add(entry.getKey());
        values.add(entry.getValue());
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
    return true;
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
      selectedTemplate.setProperty(currentKey, null);
    } else newValue = value;

    if (!currentKey.equals(newKey) || !currentValue.equals(newValue)) {
      // A change has occurred
      keys.set(row, newKey);
      values.set(row, newValue);
      fireTableRowsUpdated(row, row);

      selectedTemplate.setProperty(newKey, newValue);
      model.updateTemplate(selectedName, selectedTemplate);
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
      selectedTemplate.setProperty(currentKey, currentValue);
      model.updateTemplate(selectedName, selectedTemplate);
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

      selectedTemplate.setProperty(key, null);
      int index = keys.indexOf(key);
      keys.remove(key);
      values.remove(index);
    }
    model.updateTemplate(selectedName, selectedTemplate);
    
    fireTableDataChanged();
  }
}
