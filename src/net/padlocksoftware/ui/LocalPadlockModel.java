/**
 * Copyright (c) 2009 Padlock Software LLC.
 *
 * The contents of this file are private and contain confidential trade secrets.
 * Any viewing, distribution, or usage is strictly prohibited.
 */
package net.padlocksoftware.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.KeyPair;
import java.security.interfaces.DSAPrivateKey;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import net.padlocksoftware.padlock.KeyManager;
import net.padlocksoftware.padlock.license.ImportException;
import net.padlocksoftware.padlock.license.License;
import net.padlocksoftware.padlock.license.LicenseIO;
import net.padlocksoftware.padlock.license.LicenseSigner;
import net.padlocksoftware.padlock.license.PadlockState;
import net.padlocksoftware.padlocktemplates.LicenseTemplateDefinition;
import net.padlocksoftware.ui.plugins.ModelPlugin;

/**
 *
 * @author Jason Nichols
 */
public final class LocalPadlockModel implements PadlockModel {

  private interface ScannerAdapter {

    public void fileAdded(File file);

    public void fileRemoved(File file);

    public void fileUpdated(File file);
  }

  private final class FolderScanner implements Runnable {

    private final File folder;
    private final String extension;
    private final ScannerAdapter adapter;
    private final Map<File, Long> lastModified;
    private final FilenameFilter filter;

    public FolderScanner(File folder, String extension, ScannerAdapter adapter) {

      this.folder = folder;

      this.extension = extension;

      this.adapter = adapter;

      lastModified = new HashMap<File, Long>();

      filter = new FilenameFilter() {

        @Override
        public boolean accept(File dir, String name) {
          return name.toLowerCase().endsWith(FolderScanner.this.extension.toLowerCase());
        }
      };
    }

    @Override
    public void run() {

      try {
        logger.finer("Scanning " + folder);

        // Get the file listing
        File[] files = folder.listFiles(filter);

        // Note new file
        Set<File> newFiles = new HashSet<File>();

        // Note updated files
        Set<File> updatedFiles = new HashSet<File>();

        // Note removed files
        Set<File> removedFiles = new HashSet<File>();

        //
        // Check for removed files
        //

        // Removed files exist in our map but not in the array
        // Existing keys
        Set<File> fileSet = new HashSet<File>(lastModified.keySet());

        for (File f : files) {
          fileSet.remove(f);
        }

        // Any leftovers are stale files
        removedFiles.addAll(fileSet);
        for (File f : removedFiles) {
          lastModified.remove(f);
        }

        //
        // Check for new files
        //
        for (File f : files) {
          Long last = lastModified.get(f);

          if (last != null) {
            // Existing file, check for updates
            if (last != f.lastModified()) {
              lastModified.put(f, f.lastModified());
              updatedFiles.add(f);
            }
          } else {
            // New File
            newFiles.add(f);
            lastModified.put(f, f.lastModified());
          }
        }

        for (File f : removedFiles) {
          adapter.fileRemoved(f);
        }

        for (File f : newFiles) {
          adapter.fileAdded(f);
        }

        for (File f : updatedFiles) {
          adapter.fileUpdated(f);
        }
      } catch (Throwable ex) {
        logger.log(Level.SEVERE, null, ex);
      }
    }
  }

  ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  private final long SCAN_INTERVAl = 1000;

  private final TimeUnit SCAN_UNIT = TimeUnit.MILLISECONDS;

  ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  private final Logger logger;

  private final Set<PadlockModelListener> listeners;

  private final Map<String, License> licenses;

  private final Map<String, KeyPair> keyPairs;

  private final Map<String, LicenseTemplateDefinition> templates;

  private final ScheduledThreadPoolExecutor executor;

  private final File licenseFolder;

  private final File keyPairFolder;

  private final File templateFolder;

  private final Map<String, ModelPlugin> pluginMap;

  private final SelectionManager<String> keyPairSelectionManager;

  private final SelectionManager<String> licenseSelectionManager;

  private final Map<String, Map<String, String>> metaMap;
  
  private final Preferences metaPreferences;

  private PadlockState state;

  /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
  public LocalPadlockModel() {

    logger = Logger.getLogger(getClass().getName());

    License l = getPadlockLicense(getLicenseLocation());
    state = new PadlockState(l);

    File licenseFile = state.getPadlockFile();
    if (licenseFile != null) {
       setLicenseLocation(licenseFile.getPath());
    }

    listeners = new CopyOnWriteArraySet<PadlockModelListener>();

    licenses = new ConcurrentHashMap<String, License>();

    keyPairs = new ConcurrentHashMap<String, KeyPair>();

    templates = new ConcurrentHashMap<String, LicenseTemplateDefinition>();
    licenseFolder = getLicenseFolder();

    keyPairFolder = getKeyPairFolder();

    templateFolder = getTemplateFolder();
    pluginMap = new ConcurrentHashMap<String, ModelPlugin>();

    keyPairSelectionManager = new SelectionManager<String>();

    licenseSelectionManager = new SelectionManager<String>();

    metaMap = new ConcurrentHashMap<String, Map<String, String>>();

    metaPreferences = Preferences.userNodeForPackage(this.getClass()).node("licenseMetaData");

    executor = new ScheduledThreadPoolExecutor(2);

    Runnable runnable = new FolderScanner(licenseFolder, ".lic", new ScannerAdapter() {

      private String getName(File f) {
        return f.getName().split("\\.lic")[0];
      }

      @Override
      public void fileAdded(File file) {
        try {
          logger.fine("File added: " + file);
          License l = LicenseIO.importLicense(file);
          licenseFound(l, getName(file));
        } catch (IOException ex) {
          logger.log(Level.SEVERE, file.toString(), ex);
        } catch (ImportException ex) {
          logger.log(Level.SEVERE, file.toString(), ex);
        } catch (Throwable ex) {
          logger.log(Level.SEVERE, file.toString(), ex);
        }
      }

      @Override
      public void fileRemoved(File file) {
        try {
          logger.fine("File removed: " + file);
          licenseRemoved(getName(file));
        } catch (Throwable ex) {
          logger.log(Level.SEVERE, null, ex);
        }
      }

      @Override
      public void fileUpdated(File file) {
        logger.fine("File updated: " + file);
        try {
          License l = LicenseIO.importLicense(file);
          licenseFound(l, getName(file));
        } catch (Throwable ex) {
          logger.log(Level.SEVERE, null, ex);
        }
      }
    });

    executor.scheduleAtFixedRate(runnable, 0L, SCAN_INTERVAl, SCAN_UNIT);

    runnable = new FolderScanner(keyPairFolder, ".key", new ScannerAdapter() {

      private String getName(File f) {
        return f.getName().split("\\.key")[0];
      }

      @Override
      public void fileAdded(File file) {
        logger.fine("File added: " + file);
        try {
          KeyPair pair = KeyManager.importKeyPair(file);
          keyPairFound(pair, getName(file));
        } catch (Throwable ex) {
          logger.log(Level.SEVERE, null, ex);
        }
      }

      @Override
      public void fileRemoved(File file) {
        try {
          logger.fine("File removed: " + file);
          keyPairRemoved(getName(file));
        } catch (Throwable ex) {
          logger.log(Level.SEVERE, null, ex);
        }


      }

      @Override
      public void fileUpdated(File file) {
        logger.fine("File added: " + file);
        try {
          KeyPair pair = KeyManager.importKeyPair(file);
          keyPairFound(pair, getName(file));
        } catch (Throwable ex) {
          logger.log(Level.SEVERE, null, ex);
        }
      }
    });

    executor.scheduleAtFixedRate(runnable, 0L, SCAN_INTERVAl, SCAN_UNIT);


    runnable = new FolderScanner(templateFolder, ".ltp", new ScannerAdapter() {

      private String getName(File f) {
        return f.getName().split("\\.ltp")[0];
      }

      @Override
      public void fileAdded(File file) {
        logger.fine("File added: " + file);
        try {
          LicenseTemplateDefinition template = new LicenseTemplateDefinition(
                  new FileInputStream(file));
          templateFound(template, getName(file));
        } catch (Throwable ex) {
          logger.log(Level.SEVERE, null, ex);
        }
      }

      @Override
      public void fileRemoved(File file) {
        try {
          logger.fine("File removed: " + file);
          templateRemoved(getName(file));
        } catch (Throwable ex) {
          logger.log(Level.SEVERE, null, ex);
        }
      }

      @Override
      public void fileUpdated(File file) {
        logger.fine("File added: " + file);
        try {
          LicenseTemplateDefinition template = new LicenseTemplateDefinition(
                  new FileInputStream(file));
          templateFound(template, getName(file));
        } catch (Throwable ex) {
          logger.log(Level.SEVERE, null, ex);
        }
      }
    });

    executor.scheduleAtFixedRate(runnable, 0L, SCAN_INTERVAl, SCAN_UNIT);

    fireStateUpdated();
    
    logger.fine("Model initialization complete.");
  }

  ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
  //------------------------ Implements: PadlockModel
  @Override
  public Set<String> getLicenses() {
    return new HashSet<String>(licenses.keySet());
  }

  @Override
  public void addLicense(String name, License l) {
    File licenseFile = new File(licenseFolder, name + ".lic");
    try {
      LicenseIO.exportLicense(l, licenseFile);
    } catch (IOException ex) {
      logger.log(Level.SEVERE, null, ex);
    }
  }

  @Override
  public void updateLicense(String name, License license) {
    try {
      LicenseIO.exportLicense(license, new File(licenseFolder, name + ".lic"));
    } catch (IOException ex) {
      logger.log(Level.SEVERE, null, ex);
    }
  }

  public void signLicense(String licenseName, String keyName) {
    License l = getLicense(licenseName);
    KeyPair kp = getKey(keyName);

    LicenseSigner signer = LicenseSigner.createLicenseSigner((DSAPrivateKey)kp.getPrivate(),
            state.getLicense());
    signer.sign(l);
    updateLicense(licenseName, l);
  }

  @Override
  public License getLicense(String name) {
    return licenses.get(name);
  }

  @Override
  public void removeLicense(String name) {
    File f = new File(licenseFolder, name + ".lic");
    if (f.exists() && !f.delete()) {
      logger.info("Could not remove License " + name);
    }

    License license = licenses.remove(name);
    if (license != null) {
      fireLicenseRemoved(name);
    }
  }

  public void renameLicense(String oldName, String newName) {
    File oldLicenseFile = new File(licenseFolder, oldName + ".lic");
    if (oldLicenseFile.exists() && oldLicenseFile.canWrite()) {

      // Move over the license metadata before copying
      Map<String, String> metaData = getLicenseMetadata(oldName);
      setLicenseMetaData(newName, metaData, false);
      oldLicenseFile.renameTo(new File(licenseFolder, newName + ".lic"));
    }
  }
  
  @Override
  public Set<String> getKeys() {
    return new HashSet<String>(keyPairs.keySet());
  }

  @Override
  public KeyPair getKey(String name) {
    return keyPairs.get(name);
  }

  @Override
  public void removeKey(String name) {
    File f = new File(keyPairFolder, name + ".key");
    if (!f.delete()) {
      logger.info("Could not remove KeyPair " + name);
    } else {
      KeyPair pair = keyPairs.remove(name);
      if (pair != null) {
        fireKeyPairRemoved(name);
      }
    }
  }

  @Override
  public void addKey(String name, KeyPair key) {
    File keyFile = new File(keyPairFolder, name + ".key");
    try {
      KeyManager.exportKeyPair(key, keyFile);
    } catch (IOException ex) {
      logger.log(Level.SEVERE, null, ex);
    }
  }

  @Override
  public void renameKey(String oldName, String newName) {
    File oldFile = new File(keyPairFolder, oldName + ".key");
    File newFile = new File(keyPairFolder, newName + ".key");
    if (!oldFile.renameTo(newFile)) {
      logger.info("Could not rename KeyPair " + oldName);
    }
  }

  @Override
  public void addModelListener(PadlockModelListener l) {
    if (l == null) {
      throw new NullPointerException("Cannot add a null listener");
    }

    listeners.add(l);
    fireNewListener(l);
  }

  @Override
  public void removeModelListener(PadlockModelListener l) {
    listeners.remove(l);
  }

  @Override
  public void addPlugin(ModelPlugin plugin) {
    plugin.setPadlockModel(this);
    pluginMap.put(plugin.getName(), plugin);
    logger.fine("Added plugin " + plugin.getName());
  }

  @Override
  public void removePlugin(ModelPlugin plugin) {
    pluginMap.remove(plugin.getName());
  }

  @Override
  public ModelPlugin getPlugin(String name) {
    return pluginMap.get(name);
  }

  public SelectionManager<String> getKeyPairSelectionManager() {
    return keyPairSelectionManager;
  }

  public SelectionManager<String> getLicenseSelectionManager() {
    return licenseSelectionManager;
  }

  public Map<String, String> getLicenseMetadata(String name) {
    Map<String, String> map = metaMap.get(name);

    if (map == null) {
      map = Collections.EMPTY_MAP;
    }

    return Collections.unmodifiableMap(map);
  }

  private void setLicenseMetaData(String name, Map<String, String> metadata, boolean fireUpdate) {
    synchronized (metaPreferences) {
      try {
        Preferences node = metaPreferences.node(name);

        if (metadata.size() == 0) {
          node.removeNode();
        } else {
          node.clear();
          for (String key : metadata.keySet()) {
            String value = metadata.get(key);
            node.put(key, value);
          }
        }
        node.flush();
      } catch (BackingStoreException ex) {
        logger.log(Level.SEVERE, null, ex);
      }
    }
    loadLicenseMetaData(name);

    if (fireUpdate) {
      fireLicenseUpdated(name);
    }
  }

  public void setLicenseMetaData(String name, Map<String, String> metadata) {
    setLicenseMetaData(name, metadata, true);
  }

  public void setLicenseMetadataItem(String name, String key, String value) {
    synchronized (metaPreferences) {
      try {
        Preferences node = metaPreferences.node(name);
        if (value == null) {
          node.remove(key);
        } else {
          node.put(key, value);
        }
        
        node.flush();
      } catch (BackingStoreException ex) {
        logger.log(Level.SEVERE, null, ex);
      }
    }
    loadLicenseMetaData(name);
    fireLicenseUpdated(name);
  }

  public PadlockState getState() {
    return state;
  }

  public Map<String, LicenseTemplateDefinition> getTemplates() {
    return new HashMap<String, LicenseTemplateDefinition>(templates);
  }

  public void addTempate(String name, LicenseTemplateDefinition template) {
    File templateFile = new File(templateFolder, name + ".ltp");
    try {
      template.export(new FileOutputStream(templateFile));
    } catch (IOException ex) {
      logger.log(Level.SEVERE, null, ex);
    }
  }

  public void renameTemplate(String oldName, String newName) {
    File oldTemplateFile = new File(templateFolder, oldName + ".ltp");
    if (oldTemplateFile.exists() && oldTemplateFile.canWrite()) {
      oldTemplateFile.renameTo(new File(templateFolder, newName + ".ltp"));
    }
  }
  
  public void removeTemplate(String name) {
    File f = new File(templateFolder, name + ".ltp");
    if (f.exists() && !f.delete()) {
      logger.info("Could not remove Template " + name);
    }

    LicenseTemplateDefinition template = templates.remove(name);
    if (template != null) {
      fireTemplateRemoved(name);
    }
  }

  public void updateTemplate(String name, LicenseTemplateDefinition template) {
    try {
      template.export(new FileOutputStream(new File(templateFolder, name + ".ltp")));
    } catch (IOException ex) {
      logger.log(Level.SEVERE, null, ex);
    }
  }

  //------------------------ Overrides:

  //---------------------------- Abstract Methods -----------------------------

  //---------------------------- Utility Methods ------------------------------

  private void licenseRemoved(String name) {
    if (licenses.remove(name) != null) {
      synchronized (metaPreferences) {
        metaPreferences.remove(name);
        try {
          metaPreferences.flush();
        } catch (BackingStoreException ex) {
          Logger.getLogger(LocalPadlockModel.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
      logger.fine("Removed license " + name);
      fireLicenseRemoved(name);
    }
  }

  private void licenseFound(License l, String name) {
    License oldLicense = licenses.put(name, l);
    if (oldLicense != null) {
      logger.fine("Updating license " + name);
      loadLicenseMetaData(name);
      fireLicenseUpdated(name);
    } else {
      logger.fine("Adding license " + name);
      loadLicenseMetaData(name);
      fireLicenseAdded(name);
    }
  }

  private void keyPairRemoved(String name) {
    if (keyPairs.remove(name) != null) {
      logger.fine("Removed KeyPair " + name);
      fireKeyPairRemoved(name);
    }
  }

  private void keyPairFound(KeyPair pair, String name) {
    KeyPair oldPair = keyPairs.put(name, pair);
    if (oldPair != null) {
      logger.fine("Updating KeyPair " + name);
      fireKeyPairUpdated(
              name);
    } else {
      logger.fine("Adding KeyPair " + name);
      fireKeyPairAdded(
              name);

    }
  }

  private void templateFound(LicenseTemplateDefinition template, String name) {
    LicenseTemplateDefinition oldTemplate = templates.put(name, template);
    if (oldTemplate != null) {
      logger.fine("Updating Template " + name);
      fireTemplateUpdated(name);
    } else {
      logger.fine("Adding template " + name);
      fireTemplateAdded(name);
    }
  }

  private void templateRemoved(String name) {
    if (templates.remove(name) != null) {
      logger.fine("Removed Template " + name);
      fireTemplateRemoved(name);
    }
  }

  private void fireTemplateAdded(String name) {

    for (ModelPlugin plugin : pluginMap.values()) {
      plugin.templateAdded(name);
    }

    for (PadlockModelListener l : listeners) {
      l.templateAdded(name);
    }
  }

  private void fireTemplateUpdated(String name) {

    for (ModelPlugin plugin : pluginMap.values()) {
      plugin.templateUpdated(name);
    }

    for (PadlockModelListener l : listeners) {
      l.templateUpdated(name);
    }
  }

  private void fireTemplateRemoved(String name) {

    for (ModelPlugin plugin : pluginMap.values()) {
      plugin.templateRemoved(name);
    }


    for (PadlockModelListener l : listeners) {
      l.templateRemoved(name);
    }
  }

  private void fireKeyPairAdded(String name) {

    for (ModelPlugin plugin : pluginMap.values()) {
      plugin.keyPairAdded(name);
    }

    for (PadlockModelListener l : listeners) {
      l.keyPairAdded(name);
    }
  }

  private void fireKeyPairRemoved(String name) {

    for (ModelPlugin plugin : pluginMap.values()) {
      plugin.keyPairRemoved(name);
    }


    for (PadlockModelListener l : listeners) {
      l.keyPairRemoved(name);
    }
  }

  private void fireKeyPairUpdated(String name) {

    for (ModelPlugin plugin : pluginMap.values()) {
      plugin.keyPairUpdated(name);
    }

    for (PadlockModelListener l : listeners) {
      l.keyPairUpdated(name);
    }
  }

  private void fireLicenseAdded(String name) {

    for (ModelPlugin plugin : pluginMap.values()) {
      plugin.licenseAdded(name);
    }

    for (PadlockModelListener l : listeners) {
      l.licenseAdded(name);
    }
  }

  private void fireLicenseRemoved(String name) {

    for (ModelPlugin plugin : pluginMap.values()) {
      plugin.licenseRemoved(name);
    }

    for (PadlockModelListener l : listeners) {
      l.licenseRemoved(name);
    }
  }

  private void fireLicenseUpdated(String name) {

    for (ModelPlugin plugin : pluginMap.values()) {
      plugin.licenseUpdated(name);
    }

    for (PadlockModelListener l : listeners) {
      l.licenseUpdated(name);
    }
  }

  private void fireStateUpdated() {
    for (ModelPlugin plugin : pluginMap.values()) {
      plugin.padlockStateUpdated();
    }

    for (PadlockModelListener l : listeners) {
      l.padlockStateUpdated();
    }
  }

  private void fireNewListener(PadlockModelListener l) {

    l.padlockStateUpdated();

    for (String keyPair : keyPairs.keySet()) {
      l.keyPairAdded(keyPair);
    }

    for (String license : licenses.keySet()) {
      l.licenseAdded(license);
    }

  }

  private File getOrCreateFolder(String path) {
    File file = new File(path);

    if (!file.exists() && !file.mkdirs()) {
      logger.severe("Cannot create path " + path);
      System.exit(1);
    }

    if (!file.canRead() || !file.canWrite()) {
      logger.severe("Cannot read/write to " + path);
      System.exit(1);
    }

    return file;
  }

  public File getLicenseFolder() {

    logger.fine("Getting license folder");
    Preferences prefs = Preferences.userNodeForPackage(this.getClass());

    // Default path
    String path = System.getProperty("user.home") + File.separator
            + "padlock" + File.separator + "licenses";

    // Saved path overrides the defaults
    path = prefs.get("licensePath", path);

    // Command line property overrides all
    path = System.getProperty("net.padlocksoftware.licensepath", path);

    return getOrCreateFolder(path);
  }

  public File getKeyPairFolder() {

    logger.fine("Getting KeyPair folder");

    Preferences prefs = Preferences.userNodeForPackage(this.getClass());

    // Default path
    String path = System.getProperty("user.home") + File.separator
            + "padlock" + File.separator + "keys";

    // Saved path overrides the defaults
    path = prefs.get("keyPath", path);

    // Command line property overrides all
    path = System.getProperty("net.padlocksoftware.keypath", path);

    return getOrCreateFolder(path);
  }

  public File getTemplateFolder() {

    logger.fine("Getting Template folder");

    Preferences prefs = Preferences.userNodeForPackage(this.getClass());

    // Default path
    String path = System.getProperty("user.home") + File.separator
            + "padlock" + File.separator + "templates";

    // Saved path overrides the defaults
    path = prefs.get("templatePath", path);

    // Command line property overrides all
    path = System.getProperty("net.padlocksoftware.templatepath", path);

    return getOrCreateFolder(path);
  }
  private void loadLicenseMetaData(String name) {
    try {
      if (metaPreferences.nodeExists(name)) {
        Preferences node = metaPreferences.node(name);
        Map<String, String> map = new ConcurrentHashMap<String, String>();
        String[] keys = node.keys();
        for (String key : keys) {
          map.put(key, node.get(key, ""));
        }
        metaMap.put(name, map);
      }
    } catch (BackingStoreException ex) {
      logger.log(Level.SEVERE, null, ex);
    }
    
  }

  private License getPadlockLicense(String location) {
    License l = null;
    if (location != null) {
       try {
        File licenseFile = new File(location);
        l = LicenseIO.importLicense(licenseFile);
       } catch (Exception e){
         logger.fine("Could not import license file " + location);
       }
    }

    return l;
  }
  //---------------------------- Property Methods -----------------------------

  public String getLicenseLocation() {
   Preferences prefs = Preferences.userNodeForPackage(this.getClass());

   return prefs.get("padlockLicense", null);
  }

  public void setLicenseLocation(String location) {
   Preferences prefs = Preferences.userNodeForPackage(this.getClass());

   prefs.put("padlockLicense", location);

   state = new PadlockState(getPadlockLicense(location));
   fireStateUpdated();
  }


  public void setLicenseFolder(File licenseFolder) {
      Preferences prefs = Preferences.userNodeForPackage(this.getClass());
      prefs.put("licensePath", licenseFolder.getPath());
      try {
         prefs.sync();
      } catch (BackingStoreException ex) {
         logger.log(Level.SEVERE, null, ex);
      }

  }

  public void setKeyPairFolder(File keyFolder) {
      Preferences prefs = Preferences.userNodeForPackage(this.getClass());
      prefs.put("keyPath", keyFolder.getPath());
      try {
         prefs.sync();
      } catch (BackingStoreException ex) {
         logger.log(Level.SEVERE, null, ex);
      }
  }

  public void setTemplateFolder(File templateFolder) {
      Preferences prefs = Preferences.userNodeForPackage(this.getClass());
      prefs.put("templatePath", templateFolder.getPath());
      try {
         prefs.sync();
      } catch (BackingStoreException ex) {
         logger.log(Level.SEVERE, null, ex);
      }

  }

}
