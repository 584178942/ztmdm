package com.zt.mdm.custom.device.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zt.mdm.custom.device.SGTApplication;
import com.zt.mdm.custom.device.bean.AppSyncBean;
import com.zt.mdm.custom.device.bean.SimBean;
import com.zt.mdm.custom.device.service.HwMdmUtil;
import com.zt.mdm.custom.device.service.ReflexStringToMethod;
import com.zt.mdm.custom.device.util.RsaUtils;
import com.zt.mdm.custom.device.util.StorageUtil;
import com.zt.mdm.custom.device.util.TaskUtil;
import com.zt.mdm.custom.device.utils.LogUtils;
import com.zt.mdm.custom.device.utils.NetUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

import static com.zt.mdm.custom.device.util.AppConstants.BRIGHT_SCREEN_MAX;
import static com.zt.mdm.custom.device.util.AppConstants.BRIGHT_SCREEN_MIN;
import static com.zt.mdm.custom.device.util.AppConstants.CALL_WHITE_LIST;
import static com.zt.mdm.custom.device.util.AppConstants.DATASYNC_DEVICEAPP;
import static com.zt.mdm.custom.device.util.AppConstants.Dark_SCREEN_MAX;
import static com.zt.mdm.custom.device.util.AppConstants.Dark_SCREEN_MIN;
import static com.zt.mdm.custom.device.util.AppConstants.EQUIPMENT_CODE;
import static com.zt.mdm.custom.device.util.AppConstants.IS_LOCK;
import static com.zt.mdm.custom.device.util.AppConstants.LOCK;
import static com.zt.mdm.custom.device.util.AppConstants.LOCK_MSG;
import static com.zt.mdm.custom.device.util.AppConstants.PASSIVE_RECEIVE_BUS_INDEX;
import static com.zt.mdm.custom.device.util.AppConstants.SEND_STATE;
import static com.zt.mdm.custom.device.util.AppConstants.SIM_CODE1;
import static com.zt.mdm.custom.device.util.AppConstants.SIM_CODE2;
import static com.zt.mdm.custom.device.util.AppConstants.THREE_THOUSAND_SIX;
import static com.zt.mdm.custom.device.util.AppConstants.UN_LOCK;
import static com.zt.mdm.custom.device.util.MdmUtil.getSoftwareInfo;
import static com.zt.mdm.custom.device.util.TaskUtil.startBasisReceiver;
import static com.zt.mdm.custom.device.util.TaskUtil.startLockActivity;
import static com.zt.mdm.custom.device.util.TaskUtil.startPollAlarmReceiver;
import static com.zt.mdm.custom.device.utils.NetUtils.appUrl;

/**
 * @author ZT
 * @date
 */
public class BasisSyncReceiver extends BroadcastReceiver {
    private static final String TAG = "BasisReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.info(TAG,"==" + getSoftwareInfo());
        startBasisReceiver(THREE_THOUSAND_SIX);
        Map paramMap = new HashMap();
        paramMap.put(EQUIPMENT_CODE, TaskUtil.getImei());
        paramMap.put("deviceApps",  getSoftwareInfo());
        deviceApp(paramMap, DATASYNC_DEVICEAPP);
    }
    /**
     * @param params
     * @param url
     */
    public void deviceApp(Map<String,String> params, String url) {
        NetUtils netUtils = NetUtils.getInstance();
        netUtils.postDataAsynToNet(appUrl + url, params, new NetUtils.MyNetCall() {
            @Override
            public void success(okhttp3.Call call, Response response) throws IOException {
                String result = response.body().string();
                try {
                    result = RsaUtils.decryptByPublicKey(result);
                    AppSyncBean appSyncBean = new Gson().fromJson(result,AppSyncBean.class);
                    if (appSyncBean.getState() == 50000){
                        //LogUtils.info("CALL_WHITE_LIST",new Gson().toJson(appSyncBean.getData().getWhiteList()));
                        StorageUtil.put(CALL_WHITE_LIST,new Gson().toJson(appSyncBean.getData().getWhiteList()));
                        StorageUtil.put(SEND_STATE,appSyncBean.getData().getSendState());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LogUtils.info(TAG,result);
            }
            @Override
            public void failed(okhttp3.Call call, IOException e) {
                LogUtils.info(TAG,e.getLocalizedMessage());
            }
        });
    }
}
