package com.wcdok.lib_strengthen;

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
    /**
     * 压缩目录为zip
     * @param dir 待压缩目录
     * @param zip 输出的zip文件
     * @throws Exception
     */
    public static void zip(File dir, File zip) throws Exception {
        ZipOutputStream zos = null;
        try {
            zip.delete();
            // 对输出文件做CRC32校验：
            // CRC32:CRC本身是“冗余校验码”的意思，CRC32则表示会产生一个32bit（8位十六进制数）的校验值。
            // 由于CRC32产生校验值时源数据块的每一个bit（位）都参与了计算，所以数据块中即使只有一位发生了变化，也会得到不同的CRC32值.
            CheckedOutputStream cos = new CheckedOutputStream(new FileOutputStream(zip), new CRC32());
            zos = new ZipOutputStream(cos);
            //压缩
            compress(dir, zos, "");
        } finally {
            if(zos!=null){
                zos.flush();
                zos.close();
            }

        }

    }
    /**
     * 添加目录/文件 至zip中
     * @param srcFile 需要添加的目录/文件
     * @param zos   zip输出流
     * @param basePath  递归子目录时的完整目录 如 lib/x86
     * @throws Exception
     */
    private static void compress(File srcFile, ZipOutputStream zos, String basePath) throws Exception {
        if (srcFile.isDirectory()) {
            File[] files = srcFile.listFiles();
            for (File file : files) {
                // zip 递归添加目录中的文件
                compress(file, zos, basePath + srcFile.getName() + "/");
            }
        } else {
            compressFile(srcFile, zos, basePath);
        }
    }

    private static void compressFile(File file, ZipOutputStream zos, String dir) throws Exception {
        // temp/lib/x86/libdn_ssl.so
        String fullName = dir + file.getName();
        // 需要去掉temp
        String[] fileNames = fullName.split("/");
        //正确的文件目录名 (去掉了temp)
        StringBuffer sb = new StringBuffer();
        if (fileNames.length > 1){
            for (int i = 1;i<fileNames.length;++i){
                sb.append("/");
                sb.append(fileNames[i]);
            }
        }else{
            sb.append("/");
        }
        //添加一个zip条目
        ZipEntry entry = new ZipEntry(sb.substring(1));
        zos.putNextEntry(entry);
        //读取条目输出到zip中
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            int len;
            byte data[] = new byte[2048];
            while ((len = fis.read(data, 0, 2048)) != -1) {
                zos.write(data, 0, len);
            }
        } finally {
            if(fis!=null){
                fis.close();
            }
        }
        zos.closeEntry();
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
