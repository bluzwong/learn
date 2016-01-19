package com.github.bluzwong.learn_rx;

import android.app.Application;
import android.content.Context;

/**
 * Created by wangzhijie on 2016/1/19.
 */
public class BaseApplication extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getContext() {
        return context;
    }
}
