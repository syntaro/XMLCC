/*
 * Copyright 2023 Syntarou YOSHIDA.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jp.synthtarou.cceditor.common;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Syntarou YOSHIDA
 */
public class CCUtilities {
    public static String toHexFF(int i) {
        String str = Integer.toHexString(i).toUpperCase();
        if (str.length() == 1) {
            return "0" + str;
        }
        if (str.length() >= 3) {
            return str.substring(str.length() - 2, str.length());
        }
        return str;
    }

    public static String dumpHexFF(byte[] data) {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < data.length; ++i) {
            if (i != 0) {
                str.append(" ");
            }
            str.append(toHexFF((int) data[i]));
        }
        return str.toString();
    }
    
    public static boolean isNumberFormat(String text) {
        try {
            numberFromText(text, true);
            return true;
        }catch(NumberFormatException e) {
        }
        return false;
    }
 
    public static final int numberFromText(String text, boolean strict)  {
        int mum = 10;

        if (text.startsWith("0x")) {
            text = text.substring(2);
            mum = 16;
        }
        if (text.endsWith("h") || text.endsWith("H")) {
            text = text.substring(0, text.length() - 1);
            mum = 16;
        }
        
        int start = 0;
        int end = text.length();
        
        if (start >= end) {
            if (strict) {
                throw new NumberFormatException(text);
            }
            return 0;
        }

        int x = 0;
        for (int pos = start; pos < end; ++ pos) {
            int ch = text.charAt(pos);
            if (ch >= '0' && ch <= '9') {
                x *= mum;
                x += ch - (char) '0';
            } else if (ch >= 'A' && ch <= 'F' && mum == 16) {
                x *= mum;
                x += ch - (char) 'A' + 10;
            } else if (ch >= 'a' && ch <= 'f' && mum == 16) {
                x *= mum;
                x += ch - (char) 'a' + 10;
            } else {
                if (strict) {
                    throw new NumberFormatException(text);
                }
                return x;
            }
        }
        return x;
    }
    
    public static boolean searchTextIgnoreCase(String text, String words) {
        text = text.toLowerCase();
        words = words.toLowerCase();
        if (words.indexOf(' ') < 0) {
            return text.indexOf(words) >= 0;
        }
        ArrayList<String> cells = new ArrayList();
        split(words, cells, ' ');
        for (String parts : cells) {
            if (text.indexOf(parts) < 0) {
                return false;
            }
        }
        return true;
    }

    public static void swingTreeEnable(Component c, boolean enable) {
        if (c instanceof Container) {
            Container parent = (Container) c;
            int count = parent.getComponentCount();
            for (int x = 0; x < count; ++x) {
                swingTreeEnable(parent.getComponent(x), enable);
            }
        }
        if (c instanceof JComponent) {
            ((JComponent) c).setEnabled(enable);
        }
    }

    public static void centerWindow(Component c) {
        Component owner = (c instanceof Window) ? ((Window) c).getOwner() : null;
        if (owner != null && !owner.isVisible()) {
            owner = null;
        }
        Component parent = (owner != null) ? owner : c.getParent();
        if (parent != null && !parent.isVisible()) {
            parent = null;
        }
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension mySize = c.getSize();
        Point loc = new Point(0, 0);
        if (parent != null) {
            screenSize = parent.getSize();
            if (parent == owner) {
                loc = owner.getLocation();
            }
        }
        loc.x += (screenSize.width - mySize.width) / 2;
        loc.y += (screenSize.height - mySize.height) / 2;
        c.setLocation(loc);
    }

    public static void autoResizeTableColumnWidth(JScrollPane ownwer, JTable table) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        final TableColumnModel columnModel = table.getColumnModel();
        int totalWidth = table.getWidth();
        if (ownwer != null) {
            totalWidth = ownwer.getViewport().getWidth();
        }

        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 10; // Min width
            for (int row = -1; row < table.getRowCount(); row++) {
                if (row < 0) {
                    TableCellRenderer renderer = table.getTableHeader().getDefaultRenderer();
                    TableColumnModel model = table.getColumnModel();
                    TableColumn col = model.getColumn(column);
                    col.getHeaderValue();
                    Object r = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, column);
                    if (r instanceof Component) {
                        Component comp = (Component) r;
                        width = Math.max(comp.getPreferredSize().width + 30, width);
                    } else {
                        width = 50;
                    }
                } else {
                    TableCellRenderer renderer = table.getCellRenderer(row, column);
                    Component comp = table.prepareRenderer(renderer, row, column);
                    width = Math.max(comp.getPreferredSize().width + 30, width);
                }
            }
            if (width > 300) {
                width = 300;
            }
            if (column == table.getColumnCount() - 1) {
                columnModel.getColumn(column).setPreferredWidth(totalWidth);
            }
            else {
                totalWidth -= width;
                columnModel.getColumn(column).setPreferredWidth(width);
            }
        }
    }

    
    public static void autoResizeTableLastColumnWidth(JScrollPane ownwer, JTable table) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        final TableColumnModel columnModel = table.getColumnModel();
        int totalWidth = table.getWidth();
        if (ownwer != null) {
            totalWidth = ownwer.getViewport().getWidth();
        }

        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = columnModel.getColumn(column).getWidth();
            if (column == table.getColumnCount() - 1) {
                columnModel.getColumn(column).setPreferredWidth(totalWidth);
            }
            else {
                totalWidth -= width;
            }
        }
    }
    
    public static Container getOwnerWindow(Component panel) {
        while (panel != null) {
            if (panel instanceof Window) {
                return (Window) panel;
            }
            if (panel instanceof Dialog) {
                return (Dialog) panel;
            }
            panel = panel.getParent();
        }
        return null;
    }

    public static void split(String str, ArrayList<String> list, char splitter) {
        list.clear();
        int len = str.length();
        int from = 0;
        for (int i = 0; i < len; ++i) {
            char ch = str.charAt(i);
            if (ch == splitter) {
                list.add(str.substring(from, i));
                from = i + 1;
                continue;
            }
        }
        if (from < len) {
            list.add(str.substring(from, len));
        }
    }

    public static Color mixedColor(Color left, Color right, int percent) {
        int lr = (int) (left.getRed() * (100 - percent) / 100);
        int lg = (int) (left.getGreen() * (100 - percent) / 100);
        int lb = (int) (left.getBlue() * (100 - percent) / 100);
        int rr = (int) (right.getRed() * percent / 100);
        int rg = (int) (right.getGreen() * percent / 100);
        int rb = (int) (right.getBlue() * percent / 100);
        return new Color(lr + rr, lg + rg, lb + rb);
    }
    
    public static void backgroundRecursive(Container container, Color color) {
        LinkedList<Container> listContainer = new LinkedList();
        listContainer.add(container);
        
        while(listContainer.isEmpty() == false) {
            Container cont = listContainer.remove();
            if (cont == null) {
                continue;
            }
            cont.setBackground(color);
            
            Component[] list = cont.getComponents();
            for (Component child : list) {
                if (child instanceof Container) {
                    listContainer.add((Container)child);
                }else {
                    child.setBackground(color);
                }
            }
        }
    }
    
    public static boolean isShrinkTarget(char c) {
        if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
            return true;
        }
        return false;
    }
    
    public static String shrinkText(String text) {
        if (text == null) {
            return null;
        }
        if (text.length() == 0) {
            return text;
        }
        int start = 0;
        int end = text.length() - 1;
        while (start <= end && isShrinkTarget(text.charAt(start))) {
            start ++;
        }
        while (start <= end && isShrinkTarget(text.charAt(end))) {
            end ++;
        }
        if (start > end) {
            return "";
        }
        return text.substring(start, end);
    }
}
