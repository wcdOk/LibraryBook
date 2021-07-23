package com.wcdok.lib_strengthen;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author: wcd
 * @email: wcdwangyi@163.com
 * @date: 7/22/21 2:40 PM
 * @desc:
 */
public class Utils {
    public static byte[] getBytes(File file) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file,"r");
        byte[] buffer  = new byte[(int)randomAccessFile.length()];
        randomAccessFile.readFully(buffer);
        buffer.clone();
        return buffer;

    }

}
