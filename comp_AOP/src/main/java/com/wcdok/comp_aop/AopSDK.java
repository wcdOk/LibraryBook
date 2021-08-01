package com.wcdok.comp_aop;

import android.app.Application;

/**
 * @author: wcd
 * @email: wcdwangyi@163.com
 * @date: 8/1/21 4:49 PM
 * @desc:
 */
public class AopSDK {
    private static Application mApplication;

    public static void init(Application application) {
        mApplication = application;

    }

    public static Application getApplication() {
        return mApplication;

    }
}
