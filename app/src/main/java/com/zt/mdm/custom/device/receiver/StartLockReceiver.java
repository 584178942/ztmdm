package com.zt.mdm.custom.device.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zt.mdm.custom.device.util.StorageUtil;
import com.zt.mdm.custom.device.utils.LogUtils;

import static com.zt.mdm.custom.device.receiver.ScreenStatusReceiver.mScreenPowerStatus;
import static com.zt.mdm.custom.device.util.AppConstants.IS_LOCK;
import static com.zt.mdm.custom.device.util.AppConstants.LOCK;
import static com.zt.mdm.custom.device.util.AppConstants.UN_LOCK;
import static com.zt.mdm.custom.device.util.TaskUtil.isTopActivity;
import static com.zt.mdm.custom.device.util.TaskUtil.startLockActivity;

/**
 * @author ZT
 * @date 20201110
 */
public class StartLockReceiver extends BroadcastReceiver {
    private static final String TAG = "StartLockReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.info(TAG,(String) StorageUtil.get(IS_LOCK,UN_LOCK));
        if (LOCK.equals(StorageUtil.get(IS_LOCK,UN_LOCK))&&mScreenPowerStatus){
            LogUtils.info(TAG,"startLockActivity");
            startLockActivity();
        }
    }
}
