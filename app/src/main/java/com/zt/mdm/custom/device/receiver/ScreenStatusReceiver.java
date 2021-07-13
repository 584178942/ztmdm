package com.zt.mdm.custom.device.receiver;

// UpdateReceiver

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zt.mdm.custom.device.utils.LogUtils;
import static android.content.ContentValues.TAG;
import static com.zt.mdm.custom.device.util.AppConstants.ANDROID_INTENT_ACTION_SCREEN_OFF;
import static com.zt.mdm.custom.device.util.AppConstants.ANDROID_INTENT_ACTION_SCREEN_ON;
import static com.zt.mdm.custom.device.util.TaskUtil.startPollAlarmReceiver;

/**
 * 灭屏/亮屏广播
 * @author Z T
 */
public class ScreenStatusReceiver extends BroadcastReceiver {
    public static boolean mScreenPowerStatus = true;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ANDROID_INTENT_ACTION_SCREEN_ON.equals(intent.getAction())) {
           // LogUtils.info(TAG, "Detect screen on and set mScreenPowerStatus false");
            /*if (!isTopActivity() && LOCK.equals(StorageUtil.get(IS_LOCK,UN_LOCK))){
                LogUtils.info(TAG,"startLockActivity");
                startLockActivity();
            }*/
            mScreenPowerStatus = true;
            startPollAlarmReceiver(true);
            //updateApp();
        } else if (ANDROID_INTENT_ACTION_SCREEN_OFF.equals(intent.getAction())) {
            //LogUtils.info(TAG, "Detect screen off and set mScreenPowerStatus ture");
            mScreenPowerStatus = false;
        }
    }
}
