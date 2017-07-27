package com.yuyife.yyimagecrop;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yuyife.yyimagecrop.utils.CompressUtil;
import com.yuyife.yyimagecrop.utils.FileUtil;
import com.yuyife.yyimagecrop.utils.IntentUtil;

import java.io.File;

import top.zibin.luban.OnCompressListener;

public class SampleActivity extends AppCompatActivity {

    public static final String selectFileName = "select_image";             //从相机、相册选择后的某图片，保存在根目录的名字
    public static final String compressFileName = "compress_image";         //某图片被压缩后，保存在根目录的名字
    public static final String copyFileName = "copy_image";                 //某图片被复制到根目录的名字

    private TextView oldText, newText;
    private PinchImageView previewImage;

    private int type = -1;  //0=裁剪  1=压缩 2=复制

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sample_crop_btn:
                type = 0;
                showSelectDialog(getString(R.string.label_crop_text));
                break;
            case R.id.sample_compress_btn:
                type = 1;
                showSelectDialog(getString(R.string.label_compress_text));

                break;
            case R.id.sample_copy_btn:
                type = 2;
                showSelectDialog(getString(R.string.label_copy_text));

                break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        onData();
        onView();
    }

    private java.text.DecimalFormat decimalFormat = new java.text.DecimalFormat("#.00");  //小数点精确到2位

    private void onData() {

    }

    private void onView() {
        previewImage = (PinchImageView) findViewById(R.id.sample_preview_image);
        oldText = (TextView) findViewById(R.id.sample_old_text);
        newText = (TextView) findViewById(R.id.sample_new_text);
    }


    private void showSelectDialog(String title) {
        new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setItems(new String[]{getString(R.string.value_photo), getString(R.string.value_camera)},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i) {

                                    case 0:
                                        IntentUtil.imagePhotoSelect(SampleActivity.this,
                                                IntentUtil.IMAGE_SELECT_PHOTO_CODE);
                                        break;
                                    case 1:
                                        IntentUtil.imageCameraArtwork(SampleActivity.this,
                                                FileUtil.getAppFileDir(), selectFileName,
                                                IntentUtil.IMAGE_SELECT_CAMERA_ARTWORK_CODE);

                                        break;
                                }
                            }
                        })
                .create()
                .show();
    }

    private Uri defaultUri = null;//从相机、相机选择图片后，请务必经历：Uri--File-Uri，图片裁剪后，会直接操作defaultUri并返回

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (resultCode == Activity.RESULT_OK) {
                    switch (requestCode) {
                        case IntentUtil.IMAGE_SELECT_CROP_CODE:
                            //得到裁剪后的图片
                            String fileCrop = FileUtil.getPathByUri4kitkat(SampleActivity.this, defaultUri);
                            setImage(new File(fileCrop), true);
                            break;
                        case IntentUtil.IMAGE_SELECT_PHOTO_CODE:
                            //来自相册
                            showProgress();
                            Uri photoUri = data.getData();
                            final File filePhoto = new File(FileUtil.getPathByUri4kitkat(SampleActivity.this, photoUri));
                            Bitmap bitmap = BitmapFactory.decodeFile(filePhoto.getPath());
                            FileUtil.saveBitmapToAppDir(bitmap, selectFileName);
                            setOldText(new File(FileUtil.getAppFileDir() + "/" + selectFileName + ".png"));
                            defaultUri = Uri.fromFile(new File(FileUtil.getAppFileDir() + "/" + selectFileName + ".png"));
                            dismissProgress();
                            switch (type) {
                                case 0:
                                    IntentUtil.imageCrop(SampleActivity.this, defaultUri, IntentUtil.IMAGE_SELECT_CROP_CODE);
                                    break;
                                case 1:
                                    setImage(filePhoto, true);
                                    break;
                                case 2:
                                    //复制
                                    setImage(filePhoto, false);

                                    break;
                            }
                            break;
                        case IntentUtil.IMAGE_SELECT_CAMERA_ARTWORK_CODE:
                            //来自相机
                            showProgress();
                            final File fileCamera = new File(FileUtil.getAppFileDir() + "/" + selectFileName + ".png");
                            setOldText(fileCamera);
                            defaultUri = Uri.fromFile(new File(FileUtil.getAppFileDir() + "/" + selectFileName + ".png"));
                            dismissProgress();
                            switch (type) {
                                case 0:
                                    IntentUtil.imageCrop(SampleActivity.this, Uri.fromFile(new File(FileUtil.getAppFileDir() + "/" + selectFileName + ".png")), IntentUtil.IMAGE_SELECT_CROP_CODE);
                                    break;
                                case 1:
                                    setImage(fileCamera, true);
                                    break;
                                case 2:
                                    //复制
                                    setImage(fileCamera, false);
                                    break;
                            }
                            break;
                    }
                }
            }
        }).start();

    }


    private static ProgressDialog dialog;

    /**
     * 显示进度对话框
     */
    private void showProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog = new ProgressDialog(SampleActivity.this);
                dialog.setMessage(getString(R.string.prompt_handle_ing));
                dialog.setCancelable(false);
                dialog.show();
            }
        });

    }

    /**
     * 隐藏进度对话框
     */
    private void dismissProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

    }

    /**
     * 设置图片原来的大小
     *
     * @param f 目标文件
     */
    private void setOldText(final File f) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    double size = (((double) FileUtil.getFileSizes(f)) / 1024);
                    oldText.setText(getString(R.string.label_old_size_text) + decimalFormat.format(size) + "KB");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 显示图片
     *
     * @param file       目标文件
     * @param isCompress 是否需要压缩
     */
    private void setImage(final File file, boolean isCompress) {
        showProgress();

        if (isCompress) {
            CompressUtil.compress(this, file, new OnCompressListener() {
                @Override
                public void onStart() {
                }

                @Override
                public void onSuccess(final File file) {
                    try {
                        Log.e(SampleActivity.class.getSimpleName(), "ratioCompress onSuccess file size:" + FileUtil.getFileSizes(file));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    final Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());

                    FileUtil.saveBitmapToAppDir(bitmap, compressFileName);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgress();
                            previewImage.setImageBitmap(bitmap);
                            try {
                                double size = (FileUtil.getFileSizes(new File(FileUtil.getAppFileDir() + "/" + compressFileName + ".png")) / 1024);
                                newText.setText(getString(R.string.label_new_size_text) + decimalFormat.format(size) + "KB");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }

                @Override
                public void onError(Throwable e) {

                    dialog.dismiss();
                }
            });
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Bitmap bitmapCamera = BitmapFactory.decodeFile(file.getPath());
                    FileUtil.saveBitmapToAppDir(bitmapCamera, copyFileName);
                    dismissProgress();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            previewImage.setImageBitmap(bitmapCamera);
                            Toast.makeText(SampleActivity.this, getString(R.string.prompt_copy_success) + new File(FileUtil.getAppFileDir() + "/" + copyFileName + ".png").getPath(), Toast.LENGTH_SHORT).show();
                            try {
                                double size = (((double) FileUtil.getFileSizes(new File(FileUtil.getAppFileDir() + "/" + copyFileName + ".png"))) / 1024);
                                newText.setText(getString(R.string.label_new_size_text) + decimalFormat.format(size) + "KB");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }).start();
        }

    }
}
