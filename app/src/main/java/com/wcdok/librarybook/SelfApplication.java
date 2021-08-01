package com.wcdok.librarybook;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.multidex.MultiDex;


/**
 * @author: wcd
 * @email: wcdwangyi@163.com
 * @date: 7/23/21 2:14 PM
 * @desc:
 */
public class SelfApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("wcd","SelfApplication:onCreate");
    }
}
