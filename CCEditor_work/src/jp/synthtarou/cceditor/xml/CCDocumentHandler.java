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

import java.util.LinkedList;
import jp.synthtarou.cceditor.common.CCUtilities;
import jp.synthtarou.cceditor.xml.definition.CCXMLRule;
import org.w3c.dom.Node;
import org.w3c.dom.UserDataHandler;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Syntarou YOSHIDA
 */
public class CCDocumentHandler extends DefaultHandler {
    public static final String USERDATA_PATH = "user.path"; 
    public static final String USERDATA_STARTLINE = "user.startLine"; 
    public static final String USERDATA_STARTCOLUMN = "user.startColumn";     

    LinkedList<CCXMLNode> _cursor = new LinkedList();
    CCXMLNode _document = new CCXMLNode(null, "", CCXMLRule.getInstance().getRootTag());
    Locator _locator;
    
    public CCDocumentHandler() {
    }

    @Override
    public void startDocument() {
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        _locator = locator;

    }

    UserDataHandler _dataHandler = new UserDataHandler() {
        @Override
        public void handle(short operation, String key, Object data, Node src, Node dst) {

            switch (operation) {
                case NODE_ADOPTED:
                    break;
                case NODE_CLONED:
                    break;
                case NODE_DELETED:
                    break;
                case NODE_IMPORTED:
                    break;
                case NODE_RENAMED:
                    break;
            }
        }
    };
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        CCXMLNode parent = _cursor.isEmpty() ? _document: _cursor.getLast();
        CCXMLNode child = new CCXMLNode(parent,  qName, null);

        parent._listChildTags.add(child);
        _cursor.add(child);

        if (attributes.getLength() > 0) {
            for (int i = 0; i < attributes.getLength(); ++i) {
                String name = attributes.getQName(i);
                String value = attributes.getValue(i);
                child._listAttributes.addNameAndValue(name, value);
            }
        }

        child._lineNumber = _locator.getLineNumber();
        child._columnNumber = _locator.getColumnNumber();
    }

    @Override
    public void characters(char[] ch, int offset, int length) {
        StringBuffer ret = new StringBuffer();
        ret.append(ch, offset, length);
        
        if (_cursor == null || _cursor.getLast() == null) {
            return;
        }

        if (ret.length() > 0) {
            String prev = _cursor.getLast().getTextContent();
            if (prev != null) {
                _cursor.getLast().setTextContent(prev + ret);
            }
            else {
                _cursor.getLast().setTextContent(ret.toString());
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (_cursor.isEmpty() == false) {
            CCXMLNode lastLeaf = _cursor.getLast();
            if (lastLeaf._name.equals(qName)) {
                _cursor.removeLast();
                
                if (_cursor.isEmpty() == false) {
                    lastLeaf = _cursor.getLast();
                }
            }
        }
    }

    @Override
    public void endDocument() {
    }
}
