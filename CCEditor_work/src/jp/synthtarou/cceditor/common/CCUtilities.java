/*
 * Copyright 2023 Syntarou YOSHIDA.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty ofr
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import jp.synthtarou.cceditor.view.common.CCFileChooser;

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
        } catch (NumberFormatException e) {
        }
        return false;
    }

    public static final int numberFromText(String text, boolean strict) {
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
        for (int pos = start; pos < end; ++pos) {
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
            } else {
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
            } else {
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

        while (listContainer.isEmpty() == false) {
            Container cont = listContainer.remove();
            if (cont == null) {
                continue;
            }
            cont.setBackground(color);

            Component[] list = cont.getComponents();
            for (Component child : list) {
                if (child instanceof Container) {
                    listContainer.add((Container) child);
                } else {
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
            start++;
        }
        while (start <= end && isShrinkTarget(text.charAt(end))) {
            end--;
        }
        if (start > end) {
            return "";
        }
        return text.substring(start, end + 1);
    }

    public static String getStackTraceAsString(Throwable th) {
        StringBuffer ret = new StringBuffer();
        StackTraceElement[] elems = th.getStackTrace();
        ret.append(th.toString());
        for (int i = 1; i < elems.length; ++i) {
            StackTraceElement x = elems[i];
            ret.append("\n    ");
            ret.append(x.toString());
        }
        return ret.toString();
    }

    public static File getAppBaseDirectory() {
        String fileName = null;
        try {
            ProtectionDomain pd = CCFileChooser.class.getProtectionDomain();
            CodeSource cs = pd.getCodeSource();
            URL location = cs.getLocation();
            URI uri = location.toURI();
            Path path = Paths.get(uri);
            fileName = path.toString();
        } catch (URISyntaxException ex) {
            Logger.getLogger(CCUtilities.class.getName()).log(Level.SEVERE, null, ex);
            return new File(".");
        }

        File base = new File(fileName);

        if (base.isFile()) {
            base = base.getParentFile();
        }
        return base;
    }

    public static boolean isInteger(String text) {
        for (int x = 0; x < text.length(); ++x) {
            char ch = text.charAt(x);
            if (ch >= '0' && ch <= '9') {
                continue;
            }
            return false;
        }
        if (text.length() == 0) {
            return false;
        }
        return true;
    }

    public static File getSettingDirectory() {
        File dir = new File(CCUtilities.getAppBaseDirectory(), "settings");
        if (dir.isDirectory()) {
            return dir;
        }

        try {
            Path p = Paths.get(dir.toURI());
            Files.createDirectory(p);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (dir.isDirectory()) {
            return dir;
        }

        return null;
    }

    public static File createTemporaryFile(File target) {
        File dir = target.getParentFile();
        String fileName = target.getName();
        for (int i = 1; i < 100; ++i) {
            String newName = fileName + "_temporary" + String.valueOf(i);
            File newFile = new File(dir, newName);
            if (newFile.exists()) {
                continue;
            }
            try {
                new FileOutputStream(newFile).close();
            } catch (IOException e) {
                continue;
            }
            return newFile;
        }
        return null;
    }

    public static File safeRenameToBackup(File target) {
        File parent = target.getParentFile();
        
        parent = new File(parent, "Old");
        parent.mkdir();

        String fileName = target.getName();

        int lastDot = fileName.indexOf('.');
        String forward, fileExt;

        if (lastDot >= 0) {
            forward = fileName.substring(0, lastDot);
            fileExt = fileName.substring(lastDot);
        } else {
            forward = fileName;
            fileExt = "";
        }

        Date lastMod = new Date(target.lastModified());
        Calendar cal = Calendar.getInstance();
        cal.setTime(lastMod);

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        String newName1 = forward + "_back" + year + "-" + month + "-" + day;
        String newName3 = fileExt;

        for (int count = 1; count <= 99; ++count) {
            String newName;
            if (count == 0) {
                newName = newName1 + newName3;
            } else {
                String newName2 = "(" + count + ")";
                newName = newName1 + newName2 + newName3;
            }

            File f = new File(parent, newName);
            if (f.isFile()) {
                continue;
            }
            target.renameTo(f);
            return f;
        }
        return null;
    }

    public static int compareFileText(File f1, File f2) {
        CCLineReader r1 = null, r2 = null;
        InputStream i1 = null, i2 = null;

        try {
            if (!f1.exists() || !f2.exists()) {
                return -1;
            }
            i1 = new FileInputStream(f1);
            i2 = new FileInputStream(f2);

            r1 = new CCLineReader(i1, "utf-8");
            r2 = new CCLineReader(i2, "utf-8");

            while (true) {
                String line1 = "", line2 = "";

                while (line1 != null && line1.isEmpty()) {
                    line1 = r1.readLine();
                    line1 = shrinkText(line1);
                }

                while (line2 != null && line2.isEmpty()) {
                    line2 = r2.readLine();
                    line2 = shrinkText(line2);
                }

                if (line1 == null) {
                    if (line2 == null) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
                if (line2 == null) {
                    return 1;
                }

                int x = line1.compareTo(line2);
                if (x != 0) {
                    return x;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(CCUtilities.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        } finally {
            if (i1 != null) {
                try {
                    i1.close();
                } catch (IOException ex) {
                    Logger.getLogger(CCUtilities.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (i2 != null) {
                try {
                    i2.close();
                } catch (IOException ex) {
                    Logger.getLogger(CCUtilities.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static int compareFileBinary(File f1, File f2) throws IOException {
        long l1 = f1.length();
        long l2 = f2.length();
        if (l1 < l2) {
            return -1;
        }
        if (l1 > l2) {
            return -1;
        }

        byte[] data1 = new byte[4096];
        byte[] data2 = new byte[4096];
        FileInputStream in1 = null;
        FileInputStream in2 = null;

        try {
            in1 = new FileInputStream(f1);
            in2 = new FileInputStream(f2);

            while (true) {
                int len1 = in1.read(data1);
                int len2 = in1.read(data2);
                if (len1 < len2) {
                    return -1;
                }
                if (len1 > len2) {
                    return 1;
                }
                if (len1 <= 0) {
                    break;
                }
                for (int x = 0; x < len1; ++x) {
                    if (data1[x] < data2[x]) {
                        return -1;
                    }
                    if (data1[x] > data2[x]) {
                        return 1;
                    }
                }
            }
            return 0;
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (in1 != null) {
                try {
                    in1.close();

                } catch (IOException ex) {
                    Logger.getLogger(CCUtilities.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
                ;

            }
            if (in2 != null) {
                try {
                    in2.close();

                } catch (IOException ex) {
                    Logger.getLogger(CCUtilities.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
