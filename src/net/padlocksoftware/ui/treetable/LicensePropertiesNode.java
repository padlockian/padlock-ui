package net.padlocksoftware.ui.treetable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import net.padlocksoftware.padlock.license.License;

/**
 *
 * @author Jason Nichols
 */
public class LicensePropertiesNode extends AbstractTreeNode {

  ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  private final License license;

  private final List<LicensePropertyNode> nodes;


  /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  public LicensePropertiesNode(License l) {
    license = l;

    nodes = new ArrayList<LicensePropertyNode>();

    Properties props = l.getProperties();
    for (String key : props.stringPropertyNames()) {
      nodes.add(new LicensePropertyNode(key, props.getProperty(key)));
    }

    Collections.sort(nodes);
  }

  ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //------------------------ Implements:

  //------------------------ Overrides:

  //---------------------------- Abstract Methods -----------------------------

  @Override
  public Object getValueAt(int column) {
    if (column == 0) {
      return "Properties";
    } else return null;
  }

  @Override
  public Object getChild(int index) {
    return nodes.get(index);
  }

  @Override
  public int getChildCount() {
    return nodes.size();
  }

  @Override
  public int getIndexOfChild(Object child) {
    return nodes.indexOf(child);
  }

  //---------------------------- Utility Methods ------------------------------

  //---------------------------- Property Methods -----------------------------
}
