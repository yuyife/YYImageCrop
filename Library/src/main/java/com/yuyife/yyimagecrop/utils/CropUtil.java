package com.yuyife.yyimagecrop.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.yuyife.yyimagecrop.R;

import java.io.File;

/**
 * 对裁剪封装
 * 1.从相册选择图片  -->然后裁剪
 * 2.从相机拍摄图片  -->然后裁剪
 *
 * 3.可单独使用 从相册选择图片的功能
 * 4.可单独使用 从相机拍摄图片的功能
 *
 * @author yuyife
 * create by 2017-08-11
 */

public class CropUtil {
    private static final String selectFileName = "yy_cache_select_image";             //从相机、相册选择后的某图片，保存在根目录的名字

    /**
     * 开始、选择来源
     * 务必和 public static void handleCrop()一起使用
     */
    public static void startCrop(final Activity activity) {
        CropFileUtil.setAppFileDir();
        new android.support.v7.app.AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.label_crop_source))
                .setItems(new String[]{activity.getString(R.string.value_photo), activity.getString(R.string.value_camera)},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i) {
                                    case 0:
                                        startPhoto(activity);
                                        break;
                                    case 1:
                                        startCamera(activity);
                                        break;
                                }
                            }
                        })
                .create()
                .show();
    }

    /**
     * 从相册选择
     * 单独调用的时候，
     * 务必和 public static void handlePhoto()一起使用
     */
    public static void startPhoto(Activity activity) {
        CropIntentUtil.imagePhotoSelect(activity,
                CropConstant.IMAGE_SELECT_PHOTO_CODE);
    }

    /**
     * 从相机拍摄
     * 单独调用的时候，
     * 务必和 public static void handleCamera()一起使用
     */
    public static void startCamera(Activity activity) {
        CropIntentUtil.imageCameraArtwork(activity,
                CropFileUtil.getAppFileDir(), selectFileName,
                CropConstant.IMAGE_SELECT_CAMERA_ARTWORK_CODE);
    }

    public interface OnHandleCallback {
        void onProgressStart();     //可以弹出提示

        void onProgressEnd();       //可以关闭提示

        void callback(File file);  //返回文件
    }

    private static Uri defaultUri = null;//从相机、相机选择图片后，请务必经历：Uri--File-Uri，图片裁剪后，会直接操作defaultUri并返回

    /**
     * 用于裁剪
     *
     */
    public static void handleCrop(final Activity activity,
                                  final int requestCode,
                                  final int resultCode,
                                  final Intent data,
                                  final OnHandleCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (resultCode == Activity.RESULT_OK) {
                    switch (requestCode) {
                        case CropConstant.IMAGE_SELECT_CROP_CODE:
                            //得到裁剪后的图片
                            final String fileCrop = CropFileUtil.getPathByUri4kitkat(activity, defaultUri);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (callback != null) {
                                        if (fileCrop != null) {
                                            callback.callback(new File(fileCrop));
                                        } else {
                                            callback.callback(null);
                                        }
                                    }
                                }
                            });

                            break;
                        case CropConstant.IMAGE_SELECT_PHOTO_CODE:
                            //来自相册
                            handlePhoto(true, activity, data, callback);
                            break;
                        case CropConstant.IMAGE_SELECT_CAMERA_ARTWORK_CODE:
                            //来自相机

                            handleCamera(true, activity, callback);
                            break;
                    }
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.callback(null);
                            }
                        }
                    });
                }
            }
        }).start();

    }

    /**
     * 处理从相册选择的图片
     * 真正的执行者
     */
    private static void handlePhoto(boolean isCrop, final Activity activity, final Intent data, final OnHandleCallback callback) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onProgressStart();
                }
            }
        });
        Uri photoUri = data.getData();
        String photoPath = CropFileUtil.getPathByUri4kitkat(activity, photoUri);
        if (photoPath != null) {
            final File filePhoto = new File(photoPath);
            Bitmap bitmap = BitmapFactory.decodeFile(filePhoto.getPath());
            CropFileUtil.saveBitmapToAppDir(bitmap, selectFileName);
            defaultUri = Uri.fromFile(new File(CropFileUtil.getAppFileDir() + "/" + selectFileName + ".png"));

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (callback != null) {
                        callback.onProgressEnd();
                        callback.callback(new File(CropFileUtil.getAppFileDir() + "/" + selectFileName + ".png"));
                    }
                }
            });

            if (isCrop) {
                CropIntentUtil.imageCrop(activity,
                        defaultUri,
                        CropConstant.IMAGE_SELECT_CROP_CODE);
            }
        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (callback != null) {
                        callback.onProgressEnd();
                        callback.callback(null);
                    }
                }
            });

        }
    }

    /**
     * 处理从相册选择的图片
     */
    public static void handlePhoto(final Activity activity,
                                   final int requestCode,
                                   final int resultCode,
                                   final Intent data,
                                   final OnHandleCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (resultCode == Activity.RESULT_OK) {
                    switch (requestCode) {

                        case CropConstant.IMAGE_SELECT_PHOTO_CODE:
                            //来自相册
                            handlePhoto(false, activity, data, callback);
                            break;

                    }
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.callback(null);
                            }
                        }
                    });
                }
            }
        }).start();

    }


    /**
     * 处理从相机拍摄的图片
     * 真正的执行者
     */
    private static void handleCamera(boolean isCrop, final Activity activity,
                                     final OnHandleCallback callback) {

        //来自相机
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onProgressStart();
                }
            }
        });
        final File fileCamera = new File(CropFileUtil.getAppFileDir() + "/" + selectFileName + ".png");
        defaultUri = Uri.fromFile(fileCamera);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onProgressEnd();
                    callback.callback(fileCamera);
                }
            }
        });

        if (isCrop) {
            CropIntentUtil.imageCrop(activity,
                    defaultUri,
                    CropConstant.IMAGE_SELECT_CROP_CODE);
        }

    }

    /**
     * 处理从相机拍摄的图片
     */
    public static void handleCamera(final Activity activity,
                                    final int requestCode,
                                    final int resultCode,
                                    final Intent data,
                                    final OnHandleCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (resultCode == Activity.RESULT_OK) {
                    switch (requestCode) {

                        case CropConstant.IMAGE_SELECT_CAMERA_ARTWORK_CODE:
                            //来自相机
                            handleCamera(false, activity, callback);
                            break;

                    }
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.callback(null);
                            }
                        }
                    });
                }
            }
        }).start();

    }

}
