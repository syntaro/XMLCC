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
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Syntarou YOSHIDA
 */
public class CCDriverManager {
    public static void receiveShort(CCDriver driver, int driverPort, int dword) {
        
    }
    
    public static void receiveBytes(CCDriver driver, int driverPort, byte[] data) {
    }
    
    private final ArrayList<CCDriver> _listAvailableDriver;
    private final List<CCDriver> _unmodList;
    
    public CCDriverManager() {
        _listAvailableDriver = new ArrayList<>();
        
        CCDriver[] checkTarget = {
            CCDriver_Java._instance,
            CCDriver_UWP._instance
        };
        
        for (CCDriver driver : checkTarget) {
            if (driver.isUsable()) {
                _listAvailableDriver.add(driver);
            }
        }
        _unmodList = Collections.unmodifiableList(_listAvailableDriver);
    }
    
    public List list() {
       return _unmodList;
    }
}
