package com.zt.mdm.custom.device.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zt.mdm.custom.device.SGTApplication;
import com.zt.mdm.custom.device.bean.SimBean;
import com.zt.mdm.custom.device.util.RsaUtils;
import com.zt.mdm.custom.device.util.StorageUtil;
import com.zt.mdm.custom.device.util.TaskUtil;
import com.zt.mdm.custom.device.utils.LogUtils;
import com.zt.mdm.custom.device.utils.NetUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

import static com.zt.mdm.custom.device.activity.LockActivity.getLockActivity;
import static com.zt.mdm.custom.device.util.AppConstants.ACTIVE_RECEIVE_BUS_INDEX;
import static com.zt.mdm.custom.device.util.AppConstants.CALLBACK_INDEX;
import static com.zt.mdm.custom.device.util.AppConstants.EQUIPMENT_CODE;
import static com.zt.mdm.custom.device.util.AppConstants.IS_LOCK;
import static com.zt.mdm.custom.device.util.AppConstants.LOCK;
import static com.zt.mdm.custom.device.util.AppConstants.LOCK_MSG;
import static com.zt.mdm.custom.device.util.AppConstants.SIM_CODE1;
import static com.zt.mdm.custom.device.util.AppConstants.SIM_CODE2;
import static com.zt.mdm.custom.device.util.AppConstants.UN_LOCK;

/**
 * @author Z T
 * @data 20200924
 */
public class HwMdmUtil {
    private static final String TAG = "MdmUtil";
    /**
     * 主动触发
     */
    public static void replaceSim() {
        Map paramMap = new HashMap();
        paramMap.put(SIM_CODE1, TaskUtil.getImsiCode());
        paramMap.put(SIM_CODE2, TaskUtil.getImsiCode2());
        paramMap.put(EQUIPMENT_CODE, TaskUtil.getImei());
        paramMap.put("command", "replaceSim");
        LogUtils.info(null, paramMap.toString());
        NetUtils netUtils = NetUtils.getInstance();
        netUtils.postDataAsynToNet(NetUtils.appUrl + ACTIVE_RECEIVE_BUS_INDEX, paramMap, new NetUtils.MyNetCall() {
            @Override
            public void success(Call call, Response response) throws IOException {
                String result = response.body().string();
                result = RsaUtils.decryptByPublicKey(result);
                LogUtils.info(null, result);
                if (!result.contains("<`")) {
                    Gson gson = new Gson();
                    SimBean simBean = gson.fromJson(result, SimBean.class);
                    if (simBean == null){
                        return;
                    }
                    ReflexStringToMethod reflexStringToMethod = new ReflexStringToMethod();
                    boolean flag = false;
                    String data = new Gson().toJson(simBean.getData());
                    List<SimBean.DataBean> dataBeanList = new Gson().fromJson(data, new TypeToken<List<SimBean.DataBean>>(){}.getType());
                    if (simBean.getData() != null) {
                        for (SimBean.DataBean datum : dataBeanList) {
                            // 根据反射调用后台配置方法
                            flag = (boolean) reflexStringToMethod.reflexToMethod(datum.getPkgAddress(), datum.getClassAddress(), datum.getMethodName(), datum, SGTApplication.getContextApp());
                            if (!flag) {
                                break;
                            } else {
                                if (datum.getMethodName().equals(LOCK)) {
                                    StorageUtil.put(LOCK_MSG,simBean.getMessage());
                                    StorageUtil.put(IS_LOCK,LOCK);
                                    //启用锁机页面
                                    TaskUtil.startLockActivity();
                                } else if (datum.getMethodName().equals(UN_LOCK)) {
                                    StorageUtil.put(IS_LOCK,UN_LOCK);
                                    finishActivity();
                                }
                            }
                        }
                        // 方法全部调用成功，回调
                        if (flag) {
                            callback(simBean.getDraw(), flag + "");
                        }
                    }
                }
            }
            @Override
            public void failed(Call call, IOException e) {
            }
        });
    }

    /**
     * @param draw     事务ID
     * @param callback 返回成功与否
     */
    public static void callback(String draw, String callback) {
        if (callback.equals("true")) {
            callback = "sendsucc";
        } else {
            callback = "senderror";
        }
        Map<String, String> paramMap = new HashMap();
        paramMap.put(EQUIPMENT_CODE , TaskUtil.getImei());
        paramMap.put("commandId", draw);
        paramMap.put("callback", callback);
        LogUtils.info(null, paramMap.toString());
        NetUtils netUtils = NetUtils.getInstance();
        netUtils.postDataAsynToNet(NetUtils.appUrl + CALLBACK_INDEX, paramMap, new NetUtils.MyNetCall() {
            @Override
            public void success(Call call, Response response) throws IOException {
                String result = response.body().string();
                try {
                    result = RsaUtils.decryptByPublicKey(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LogUtils.info(TAG,  result);
            }
            @Override
            public void failed(Call call, IOException e) {

            }
        });
    }

    public static void finishActivity() {
        if (getLockActivity() != null) {
            getLockActivity().finish();
        }
    }

}
