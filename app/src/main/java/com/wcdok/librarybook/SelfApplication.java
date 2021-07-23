package com.wcdok.librarybook;

import android.app.Application;
import android.util.Log;

/**
 * @author: wcd
 * @email: wcdwangyi@163.com
 * @date: 7/23/21 2:14 PM
 * @desc:
 */
public class SelfApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("wcd","SelfApplication:onCreate");
    }
}
