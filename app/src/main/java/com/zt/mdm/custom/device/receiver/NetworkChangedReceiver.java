package com.zt.mdm.custom.device.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.zt.mdm.custom.device.util.TaskUtil;
import com.zt.mdm.custom.device.utils.LogUtils;
import com.zt.mdm.custom.device.utils.UpdateUtils;

import static com.zt.mdm.custom.device.util.AppConstants.TYPE_MOBILE;
import static com.zt.mdm.custom.device.util.AppConstants.TYPE_NONE;
import static com.zt.mdm.custom.device.util.AppConstants.TYPE_WIFI;

/**
 * @author Z T
 * @date 20200925
 **/
public class NetworkChangedReceiver extends BroadcastReceiver {
    private static final String TAG = "NetworkChangedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        int netWorkStates = getNetWorkStates(context);
        UpdateUtils updateUtils = new UpdateUtils();
        switch (netWorkStates) {
            case TYPE_NONE:
                break;
            case TYPE_MOBILE:
                LogUtils.info(TAG, "打开了移动网络");
//                updateApp();
                  TaskUtil.startPollAlarmReceiver(true);
                break;
            case TYPE_WIFI:
                break;
            default:
                break;
        }
    }

    /**
     * 获取网络状态
     *
     * @param context
     * @return one of TYPE_NONE, TYPE_MOBILE, TYPE_WIFI
     * @permission android.permission.ACCESS_NETWORK_STATE
     */
    public static final int getNetWorkStates(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            return TYPE_NONE;
        }
        int type = activeNetworkInfo.getType();
        switch (type) {
            //移动数据
            case ConnectivityManager.TYPE_MOBILE:
                return TYPE_MOBILE;
            //WIFI
            case ConnectivityManager.TYPE_WIFI:
                return TYPE_WIFI;
            default:
                break;
        }
        return TYPE_NONE;
    }
}