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
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import jp.synthtarou.cceditor.common.CCWrapDataList;
import jp.synthtarou.cceditor.xml.definition.CCXMLDefTag;
import jp.synthtarou.cceditor.xml.definition.CCXMLDefinitions;
import jp.synthtarou.cceditor.xml.definition.CCXMLDumpForDef;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author Syntarou YOSHIDA
 */
public class CCXMLFile {

    final ArrayList<CCXMLNode> _arrayModuleData = new ArrayList<>();
    final File _file;
    final ArrayList<String> _adviceForXML = new ArrayList<>();
    final Exception _loadError;
    
    public String toString() {
        return  _file.toString();
    }
    
    public boolean isLoaded() {
        return  (_loadError == null) && countModule() > 0;
    }
    
    public int countModule() {
        return _arrayModuleData.size();
    }

    public CCXMLNode getModule(int module) {
        return _arrayModuleData.get(module);
    }
   
    public CCWrapDataList<CCXMLNode> listModules() {
        CCWrapDataList<CCXMLNode> list = new CCWrapDataList<>();

        for (CCXMLNode tag : _arrayModuleData) {
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

        return list;
    }
    
    public static void main(String[] args) {
        CCXMLDumpForDef.dumpDefinition();

        File moduleDirectory = new File("C:/Domino144/Module");
        for (File file : moduleDirectory.listFiles()) {
            if (file.isFile() == false) {
                continue;
            }
            String name = file.getName();
            name = name.toLowerCase();
            if (name.endsWith(".xml")) {
                if (file.canRead()) {
                    System.out.println("TryRead " + file);
                    CCXMLFile f2 = new CCXMLFile(file);
                    f2.dump();
                }
            }
        }
    }

    public CCXMLFile(File file) {
        _file = file;

        DocumentBuilderFactory factory;
        DocumentBuilder builder;
        Document document;
        factory = DocumentBuilderFactory.newInstance();
        Element docElement;
        NodeList list;

        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            _loadError = ex;
            return;
        }
        try {
            document = builder.parse(file);
            docElement = document.getDocumentElement();
            list = document.getElementsByTagName("ModuleData");
        } catch (SAXException ex) {
            _loadError = ex;
            return;
        } catch (IOException ex) {
            _loadError = ex;
            return;
        }

        _arrayModuleData.clear();

        for (int i = 0; i < list.getLength(); ++i) {
            Node moduleNode = list.item(i);
            if (moduleNode.getNodeType() == Node.ELEMENT_NODE) {
                CCXMLDefinitions total = CCXMLDefinitions.getInstance();
                CCXMLNode doc = new CCXMLNode(null, "ModuleData", total.getModuleData());
                tryRead(doc, (Element) moduleNode);

                _arrayModuleData.add(doc);
            }
        }
        _loadError = null;
    }
        
    public void tryRead(CCXMLNode target, Element e) {
        StringBuffer indent = new StringBuffer();

        NamedNodeMap attr = e.getAttributes();

        for (int x = 0; x < attr.getLength(); ++x) {
            String name = attr.item(x).getNodeName();
            String value = attr.item(x).getNodeValue();

            if (target._definition == null) {
                _adviceForXML.add("Undocumented Tag has Attributes: " + name + "=" + value + " @" + getPathOfNode(e) + " in [" + _file + "]");
                target._listAttributes.add(new CCXMLAttribute(name, value));
            }
            else if (target._definition.getAttribute(name) != null) {
                target._listAttributes.add(new CCXMLAttribute(name, value));
            } else {
                _adviceForXML.add("Undocumented Attributes: " + name + "=" + value + " @" + getPathOfNode(e) + " in [" + _file + "]");
                target._listAttributes.add(new CCXMLAttribute(name, value));
            }
        }

        NodeList list = e.getChildNodes();

        ArrayList<Element> listChild = new ArrayList();

        for (int x = 0; x < list.getLength(); ++x) {
            Node node = list.item(x);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                listChild.add((Element) node);
            }

            if (target._definition == null) {
                if (node.getNodeType() == Node.TEXT_NODE) {
                    String text = shrinkSpace(node.getTextContent());
                    if (text.length() > 0) {
                        _adviceForXML.add("Undocumented Tag has Text: " + text + " @" + getPathOfNode(e) + " in [" + _file + "]");
                        target._textContext = text;
                    }
                }
            }
            else if (target._definition.hasTextContents()) {
                if (node.getNodeType() == Node.TEXT_NODE) {
                    String text = shrinkSpace(node.getTextContent());
                    if (text.length() > 0) {
                        target._textContext = text;
                    }
                }
            }
        }
        for (Element child : listChild) {
            String name = child.getNodeName();

            if (target._definition == null) {
                _adviceForXML.add("Undocumented Tag has SubTag : " + name + " @" + getPathOfNode(e) + " in [" + _file + "]");
                CCXMLNode childTag = new CCXMLNode(target, null, null);
                target._listChildTags.add(childTag);

                tryRead(childTag, child);
            }
            else {
                CCXMLDefTag childDefinition = target._definition.getTag(name);
                if (childDefinition != null) {
                    CCXMLNode childTag = new CCXMLNode(target, childDefinition.getName(), childDefinition);
                    target._listChildTags.add(childTag);

                    tryRead(childTag, child);
                }
                else {
                    _adviceForXML.add("Undocumented Tag: " + name + " @" + getPathOfNode(e) + " in [" + _file + "]");
                    CCXMLNode childTag = new CCXMLNode(target, null, null);
                    target._listChildTags.add(childTag);

                    tryRead(childTag, child);
                }
            }
        }
    }
    
    public void dump() {
        System.out.println("XMLFile [" + _file + "]");
        System.out.println(getAdviceForXML());
        for (int i = 0; i < _arrayModuleData.size(); ++ i) {
            System.out.println("Dumping " + (i+1) + " / " + _arrayModuleData.size());
            dump(0, _arrayModuleData.get(i));
        }
    }

    public void dump(int indent, CCXMLNode module) {
        StringBuffer strIndent = new StringBuffer();
        for (int i = 0; i < indent; ++i) {
            strIndent.append("....");
        }

        StringBuffer strAttributes = new StringBuffer();
        for (CCXMLAttribute attr : module._listAttributes) {
            strAttributes.append("[");
            strAttributes.append(attr.getName());
            strAttributes.append("=");
            strAttributes.append(attr.getValue());
            strAttributes.append("]");
        }

        System.out.println(strIndent.toString() + "\"" + module._name + "\"" + strAttributes.toString());

        if (module._definition.hasTextContents()) {
            System.out.println(strIndent.toString() + "=" + module._textContext);
        }

        for (CCXMLNode child : module._listChildTags) {
            dump(indent + 1, child);
        }
    }

    public static String shrinkSpace(String original) {
        if (original == null) {
            return null;
        }

        int start = 0;
        int end = original.length();

        if (start < end) {
            char c = original.charAt(start);
            if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
                start++;
            }
            c = original.charAt(end - 1);
            if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
                end--;
            }
        }

        if (start == 0 && end == original.length() && original.indexOf('\n') < 0) {
            return original;
        }

        StringBuffer buf = new StringBuffer();
        for (int i = start; i < end; ++i) {
            char c = original.charAt(i);
            if (c == '\n') {
                buf.append("\\n");
            } else {
                buf.append(c);
            }
        }
        return buf.toString();
    }

    public static String getPathOfNode(Node node) {
        LinkedList path = new LinkedList();

        while (node != null) {
            if (node.getParentNode() == null) {
                if (node.getNodeName().equals("#document")) {
                    break;
                }
            }
            path.addFirst(node.getNodeName());
            node = node.getParentNode();
        }

        return path.toString();
    }
    
    public String getAdviceForXML() {
        if (_loadError != null) {
            if (_loadError instanceof SAXParseException) {
                SAXParseException saxe = (SAXParseException)_loadError;
                return "XML Error at Line " + saxe.getLineNumber() + ", Column " + saxe.getColumnNumber() + "\n -> " + saxe.getMessage();
            }
            return "Error: " + _loadError.getMessage();
        }
        if (countModule() == 0) {
            return "Error, This XML Don't have '<Module>'";
        }
        StringBuffer str = new StringBuffer();
        for (String text : _adviceForXML) {
            if (str.length() > 0) {
                str.append("\n");
            }
            str.append(text);
        }
        return str.toString();
    }
}
