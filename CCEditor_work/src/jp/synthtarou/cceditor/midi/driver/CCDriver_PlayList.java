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
package jp.synthtarou.cceditor.midi.driver;

/**
 *
 * @author Syntarou YOSHIDA
 */
public class CCDriver_PlayList implements CCDriver {
    public static final CCDriver_PlayList _instance = new CCDriver_PlayList();
   
    public boolean isUsable() {
        return true;
    }

    @Override
    public String getDriverName() {
        return "playlist";
    }

    public void StartLibrary() {
    }

    public int InputDevicesRoomSize() {
        return 1;
    }
    
    public String InputDeviceName(int x) {
        return "<PlayList>";
    }
    
    public String InputDeviceId(int x) {
        return "PlayL+ist>";
    }

    public boolean InputDeviceIsOpen(int x) {
        return true;
    }

    public boolean InputDeviceOpen(int device, long timeout) {
        return true;
    }
    
    public void InputDeviceClose(int x) {
    }

    public int OutputDevicesRoomSize() {
        return 0;
    }
    
    public String OutputDeviceName(int x) {
        return null;
    }
    
    public String OutputDeviceId(int x) {
        return null;
    }

    public boolean OutputDeviceIsOpen(int x) {
        return false;
    }

    public boolean OutputDeviceOpen(int device, long timeout) {
        return false;
    }    
    
    public void OutputDeviceClose(int x) {
    }
    
    public boolean OutputShortMessage(int x, int message) {
        return false;
    }
    
    public boolean OutputLongMessage(int x, byte[] data) {
        return false;
    }
}
