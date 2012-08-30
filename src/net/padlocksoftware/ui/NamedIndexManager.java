package net.padlocksoftware.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 *
 * @author Jason Nichols
 */
public final class NamedIndexManager {
  ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  private final Logger logger;

  private final String baseName;

  private final Preferences prefs;

  private final SimpleDateFormat dateFormat;

  /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  public NamedIndexManager(String baseName) {
    logger = Logger.getLogger(getClass().getName());
    this.baseName = baseName;
    prefs = Preferences.userNodeForPackage(this.getClass());
    dateFormat = new SimpleDateFormat("yyyyMMdd");
  }

  ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //------------------------ Implements:

  //------------------------ Overrides:

  //---------------------------- Abstract Methods -----------------------------

  //---------------------------- Utility Methods ------------------------------

  //---------------------------- Property Methods -----------------------------

  public synchronized String getNext() {
    int serial = 1;
    String lastRun = baseName + "LastRun";
    String lastSerial = baseName + "LastSerial";

    String today = dateFormat.format(new Date());

    try {
      if (today.equals(prefs.get(lastRun, ""))) {
        serial = prefs.getInt(lastSerial, serial);
        serial++;
        prefs.putInt(lastSerial, serial);
      } else {
        //
        // This is the first time running today.  Save current usage
        //
        prefs.put(lastRun, today);
        prefs.putInt(lastSerial, serial);
      }
      prefs.flush();

    } catch (BackingStoreException ex) {
      logger.log(Level.WARNING, null, ex);
    }

    return baseName + "-" + today + "-" + serial;
  }
}
