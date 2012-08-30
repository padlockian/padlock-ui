package net.padlocksoftware.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Image;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 *
 * @author Jason Nichols
 */
public class UIUtils {

  private static class StringTableCellEditor extends DefaultCellEditor {

    StringTableCellEditor() {
      super(new JTextField());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
      JTextField field = (JTextField)super.getTableCellEditorComponent(table, value, isSelected, row, column);
      field.setText(value.toString());
      field.selectAll();
      return field;
    }
  }
  ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  private static URL url = UIUtils.class.getResource("/net/padlocksoftware/ui/resources/padlock-32.png");
  private static ImageIcon icon = new ImageIcon(url);

  private static URL urlSmall = UIUtils.class.getResource("/net/padlocksoftware/ui/resources/padlock-20.png");
  private static ImageIcon iconSmall = new ImageIcon(urlSmall);

  private static URL urlLarge = UIUtils.class.getResource("/net/padlocksoftware/ui/resources/padlock-64.png");
  private static ImageIcon iconLarge = new ImageIcon(urlLarge);

  private static List<Image> icons = Collections.unmodifiableList(Arrays.asList(icon.getImage(), iconSmall.getImage(),
          iconLarge.getImage()));

  ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  public static boolean isMac() {
    return System.getProperty("os.name").toLowerCase().startsWith("mac");
  }

  public static void macifyTable(JTable table) {
    table.getTableHeader().setDefaultRenderer(new MacTableHeader(table));
    table.setBorder(BorderFactory.createEmptyBorder());
    Container parent = table.getParent();
    if (parent instanceof JScrollPane) {
      JScrollPane pane = (JScrollPane) parent;
      pane.setBorder(BorderFactory.createEmptyBorder());
    }
  }

  public static String formatFloatDuration(long duration) {

    PeriodFormatter formatter = new PeriodFormatterBuilder().printZeroNever().appendYears().appendSuffix(" year", " years").appendSeparator(", ").appendMonths().appendSuffix(" month", " months").appendSeparator(", ").appendWeeks().appendSuffix(" week", " weeks").appendSeparator(", ").appendDays().appendSuffix(" day", " days").appendSeparator(", ").appendHours().appendSuffix(" hour", " hours").appendSeparator(" and ").appendMinutes().appendSuffix(" minute", " minutes").toFormatter();

    Period period = new Period(duration).normalizedStandard();

    return period.toString(formatter);
  }
  //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

  //------------------------ Implements:

  //------------------------ Overrides:

  //---------------------------- Abstract Methods -----------------------------

  //---------------------------- Utility Methods ------------------------------
  
  //---------------------------- Property Methods -----------------------------
  public static List<Image> getIcons() {
    return icons;
  }

  public static TableCellEditor getStringTableCellEditor() {
    return new StringTableCellEditor();
  }
}
