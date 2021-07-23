package com.wcdok.comp_strengthen;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;


import com.wcdok.comp_strengthen.tools.AES;
import com.wcdok.comp_strengthen.tools.Utils;
import com.wcdok.comp_strengthen.tools.Zip;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: wcd
 * @email: wcdwangyi@163.com
 * @date: 7/22/21 1:22 PM
 * @desc: 壳Application，用于解密和替换
 *
 * 这里有个弊端，就是你的app的AndroidManifest.xml需要使用ProxyApplication
 * 当然，如果不使用她，可以仿照thinker的方式，写一个gradle-task【TinkerManifestTask】在打包过程中修改mager后的AndroidManifest.xml
 *
 */
public class ProxyApplication  extends Application {
    //定义好解密后文件的存储路径
    private String app_name;
    private String app_version;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //1.获取用户填入的metadata[元数据]
        getMetaData();
        //2.得到当前加密了的APK文件--【资源路径  应用APK的全路径】
        File apkFile = new File(getApplicationInfo().sourceDir);
        //3.把apk解压--/data/user/0/应用包名, app_name+"_"+app_version目录中的内容需要boot权限才能用
        File versionDir = getDir(app_name+"_"+app_version,MODE_PRIVATE);
        File appDir = new File(versionDir,"app");
        File dexDir = new File(appDir,"dexDir");

        //4.获取我们需要加载的Dex文件
        List<File> dexFiles = new ArrayList<>();
        if(!dexDir.exists()|| dexDir.listFiles().length==0){
            //解压apk文件到appDir目录下
            Zip.unZip(apkFile,appDir);
            //获取apk解压后的dex文件
            File[] files = appDir.listFiles();
            for (File file:files){
                String fileName = file.getName();
                if(fileName.endsWith(".dex")&& !TextUtils.equals(fileName,"classes.dex")){
                    //解密dex文件，并覆盖原来的dex文件
                    try {
                        byte[] bytes = Utils.getBytes(file);
                        //解密dex文件
                        byte[] decrypt = AES.getInstance().decrypt(bytes);
                        //写到指定目录
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(decrypt);
                        fos.flush();
                        fos.close();
                        dexFiles.add(file);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        //5.把解密后的文件加载到系统中

        try {
            loadDex(dexFiles,versionDir);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }


    }

    private void loadDex(List<File> dexFiles,File versionDir) throws IllegalAccessException, InvocationTargetException, NoSuchFieldException, NoSuchMethodException {
        //1.获取ClassLoader类中DexPathList成员变量pathList
        /**
         * ClassLoader.java
         * private final DexPathList pathList;
         * */
        Field pathListField = Utils.findField(getClassLoader(),"pathList");
        Object pathList = pathListField.get(getClassLoader());
        //2.获取DexPathList类中 Element[]类型的成员变量dexElements
        /**
         * DexPathList.java
         * private Element[] dexElements;
         * */
        Field dexElementsField = Utils.findField(pathList,"dexElements");
        Object[] originDexElements = (Object[]) dexElementsField.get(pathList);
        //3.获取DexPathList中的makePathElements方法，这个方法能加载指定目录下的Dex文件，通过这个方法将dexFiles目录下的dex文件读取到内存中【内存中以DexFile的形式存在】，并将优化的dex文件存储到versionDir目录下
        /**
         *
         * 这个方法是DexPathList提供的static用于查找dex的
         *  @SuppressWarnings("unused")
         *  private static Element[] makePathElements(List<File> files, File optimizedDirectory,
         *          List<IOException> suppressedExceptions) {
         *      return makeDexElements(files, optimizedDirectory, suppressedExceptions, null);
         *  }
         *
         * */
        Method makeDexElements = Utils.findMethod(pathList,"makePathElements",List.class,File.class,List.class);

        ArrayList<IOException> suppressedExceptions = new ArrayList<>();
        Object[] addElements = (Object[]) makeDexElements.invoke(pathList,dexFiles,versionDir,suppressedExceptions);

        //4.合并dex数组
        Object[] newElements = (Object[]) Array.newInstance(originDexElements.getClass().getComponentType(), originDexElements.length + addElements.length);
        System.arraycopy(originDexElements,0,newElements,0,originDexElements.length);
        System.arraycopy(addElements,0,newElements,0,addElements.length);

        //5.使用hook进行替换，原来DexPathList中的： Element[]类型的成员变量dexElements
        dexElementsField.set(pathList,newElements);




    }

    private void getMetaData(){

        try {
            /**
             * 1 ApplicationInfo是android.content.pm包下的一个实体类，用于封装应用的信息，
             *          flags是其中的一个成员变量public int flags = 0;用于保存应用的标志信息。
             *
             * 2 ApplicationInfo 通过它可以得到一个应用基本信息。
             *      这些信息是从AndroidManifest.xml的< application >标签获取的
             *
             * 3 ApplicationInfo对象里保存的信息都是<application>标签里的属性值
             *
             * 4 ApplicationInfo与ResolveInfo比较：前者能够得到Icon、Label、meta-data、description。后者只能得到Icon、Label
             *
             * */
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(
                    getPackageName(), PackageManager.GET_META_DATA);

            Bundle metaData = applicationInfo.metaData;
            if(metaData!=null){
                if(metaData.containsKey("app_name")){
                    app_name = metaData.getString("app_name");
                }
                if(metaData.containsKey("app_version")){
                    app_version = metaData.getString("app_version");
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    private boolean isBindReal;
    private Application application;
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            bindRealApplication();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 这下面的步骤是依据：ActivityThrad.handleBindApplication()方法开展的
     *
     * bindRealApplication（），就是将所有被赋值过壳Application的地方全部替换成真正的application
     *
     *
     * 1.Application的创建是在LoadedApk.java的makeApplication中，通过反射获取的实例，创建后第一步就调用了application.attach()
     * 2.创建时，调用attachBaseContext(context)的时候，context是创建的ContextImpl，创建ContextImpl的时候，将ActivityThread传进去了，并且在方法attachBaseContext中，将context复制给Application的成员变量mBase
     * 3。创建Application后，又调用了 ContextImpl.setOuterContext(application);
     * 4。创建完成后，在ActivityThread中，又将application赋值给了ActivityThread的成员变量mInitialApplication
     * 5。LoadedApk.makeApplication()中，创建完Application后，又将Application存放到到了ActivityThread成员变量ArrayList<Application> mAllApplications中
     * 6。在LoadedApk.makeApplication()中，将创建后的application。赋值给了LoadedApk的成员变量mApplication
     * */

    private void bindRealApplication() throws Exception{
        if(isBindReal){
            return;
        }
        if(TextUtils.isEmpty(app_name)){
            return;
        }
       //1.模拟调用Application的attach()方法
        Context baseContext = getBaseContext();
        //创建用户真实的application
        application = (Application) Class.forName(app_name).newInstance();
        //得到attach()方法,将壳applica的context传入真正的Application中
        Method attach = Application.class.getDeclaredMethod("attach", Context.class);
        attach.setAccessible(true);
        attach.invoke(application,baseContext);

        //2.替换ContextImpl中application的实例，这一步对应上面的第三步
        Class<?> contextImplClass = Class.forName("android.app.ContextImpl");
        //获取Context mOuterContext成员变量
        Field mOuterContext = contextImplClass.getDeclaredField("mOuterContext");
        mOuterContext.setAccessible(true);
        mOuterContext.set(baseContext,application);

        //3.替换ActivityThread中mMainThread的实例，这一步对应上面的第三步
        //获取ActivityThread，通过ContextImpl中的mMainThread变量获取的，对应注释第二步创建ContextImpl
        Field mMainThreadField = contextImplClass.getDeclaredField("mMainThread");
        mMainThreadField.setAccessible(true);
        Object mMainThread = mMainThreadField.get(baseContext);
        //获取ActivityThread的mInitialApplication成员变量
        Class<?> activityThreadClass=Class.forName("android.app.ActivityThread");
        Field mInitialApplicationField = activityThreadClass.getDeclaredField("mInitialApplication");
        mInitialApplicationField.setAccessible(true);
        mInitialApplicationField.set(mMainThread,application);

        //4.替换ActivityThread成员变量ArrayList<Application> mAllApplications中存储的application
        Field mAllApplicationsField = activityThreadClass.getDeclaredField("mAllApplications");
        mAllApplicationsField.setAccessible(true);
        ArrayList<Application> mAllApplications =(ArrayList<Application>) mAllApplicationsField.get(mMainThread);
        mAllApplications.remove(this);
        mAllApplications.add(application);

        //5.替换LoadedApk中的mApplication
        //ContextImpl.java的成员变量LoadedApk mPackageInfo;通过ContextImpl获取LoadedApk实例
        Field mPackageInfoField = contextImplClass.getDeclaredField("mPackageInfo");
        mPackageInfoField.setAccessible(true);
        Object mPackageInfo=mPackageInfoField.get(baseContext);
        //通过LoadedApk实例，修改里面的mApplication
        Class<?> loadedApkClass=Class.forName("android.app.LoadedApk");
        Field mApplicationField = loadedApkClass.getDeclaredField("mApplication");
        mApplicationField.setAccessible(true);
        mApplicationField.set(mPackageInfo,application);

        //6.修改ApplicationInfo中的 className
        //通过LoadedApk，获取ApplicationInfo实例
        Field mApplicationInfoField = loadedApkClass.getDeclaredField("mApplicationInfo");
        mApplicationInfoField.setAccessible(true);
        ApplicationInfo mApplicationInfo = (ApplicationInfo)mApplicationInfoField.get(mPackageInfo);
        mApplicationInfo.className=app_name;
        //7.调用application的onCreate()方法
        application.onCreate();
        isBindReal = true;

    }

    @Override
    public String getPackageName() {
        if(!TextUtils.isEmpty(app_name)){
            return app_name;
        }
        return super.getPackageName();
    }

    @Override
    public Context createPackageContext(String packageName, int flags) throws PackageManager.NameNotFoundException {
        if(TextUtils.isEmpty(app_name)){
            return super.createPackageContext(packageName, flags);
        }
        try {
            bindRealApplication();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return application;
    }
}
