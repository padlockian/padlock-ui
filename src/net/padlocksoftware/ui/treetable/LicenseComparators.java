package net.padlocksoftware.ui.treetable;

import java.util.Comparator;
import java.util.Date;

/**
 *
 * @author Jason Nichols
 */
public enum LicenseComparators {
  ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  NAME("Name", new Comparator<LicenseNode>() {
    public int compare(LicenseNode o1, LicenseNode o2) {
      return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
    }
  }),

  STARTDATE("Start Date", new Comparator<LicenseNode>() {
    public int compare(LicenseNode o1, LicenseNode o2) {
      return o1.getLicense().getStartDate().compareTo(o2.getLicense().getStartDate());
    }
  }),

  EXPIRATIONDATE("Expiration Date", new Comparator<LicenseNode>() {
    public int compare(LicenseNode o1, LicenseNode o2) {
      Date o1ExpirationDate = o1.getLicense().getExpirationDate();
      Long o1Expiration = o1ExpirationDate != null ? o1ExpirationDate.getTime() : Long.MIN_VALUE;

      Date o2ExpirationDate = o2.getLicense().getExpirationDate();
      Long o2Expiration = o2ExpirationDate != null ? o2ExpirationDate.getTime() : Long.MIN_VALUE;

      return o1Expiration.compareTo(o2Expiration);
    }
  }),

  STATUS("Status", new Comparator<LicenseNode>() {
    public int compare(LicenseNode o1, LicenseNode o2) {
      return o1.getLicenseStatus().compareTo(o2.getLicenseStatus());
    }
  });

  ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\


  //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  private final String name;

  private final Comparator<LicenseNode> comparator;

  /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  LicenseComparators(String name, Comparator<LicenseNode> comparator) {

    this.name = name;

    this.comparator = comparator;
  }

  ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //------------------------ Implements:

  //------------------------ Overrides: Object
  @Override
  public String toString() {
    return name;
  }

  //---------------------------- Abstract Methods -----------------------------

  //---------------------------- Utility Methods ------------------------------

  //---------------------------- Property Methods -----------------------------
  public Comparator<LicenseNode> getComparator() {
    return comparator;
  }
}

