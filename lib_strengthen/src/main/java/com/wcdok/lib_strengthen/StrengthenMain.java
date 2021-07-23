package com.wcdok.lib_strengthen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;

/**
 * @author: wcd
 * @email: wcdwangyi@163.com
 * @date: 7/23/21 2:05 PM
 * @desc: 需要自己修改路径和签名文件
 */
public class StrengthenMain {
    public static void main(String[] args) throws Exception {
        /***
         * 1.[1~3]步，我们做的操作是：将comp_strengthen的aar打包成dex文件,生成：strengthen/shell/classes.dex
         * 2.[4~5]步，将老apk进行解压：解压后存放路径是：strengthen/unzip_apk
         * 3.[6~7]步，将解压后的apk下的dex文件进行，加密，加密后的dex，命名为secret-classesN.dex
         * 4.[8]步，将壳strengthen/shell/classes.dex，放入到apk解压后的strengthen/unzip_apk文件中
         * 5.[9]步，将strengthen/unzip_apk的文件重新压缩成apk文件，放入到strengthen/app-unsigned.apk
         * 6.[10~11]步，对齐apk、重签名apk
         */
        File oldTempFile = new File("strengthen");
        if(oldTempFile.exists()){
            oldTempFile.delete();
        }

        //1.拿到壳aar
        File aarFile = new File("comp_strengthen/build/outputs/aar/comp_strengthen-release.aar");
        File aarTemp = new File("strengthen/shell");
        //2.将aar解压到temp文件夹下
        Zip.unZip(aarFile, aarTemp);
        //3.将壳jar，通过本地dx工具达成dex文件，这里只会有一个classes.jar,此步骤完成后，就会有一个.jar转成的classes.dex文件
        File classesJar = new File(aarTemp, "classes.jar");
        File classesDex = new File(aarTemp, "classes.dex");
        //这个dx脚本就是你本地任意脚本的SDK中的dx工具，如果是window，那么使用 "cmd "+deToolsPath+" dx --dex --output "
        //mac直接使用脚本的全路径就ok
        String dxToolsPath = "/Users/xuxinxin/Library/Android/sdk/build-tools/30.0.3/";
        Process process = Runtime.getRuntime().exec(dxToolsPath + "/dx --dex --output "
                + classesDex.getAbsolutePath()
                + " " + classesJar.getAbsolutePath());
        process.waitFor();
        if(process.exitValue()!=0){
            throw new RuntimeException("dex error");
        }
        //4.获取apk
        File apkFile=new File("app/build/outputs/apk/debug/app-debug.apk");
        File apkTemp = new File("strengthen/unzip_apk");
        //5.解压apk文件放到临时目录 apkTemp
        Zip.unZip(apkFile,apkTemp);
        //6.抽取解压后apk文件中的dex文件
        File[] dexFiles = apkTemp.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.endsWith(".dex");
            }
        });
        //7.对原来的dex进行加密，加密成新的文件secret-.dex;并删除原来的.dex文件
        for (File dexFile:dexFiles){
            String encryptDexFile = "secret-" + dexFile.getName();
            byte[] bytes = Utils.getBytes(dexFile);
            byte[] encrypt = AES.getInstance().encrypt(bytes);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(new File(apkTemp, encryptDexFile));
                fos.write(encrypt);
                fos.flush();

            } finally {
                fos.close();
                dexFile.delete();
            }
        }
        //8.把壳dex放入搭配apk解压目录，重新压成apk文件,此时这个apk文件，并没有进行签名
        classesDex.renameTo(new File(apkTemp,"classes.dex"));
        File unSignedApk = new File("strengthen/app-unsigned.apk");
        //9.重新将解压后的文件，压缩成一个新的apk文件
        Zip.zip(apkTemp,unSignedApk);

        //10.对齐apk：使用sdk下的工具zipalign进行apk对齐，对齐后的apk为：app-unsigned-aligned.apk
        /**
         * zipalign是Android SDK中的一个用于优化APK的新工具，它提高了优化后的Applications与Android系统的交互效率，从而可以使整个系统的运行速度有了较大的提升。
         * zipalign优化的最根本目的是帮助操作系统更高效率的根据请求索引资源，通过将apk中的未压缩数据进行字节对齐（一般为4字节对齐），允许系统使用mmap方法直接映射文件至内存空间，降低内存的消耗。
         * */
        File zipalignApk = new File("strengthen/app-unsigned-zipalign.apk");
        process = Runtime.getRuntime().exec("/Users/xuxinxin/Library/Android/sdk/build-tools/30.0.3/zipalign -v -p 4 " + unSignedApk.getAbsolutePath()
                + " " + zipalignApk.getAbsolutePath());

        //11.对[对齐后的apk进行签名]:工具SDK下的：apksigner脚本
        File signedApk = new File("strengthen/app-signed-zipalign-aligned.apk");
        //这是AndroidStudio默认的签名文件，进入.android目录下【debug.keystore所在目录】
        //执行：keytool -list -keystore debug.keystore
        //输入android，获取你的签名文件
        File jks = new File("/Users/xuxinxin/.android/debug.keystore");
        process = Runtime.getRuntime().exec("/Users/xuxinxin/Library/Android/sdk/build-tools/30.0.3/apksigner sign --ks " + jks.getAbsolutePath()
                + " --ks-key-alias androiddebugkey --ks-pass pass:android --key-pass pass:android --out "
                + signedApk.getAbsolutePath() + " " + zipalignApk.getAbsolutePath());

        process.waitFor();
        if(process.exitValue()!=0){
           System.out.println("执行失败");
        }else{
            System.out.println("执行成功");
        }

    }
}
