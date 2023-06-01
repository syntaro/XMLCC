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

import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
    public static final String USERDATA_STARTLINE = "startLineNumber"; 
    public static final String USERDATA_STARTCOLUMN = "startColumnNumber";     

    ArrayList<String> _listForPathName = new ArrayList();
    DocumentBuilder _builder;
    Document _document;
    ArrayList<Element> _nestedPosition = new ArrayList();
    Locator _locator;

    public CCDocumentHandler() {
        try {
            _builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
            return;
        }
        _document = _builder.newDocument();
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
                    System.out.println("NODE_ADOPTED");
                    break;
                case NODE_CLONED:
                    System.out.println("NODE_CLONED");
                    break;
                case NODE_DELETED:
                    System.out.println("NODE_DELETED");
                    break;
                case NODE_IMPORTED:
                    System.out.println("NODE_IMPORTED");
                    break;
                case NODE_RENAMED:
                    System.out.println("NODE_RENAMED");
                    break;
            }
        }
    };

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        _listForPathName.add(qName);

        Element element = _document.createElement(qName);
        if (attributes.getLength() > 0) {
            for (int i = 0; i < attributes.getLength(); ++i) {
                String aName = attributes.getQName(i);
                String aValue = attributes.getValue(i);
                element.setAttribute(aName, aValue);
            }
        }
        
        if (_nestedPosition.size() == 0) {
            _document.appendChild(element);
        } else {
            _nestedPosition.get(_nestedPosition.size() - 1).appendChild(element);
        }
        _nestedPosition.add(element);
        element.setUserData(USERDATA_STARTLINE, (Integer)_locator.getLineNumber(), _dataHandler);
        element.setUserData(USERDATA_STARTCOLUMN, (Integer)_locator.getColumnNumber(), _dataHandler);
    }

    @Override
    public void characters(char[] ch, int offset, int length) {
        StringBuffer text = new StringBuffer();
        text.append(ch, offset, length);

        String ret = CCXMLFile.shrinkSpace(text.toString());
        if (ret.length() > 0) {
            _nestedPosition.get(_nestedPosition.size() - 1).setTextContent(text.toString());
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (_listForPathName.size() > 0) {
            if (_listForPathName.get(_listForPathName.size() - 1).equals(qName)) {
                _listForPathName.remove(_listForPathName.size() - 1);
                _nestedPosition.remove(_nestedPosition.size() - 1);
            } else {
                System.err.println("XML ERROR " + qName);
            }
        } else {
            System.err.println("XML ERROR " + qName);
        }
    }

    @Override
    public void endDocument() {
    }

    public static boolean writeDocument(File file, Document doc) {
        Transformer tf = null;

        try {
            TransformerFactory factory = TransformerFactory
                    .newInstance();
            tf = factory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            return false;
        }

        tf.setOutputProperty("indent", "yes");
        tf.setOutputProperty("encoding", "UTF-8");

        try {
            tf.transform(new DOMSource(doc), new StreamResult(
                    file));
        } catch (TransformerException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
