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

import java.util.ArrayList;
import jp.synthtarou.cceditor.common.CCWrapDataList;
import jp.synthtarou.cceditor.xml.definition.CCXMLTagRule;
import jp.synthtarou.cceditor.xml.definition.CCXMLRule;

/**
 *
 * @author Syntarou YOSHIDA
 */
public class CCXMLNode  {
    public CCXMLNode(CCXMLNode parent, String name, CCXMLTagRule definition) {
        _name = name;
        _definition = definition;
        _parent = parent;
    }
    
    final CCXMLTagRule _definition;
    final String _name;
    final CCXMLNode _parent;

    String _textContext;
    String _warningText;
    int _lineNumber;
    int _columnNumber;

    ArrayList<CCXMLAttribute> _listAttributes = new ArrayList<>();
    ArrayList<CCXMLNode> _listChildTags = new ArrayList<>();

    public String getName() {
        return _name;
    }

    public String getTextContent() {
        return _textContext;
    }

    public void setTextContent(String text) {
        _textContext = text;
    }
    
    public CCXMLTagRule getDefinition() {
        return _definition;
    }
    
    public int countAttribute() {
        return _listAttributes.size();
    }
    
    public CCXMLAttribute getAttribute(int index) {
        return _listAttributes.get(index);
    }

    public String getAttributeName(int index) {
        return _listAttributes.get(index).getName();
    }

    public String getAttributeText(int index) {
        return _listAttributes.get(index).getValue();
    }
    
    public int indexOfAttribute(String name) {
        for (int i = 0; i < _listAttributes.size(); ++ i) {
            CCXMLAttribute attr = _listAttributes.get(i);
            if (attr.getName().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    public String getAttributeValue(String name) {
        int x = indexOfAttribute(name);
        if (x < 0) {
            return null;
        }
        return getAttributeText(x);
    }
    
    public CCXMLNode getParent() {
        return _parent;
    }
    
    public ArrayList<CCXMLNode> getAsPath() {
        ArrayList<CCXMLNode> list = new ArrayList<>();
        CCXMLNode node = this;
        while(node != null) {
            list.add(0, node);
            node = node.getParent();
        }
        return list;
    }
    
    public String getAsPathString() {
        ArrayList<CCXMLNode> list = getAsPath();
        StringBuffer str = new StringBuffer();
        for (CCXMLNode node : list) {
            if (str.length() != 0) {
                str.append("/");
            }
            str.append(node._name);
        }
        return str.toString();
    }


    public int countChildTags() {
        return _listChildTags.size();
    }
    
    public CCXMLNode getChild(int index) {
        return _listChildTags.get(index);
    }
    
    public CCWrapDataList<CCXMLNode> getChildren(String name) {
        CCWrapDataList<CCXMLNode> list = new CCWrapDataList<>();

        for (CCXMLNode tag : _listChildTags) {
            if (tag._name.equalsIgnoreCase(name)) {
                String nameAttr = tag.getAttributeValue("name");
                String idAttr = tag.getAttributeValue("id");
                
                String dispName = tag._name;
               
                if (idAttr != null) {
                    dispName = idAttr;
                }
                if (nameAttr != null) {
                    dispName = nameAttr;
                }
                list.addNameAndValue(dispName, tag);
            }
        }

        return list;
    }

    public CCWrapDataList<CCXMLNode> getChildInstruemntsList() {
        CCXMLRule def = CCXMLRule.getInstance();
        return getChildren(def.getInstrumentListTag().getName());
    }

    public CCWrapDataList<CCXMLNode> getChildDrumSetList() {
        CCXMLRule def = CCXMLRule.getInstance();
        return getChildren(def.getDrumSetListTag().getName());
    }

    public CCWrapDataList<CCXMLNode> getChildControlChangeMacroList() {
        CCXMLRule def = CCXMLRule.getInstance();
        return getChildren(def.getControlChangeMacroListTag().getName());
    }

    public CCWrapDataList<CCXMLNode> getChildTemplateList() {
        CCXMLRule def = CCXMLRule.getInstance();
        return getChildren(def.getTemplateListTag().getName());
    }

    public CCWrapDataList<CCXMLNode> getChildDefaultData() {
        CCXMLRule def = CCXMLRule.getInstance();
        return getChildren(def.getDefaultDataTag().getName());
    }

}
