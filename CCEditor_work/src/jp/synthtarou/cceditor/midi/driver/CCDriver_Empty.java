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

import java.util.ArrayList;

/**
 *
 * @author Syntarou YOSHIDA
 */
public class CCDriver_Empty implements CCDriver {
    static CCDriver_Empty _instance = new CCDriver_Empty();

    public static CCDriver_Empty getInstance() {
        return _instance;
    }

    @Override
    public String getDriverName() {
        return "empty";
    }

    ArrayList<String> _inputNames = new ArrayList();
    ArrayList<String> _outputNames = new ArrayList();
    
    public int InputAddDevice(String name) {
        int x = _inputNames.indexOf(name);
        if (x >= 0) {
            return x;
        }
        _inputNames.add(name);
        return _inputNames.size() - 1;
    }

    public int OuputAddDevice(String name) {
        int x = _outputNames.indexOf(name);
        if (x >= 0) {
            return x;
        }
        _outputNames.add(name);
        return _outputNames.size() - 1;
    }

    @Override
    public boolean isUsable() {
        return true;
    }

    @Override
    public void StartLibrary() {
    }

    @Override
    public int InputDevicesRoomSize() {
        return _inputNames.size();
    }

    @Override
    public String InputDeviceName(int device) {
        return _inputNames.get(device);
    }

    @Override
    public String InputDeviceId(int device) {
        // no used
        return "";
    }

    @Override
    public boolean InputDeviceIsOpen(int device) {
        return false;
    }

    @Override
    public boolean InputDeviceOpen(int device, long timeout) {
        return false;
    }

    @Override
    public void InputDeviceClose(int device) {
    }

    @Override
    public int OutputDevicesRoomSize() {
        return _outputNames.size();
    }

    @Override
    public String OutputDeviceName(int device) {
        return _outputNames.get(device);
    }

    @Override
    public String OutputDeviceId(int device) {
        //not used
        return  "";
    }

    @Override
    public boolean OutputDeviceIsOpen(int device) {
        return false;
    }

    @Override
    public boolean OutputDeviceOpen(int device, long timeout) {
        return false;
    }

    @Override
    public void OutputDeviceClose(int device) {
    }

    @Override
    public boolean OutputShortMessage(int port, int message) {
        return false;
    }

    @Override
    public boolean OutputLongMessage(int port, byte[] data) {
        return false;
    }
    
}
