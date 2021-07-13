package com.zt.mdm.custom.device.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zt.mdm.custom.device.SGTApplication;
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
import static com.zt.mdm.custom.device.util.AppConstants.CALLBACK_INDEX;
import static com.zt.mdm.custom.device.util.AppConstants.Dark_SCREEN_MAX;
import static com.zt.mdm.custom.device.util.AppConstants.Dark_SCREEN_MIN;
import static com.zt.mdm.custom.device.util.AppConstants.EQUIPMENT_CODE;
import static com.zt.mdm.custom.device.util.AppConstants.IS_LOCK;
import static com.zt.mdm.custom.device.util.AppConstants.LOCK;
import static com.zt.mdm.custom.device.util.AppConstants.LOCK_MSG;
import static com.zt.mdm.custom.device.util.AppConstants.PASSIVE_RECEIVE_BUS_INDEX;
import static com.zt.mdm.custom.device.util.AppConstants.SIM_CODE1;
import static com.zt.mdm.custom.device.util.AppConstants.SIM_CODE2;
import static com.zt.mdm.custom.device.util.AppConstants.UN_LOCK;
import static com.zt.mdm.custom.device.util.TaskUtil.startLockActivity;
import static com.zt.mdm.custom.device.util.TaskUtil.startPollAlarmReceiver;
import static com.zt.mdm.custom.device.utils.NetUtils.appUrl;

/**
 * Created by 闹钟广播
 * @author Z T
 * @date 20200924
 */
public class PollAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "PollAlarmReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        //LogUtils.info("UN_LOCK",(String) StorageUtil.get(IS_LOCK,UN_LOCK));
        if (LOCK.equals(StorageUtil.get(IS_LOCK,UN_LOCK))){
            //LogUtils.info("UN_LOCK2",(String) StorageUtil.get(IS_LOCK,UN_LOCK));
            // startLockActivity();
        } else {
            HwMdmUtil.finishActivity();
        }
        Map paramMap = new HashMap();
        paramMap.put(SIM_CODE1, TaskUtil.getImsiCode());
        paramMap.put(SIM_CODE2, TaskUtil.getImsiCode2());
        paramMap.put(EQUIPMENT_CODE, TaskUtil.getImei());
        paramMap.put("lat", SGTApplication.getBdLocationUtil().getLatitude() + "");
        paramMap.put("lng", SGTApplication.getBdLocationUtil().getLontitude() + "");
        passiveReceiveBus(context, paramMap, PASSIVE_RECEIVE_BUS_INDEX);
    }

    /**
     * @param context
     * @param params
     * @param url
     */
    public void passiveReceiveBus(final Context context, Map<String,String> params, String url) {
        NetUtils netUtils = NetUtils.getInstance();
        netUtils.postDataAsynToNet(appUrl + url, params, new NetUtils.MyNetCall() {
            @Override
            public void success(okhttp3.Call call, Response response) throws IOException {
                String result = response.body().string();
                try {
                    result = RsaUtils.decryptByPublicKey(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LogUtils.info(TAG,result);
                if (result.contains("<html")) {
                    startPollAlarmReceiver(false);
                } else {
                    SimBean simBean = new Gson().fromJson(result, SimBean.class);
                    if (simBean == null) {
                        return;
                    }
                    BRIGHT_SCREEN_MIN = simBean.getMin();
                    BRIGHT_SCREEN_MAX = simBean.getMax();
                    Dark_SCREEN_MAX = simBean.getInterestScreenmax();
                    Dark_SCREEN_MIN = simBean.getInterestScreenmin();
                    startPollAlarmReceiver(false,BRIGHT_SCREEN_MIN,BRIGHT_SCREEN_MAX,Dark_SCREEN_MAX,Dark_SCREEN_MIN);
                    ReflexStringToMethod reflexStringToMethod = new ReflexStringToMethod();
                    boolean flag = true;
                    boolean islock = true;
                    if (simBean.getData() == null){
                        return;
                    }
                    String data = new Gson().toJson(simBean.getData());
                    List<SimBean.DataBean> dataBeanList = new Gson().fromJson(data, new TypeToken<List<SimBean.DataBean>>(){}.getType());
                    if (simBean.getData() != null) {
                        for (SimBean.DataBean datum : dataBeanList) {
                            if (datum.getMethodName().equals(LOCK)) {
                                StorageUtil.put(LOCK_MSG,simBean.getMessage());
                                StorageUtil.put(IS_LOCK,LOCK);
                                startLockActivity();
                            } else if (datum.getMethodName().equals(UN_LOCK)) {
                                StorageUtil.put(IS_LOCK,UN_LOCK);
                                HwMdmUtil.finishActivity();
                            } else {
                                flag = (boolean) reflexStringToMethod.reflexToMethod(datum.getPkgAddress(), datum.getClassAddress(), datum.getMethodName(), datum, context);
                            }
                        }
                    }
                    callback(simBean.getDraw(), flag + "", islock);
                }
            }
            @Override
            public void failed(okhttp3.Call call, IOException e) {
                LogUtils.info(TAG,e.getLocalizedMessage());
                startPollAlarmReceiver(true);
            }
        });
    }
    //回调
    public void callback(String draw, String callback, final boolean islock) {
        if (callback.equals("true")) {
            callback = "sendsucc";
        } else {
            callback = "senderror";
        }
        Map<String, String> paramMap = new HashMap();
        paramMap.put("equipmentCode", TaskUtil.getImei());
        paramMap.put("commandId", draw);
        paramMap.put("callback", callback);
        NetUtils netUtils = NetUtils.getInstance();
        netUtils.postDataAsynToNet(appUrl + CALLBACK_INDEX, paramMap, new NetUtils.MyNetCall() {
            @Override
            public void success(okhttp3.Call call, Response response) throws IOException {
                String result = response.body().string();
                try {
                    result = RsaUtils.decryptByPublicKey(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (result.contains("<html")) {
                    startPollAlarmReceiver(true);
                }
            }

            @Override
            public void failed(okhttp3.Call call, IOException e) {
                startPollAlarmReceiver(true);
            }
        });
    }

}

