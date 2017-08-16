package com.yuyife.cropsample;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * 权限管理工具
 * <p>
 * Normal Permissions 普通权限
 * ACCESS_LOCATION_EXTRA_COMMANDS
 * ACCESS_NETWORK_STATE
 * ACCESS_NOTIFICATION_POLICY
 * ACCESS_WIFI_STATE
 * BLUETOOTH
 * BLUETOOTH_ADMIN
 * BROADCAST_STICKY
 * CHANGE_NETWORK_STATE
 * CHANGE_WIFI_MULTICAST_STATE
 * CHANGE_WIFI_STATE
 * DISABLE_KEYGUARD
 * EXPAND_STATUS_BAR
 * GET_PACKAGE_SIZE
 * INSTALL_SHORTCUT
 * INTERNET
 * KILL_BACKGROUND_PROCESSES
 * MODIFY_AUDIO_SETTINGS
 * NFC
 * READ_SYNC_SETTINGS
 * READ_SYNC_STATS
 * RECEIVE_BOOT_COMPLETED
 * REORDER_TASKS
 * REQUEST_INSTALL_PACKAGES
 * SET_ALARM
 * SET_TIME_ZONE
 * SET_WALLPAPER
 * SET_WALLPAPER_HINTS
 * TRANSMIT_IR
 * UNINSTALL_SHORTCUT
 * USE_FINGERPRINT
 * VIBRATE
 * WAKE_LOCK
 * WRITE_SYNC_SETTINGS
 * ============危险权限
 * Dangerous Permissions:
 * group:android.permission-group.CONTACTS
 * permission:android.permission.WRITE_CONTACTS
 * permission:android.permission.GET_ACCOUNTS
 * permission:android.permission.READ_CONTACTS
 * <p>
 * group:android.permission-group.PHONE
 * permission:android.permission.READ_CALL_LOG
 * permission:android.permission.READ_PHONE_STATE
 * permission:android.permission.CALL_PHONE
 * permission:android.permission.WRITE_CALL_LOG
 * permission:android.permission.USE_SIP
 * permission:android.permission.PROCESS_OUTGOING_CALLS
 * permission:com.android.voicemail.permission.ADD_VOICEMAIL
 * <p>
 * group:android.permission-group.CALENDAR
 * permission:android.permission.READ_CALENDAR
 * permission:android.permission.WRITE_CALENDAR
 * <p>
 * group:android.permission-group.CAMERA
 * permission:android.permission.CAMERA
 * <p>
 * group:android.permission-group.SENSORS
 * permission:android.permission.BODY_SENSORS
 * <p>
 * group:android.permission-group.LOCATION
 * permission:android.permission.ACCESS_FINE_LOCATION
 * permission:android.permission.ACCESS_COARSE_LOCATION
 * <p>
 * group:android.permission-group.STORAGE
 * permission:android.permission.READ_EXTERNAL_STORAGE
 * permission:android.permission.WRITE_EXTERNAL_STORAGE
 * <p>
 * group:android.permission-group.MICROPHONE
 * permission:android.permission.RECORD_AUDIO
 * <p>
 * group:android.permission-group.SMS
 * permission:android.permission.READ_SMS
 * permission:android.permission.RECEIVE_WAP_PUSH
 * permission:android.permission.RECEIVE_MMS
 * permission:android.permission.RECEIVE_SMS
 * permission:android.permission.SEND_SMS
 * permission:android.permission.READ_CELL_BROADCASTS
 */

public class PermissionUtil {

    public final static int REQUEST_CODE_SDCARD = 120;
    public final static int REQUEST_CODE_CAMERA = 121;

    public interface PermissionRequestCallback{
        void onPermissionRequest();
    }

    /**
     * 申请SDCard写入
     * */
    public static void checkSDCardWrite(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Android 原生会给予一次解释的机会，小米等手机则直接返回false，不给解释。只要用户选择拒绝，下次不再提示。
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Log.e(PermissionUtil.class.getSimpleName(),"解释");
            } else {
                Log.e(PermissionUtil.class.getSimpleName(),"不用解释，直接申请");
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_SDCARD);

            }
        }
    }
    /**
     * 申请拍照
     * */
    public static void checkCamera(Activity activity) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CODE_SDCARD);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }


}
