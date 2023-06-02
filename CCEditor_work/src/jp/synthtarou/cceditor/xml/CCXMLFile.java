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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import jp.synthtarou.cceditor.common.CCWrapData;
import jp.synthtarou.cceditor.common.CCWrapDataList;
import jp.synthtarou.cceditor.xml.definition.CCXMLAttributeRule;
import jp.synthtarou.cceditor.xml.definition.CCXMLTagRule;
import jp.synthtarou.cceditor.xml.definition.CCXMLRule;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author Syntarou YOSHIDA
 */
public class CCXMLFile {

    final ArrayList<CCXMLNode> _arrayModuleData = new ArrayList<>();
    final File _file;
    final ArrayList<CCXMLNode> _listWarning = new ArrayList<>();
    final Exception _loadError;

    public String toString() {
        return _file.toString();
    }

    public boolean isLoaded() {
        return (_loadError == null) && countModule() > 0;
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
            String nameAttr = tag._listAttributes.valueOfName("name");
            String idAttr = tag._listAttributes.valueOfName("id");

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
        CCXMLRule.dumpRules();

        File moduleDirectory = new File("C:/Domino144/Module");
        for (File file : moduleDirectory.listFiles()) {
            if (file.isFile() == false) {
                continue;
            }
            String name = file.getName();
            name = name.toLowerCase();
            if (!name.contains("8850")) {
                continue;
            }
            if (name.endsWith(".xml")) {
                if (file.canRead()) {
                    System.out.println("XMLFile [" + name + "]");

                    CCXMLFile f2 = new CCXMLFile(file);
                    f2.dumpWarning();
                    f2.dumpXML();
                }
            }
        }
    }

    public CCXMLFile(File file) {
        _file = file;

        Element docElement;

        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            InputStream stream = new FileInputStream(file);
            CCDocumentHandler handler = new CCDocumentHandler();

            try {
                InputSource source = new InputSource(stream);
                saxParser.parse(source, handler);
            } finally {
                stream.close();
            }

            _arrayModuleData.clear();

            for (CCXMLNode moduleNode : handler._document._listChildTags) {
                CCXMLRule rule = CCXMLRule.getInstance();

                fillRules(moduleNode, rule.getModuleDataTag());
                _arrayModuleData.add(moduleNode);
            }
        } catch (ParserConfigurationException ex) {
            _loadError = ex;
            return;
        } catch (SAXException ex) {
            _loadError = ex;
            return;
        } catch (IOException ex) {
            _loadError = ex;
            return;
        }
        _loadError = null;
    }

    public void fillRules(CCXMLNode target, CCXMLTagRule targetRule) {
        target._warningText = null;
        
        if (targetRule == null) {
            target._warningText = " this is undocumented ";
            _listWarning.add(target);
            return;
        }
 
        StringBuffer warning = new StringBuffer();
        ArrayList<String> missingAttr = new ArrayList<>();
        ArrayList<String> undocumentedAttr = new ArrayList<>();
        
        for (CCXMLAttributeRule ruleAttr : targetRule.listAttributes()) {
            if (ruleAttr.isMust()) {
                if (target._listAttributes.indexOfName(ruleAttr.getName()) < 0) {
                    missingAttr.add(ruleAttr.getName());
                }
            }
        }
        if (missingAttr.size() > 0) {
            warning.append(" missing attributes [" + missingAttr +"]");
        }
        for (CCWrapData<String> keyValue :  target._listAttributes) {
            if (targetRule.getAttribute(keyValue.name) == null) {
                undocumentedAttr.add(keyValue.name + "=" + keyValue.value);
            }
        }

        if (undocumentedAttr.size() > 0) {
            warning.append(" undocumented attributes [" + undocumentedAttr +"]");
        }

        ArrayList<String> undocumentedTag = new ArrayList<>();
        
        for (CCXMLNode child : target._listChildTags) {
            CCXMLTagRule childRule = targetRule.findChildRule(child._name);
            if (childRule == null) {
                undocumentedTag.add(child._name);
            }
            fillRules(child, childRule);
        }

        if (undocumentedTag.size() > 0) {
            warning.append(" undocumented tags [" + undocumentedTag +"]");
        }
        if (warning.length() > 0) {
            target._warningText = warning.toString();
            _listWarning.add(target);
        }
    }

    public void dumpWarning() {
        System.out.println(getAdviceForXML());
    }

    public void dumpXML() {
        for (int i = 0; i < _arrayModuleData.size(); ++i) {
            System.out.println("Dumping " + (i + 1) + " / " + _arrayModuleData.size());
            dumpXMLSub(0, _arrayModuleData.get(i));
        }
    }

    public void dumpXMLSub(int indent, CCXMLNode module) {
        StringBuffer strIndent = new StringBuffer();
        for (int i = 0; i < indent; ++i) {
            strIndent.append("....");
        }

        StringBuffer strAttributes = new StringBuffer();
        for (CCWrapData<String> attr : module._listAttributes) {
            strAttributes.append("[");
            strAttributes.append(attr.name);
            strAttributes.append("=");
            strAttributes.append(attr.value);
            strAttributes.append("]");
        }

        System.out.println(strIndent.toString() + "\"" + module._name + "\"" + strAttributes.toString());

        if (module._rule != null && module._rule.hasTextContents()) {
            System.out.println(strIndent.toString() + "=" + module._textContext);
        }

        for (CCXMLNode child : module._listChildTags) {
            dumpXMLSub(indent + 1, child);
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
            if (start == 0 && end == original.length() /* && original.indexOf('\n') < 0 */) {
                return original;
            }

            if (start == end) {
                return "";
            }
            if (start < end) {
                return original.substring(start, end - 1);
            }
        }

        return original;
        /*
        StringBuffer buf = new StringBuffer();
        for (int i = start; i < end; ++i) {
            char c = original.charAt(i);
            if (c == '\n') {
                buf.append("\\n");
            } else {
                buf.append(c);
            }
        }
        return buf.toString();*/
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

    public List<CCXMLNode> listWarning() {
        return Collections.unmodifiableList(_listWarning);
    }
    
    public String getAdviceForXML() {
        if (_loadError != null) {
            if (_loadError instanceof SAXParseException) {
                SAXParseException saxe = (SAXParseException) _loadError;
                return "Error [" + saxe.getLineNumber() + ", " + saxe.getColumnNumber() + "] -> " + saxe.getMessage();
            }
            return "Error: " + _loadError.getMessage();
        }
        if (countModule() == 0) {
            return "Error, This XML Don't have '<Module>'";
        }
        StringBuffer str = new StringBuffer();
        for (CCXMLNode node : _listWarning) {
            if (str.length() > 0) {
                str.append("\n");
            }
            str.append("" + node._lineNumber + ", " + node._columnNumber + ": " + node._warningText);
        }
        return str.toString();
    }
}
