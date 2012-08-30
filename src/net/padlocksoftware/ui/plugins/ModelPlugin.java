package net.padlocksoftware.ui.plugins;

import java.util.logging.Logger;
import net.padlocksoftware.ui.*;

/**
 *
 * @author Jason Nichols
 */
public abstract class ModelPlugin extends AbstractPadlockModelListener {

  ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  protected Logger logger = Logger.getLogger(getClass().getName());
  
  protected PadlockModel model;

  /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\


  ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  public final String getName() {
    return getClass().getName();
  }

  //------------------------ Implements:

  //------------------------ Overrides:

  //---------------------------- Abstract Methods -----------------------------

  protected abstract void init();

  //---------------------------- Utility Methods ------------------------------

  //---------------------------- Property Methods -----------------------------
  public void setPadlockModel(PadlockModel model) {
    this.model = model;
    init();
  }
}
