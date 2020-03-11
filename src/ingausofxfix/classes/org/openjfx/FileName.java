/*
 * Copyright (C) 2020 Chris Good
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

package org.openjfx;

/**
 * Splits a full file string into
 *      path
 *      filename (without extension)
 *      extension
 * 
 * 16/05/2016 filename() : Return file name if no extensionSeparator to fix
 *              index out of bounds.
 *
 * @author goodc
 */
public class FileName {
    private final String fullPath;
    private final char pathSeparator, 
                 extensionSeparator;

    // constructor)
    public FileName(String str, char sep, char ext) {
        fullPath = str;
        pathSeparator = sep;
        extensionSeparator = ext;
    }

    /**
     * @return file extension
     */
    public String extension() {
        int dot = fullPath.lastIndexOf(extensionSeparator);
        return fullPath.substring(dot + 1);
    }

    /**
     * @return filename without extension
     */
    public String filename() {
        String fname;
        int sep = fullPath.lastIndexOf(pathSeparator);
        if (sep > -1) {
            // there IS a pathSeparator in the fullPath
            fname = fullPath.substring(sep + 1);
        } else {
            fname = fullPath;
        }
        int dot = fname.lastIndexOf(extensionSeparator);
        if (dot > -1) {
            // there IS an extensionSeparator 
            return fname.substring(0, dot);
        }
        return fname;
    }

    /**
     * @return full path without filename
     */
    public String path() {
        int sep = fullPath.lastIndexOf(pathSeparator);
        return fullPath.substring(0, sep);
    }
}
