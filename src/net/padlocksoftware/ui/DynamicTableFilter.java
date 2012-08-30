package net.padlocksoftware.ui;

import java.awt.Color;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Jason Nichols
 */
public final class DynamicTableFilter{

  private class DynamicDocumentListener implements DocumentListener {

    @Override
    public void insertUpdate(DocumentEvent e) {
      ignore = false;
      updateFilter();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      ignore = false;
      updateFilter();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
      ignore = false;
      updateFilter();
    }
  }

  ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  private static final Color EMPTY_COLOR = new Color(128, 128, 128);

  ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  private final JTextField field;

  private final TableModel model;

  private final TableRowSorter<TableModel> sorter;

  private boolean ignore = true; // don't filter anything until the document has changed

  private final Color originalTextColor;

  /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  public DynamicTableFilter(JTable table, JTextField field) {

    this.field = field;
    originalTextColor = field.getForeground();
    field.setForeground(EMPTY_COLOR);
    field.getDocument().addDocumentListener(new DynamicDocumentListener());
    this.model = table.getModel();
    sorter = new TableRowSorter<TableModel>(model);
    sorter.setSortsOnUpdates(true);
    table.setRowSorter(sorter);
  }

  ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
  public void updateFilter() {
    field.setForeground(originalTextColor);
    if (ignore) {
      return;
    }

    //If current expression doesn't parse, don't update.
    try {
      //
      // Add the (?i) for case insensitivity
      //
      String filter = "(?i)" + field.getText();
      final RowFilter<TableModel, Object> rf = RowFilter.regexFilter(filter);
      SwingUtilities.invokeLater(new Runnable() {

        public void run() {
          sorter.setRowFilter(rf);
        }
      });
    } catch (java.util.regex.PatternSyntaxException e) {
      return;
    }

  }

  //------------------------ Implements:

  //------------------------ Overrides:

  //---------------------------- Abstract Methods -----------------------------

  //---------------------------- Utility Methods ------------------------------

  //---------------------------- Property Methods -----------------------------

  public TableRowSorter<TableModel> getSorter() {
    return sorter;
  }
}
