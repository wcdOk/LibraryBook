package com.wcdok.comp_strengthen.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author: wcd
 * @email: wcdwangyi@163.com
 * @date: 7/22/21 1:56 PM
 * @desc:
 */
public class Zip {

    public static void unZip(File zip, File dir) {
        ZipFile zipFile = null;
        try {
            deleteFile(dir);
             zipFile = new ZipFile(zip);
            //zip 文件中每一个条目
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                if (zipEntry == null) {
                    continue;
                }
                //zip中 文件/目录
                String name = zipEntry.getName();
                //删除原来的签名文件
                if (name.startsWith("META-INF/")) {
                    continue;
                }
//                if(name.equals("META-INF/CERT.RSA")||name.equals("META-INF/CERT.SF") ||name.equals("META-INF/MANIFEST.MF")){
//                    continue;
//                }
                //空目录不管
                if (!zipEntry.isDirectory()) {
                    File file = new File(dir, name);
                    //创建目录
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    FileOutputStream fos = null;
                    InputStream is = null;

                    try {
                        fos = new FileOutputStream(file);
                        is = zipFile.getInputStream(zipEntry);
                        byte[] buffer = new byte[2048];
                        int len;
                        while ((len = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }

                    } finally {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(zipFile!=null){
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }




    public static void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                deleteFile(f);
            }
        } else {
            file.delete();
        }
    }
}
