package com.yuyife.yyimagecrop.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.yuyife.yyimagecrop.R;

import java.io.File;

/**
 * 意图工具
 */

public class IntentUtil {
    public final static int IMAGE_SELECT_CAMERA_THUMBNAIL_CODE = 0;         //相机压缩
    public final static int IMAGE_SELECT_CAMERA_ARTWORK_CODE = 1;           //相机高清
    public final static int IMAGE_SELECT_PHOTO_CODE = 2;                    //相册
    public final static int IMAGE_SELECT_CROP_CODE = 10;                    //图片裁剪


    /**
     * 使用系统相册选择图片之后裁截取图片
     * @param inputUri 需要裁剪的图片
     */
    public static void imageCrop(Activity aty, Uri inputUri,  int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(inputUri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 960);
        intent.putExtra("outputY", 960);
        intent.putExtra("return-data", false);
        //intent.putExtra("noFaceDetection", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, inputUri);
        aty.startActivityForResult(intent, requestCode);
    }

    /**
     * 从相册选择图片
     */
    public static void imagePhotoSelect(Activity aty, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        aty.startActivityForResult(intent, requestCode);
    }

    /**
     * 选择相机（缩略图）
     */
    public static void imageCameraThumbnail(Activity aty, int request) {
        // 跳转到系统照相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(aty.getPackageManager()) != null) {
            aty.startActivityForResult(cameraIntent, request);
        } else {
            Toast.makeText(aty, aty.getString(R.string.prompt_no_camera), Toast.LENGTH_SHORT).show();

        }
    }



    /**
     * 选择相机（原图）
     */
    public static void imageCameraArtwork(Activity aty, String filePath, String imageName, int request) {
        // 跳转到系统照相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(aty.getPackageManager()) != null) {
            String path = filePath + "/" + imageName + ".png";
            Uri uri = Uri.fromFile(new File(path));
            //为拍摄的图片指定一个存储的路径
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            aty.startActivityForResult(cameraIntent, request);
        } else {
            Toast.makeText(aty, aty.getString(R.string.prompt_no_camera), Toast.LENGTH_SHORT).show();
        }
    }
}
