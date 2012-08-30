/**
 * As seen on http://explodingpixels.wordpress.com/2008/05/11/sexy-swing-app-itunes-table-header/
 */

package net.padlocksoftware.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class MacTableHeader extends EmphasizedLabel
        implements TableCellRenderer{

    private JTable fTable;
    private int fSelectedColumn = -1;
    private int fPressedColumn = -1;
    private Color fTopColor = new Color(0xdbdbdb);
    private Color fBottomColor = new Color(0xbbbbbb);

    public static final Color ITUNES_TABLE_HEADER_LEFT_UNSELECTED_BORDER_COLOR =
            new Color(0xd9d9d9);
    public static final Color ITUNES_TABLE_HEADER_LEFT_PRESSED_UNSELECTED_BORDER_COLOR =
            new Color(0xc0c0c0);
    public static final Color ITUNES_TABLE_HEADER_LEFT_SELECTED_BORDER_COLOR =
            new Color(0xabbbce);
    public static final Color ITUNES_TABLE_HEADER_RIGHT_UNSELECTED_BORDER_COLOR =
            new Color(0x9c9c9c);
    public static final Color ITUNES_TABLE_HEADER_RIGHT_SELECTED_BORDER_COLOR =
            new Color(0x8a97a6);
    public static final Color ITUNES_TABLE_HEADER_BOTTOM_BORDER_COLOR =
            new Color(0x555555);
    public static final Color ITUNES_TABLE_HEADER_UNSELECTED_TOP_COLOR =
            new Color(0xdbdbdb);
    public static final Color ITUNES_TABLE_HEADER_UNSELECTED_BOTTOM_COLOR =
            new Color(0xbbbbbb);
    public static final Color ITUNES_TABLE_HEADER_SELECTED_TOP_COLOR =
            new Color(0xc2cfdd);
    public static final Color ITUNES_TABLE_HEADER_SELECTED_BOTTOM_COLOR =
            new Color(0x7d93b2);
    public static final Color ITUNES_TABLE_HEADER_PRESSED_UNSELECTED_TOP_COLOR =
            new Color(0xc4c4c4);
    public static final Color ITUNES_TABLE_HEADER_PRESSED_UNSELECTED_BOTTOM_COLOR =
            new Color(0x959595);
    public static final Color ITUNES_TABLE_HEADER_PRESSED_SELECTED_TOP_COLOR =
            new Color(0x96b7cb);
    public static final Color ITUNES_TABLE_HEADER_PRESSED_SELECTED_BOTTOM_COLOR =
            new Color(0x536b90);

    MacTableHeader() {
        setOpaque(false);
        setFont(UIManager.getFont("Table.font").deriveFont(Font.PLAIN, 12.0f));
    }

    MacTableHeader(JTable table) {
        this();
        table.getTableHeader().addMouseListener(new HeaderClickHandler());
        fTable = table;
    }

    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {

        // determine if the window has focus.
        Window window = SwingUtilities.getWindowAncestor(fTable);
        boolean windowHasFocus = window != null && window.isFocused();

        int modelColumn = fTable.convertColumnIndexToModel(column);

        setText(value.toString());
        Border leftSpacerBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,1,0,0,
                        getLeftBorderColor(modelColumn, windowHasFocus)),
                BorderFactory.createEmptyBorder(1,4,0,4));

        Border bottomRightBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,0,1,0,
                       ITUNES_TABLE_HEADER_BOTTOM_BORDER_COLOR),
                            BorderFactory.createMatteBorder(0,0,0,1,
                        getRightBorderColor(modelColumn, windowHasFocus)));

        setBorder(BorderFactory.createCompoundBorder(
                bottomRightBorder, leftSpacerBorder));

        fTopColor = getTopColor(modelColumn, windowHasFocus);
        fBottomColor = getBottomColor(modelColumn, windowHasFocus);

        return this;
    }

    /**
     * Gets the color to use as the top gradient color. This color takes into account window
     * focus as well as the selection state of the column.
     */
    private Color getTopColor(int column, boolean windowHasFocus) {
        Color retVal;

        if (!windowHasFocus) {
            retVal = ITUNES_TABLE_HEADER_UNSELECTED_TOP_COLOR;
        } else if (column == fSelectedColumn && column == fPressedColumn) {
            retVal = ITUNES_TABLE_HEADER_PRESSED_SELECTED_TOP_COLOR;
        } else if (column == fSelectedColumn) {
            retVal = ITUNES_TABLE_HEADER_SELECTED_TOP_COLOR;
        } else if (column == fPressedColumn) {
            retVal = ITUNES_TABLE_HEADER_PRESSED_UNSELECTED_TOP_COLOR;
        } else {
            retVal = ITUNES_TABLE_HEADER_UNSELECTED_TOP_COLOR;
        }

        return retVal;
    }

    /**
     * Gets the color to use as the bottom gradient color. This color takes into account window
     * focus as well as the selection state of the column.
     */
    private Color getBottomColor(int column, boolean windowHasFocus) {
        Color retVal;

        if (!windowHasFocus) {
            retVal = ITUNES_TABLE_HEADER_UNSELECTED_BOTTOM_COLOR;
        } else if (column == fSelectedColumn && column == fPressedColumn) {
            retVal = ITUNES_TABLE_HEADER_PRESSED_SELECTED_BOTTOM_COLOR;
        } else if (column == fSelectedColumn) {
            retVal = ITUNES_TABLE_HEADER_SELECTED_BOTTOM_COLOR;
        } else if (column == fPressedColumn) {
            retVal = ITUNES_TABLE_HEADER_PRESSED_UNSELECTED_BOTTOM_COLOR;
        } else {
            retVal = ITUNES_TABLE_HEADER_UNSELECTED_BOTTOM_COLOR;
        }

        return retVal;
    }

    private Color getLeftBorderColor(int column, boolean windowHasFocus) {
        Color retVal;

        if (!windowHasFocus) {
            retVal = ITUNES_TABLE_HEADER_LEFT_UNSELECTED_BORDER_COLOR;
        } else if (column == fSelectedColumn && column == fPressedColumn) {
            retVal = ITUNES_TABLE_HEADER_LEFT_SELECTED_BORDER_COLOR;
        } else if (column == fSelectedColumn) {
            retVal = ITUNES_TABLE_HEADER_LEFT_SELECTED_BORDER_COLOR;
        } else if (column == fPressedColumn) {
            retVal = ITUNES_TABLE_HEADER_LEFT_PRESSED_UNSELECTED_BORDER_COLOR;
        } else {
            retVal = ITUNES_TABLE_HEADER_LEFT_UNSELECTED_BORDER_COLOR;
        }

        return retVal;
    }

    private Color getRightBorderColor(int column, boolean windowHasFocus) {
        return windowHasFocus && column == fSelectedColumn
                ? ITUNES_TABLE_HEADER_RIGHT_SELECTED_BORDER_COLOR
                : ITUNES_TABLE_HEADER_RIGHT_UNSELECTED_BORDER_COLOR;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D graphics = (Graphics2D) g.create();

        Paint paint = new GradientPaint(
                0, 0, fTopColor, 0, getHeight(), fBottomColor);

        graphics.setPaint(paint);
        graphics.fillRect(0,0,getWidth(),getHeight());

        graphics.dispose();

        super.paintComponent(g);
    }

    private class HeaderClickHandler extends MouseAdapter {

        private boolean mouseEventIsPerformingPopupTrigger = false;

        public void mouseClicked(MouseEvent mouseEvent) {
            // if the MouseEvent is popping up a context menu, do not sort
            if (mouseEventIsPerformingPopupTrigger) return;

            // if the cursor indicates we're resizing columns, do not sort
            if (fTable.getTableHeader().getCursor() == Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR)) {
                return;
            }

            final TableColumnModel columnModel = fTable.getColumnModel();
            int viewColumn = columnModel.getColumnIndexAtX(mouseEvent.getX());
            fSelectedColumn = fTable.convertColumnIndexToModel(viewColumn);

            fTable.getTableHeader().repaint();
        }

        public void mousePressed(MouseEvent mouseEvent) {
            this.mouseEventIsPerformingPopupTrigger = mouseEvent.isPopupTrigger();

            if (fTable.getTableHeader().getCursor() != Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR)) {
                final TableColumnModel columnModel = fTable.getColumnModel();
                int viewColumn = columnModel.getColumnIndexAtX(mouseEvent.getX());
                fPressedColumn = fTable.convertColumnIndexToModel(viewColumn);

                fTable.getTableHeader().repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            fPressedColumn = -1;
            fTable.getTableHeader().repaint();
        }
    }
}