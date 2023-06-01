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
package jp.synthtarou.cceditor.xml;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import jp.synthtarou.cceditor.xml.definition.CCXMLAttributeRule;

/**
 *
 * @author Syntarou YOSHIDA
 */
public class CCXMLTreeRenderer implements TreeCellRenderer {
    TreeCellRenderer _base = new DefaultTreeCellRenderer();
    
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        String text = "";
        if (value != null) {
            CCXMLNode node = (CCXMLNode)value;
            text = format(node);
        }
        return _base.getTreeCellRendererComponent(tree, text, selected, expanded, leaf, row, hasFocus);
    }


    public static boolean _displayAttribute = true;
    public static boolean _displayTextContent = true;
    
    public String escapeDQuote(String text) {
        StringBuffer ret = new StringBuffer();

        final char DQUOTE = '\"';
        final char BSLASH = '\\';

        ret.append(DQUOTE);
        
        for (char ch : text.toCharArray()) {
            switch(ch) {
                case DQUOTE:
                    ret.append(BSLASH);
                    ret.append(DQUOTE);
                    break;
                case BSLASH:
                    ret.append(BSLASH);
                    ret.append(BSLASH);
                    break;
                case '\n':
                    ret.append(BSLASH);
                    ret.append('n');
                    break;
                default:
                    ret.append(ch);
            }
        }
        
        ret.append(DQUOTE);
        
        return ret.toString();
    }
    
    public String format(CCXMLNode node) {
        StringBuffer text = new StringBuffer();
        text.append(node._name);

        if (_displayAttribute) {
            ArrayList<String> listAttrDefined = new ArrayList();
            ArrayList<String> listAttrNonedefined = new ArrayList();

            if (node._definition != null) {
                HashSet<String> already = new HashSet();

                for (CCXMLAttributeRule dump :  node._definition.listAttributes()) {
                    String name = dump.getName();
                    String value = node.getAttributeValue(name);
                    if (value != null) {
                        listAttrDefined.add(name + "=" + escapeDQuote(value));
                    }
                    already.add(name.toLowerCase());
                }

                for (CCXMLAttribute attr : node._listAttributes) {
                    String name = attr.getName();
                    String value = attr.getValue();
                    if (already.contains(name.toLowerCase())) {
                        continue;
                    }
                    listAttrNonedefined.add(name + "="  + escapeDQuote(value));
                }
            }
            else {
                for (CCXMLAttribute attr : node._listAttributes) {
                    String name = attr.getName();
                    String value = attr.getValue();
                    listAttrNonedefined.add(name + "="  + value);
                }
            }
            
            boolean first = true;
            if (listAttrDefined.size() > 0) {
                for (String seg : listAttrDefined) {
                    if (!first)  {
                        text.append(", ");
                    }
                    else {                        
                        text.append(":");
                    }
                    first = false;
                    text.append(seg);
                }
            }
            if (listAttrNonedefined.size() > 0) {
                for (String seg : listAttrDefined) {
                    if (!first)  {
                        text.append(", ");
                    }else {                        
                        text.append(":");
                    }
                    first = false;
                    text.append(seg);
                }
            }
        }

        if (_displayTextContent) {
            if (node._textContext != null && node._textContext.length() > 0) {
                text.append("(");
                text.append(node._textContext);
                text.append(")");
            }
        }
        return text.toString();
    }
    
}
