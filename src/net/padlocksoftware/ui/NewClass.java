package net.padlocksoftware.ui;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;

/**
 *
 * @author Jason Nichols
 */
public class NewClass {

  private class Listener implements TableColumnModelListener {

    int lastFrom = 0;
    int lastTo = 0;

    private void verifyChange(int from, int to) {
      if (from != lastFrom || to != lastTo) {
        lastFrom = from;
        lastTo = to;

        ///////////////////////////////////////
        // Column order has changed!  Do something here
        ///////////////////////////////////////
      }
    }

    public void columnMoved(TableColumnModelEvent e) {
      verifyChange(e.getFromIndex(), e.getToIndex());
    }

    public void columnAdded(TableColumnModelEvent e) {
      verifyChange(e.getFromIndex(), e.getToIndex());
    }

    public void columnRemoved(TableColumnModelEvent e) {
      verifyChange(e.getFromIndex(), e.getToIndex());
    }

    public void columnMarginChanged(ChangeEvent e) {}

    public void columnSelectionChanged(ListSelectionEvent e) {}
  }
}
