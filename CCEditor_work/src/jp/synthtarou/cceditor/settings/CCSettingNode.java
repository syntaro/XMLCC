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
package jp.synthtarou.cceditor.settings;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import jp.synthtarou.cceditor.common.CCUtilities;

/**
 *
 * @author Syntarou YOSHIDA
 */
public class CCSettingNode {
    protected CCSetting _setting;
    
    public String toString() {
        return "[" + _path + "=" + _value + "(" + _children.size() + ")]";
    }

    public void register(String name) {
        try {
            CCSettingPathList p = _path.clone();
            p.addAll(CCSettingPathList.parsePath(name));
            _setting.register(p);
        }catch(CCSettingException e) {
            e.printStackTrace();
        }
    }

    public boolean isRegistered(String name) {
        try { 
            CCSettingPathList p = _path.clone();
            p.addAll(CCSettingPathList.parsePath(name));
            return _setting.isRegistered(p);
        }catch(CCSettingException e) {
            e.printStackTrace();
            return false;
        }
    }

    private final CCSettingPathList _path;
    public String _value;
 
    protected CCSettingPathList getPath() {
        return _path;
    }
    
    public String getName() {
        if (_path.size() > 0) {
            return _path.get(_path.size() - 1);
        }
        return null;
    }
    
    public boolean isInteger() {
        if (_path.size() > 0) {
            return CCUtilities.isInteger(_path.get(_path.size() -1));
        }
        return false;
    }
    
    public ArrayList<CCSettingNode> _children;
    
    public CCSettingNode(CCSetting setting, CCSettingPathList path, String name) {
        _children = new ArrayList();
        _setting = setting;
        _path = new CCSettingPathList();
        if (path != null) {
            _path.addAll(path);
        }
        if (name != null) {
            _path.add(name);
        }
    }
    
    public int size() {
        return _children.size();
    }

    protected  CCSettingNode childByIndex(int index) {
        if (_children.size() >= index - 1) {
            return _children.get(index);
        }
        return null;
    }

    protected  CCSettingNode childByKey(String name) {
        for (CCSettingNode e : _children) {
            if (name.equals(e.getName())) {
                return e;
            }
        }
        return null;
    }
    
    public boolean isEmpty() {
        if (_setting.isRegistered(_path)) {
            if (_value != null) {
                return false;
            }
        }
        for (CCSettingNode node : _children) {
            if (node.isEmpty() == false) {
                return false;
            }
        }
        return true;
    }
        
    public void clearValues() {
        _value = null;
        _children = new ArrayList();
    }
    
    public CCSettingNode findNode(String name) {
        try {
            CCSettingPathList path = CCSettingPathList.parsePath(name);
            CCSettingNode node = this;
            for (String text : path) {
                CCSettingNode e = node.childByKey(text);
                if (e == null) {
                    return null;
                }
                node = e;
            }
            return node;
        }catch(CCSettingException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<CCSettingNode> findNumbers() {
        ArrayList<CCSettingNode> retList = new ArrayList();
        for (CCSettingNode node : _children) {
            if (node.isInteger()) {
                retList.add(node);
            }
        }
        return retList;
    }

    public String getSetting(String name) {
        CCSettingNode node = findNode(name);
        if (node != null) {
            return node._value;
        }
        return null;
    }

    public int getSettingAsInt(String name, int defval) {
        CCSettingNode node = findNode(name);
        if (node != null) {
            String text = node._value;
            try {
                return Integer.parseInt(text);
            }catch(NumberFormatException e) {
                e.printStackTrace();
                return defval;
            }
        }
        return defval;
    }
    
    public boolean getSettingAsBoolean(String name, boolean defval) {
        if (defval) {
            return getSettingAsInt(name, 1) != 0;
        }else {
            return getSettingAsInt(name, 0) != 0;
        }
    }
    
    public boolean setSetting(String name, String value) throws CCSettingException {
        if (isRegistered(name) == false) {
            throw new CCSettingException("setSetting Not registered " + name + " = " + value);
        }

        CCSettingPathList child = CCSettingPathList.parsePath(name);
        CCSettingPathList current = _path;

        CCSettingNode node = this;
        for (String text : child) {
            CCSettingNode e = node.childByKey(text);
            if (e == null) {
                e = new CCSettingNode(_setting, current, text);
                node._children.add(e);
            }
            node = e;
            current = e._path;
        }
        node._value = value;
        return true;
    }
    
    public boolean havingName(String name) {
        return childByKey(name) != null;
    }

    protected void recuesiveDump(Writer writer) throws IOException {
        TreeSet reg = new TreeSet();
        recuesiveDump(writer, 0, reg);
    }

    protected void recuesiveDump(Writer writer, int indent, TreeSet registered) throws IOException {
        StringBuffer text = new StringBuffer();
        boolean wasnum = false;
        boolean first = true;
        for (String e : _path) {
            if (CCUtilities.isInteger(e)) {
                if (wasnum) {
                }else {
                }
                text.append("[" + e + "]");
                wasnum = true;
                first = false;
            }else {
                if (!first) {
                    if (wasnum) {
                        text.append(".");
                    }else {
                        text.append(".");
                    }
                }
                text.append(e);
                wasnum = false;
                first = false;
            }
        }
        if (_setting.isRegistered(_path)) {
            if (_value == null) {
                writer.write(text + "=" + "" + "\n");
            }else {
                writer.write(text + "=" + _value + "\n");
            }
        }else if (_value != null) {
            System.out.println("not registered " + text + "=" + _value);
        }

        for (CCSettingNode e : _children) {
            e.recuesiveDump(writer, indent + 1, registered);
        }
    }
}
