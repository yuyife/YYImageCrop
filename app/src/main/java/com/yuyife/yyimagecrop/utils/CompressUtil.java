package com.yuyife.yyimagecrop.utils;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * 图片压缩
 */

public class CompressUtil {

    /**
     * 异步压缩
     * */
    public static void compress(Context context, File file,OnCompressListener listener) {
        Luban.with(context)
                .load(file)                     //传入要压缩的图片
                .setCompressListener(listener).launch();    //启动压缩
    }

    /**
     * 同步
     * */
    public static void compressSync(Context context, File file) {
        try {
            Luban.with(context).load(file).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
