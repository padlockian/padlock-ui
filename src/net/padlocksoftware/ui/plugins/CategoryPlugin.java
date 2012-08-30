package net.padlocksoftware.ui.plugins;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Jason Nichols
 */
public class CategoryPlugin extends ModelPlugin {

  ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  private static final String CATEGORY_KEY = "net.padlock.licenseCategory";

  ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  // Map of License Name - > Category
  private final Map<String, String> categoryMap;

  /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  public CategoryPlugin() {
    categoryMap =  new ConcurrentHashMap<String, String>();
  }

  ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
  
  //------------------------ Implements:

  //------------------------ Overrides: ModelPlugin

  @Override
  protected void init() {
    for (String licenseName : model.getLicenses()) {
      updateMap(licenseName);
    }
  }

  @Override
  public void licenseAdded(String name) {
    updateMap(name);
  }

  @Override
  public void licenseRemoved(String name) {
    categoryMap.remove(name);
  }

  @Override
  public void licenseUpdated(String name) {
    updateMap(name);

  }

  //---------------------------- Abstract Methods -----------------------------

  //---------------------------- Utility Methods ------------------------------

  private void updateMap(String licenseName) {
    Map<String,String> metaData = model.getLicenseMetadata(licenseName);

    String category = metaData.get(CATEGORY_KEY);

    if (category == null) {
      categoryMap.remove(licenseName);
    } else {
    categoryMap.put(licenseName, category);
    }
    logger.finer("Updating " + licenseName + " with category " + category);
  }

  //---------------------------- Property Methods -----------------------------

  public String getLicenseCategory(String licenseName) {
    return categoryMap.get(licenseName);
  }

  public SortedSet<String> getLicenseCategories() {
    return new TreeSet<String>(categoryMap.values());
  }

  public void setLicenseCategory(String licenseName, String category) {
    logger.info("Setting " + licenseName + " to category " + category);
    model.setLicenseMetadataItem(licenseName, CATEGORY_KEY, category);
  }
}
