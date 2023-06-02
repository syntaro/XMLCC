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

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Syntarou YOSHIDA
 */
public class CCXMLTreeModel implements TreeModel {
    final CCXMLNode _root;

    public CCXMLTreeModel(CCXMLNode ddfile) {
        _root = ddfile;
    }

    @Override
    public Object getRoot() {
        return _root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        CCXMLNode it =  (CCXMLNode)parent;
        return it._listChildTags.get(index);
    }

    @Override
    public int getChildCount(Object parent) {
        CCXMLNode it =  (CCXMLNode)parent;
        return it._listChildTags.size();
    }

    @Override
    public boolean isLeaf(Object node) {
        CCXMLNode it =  (CCXMLNode)node;
        if (it._rule != null) {
            if (it._rule.listChildTags() == null
              ||it._rule.listChildTags().size() == 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        return;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        CCXMLNode it =  (CCXMLNode)parent;
        return it._listChildTags.indexOf(child);
    }
    
    @Override
    public void addTreeModelListener(TreeModelListener l) {
        return;
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        return;
    }
}
