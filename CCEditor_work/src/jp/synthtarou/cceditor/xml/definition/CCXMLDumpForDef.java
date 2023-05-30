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

package jp.synthtarou.cceditor.xml.definition;

import java.util.HashSet;
import java.util.List;

/**
 *
 * @author Syntarou YOSHIDA
 */
public class CCXMLDumpForDef {
    static boolean _dumpSysout = true;

    public static void dumpDefinition(int space, CCXMLDefTag current, HashSet<CCXMLDefTag> already) {
        StringBuffer indent = new StringBuffer();
        for (int i = 0; i < space; ++ i) {
            indent.append("    ");
        }
        if (already.contains(current)) {
            return;
        }
        already.add(current);

        List<CCXMLDefAttributes> listAttributes = current.listAttributes();
        StringBuffer strAttributes = new StringBuffer();
        for (CCXMLDefAttributes attribute : listAttributes) {
            if (strAttributes.length() > 0) {
                strAttributes.append(", ");
            }
            strAttributes.append("[");
            strAttributes.append(attribute.getName());
            if (attribute.getDefaultValue() != null) {
                strAttributes.append("=");
                strAttributes.append(attribute.getDefaultValue());
            }
            strAttributes.append("]");
        }
        if (current.hasTextContents()) {
            if (strAttributes.length() > 0) {
                strAttributes.append(", ");
            }
            strAttributes.append("[Text]");
        }
        
        if (_dumpSysout) {
            System.out.println(indent.toString() + current.getName() + strAttributes.toString());
        }
        
        List<CCXMLDefTag> listChildren = current.listChildTags();
        for (CCXMLDefTag child : listChildren) {
            dumpDefinition(space + 1, child, already);
        }
    }
    
    public static void dumpDefinition() {
        dumpDefinition(0, CCXMLDefinitions.getInstance().getModuleData(), new HashSet<CCXMLDefTag>());
    }
}
