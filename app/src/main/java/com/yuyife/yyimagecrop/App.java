package com.yuyife.yyimagecrop;

import android.app.Application;

import com.yuyife.yyimagecrop.utils.FileUtil;

/**
 * Created by ZH4 on 2017/7/26.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FileUtil.setAppFileDir();
    }
}
