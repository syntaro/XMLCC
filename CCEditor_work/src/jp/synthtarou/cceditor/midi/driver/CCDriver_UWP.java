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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package jp.synthtarou.cceditor.midi.driver;

import jp.synthtarou.midimixer.windows.MXLIB01UWPMidi;

/**
 *
 * @author Syntarou YOSHIDA
 */
public class CCDriver_UWP implements CCDriver {
    public static final CCDriver_UWP _instance = new CCDriver_UWP();

    @Override
    public String getDriverName() {
        return "UWP";
    }

    MXLIB01UWPMidi windows10;

    public CCDriver_UWP() {
        windows10 = new MXLIB01UWPMidi();
    }

    public String driverSuffix() {
        return "(UWP)";
    }

    @Override
    public boolean isUsable() {
        return  windows10.isDLLAvail();
    }

    @Override
    public void StartLibrary() {
        if (isUsable()) {
            windows10.StartLibrary();
        }
    }

    @Override
    public int InputDevicesRoomSize() {
        if (!isUsable()) {
            return 0;
        }
        return windows10.InputDevicesRoomSize();
    }

    @Override
    public String InputDeviceName(int device) {
        if (!isUsable()) {
            return "";
        }
        return windows10.InputDeviceName(device) + "(UWP)";
    }

    @Override
    public String InputDeviceId(int device) {
        if (!isUsable()) {
            return "";
        }
        return windows10.InputDeviceId(device);
    }

    @Override
    public boolean InputDeviceIsOpen(int device) {
        if (!isUsable()) {
            return false;
        }
        return windows10.InputIsOpen(device);
    }

    @Override
    public boolean InputDeviceOpen(int device, long timeout) {
        if (!isUsable()) {
            return false;
        }
        return  windows10.InputOpen(device, timeout);
    }
    
    @Override
    public void InputDeviceClose(int device) {
        if (!isUsable()) {
            return;
        }
        windows10.InputClose(device);
    }

    @Override
    public int OutputDevicesRoomSize() {
        if (!isUsable()) {
            return 0;
        }
        return windows10.OutputDevicesRoomSize();
    }

    @Override
    public String OutputDeviceName(int device) {
        if (!isUsable()) {
            return "";
        }
        return windows10.OutputDeviceName(device) + "(UWP)";
    }

    @Override
    public String OutputDeviceId(int device) {
        if (!isUsable()) {
            return "";
        }
        return windows10.OutputDeviceId(device);
    }

    @Override
    public boolean OutputDeviceIsOpen(int device) {
        if (!isUsable()) {
            return false;
        }
        return windows10.OutputIsOpen(device);
    }

    @Override
    public boolean OutputDeviceOpen(int device, long timeout) {
        if (!isUsable()) {
            return false;
        }
        return windows10.OutputOpen(device, timeout);
    }

    @Override
    public void OutputDeviceClose(int device) {
        if (!isUsable()) {
            return;
        }
        windows10.OutputClose(device);
    }

    @Override
    public boolean OutputShortMessage(int device, int message) {
        if (!isUsable()) {
            return false;
        }
        return windows10.OutputShortMessage(device, message);
    }

    @Override
    public boolean OutputLongMessage(int device, byte[] message) {
        if (!isUsable()) {
            return false;
        }
        return windows10.OutputLongMessage(device, message);
    }
}
