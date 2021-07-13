package com.zt.mdm.custom.device.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.zt.mdm.custom.device.util.StorageUtil;
import com.zt.mdm.custom.device.utils.LogUtils;

import static com.zt.mdm.custom.device.util.AppConstants.IS_LOCK;
import static com.zt.mdm.custom.device.util.AppConstants.LOCK;
import static com.zt.mdm.custom.device.util.AppConstants.UN_LOCK;
import static com.zt.mdm.custom.device.util.TaskUtil.isTopActivity;
import static com.zt.mdm.custom.device.util.TaskUtil.startLockActivity;
import static com.zt.mdm.custom.device.util.TaskUtil.startLockReceiver;

/**
 * Created by zt.
 */

public class  CallPhoneStateReceiver extends BroadcastReceiver {
    private static final String TAG = "CallPhoneStateReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if("android.intent.action.PHONE_STATE".equals(intent.getAction())){
            //获取电话号码
            String number=intent.getStringExtra("incoming_number");
            LogUtils.info("test","有电话进来了"+number);
            startLockReceiver();
            //获取电话状态
            //电话管理者
            TelephonyManager tm= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            int state= tm.getCallState();
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING://来电

                    //LogUtils.info("test","来电话了_前");
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://接听
                    //LogUtils.info("test","通话中_中");
                    break;
                case TelephonyManager.CALL_STATE_IDLE:   //挂断
                    if (!isTopActivity() && LOCK.equals(StorageUtil.get(IS_LOCK,UN_LOCK))){
                        LogUtils.info(TAG,"startLockActivity");
                        startLockActivity();
                    }
                    break;
            }
        }
    }
}