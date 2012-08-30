package net.padlocksoftware.ui;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 *
 * @author Jason Nichols
 */
public class SelectionManager<T> {

  ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  private final List<T> selectionList;

  private final Set<SelectionListener<T>> listeners;

  /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  public SelectionManager() {
    selectionList = new CopyOnWriteArrayList<T>();
    listeners = new CopyOnWriteArraySet<SelectionListener<T>>();
  }

  ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  public void addListener(SelectionListener<T> listener) {
    listeners.add(listener);
    listener.selectionChanged(selectionList);
  }

  public void removeListener(SelectionListener<T> listener) {
    listeners.remove(listener);
  }

  synchronized public void addToSelection(T element) {
    if (!selectionList.contains(element)) {
      selectionList.add(element);
      fire();
    }
  }

  synchronized public void setSelection(Collection<T> selection) {
    selectionList.clear();
    selectionList.addAll(selection);
    fire();
  }

  synchronized public void removeFromSelection(T element) {
    if (selectionList.contains(element)) {
      selectionList.remove(element);
      fire();
    }
  }
  
  //------------------------ Implements:

  //------------------------ Overrides:

  //---------------------------- Abstract Methods -----------------------------

  //---------------------------- Utility Methods ------------------------------

  private void fire() {
    for (SelectionListener<T> listener : listeners) {
      listener.selectionChanged(selectionList);
    }
  }
  //---------------------------- Property Methods -----------------------------
}
