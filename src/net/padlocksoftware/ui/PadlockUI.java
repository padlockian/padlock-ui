/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PadlockUI.java
 *
 * Created on Dec 4, 2009, 11:09:29 AM
 */
package net.padlocksoftware.ui;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;
import net.padlocksoftware.padlock.VersionInfo;
import net.padlocksoftware.padlock.license.License;
import net.padlocksoftware.padlock.license.LicenseFactory;
import net.padlocksoftware.padlock.license.PadlockState;
import net.padlocksoftware.padlocktemplates.LicenseTemplate;
import net.padlocksoftware.padlocktemplates.LicenseTemplateDefinition;
import net.padlocksoftware.ui.plugins.CategoryPlugin;
import net.padlocksoftware.ui.plugins.StatusPlugin;
import net.padlocksoftware.ui.treetable.AbstractTreeNode;
import net.padlocksoftware.ui.treetable.LicenseComparators;
import net.padlocksoftware.ui.treetable.LicenseNode;
import net.padlocksoftware.ui.treetable.LicenseTreeCellRenderer;
import org.jdesktop.swingx.JXTreeTable;

/**
 *
 * @author jason
 */
public final class PadlockUI extends javax.swing.JFrame {

  private class PropertiesTableSelectionListener implements ListSelectionListener {

    public void valueChanged(ListSelectionEvent e) {
      if (!e.getValueIsAdjusting()) {
        List<String> selected = getSelectedProperties();
        removePropertyButton.setEnabled(selected.size() > 0);
      }
    }
  }

  private class TableSelectionListener implements TreeSelectionListener {

    public void valueChanged(TreeSelectionEvent e) {
      List<String> selection = getSelectedLicenses();
      model.getLicenseSelectionManager().setSelection(selection);
      updateModels(selection);
      updateToolbar(selection);
      updatePanels(selection);
    }
  }

  public static PadlockUI padlockUI = null;
  
  URL urlKey = this.getClass().getResource("/net/padlocksoftware/ui/resources/toolbar/key-20.png");
  ImageIcon iconKey20 = new ImageIcon(urlKey);

  URL urlTemplate = this.getClass().getResource("/net/padlocksoftware/ui/resources/toolbar/template-20.png");
  ImageIcon iconTemplate20 = new ImageIcon(urlTemplate);

  URL urlCopy = this.getClass().getResource("/net/padlocksoftware/ui/resources/toolbar/copy-20.png");
  ImageIcon iconCopy20 = new ImageIcon(urlCopy);

  URL urlDelete = this.getClass().getResource("/net/padlocksoftware/ui/resources/toolbar/delete-20.png");
  ImageIcon iconDelete20 = new ImageIcon(urlDelete);

  URL urlExport = this.getClass().getResource("/net/padlocksoftware/ui/resources/toolbar/export-20.png");
  ImageIcon iconExport20 = new ImageIcon(urlExport);

  URL urlImport = this.getClass().getResource("/net/padlocksoftware/ui/resources/toolbar/import-20.png");
  ImageIcon iconImport20 = new ImageIcon(urlImport);

  URL urlNew = this.getClass().getResource("/net/padlocksoftware/ui/resources/toolbar/new-20.png");
  ImageIcon iconNew20 = new ImageIcon(urlNew);

  private final JXTreeTable treeTable;

  private final PadlockModel model;

  private KeysDialog keysDialog;

  private final LicenseTreeTableModel treeTableModel;

  private final LicenseStatusTableModel licenseStatusTableModel;
  
  private final LicensePropertiesTableModel licensePropertiesTableModel;

  private final LicenseImportDialog importDialog;

  private final LicenseExportDialog exportDialog;

  private final PreferencesDialog preferencesDialog;

  private final AboutDialog aboutDialog;

  private final NamedIndexManager nameManager;

  private final TemplateManagerDialog templateDialog;

  /** Creates new form PadlockUI */
  public PadlockUI() {
    getRootPane().putClientProperty("apple.awt.brushMetalLook", Boolean.TRUE);
    model = new LocalPadlockModel();

    nameManager = new NamedIndexManager("License");

    model.addModelListener(new AbstractPadlockModelListener(){
         @Override
      public void padlockStateUpdated() {
         updateTitle();
      }
    });

    importDialog = new LicenseImportDialog(this, model);
    exportDialog = new LicenseExportDialog(this, model);

    preferencesDialog = new PreferencesDialog((LocalPadlockModel)model);

    templateDialog = new TemplateManagerDialog(model);
    
    aboutDialog = new AboutDialog((LocalPadlockModel)model);

    StatusPlugin statusPlugin = new StatusPlugin();
//    statusPlugin.setPadlockModel(model);
    model.addPlugin(statusPlugin);

    CategoryPlugin categoryPlugin = new CategoryPlugin();
//    categoryPlugin.setPadlockModel(model);
    model.addPlugin(categoryPlugin);

    initComponents();

    DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
    for(LicenseComparators c : LicenseComparators.values()) {
      comboBoxModel.addElement(c);
    }
    sortComboBox.setModel(comboBoxModel);
    
    setupMenu();

    if (UIUtils.isMac()) {
        toolbar.remove(sep);
        Set<JButton> buttons = new HashSet<JButton>(Arrays.asList(new JButton[]{
            keysButton, newLicenseToolButton, copyLicenseToolButton,
            deleteLicenseToolButton, jButton2, exportButton, templateButton
        }));

        keysButton.setIcon(iconKey20);
        templateButton.setIcon(iconTemplate20);
        newLicenseToolButton.setIcon(iconNew20);
        copyLicenseToolButton.setIcon(iconCopy20);
        deleteLicenseToolButton.setIcon(iconDelete20);
        jButton2.setIcon(iconImport20);
        exportButton.setIcon(iconExport20);
        
        for (JButton button : buttons) {
            button.putClientProperty("JButton.buttonType", "segmentedCapsule");
            button.putClientProperty("JButton.segmentPosition", "middle");
            button.setMargin(new Insets(0, 0, 0, 0));
        }

        keysButton.putClientProperty("JButton.segmentPosition", "first");
        exportButton.putClientProperty("JButton.segmentPosition", "last");
        try {
            OSXAdapter.setAboutHandler(this, getClass().getDeclaredMethod("about", (Class[]) null));
            OSXAdapter.setPreferencesHandler(this, getClass().getDeclaredMethod("preferences", (Class[])null));
            OSXAdapter.setQuitHandler(this, getClass().getDeclaredMethod("quit", (Class[]) null));
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(PadlockUI.class.getName()).log(Level.SEVERE, null, ex);
         } catch (SecurityException ex) {
            Logger.getLogger(PadlockUI.class.getName()).log(Level.SEVERE, null, ex);
         }

    }


    Dimension minimumSize = new Dimension(0, 0);
    outerSplitPane.setResizeWeight(1);
    outerSplitPane.getTopComponent().setMinimumSize(new Dimension(0, 300));
    outerSplitPane.getBottomComponent().setMinimumSize(minimumSize);
    innerSplitPane.getLeftComponent().setMinimumSize(minimumSize);
    innerSplitPane.getLeftComponent().setPreferredSize(new Dimension(300, 400));
    innerSplitPane.getRightComponent().setMinimumSize(minimumSize);

    setIconImages(UIUtils.getIcons());
    this.setTitle("Padlock License Manager 2.0.2");
    removePropertyButton.setEnabled(false);
    licenseStatusTableModel = new LicenseStatusTableModel(model);
    licenseStatusTable.setModel(licenseStatusTableModel);
    licenseStatusTable.setDefaultRenderer(Boolean.class, new TestResultTableCellRenderer());

    TableColumn column = licenseStatusTable.getColumnModel().getColumn(0);
    column.setPreferredWidth(150);
    column.setMaxWidth(150);

    column = licenseStatusTable.getColumnModel().getColumn(1);
    column.setPreferredWidth(50);
    column.setMaxWidth(50);
    column.setMinWidth(50);
    
    licensePropertiesTableModel = new LicensePropertiesTableModel(model);
    licensePropertiesTable.setModel(licensePropertiesTableModel);
    licensePropertiesTable.setDefaultRenderer(String.class, new LicensePropertiesTableCellRenderer());
    licensePropertiesTable.getSelectionModel().addListSelectionListener(new PropertiesTableSelectionListener());
    licensePropertiesTable.setDefaultEditor(String.class, UIUtils.getStringTableCellEditor());

    treeTableModel = new LicenseTreeTableModel(model);
    treeTable = new JXTreeTable(treeTableModel);
    treeTable.setShowsRootHandles(true);
    treeTable.setRootVisible(true);
    treeTable.setTreeCellRenderer(new LicenseTreeCellRenderer());
    treeTable.setAutoCreateRowSorter(true);
    treeTable.setRowHeight(24);
    treeTable.getTreeSelectionModel().addTreeSelectionListener(new TableSelectionListener());

    treeScrollPane.setViewportView(treeTable);

    UIUtils.macifyTable(treeTable);
    UIUtils.macifyTable(licenseStatusTable);
    UIUtils.macifyTable(licensePropertiesTable);


    updateToolbar(Collections.EMPTY_LIST);

    treeTable.addMouseListener(new MouseAdapter() {

      private void evaluatePopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
          TreePath path = treeTable.getPathForLocation(e.getX(), e.getY());
          int row = treeTable.getRowForPath(path);
          treeTable.getSelectionModel().setSelectionInterval(row, row);
          JPopupMenu menu = ((AbstractTreeNode) path.getLastPathComponent()).getPopup();
          if (menu != null) {
            menu.show(treeTable, e.getX(), e.getY());
          }
        }

      }

      @Override
      public void mousePressed(MouseEvent e) {
        evaluatePopup(e);
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        evaluatePopup(e);
      }
    });

    padlockUI = this;
    updateTitle();
  }

  public void quit() {
   System.exit(0);
  }

  public void about() {
   aboutDialog.setLocationRelativeTo(this);
   aboutDialog.setVisible(true);
  }

  public void preferences() {
   preferencesDialog.setLocationRelativeTo(this);
   preferencesDialog.setVisible(true);
  }
  

  private void setupMenu() {
   // If this is a Mac, do nothing for now.  Otherwise create a Padlock menu
   if (!UIUtils.isMac()) {
      JMenuBar menuBar = new JMenuBar();
      JMenu padlock = new JMenu("Padlock");
      padlock.setMnemonic('P');

      JMenuItem quit = new JMenuItem("Exit");
      quit.setMnemonic('x');
      quit.addActionListener(new ActionListener(){

        public void actionPerformed(ActionEvent e) {
          System.exit(1);
        }

      });

      JMenuItem prefs = new JMenuItem("Padlock Manager Preferences");
      prefs.setMnemonic('p');
      prefs.addActionListener(new ActionListener(){

        public void actionPerformed(ActionEvent e) {
          preferences();
        }

      }) ;


      JMenuItem about = new JMenuItem("About Padlock Manager");
      about.setMnemonic('a');
      about.addActionListener(new ActionListener(){

        public void actionPerformed(ActionEvent e) {
          about();
        }

      }) ;
      
      padlock.add(about);
      padlock.add(prefs);
      padlock.add(quit);

      menuBar.add(padlock);
      this.setJMenuBar(menuBar);
   }
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    toolbar = new javax.swing.JToolBar();
    keysButton = new javax.swing.JButton();
    templateButton = new javax.swing.JButton();
    sep = new javax.swing.JToolBar.Separator();
    newLicenseToolButton = new javax.swing.JButton();
    copyLicenseToolButton = new javax.swing.JButton();
    deleteLicenseToolButton = new javax.swing.JButton();
    jButton2 = new javax.swing.JButton();
    exportButton = new javax.swing.JButton();
    removePropertyButton = new javax.swing.JButton();
    detailPanel = new net.padlocksoftware.ui.LicenseDetailPanel(model);
    jPanel6 = new javax.swing.JPanel();
    outerSplitPane = new javax.swing.JSplitPane();
    treeScrollPane = new javax.swing.JScrollPane();
    innerSplitPane = new javax.swing.JSplitPane();
    jPanel4 = new javax.swing.JPanel();
    jPanel1 = new javax.swing.JPanel();
    jLabel2 = new javax.swing.JLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    licenseStatusTable = new javax.swing.JTable();
    jPanel5 = new javax.swing.JPanel();
    jPanel3 = new javax.swing.JPanel();
    jLabel4 = new javax.swing.JLabel();
    jScrollPane2 = new javax.swing.JScrollPane();
    licensePropertiesTable = new javax.swing.JTable();
    jPanel2 = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    jLabel3 = new javax.swing.JLabel();
    sortComboBox = new javax.swing.JComboBox();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

    toolbar.setFloatable(false);
    toolbar.setRollover(true);

    keysButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/padlocksoftware/ui/resources/toolbar/key.png"))); // NOI18N
    keysButton.setToolTipText("Key Manager");
    keysButton.setFocusable(false);
    keysButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    keysButton.setMaximumSize(new java.awt.Dimension(47, 30));
    keysButton.setPreferredSize(new java.awt.Dimension(47, 30));
    keysButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    keysButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        keysButtonActionPerformed(evt);
      }
    });
    toolbar.add(keysButton);

    templateButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/padlocksoftware/ui/resources/toolbar/template.png"))); // NOI18N
    templateButton.setToolTipText("Template Manager");
    templateButton.setFocusable(false);
    templateButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    templateButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    templateButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        templateButtonActionPerformed(evt);
      }
    });
    toolbar.add(templateButton);
    toolbar.add(sep);

    newLicenseToolButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/padlocksoftware/ui/resources/toolbar/new.png"))); // NOI18N
    newLicenseToolButton.setToolTipText("Create a new license");
    newLicenseToolButton.setFocusable(false);
    newLicenseToolButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    newLicenseToolButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    newLicenseToolButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        newLicenseToolButtonActionPerformed(evt);
      }
    });
    toolbar.add(newLicenseToolButton);

    copyLicenseToolButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/padlocksoftware/ui/resources/toolbar/copy.png"))); // NOI18N
    copyLicenseToolButton.setToolTipText("Copy the selected license");
    copyLicenseToolButton.setFocusable(false);
    copyLicenseToolButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    copyLicenseToolButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    copyLicenseToolButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        copyLicenseToolButtonActionPerformed(evt);
      }
    });
    toolbar.add(copyLicenseToolButton);

    deleteLicenseToolButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/padlocksoftware/ui/resources/toolbar/delete.png"))); // NOI18N
    deleteLicenseToolButton.setToolTipText("Delete selected licenses");
    deleteLicenseToolButton.setFocusable(false);
    deleteLicenseToolButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    deleteLicenseToolButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    deleteLicenseToolButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        deleteLicenseToolButtonActionPerformed(evt);
      }
    });
    toolbar.add(deleteLicenseToolButton);

    jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/padlocksoftware/ui/resources/toolbar/import.png"))); // NOI18N
    jButton2.setToolTipText("Import a license file");
    jButton2.setFocusable(false);
    jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jButton2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton2ActionPerformed(evt);
      }
    });
    toolbar.add(jButton2);

    exportButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/padlocksoftware/ui/resources/toolbar/export.png"))); // NOI18N
    exportButton.setToolTipText("Export selected licenses");
    exportButton.setFocusable(false);
    exportButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    exportButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    exportButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        exportButtonActionPerformed(evt);
      }
    });
    toolbar.add(exportButton);

    removePropertyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/padlocksoftware/ui/resources/x-20.png"))); // NOI18N
    removePropertyButton.setText("Remove Selected");
    removePropertyButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        removePropertyButtonActionPerformed(evt);
      }
    });

    jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(70, 70, 70)));
    jPanel6.setLayout(new java.awt.BorderLayout());

    outerSplitPane.setBorder(null);
    outerSplitPane.setDividerLocation(300);
    outerSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

    treeScrollPane.setBorder(null);
    outerSplitPane.setTopComponent(treeScrollPane);

    innerSplitPane.setBorder(null);
    innerSplitPane.setDividerLocation(300);

    jPanel1.setBackground(new java.awt.Color(70, 70, 70));

    jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12));
    jLabel2.setForeground(new java.awt.Color(255, 255, 255));
    jLabel2.setText("License Status");

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel2)
        .addContainerGap(201, Short.MAX_VALUE))
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
    );

    jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(70, 70, 70)));

    licenseStatusTable.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {
        {null, null, null, null},
        {null, null, null, null},
        {null, null, null, null},
        {null, null, null, null}
      },
      new String [] {
        "Title 1", "Title 2", "Title 3", "Title 4"
      }
    ));
    licenseStatusTable.setRowHeight(22);
    jScrollPane1.setViewportView(licenseStatusTable);

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
      jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
      .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    jPanel4Layout.setVerticalGroup(
      jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel4Layout.createSequentialGroup()
        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(0, 0, 0)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE))
    );

    innerSplitPane.setLeftComponent(jPanel4);

    jPanel3.setBackground(new java.awt.Color(70, 70, 70));

    jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12));
    jLabel4.setForeground(new java.awt.Color(255, 255, 255));
    jLabel4.setText("License Properties");

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel4)
        .addContainerGap(724, Short.MAX_VALUE))
    );
    jPanel3Layout.setVerticalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
    );

    jScrollPane2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(70, 70, 70)));

    licensePropertiesTable.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {
        {null, null, null, null},
        {null, null, null, null},
        {null, null, null, null},
        {null, null, null, null}
      },
      new String [] {
        "Title 1", "Title 2", "Title 3", "Title 4"
      }
    ));
    licensePropertiesTable.setRowHeight(22);
    jScrollPane2.setViewportView(licensePropertiesTable);

    javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
    jPanel5.setLayout(jPanel5Layout);
    jPanel5Layout.setHorizontalGroup(
      jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 845, Short.MAX_VALUE)
      .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    jPanel5Layout.setVerticalGroup(
      jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel5Layout.createSequentialGroup()
        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(0, 0, 0)
        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE))
    );

    innerSplitPane.setRightComponent(jPanel5);

    outerSplitPane.setRightComponent(innerSplitPane);

    jPanel6.add(outerSplitPane, java.awt.BorderLayout.CENTER);

    jPanel2.setBackground(new java.awt.Color(70, 70, 70));

    jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14));
    jLabel1.setForeground(new java.awt.Color(255, 255, 255));
    jLabel1.setText("Licenses");

    jLabel3.setForeground(new java.awt.Color(255, 255, 255));
    jLabel3.setText("Sort Licenses By:");

    sortComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
    sortComboBox.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        sortComboBoxActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 847, Short.MAX_VALUE)
        .addComponent(jLabel3)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(sortComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
          .addComponent(sortComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel3))
        .addContainerGap())
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(toolbar, javax.swing.GroupLayout.DEFAULT_SIZE, 1575, Short.MAX_VALUE)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(removePropertyButton, javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1152, Short.MAX_VALUE)
          .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addGap(10, 10, 10)
        .addComponent(detailPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addComponent(toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(detailPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, 0)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 697, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(removePropertyButton)))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents


  private void updateTitle() {
    String baseTitle = "Padlock Manager " + VersionInfo.getMajorVersion() +
            "." + VersionInfo.getMinorVersion() + "." + VersionInfo.getPointVersion();
    
    PadlockState state = model.getState();
    String name = " - Demo Version";
    if (state.isValid()) {
      License l = state.getLicense();
      String to = state.getCompanyName();
      if (to == null || to.length() < 1) {
        to = state.getName();
      }

      name = " - Licensed to " + to;
    }

    this.setTitle(baseTitle + name);
  }

  private void keysButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_keysButtonActionPerformed
    if (keysDialog == null) {
      keysDialog = new KeysDialog(model, this, true);
      keysDialog.pack();
    }

    keysDialog.setLocationRelativeTo(this);
    keysDialog.setVisible(true);
  }//GEN-LAST:event_keysButtonActionPerformed

  private void deleteLicenseToolButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteLicenseToolButtonActionPerformed
    List<String> selectedLicenses = getSelectedLicenses();

    if (selectedLicenses.size() == 0) {
      return;
    }

    for (String licenseName : selectedLicenses) {
      model.removeLicense(licenseName);
    }
  }//GEN-LAST:event_deleteLicenseToolButtonActionPerformed

  private void copyLicenseToolButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyLicenseToolButtonActionPerformed
    List<String> selectedLicenses = getSelectedLicenses();

    if (selectedLicenses.size() != 1) {
      return;
    }

    String name = selectedLicenses.get(0);

    License newLicense = model.getLicense(name).cloneLicense();
    model.addLicense("Copy of " + name, newLicense);

  }//GEN-LAST:event_copyLicenseToolButtonActionPerformed

  private void removePropertyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removePropertyButtonActionPerformed
    List<String> keys = getSelectedProperties();
    licensePropertiesTableModel.removeProperties(keys);

  }//GEN-LAST:event_removePropertyButtonActionPerformed

  private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    importDialog.setLocationRelativeTo(this);
    importDialog.setVisible(true);
  }//GEN-LAST:event_jButton2ActionPerformed

  private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
    List<String> selectedLicenses = getSelectedLicenses();
    exportDialog.setLicenses(selectedLicenses);

    exportDialog.setLocationRelativeTo(this);
    exportDialog.setVisible(true);
  }//GEN-LAST:event_exportButtonActionPerformed

  private void newLicenseToolButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newLicenseToolButtonActionPerformed

    // Show a popup menu aligned with the bottom left of the button.  The menu
    // should have an option for a "blank" license, or a license generated from
    // an existing template.
    JPopupMenu popup = new JPopupMenu();

    Rectangle bounds = newLicenseToolButton.getBounds();

    Action a = new AbstractAction("Create a blank license") {
      public void actionPerformed(ActionEvent e) {
        License l = LicenseFactory.createLicense();
        String name = nameManager.getNext();
        if (!model.getState().isValid()) {
          l.setExpirationDate(new Date(l.getCreationDate().getTime() + 1000 * 3600 * 24 * 14));
        }
        model.addLicense(name, l);
      }
    };
    JMenuItem item = new JMenuItem(a);
    item.setIcon(iconNew20);
    popup.add(item);

    // Add Templates
    TreeMap<String, LicenseTemplateDefinition> templates =
            new TreeMap<String, LicenseTemplateDefinition>(String.CASE_INSENSITIVE_ORDER);
    templates.putAll(model.getTemplates());

    if (templates.size() > 0) {
      popup.addSeparator();
      for (Entry<String, LicenseTemplateDefinition> entry : templates.entrySet()) {
        final String name = entry.getKey();
        final LicenseTemplateDefinition template = entry.getValue();

        a = new AbstractAction("Create from template " + name) {
          public void actionPerformed(ActionEvent e) {
            License l = new LicenseTemplate(template).createLicense();
            String name = nameManager.getNext();
            if (!model.getState().isValid()) {
              l.setExpirationDate(new Date(l.getCreationDate().getTime() + 1000 * 3600 * 24 * 14));
            }
            model.addLicense(name, l);
          }
        };
        item = new JMenuItem(a);
        item.setIcon(iconTemplate20);
        popup.add(item);
      }
    }

    popup.show(toolbar,(int)bounds.getMinX(), (int)bounds.getMaxY());
  }//GEN-LAST:event_newLicenseToolButtonActionPerformed

  private void sortComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortComboBoxActionPerformed
    LicenseComparators c = (LicenseComparators)sortComboBox.getSelectedItem();
    treeTableModel.setLicenseComparator(c.getComparator());
  }//GEN-LAST:event_sortComboBoxActionPerformed

  private void templateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_templateButtonActionPerformed
    templateDialog.setLocationRelativeTo(this);
    templateDialog.setVisible(true);
  }//GEN-LAST:event_templateButtonActionPerformed

  private void updatePanels(List<String> selectedLicenses) {
      if (selectedLicenses.size() == 1) {
        detailPanel.setSelectedLicense(selectedLicenses.get(0));
      } else {
        detailPanel.setSelectedLicense(null);
      }
  }

  private void updateModels(List<String> selectedLicenses) {

    if (selectedLicenses.size() == 1) {
      licenseStatusTableModel.setLicense(selectedLicenses.get(0));
      licensePropertiesTableModel.setLicense(selectedLicenses.get(0));
    } else {
      licenseStatusTableModel.setLicense(null);
      licensePropertiesTableModel.setLicense(null);
    }
  }

  private void updateToolbar(List<String> selectedLicenses) {
    if (selectedLicenses.size() == 0) {
      // No selection
      deleteLicenseToolButton.setEnabled(false);
      copyLicenseToolButton.setEnabled(false);
      exportButton.setEnabled(false);
    } else if (selectedLicenses.size() == 1) {
      // Single selection
      deleteLicenseToolButton.setEnabled(true);
      copyLicenseToolButton.setEnabled(true);
      exportButton.setEnabled(true);
    } else {
      // Multiple selection
      deleteLicenseToolButton.setEnabled(true);
      copyLicenseToolButton.setEnabled(false);
      exportButton.setEnabled(true);
    }
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {

    if (UIUtils.isMac()) {
       System.setProperty("apple.laf.useScreenMenuBar", "true");
    } else {
         try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
         } catch (ClassNotFoundException ex) {
            Logger.getLogger(PadlockUI.class.getName()).log(Level.SEVERE, null, ex);
         } catch (InstantiationException ex) {
            Logger.getLogger(PadlockUI.class.getName()).log(Level.SEVERE, null, ex);
         } catch (IllegalAccessException ex) {
            Logger.getLogger(PadlockUI.class.getName()).log(Level.SEVERE, null, ex);
         } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(PadlockUI.class.getName()).log(Level.SEVERE, null, ex);
         }
    }
   
    java.awt.EventQueue.invokeLater(new Runnable() {

      public void run() {
        new PadlockUI().setVisible(true);
      }
    });
  }

  private List<String> getSelectedProperties() {
    List<String> selected = new ArrayList<String>();

    int[] rows = licensePropertiesTable.getSelectedRows();

    for (int row : rows) {
      int selectedRow = licensePropertiesTable.convertRowIndexToModel(row);
      selected.add(licensePropertiesTableModel.getPropertyAtRow(selectedRow));
    }

    return selected;
  }

  /**
   * Determine the currently selected licenses from the current table
   * selection
   */
  private List<String> getSelectedLicenses() {
    List<String> licenses = new ArrayList<String>();

    TreePath[] paths = treeTable.getTreeSelectionModel().getSelectionPaths();
    if (paths != null) {
      for (TreePath path : paths) {
        Object item = path.getLastPathComponent();
        if (item instanceof LicenseNode) {
          licenses.add(((LicenseNode) item).getName());
        }
      }
    }
    return licenses;
  }
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton copyLicenseToolButton;
  private javax.swing.JButton deleteLicenseToolButton;
  private net.padlocksoftware.ui.LicenseDetailPanel detailPanel;
  private javax.swing.JButton exportButton;
  private javax.swing.JSplitPane innerSplitPane;
  private javax.swing.JButton jButton2;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JPanel jPanel4;
  private javax.swing.JPanel jPanel5;
  private javax.swing.JPanel jPanel6;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JButton keysButton;
  private javax.swing.JTable licensePropertiesTable;
  private javax.swing.JTable licenseStatusTable;
  private javax.swing.JButton newLicenseToolButton;
  private javax.swing.JSplitPane outerSplitPane;
  private javax.swing.JButton removePropertyButton;
  private javax.swing.JToolBar.Separator sep;
  private javax.swing.JComboBox sortComboBox;
  private javax.swing.JButton templateButton;
  private javax.swing.JToolBar toolbar;
  private javax.swing.JScrollPane treeScrollPane;
  // End of variables declaration//GEN-END:variables
}
