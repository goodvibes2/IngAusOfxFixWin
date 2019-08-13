/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
