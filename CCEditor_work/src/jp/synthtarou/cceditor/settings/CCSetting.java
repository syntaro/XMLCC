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

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;
import jp.synthtarou.cceditor.common.CCUtilities;

/**
 *
 * @author Syntarou YOSHIDA
 */
public class CCSetting {
    public static void main(String[] args) {
        CCSetting root = new CCSetting("test");
         
        root.register("base.attribute");
        root.register("base.attribute[].text");
        root.register("base[b]");
        root.register("base[1]");
        root.register("base.[].position");
        
        root.setSetting("base", "123");
        root.setSetting("base.attribute[1].text", "a1");
        root.setSetting("base.attribute[2].text", "a2");
        root.setSetting("base.attribute[3].text", "a3");
        
        root.setSetting("base[12].position", "12");
        root.setSetting("base.[13].position", "13");
        root.setSetting("base[14]position", "14");
        
        try {
            root.dump(new OutputStreamWriter(System.out));
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    protected File _settingFile;
    static ArrayList<CCSetting> everySetting = new ArrayList();

    public File getFile() {
        return _settingFile;
    }
    
    public CCSetting(File file) {
        this(file, true);
    }

    public CCSetting(File file, boolean addToEvery) {
        _settingFile = file;
        //System.out.println("CCSetting::CCSetting " + _settingFile + " : addToEvenry " + addToEvery);
        _registered = new TreeSet(comparatorForRegister);
        if (addToEvery) {
            everySetting.add(this);
        }
    }

    public CCSetting(String name) {
        this(name, true);
    }

    public CCSetting(String name, boolean addToEvery) {
        this(new File(CCUtilities.getSettingDirectory(), name +".ini"), addToEvery);
    }
    
    public static void saveEverySettingToFile() {
         for (CCSetting setting : everySetting) {
            try {
                
                setting.writeSettingFile();
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
   }
   
    protected static class Detail {
        String key;
        String value;
    }
    
    protected static class DetailArray {
        ArrayList<Detail> list = new ArrayList();
    }
    
    private CCSettingTarget _target = null;
    private String _targetName;
    
    public void setTarget(CCSettingTarget target) {
        if (target == null) {
            throw new NullPointerException();
        }
        _target = target;
        _target.prepareSettingFields(this);
        _targetName = getClassName(_target.getClass());
        //System.out.println("CCSetting " + _targetName +" : setTarget");
    }
    
    public static String getClassName(Class cls) {
        String name = cls.getName();
        if (name != null) {
            int x = name.lastIndexOf('.');
            if (x >= 0) {
                name = name.substring(x+1);
            }
        }
        return name;
    }

    CCSettingNode _root = new CCSettingNode(this, null, null);
    
    public boolean readSettingFile() {
        //System.out.println("CCSetting " + _targetName +" : readSettingFile " + _settingFile);
        if (_registered.size() == 0) {
            return false;
        }
        InputStream fin = null;
        try {
            _root.clearValues();
            fin = new FileInputStream(_settingFile);
            System.out.println("reading " + _settingFile + " = " + _targetName);
            CCLineReader reader = new CCLineReader(fin, "utf-8");
            while(true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                if (line.length() == 0) {
                    continue;
                }
                char first = line.charAt(0);
                if (first == '#' || first == '/') {
                    continue;
                }
                int index = line.indexOf('=');
                if (index < 0) {
                    continue;
                }
                String key = line.substring(0, index);
                String value = line.substring(index + 1);
                _root.setSetting(key, value);
            }
        }catch(FileNotFoundException e) {
            System.out.println("First Time for [" + _settingFile + "]");
        }catch(CCSettingException e) {
            e.printStackTrace();
        }catch(IOException e) {
            e.printStackTrace();
        }finally {
            if (fin != null) {
                try {
                    fin.close();
                }catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (_target != null) {
            _target.afterReadSettingFile(this);
        }
        return true;
    }
    
    public boolean writeSettingFile() {
        //System.out.println("CCSetting " + _targetName +" : writeSettingFile " + _settingFile);
        if (_target != null) {
            _root.clearValues();
            _target.beforeWriteSettingFile(this);
        }
        File temporary = CCUtilities.createTemporaryFile(_settingFile);
        CCLineWriter writer = null;
        System.out.println("writing " + _settingFile + " = " + _targetName);
        
        try {
            writer = new CCLineWriter(temporary, "utf-8");
            
            dump(writer.getWriter());

            writer.close();

            File backup = CCUtilities.safeRenameToBackup(_settingFile);
            temporary.renameTo(_settingFile);
            
            if (backup != null) {
                if (CCUtilities.compareFileText(backup, _settingFile) == 0) {
                    try {
                        backup.delete();
                    }catch(Exception e) {
                    }
                }else {
                    try {
                        Desktop.getDesktop().moveToTrash(backup);
                    }catch(Exception e) {
                    }
                }
            }

        }catch(IOException ioe) {
            ioe.printStackTrace();
            if (writer != null) {
                try {
                    writer.close();
                }catch(Exception e) {
                    e.printStackTrace();
                }
                temporary.delete();
            }
            return false;
        }
        
        return true;
    }
    
    public void clearValue() {
        _root.clearValues();
    }

    public String getSetting(String name) {
        return _root.getSetting(name);
    }

    public int getSettingAsInt(String name, int defvalue) {
        return _root.getSettingAsInt(name, defvalue);
    }

    public boolean getSettingAsBoolean(String name, boolean defvalue) {
        return _root.getSettingAsBoolean(name, defvalue);
    }
    
    public boolean setSetting(String name, String value) {
        try {
           return _root.setSetting(name, value);
        }catch(CCSettingException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean isEmpty() {
        return _root.isEmpty();
    }

    public boolean setSetting(String name, int value) {
        return setSetting(name, String.valueOf(value));
    }
    
    public boolean setSetting(String name, boolean value) {
        if (value) {
            return setSetting(name, "1");
        }else {            
            return setSetting(name, "0");
        }
    }
    
    public boolean havingName(String name) {
        return _root.childByKey(name) != null;
    }
    
    public void dump(Writer writer) throws IOException {
        _root.recuesiveDump(writer);
        writer.flush();
    }
    
    public void register(String name) {
        try {
            CCSettingPathList p = _root.getPath().clone();
            p.addAll(CCSettingPathList.parsePath(name));
            register(p);
        }catch(CCSettingException e) {
            e.printStackTrace();
        }
    }

    public void register(CCSettingPathList path) {
        _registered.add(path);
    }

    public boolean isRegistered(String name) {
        CCSettingPathList p = _root.getPath().clone();
        try {
            p.addAll(CCSettingPathList.parsePath(name));
            return isRegistered(p);
        }catch(CCSettingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isRegistered(CCSettingPathList path) {
        return _registered.contains(path);
    }

    protected TreeSet<CCSettingPathList> _registered;

    protected Comparator comparatorForRegister = new Comparator() {
        public int compare(Object o1, Object o2) {
            CCSettingPathList p1 = (CCSettingPathList)o1;
            CCSettingPathList p2 = (CCSettingPathList)o2;
            int length = Math.min(p1.size(), p2.size());
            for (int x = 0; x < length; ++ x) {
                String str1 = p1.get(x);
                String str2 = p2.get(x);
                
                if (CCUtilities.isInteger(str1)) {
                    str1 = "0";
                }
                if (CCUtilities.isInteger(str2)) {
                    str2 = "0";
                }
                
                int d = str1.compareTo(str2);
                if (d != 0) {
                    return d;
                }
            }
            if (p1.size() < p2.size()) {
                return -1;
            }else if (p1.size() > p2.size()) {
                return 1;
            }
            
            return 0;
        }
    };
    
    public ArrayList<CCSettingNode> findByPath(String name) {
        CCSettingPathList path = null;
        try {
            path = CCSettingPathList.parsePath(name);
        }catch(CCSettingException e) {
            e.printStackTrace();
            return null;
        }
    
        ArrayList<CCSettingNode> seeking = new ArrayList();
        seeking.add(_root);

        for (String text : path) {
            ArrayList<CCSettingNode> hit = new ArrayList();
            boolean isInteger = CCUtilities.isInteger(text);
            
            for (CCSettingNode parent : seeking) {
                int count = parent.size();
                for (int x = 0; x < count; ++ x) {
                    CCSettingNode child = parent.childByIndex(x);
                    if (isInteger) {
                        if (child.isInteger()) {
                            hit.add(child);
                        }
                    }else {
                        if (child.getName().equalsIgnoreCase(text)) {
                            hit.add(child);
                        }
                    }
                }
            }
            
            seeking = hit;
        }

        return seeking;
    }
}
