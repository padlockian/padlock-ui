package net.padlocksoftware.ui.plugins;

import java.security.KeyPair;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import net.padlocksoftware.padlock.license.License;
import net.padlocksoftware.padlock.license.LicenseState;
import net.padlocksoftware.padlock.license.LicenseTest;
import net.padlocksoftware.padlock.license.TestResult;
import net.padlocksoftware.padlock.validator.Validator;
import net.padlocksoftware.padlock.validator.ValidatorException;

/**
 *
 * @author Jason Nichols
 */
public class StatusPlugin extends ModelPlugin {

  ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  // Maps License names to their Signing key
  private final Map<String, String> keyMap;

  // Mapping of a license name to its most recent state
  private final Map<String, LicenseState> stateMap;

  /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  public StatusPlugin() {
    keyMap = new HashMap<String, String>();
    stateMap = new HashMap<String, LicenseState>();
  }

  ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //------------------------ Implements:

  //------------------------ Overrides: ModelPlugin

  @Override
  protected synchronized void init() {
    for (String licenseName : model.getLicenses()) {
      assessLicense(licenseName);
    }
  }

  @Override
  public synchronized void keyPairAdded(String name) {
    assessInvalidSignature(name);
  }

  @Override
  public synchronized void keyPairRemoved(String name) {

    // When a KeyPair is removed, check each License mapped to this key and
    // reassess its state.
    assessKey(name);
  }

  @Override
  public synchronized void keyPairUpdated(String name) {
    // Do the actions in both Added and Removed
    assessInvalidSignature(name);
    assessKey(name);
  }

  @Override
  public synchronized void licenseAdded(String name) {
    // Assess the state of the license and store it.
    assessLicense(name);
  }

  @Override
  public synchronized void licenseRemoved(String name) {
    keyMap.remove(name);
    stateMap.remove(name);
  }

  @Override
  public synchronized void licenseUpdated(String name) {
    assessLicense(name);
  }

  //------------------------`---- Abstract Methods -----------------------------

  //---------------------------- Utility Methods ------------------------------

  private void assessKey(String keyName) {
    
    Set<String> licenseNames = new HashSet<String>();

    for (Entry<String,String> entry : keyMap.entrySet()) {
      if (entry.getValue().equals(keyName)) {
        licenseNames.add(entry.getKey());
      }
    }

    for (String licenseName : licenseNames) {
      assessLicense(licenseName);
    }
  }

  /**
   * For any license with an unverified signature, re-assess it with the
   * specified key.
   */
  private void assessInvalidSignature(String keyName) {

    Set<String> licenses = new HashSet<String>();

    for (Entry<String, LicenseState> entry : stateMap.entrySet()) {
      LicenseState state = entry.getValue();
      TestResult signatureTestResult = state.findTest(LicenseTest.SIGNATURE.getId());
      if (signatureTestResult != null && !signatureTestResult.passed()) {
        licenses.add(entry.getKey());
      }
    }

    for (String licenseName : licenses) {
      LicenseState state = assess(licenseName, keyName);
      stateMap.put(licenseName, state);
      TestResult signatureTestResult = state.findTest(LicenseTest.SIGNATURE.getId());
      if (signatureTestResult != null && signatureTestResult.passed()) {
        keyMap.put(licenseName, keyName);
      }
    }
  }

  private void assessLicense(String licenseName) {
    keyMap.remove(licenseName);

    License license = model.getLicense(licenseName);


    // Check for a signed license before doing anything
    if (!license.isSigned()) {
      TestResult result = new TestResult(LicenseTest.SIGNED, false);
      LicenseState state = new LicenseState(Collections.singletonList(result));
      stateMap.put(licenseName, state);
      logger.fine("Assessing " + licenseName + " as unsigned");
      return;
    }


    // If we have no keys, this license should be marked as having an
    // unverified signature
    if (model.getKeys().size() == 0) {
        TestResult result = new TestResult(LicenseTest.SIGNATURE, false);
        LicenseState state = new LicenseState(Collections.singletonList(result));
        stateMap.put(licenseName, state);
        logger.fine("No keys found, marking " + licenseName + " as signed but unverified");
        return;
      }

    // Since we have keys, go through and assess each license until we find
    // one that passes the signature test.
    for (String keyName : model.getKeys()) {
      LicenseState state = assess(licenseName, keyName);

      for (TestResult result : state.getTests()) {
        if (result.getTest() == LicenseTest.SIGNATURE && result.passed()) {
          // We've passed the signature test, this is our key
          stateMap.put(licenseName, state);
          keyMap.put(licenseName, keyName);
          return;
        }
      }
    }

    // If we've made it down here, no key matched up with our license and
    // we should mark the license as having an unverified signature
    TestResult result = new TestResult(LicenseTest.SIGNATURE, false);
    LicenseState state = new LicenseState(Collections.singletonList(result));
    stateMap.put(licenseName, state);
    logger.fine("No matching key found for " + licenseName);

  }


  private LicenseState assess(String licenseName, String keyName) {

    License license = model.getLicense(licenseName);
    KeyPair kp = model.getKey(keyName);

    Validator v = new Validator(license, kp.getPublic().getEncoded());
    v.setIgnoreFloatTime(true);
    v.setCheckClockTurnback(false);

    LicenseState state = null;

    try {
      state = v.validate();
    } catch (ValidatorException ex) {
      state = ex.getLicenseState();
    }

    return state;

  }

  //---------------------------- Property Methods -----------------------------
  
  public synchronized String getLicenseKey(String licenseName, String def) {
    String val = keyMap.get(licenseName);
    return val != null ? val : def;
  }

  public synchronized LicenseState getState(String licenseName) {
    return stateMap.get(licenseName);
  }
}
