package com.yuyife.cropsample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.yuyife.yyimagecrop.utils.CropCompressUtil;
import com.yuyife.yyimagecrop.utils.CropUtil;
import com.yuyife.yyimagecrop.widget.PinchImageView;

import java.io.File;

import top.zibin.luban.OnCompressListener;

/**
 * 演示
 *
 * @author yuyife
 *         create by 2017-08-11
 */
public class SampleCropActivity extends AppCompatActivity {

    public void onSampleClick(View v) {
        switch (v.getId()) {
            case R.id.sample_crop_btn:
                //PermissionUtil.checkCamera(this);
                //PermissionUtil.checkSDCardWrite(this);
                CropUtil.startCrop(this);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_sample);
        onData();
        onView();
    }


    private void onData() {

    }
    //申请权限后返回的 结果
        @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == PermissionUtil.REQUEST_CODE_SDCARD) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
            } else {
                // Permission Denied
            }
        }else if (requestCode ==PermissionUtil.REQUEST_CODE_CAMERA){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
            } else {
                // Permission Denied
            }
        }
    }

    private PinchImageView previewImage;
    private ProgressDialog progressDialog;
    private void onView() {
        previewImage = (PinchImageView) findViewById(R.id.sample_preview_image);
        progressDialog = new ProgressDialog(this);
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CropUtil.handleCrop(SampleCropActivity.this, requestCode, resultCode, data, new CropUtil.OnHandleCallback() {
            @Override
            public void onProgressStart() {
                progressDialog.show();
            }

            @Override
            public void onProgressEnd() {
                progressDialog.dismiss();
            }

            @Override
            public void callback(final File file) {
                if (file != null) {
                    setImage(file);
                } else {
                    Toast.makeText(SampleCropActivity.this, "Crop cancel..", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    /**
     * 显示图片
     *
     * @param file 目标文件
     */
    private void setImage(final File file) {

        CropCompressUtil.compress(this, file, new OnCompressListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(final File file) {

                final Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                previewImage.setImageBitmap(bitmap);

            }

            @Override
            public void onError(Throwable e) {

            }
        });


    }
}
