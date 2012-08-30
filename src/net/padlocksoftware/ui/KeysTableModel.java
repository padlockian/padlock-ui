package net.padlocksoftware.ui;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Jason Nichols
 */
public class KeysTableModel extends AbstractTableModel {

  ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
  
  private static final String[] COLUMNS = {"Name", "Type"};

  private static final Map<String, String> typeMap = new HashMap<String, String>();

  static {
    typeMap.put("PKCS#8", "1024 Bit DSA");
  }
  ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  private final PadlockModel model;

  private final LinkedHashMap<String, KeyPair> keyMap;

  /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  public KeysTableModel(PadlockModel model) {
    this.model = model;
    keyMap = new LinkedHashMap<String, KeyPair>();

    model.addModelListener(new AbstractPadlockModelListener(){

      @Override
      public void keyPairAdded(final String name) {
        SwingUtilities.invokeLater(new Runnable(){
          public void run() {
            keyAdded(name);
          }
        });
      }

      @Override
      public void keyPairRemoved(final String name) {
        SwingUtilities.invokeLater(new Runnable(){
          public void run() {
            keyRemoved(name);
          }
        });
      }

      @Override
      public void keyPairUpdated(final String name) {
        SwingUtilities.invokeLater(new Runnable(){
          public void run() {
            keyUpdated(name);
          }
        });

      }
    });
  }
  
  ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
  
  private void keyAdded(String name) {
    keyMap.put(name, model.getKey(name));
    int row = keyMap.size() - 1;
    fireTableRowsInserted(row, row);
  }

  private void keyRemoved(String name) {
    KeyPair kp = keyMap.remove(name);
    if (kp != null) {
      fireTableDataChanged();
    }
  }

  private void keyUpdated(String name) {
    KeyPair kp = model.getKey(name);
    keyMap.put(name, kp);
    fireTableDataChanged();
  }

  //------------------------ Implements:
  
  //------------------------ Overrides: AbstractTableModel

  @Override
  public String getColumnName(int column) {
    return COLUMNS[column];
  }

  public int getRowCount() {
    return keyMap.size();
  }

  public int getColumnCount() {
    return COLUMNS.length;
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    String keyName = getNameAtIndex(rowIndex);
    KeyPair kp = keyMap.get(keyName);

    switch (columnIndex) {

      case 0: // Key Name
        return  keyName;
      case 1: // Key Type
        return typeMap.get(kp.getPrivate().getFormat());
      default:
        return "";

    }
  }

  //---------------------------- Abstract Methods -----------------------------

  //---------------------------- Utility Methods ------------------------------
  public String getNameAtIndex(int index) {
    return keyMap.keySet().toArray(new String[0])[index];
  }

//  private static int getKeyLength(KeyPair pair) {
//     DSAPublicKey pub = (DSAPublicKey) pair.getPublic();
//     pub.getParams().`
//     return 8 * (pub.getModulus().toByteArray().length - 1);
//   }

  //---------------------------- Property Methods -----------------------------
}
