/**
 * Copyright (c) 2009 Padlock Software LLC.
 *
 * The contents of this file are private and contain confidential trade secrets.
 * Any viewing, distribution, or usage is strictly prohibited.
 */
package net.padlocksoftware.ui;

import java.security.KeyPair;
import java.util.Map;
import java.util.Set;
import net.padlocksoftware.padlock.license.License;
import net.padlocksoftware.padlock.license.PadlockState;
import net.padlocksoftware.padlocktemplates.LicenseTemplateDefinition;
import net.padlocksoftware.ui.plugins.ModelPlugin;

/**
 *
 * @author Jason Nichols (jason@padlocksoftware.net)
 */
public interface PadlockModel {
  
  ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //------------------------ Implements:

  /**
   * Add a new listener for PadlockModel events.
   * @param l The listener to add.
   */
  public void addModelListener(PadlockModelListener l);

  /**
   * Remove a listener for PadlockModel events.
   * @param l The listener to remove.
   */
  public void removeModelListener(PadlockModelListener l);

  /**
   * Add a license to the model.  This may be either signed or unsigned.  This
   * method can also be used to update a license by giving it a name of an
   * already existing license. The new license will overwrite the original.
   * 
   * @param name The name of the license.  Names may have certain restrictions, 
   * such as being a legal file name.
   * @param l The License instance to add to the model.
   */
  public void addLicense(String name, License l);

  /**
   * Get all license names known to this model.
   * @return A set of license name Strings, or an empty set if no Licenses are
   * known to this model.
   */
  public Set<String> getLicenses();

  /**
   * Get a License instance corresponding to a particular name.
   * @param name The name of the license
   * @return The License install, or null if no license with that name is found.
   */
  public License getLicense(String name);

  /**
   * Remove a license from the model.  This removes the license from the
   * underlying storage as well (file or database entry).
   * @param name The name of the license to remove.
   */
  public void removeLicense(String name);

  public void updateLicense(String name, License license);

  public void renameLicense(String oldName, String newName);
  
  public void signLicense(String licenseName, String keyName);
  
  /**
   * Return a Set of the names of all known keys in the model.
   * @return The Set of keys, or an empty set if none are known.
   */
  public Set<String> getKeys();


  public void addKey(String name, KeyPair key);
  
  /**
   * Get a particular Keypair instance.
   * @param name The name of the KeyPair to get
   * @return The KeyPair associated with the supplied name, or null if no such
   * key exists.
   */
  public KeyPair getKey(String name);

  /**
   * Remove a particular Keypair instance.
   * @param name The name of the KeyPair to remove.
   */
  public void removeKey(String name);

  public void renameKey(String oldName, String newName);

  // Template related methods

  public Map<String, LicenseTemplateDefinition> getTemplates();

  public void addTempate(String name, LicenseTemplateDefinition template);

  public void removeTemplate(String name);

  public void updateTemplate(String name, LicenseTemplateDefinition template);

  public void renameTemplate(String oldName, String newName);

  public void addPlugin(ModelPlugin plugin);

  public void removePlugin(ModelPlugin plugin);

  public ModelPlugin getPlugin(String name);

  public SelectionManager<String> getKeyPairSelectionManager();

  public SelectionManager<String> getLicenseSelectionManager();


  // License Metadata methods

  public Map<String,String> getLicenseMetadata(String name);

  public void setLicenseMetaData(String name, Map<String, String> metadata);

  public void setLicenseMetadataItem(String name, String key, String value);

  public PadlockState getState();
  
  //------------------------ Overrides:

  //---------------------------- Abstract Methods -----------------------------

  //----------------------------- Native Methods ------------------------------

  //---------------------------- Utility Methods ------------------------------

  //---------------------------- Property Methods -----------------------------
  
}
