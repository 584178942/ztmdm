package com.zt.mdm.custom.device;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.google.gson.Gson;
import com.zt.mdm.custom.device.bean.BdLocationVo;
import com.zt.mdm.custom.device.receiver.AppInstallReceiver;
import com.zt.mdm.custom.device.receiver.BatteryBroadcastReceiver;
import com.zt.mdm.custom.device.receiver.CallPhoneStateReceiver;
import com.zt.mdm.custom.device.receiver.NetworkChangedReceiver;
import com.zt.mdm.custom.device.receiver.ScreenStatusReceiver;
import com.zt.mdm.custom.device.receiver.SimStateReceiver;
import com.zt.mdm.custom.device.util.AppConstants;
import com.zt.mdm.custom.device.util.MdmUtil;
import com.zt.mdm.custom.device.util.StorageUtil;
import com.zt.mdm.custom.device.utils.BdLocationUtil;
import com.zt.mdm.custom.device.utils.LogUtils;
import ga.mdm.PolicyManager;

import static com.zt.mdm.custom.device.util.AppConstants.FIVE_SECOND;
import static com.zt.mdm.custom.device.util.AppConstants.IS_LOCK;
import static com.zt.mdm.custom.device.util.AppConstants.LOCK;
import static com.zt.mdm.custom.device.util.AppConstants.UN_LOCK;
import static com.zt.mdm.custom.device.util.TaskUtil.startBasisReceiver;
import static com.zt.mdm.custom.device.util.TaskUtil.startLockActivity;
import static com.zt.mdm.custom.device.util.TaskUtil.startPollAlarmReceiver;

/**
 * @author Z T
 */
public class SGTApplication extends Application {
    private static final String TAG = "SGTApplication";
    private static  Context ourInstance;
    private NetworkChangedReceiver networkChangedReceiver;
    private ScreenStatusReceiver mScreenStatusReceiver;
    private AppInstallReceiver appInstallReceiver;
    private SimStateReceiver simStateReceiver;
    private BatteryBroadcastReceiver batteryBroadcastReceiver;
    private BroadcastReceiver mLocalBroadcastReceiver;
    private CallPhoneStateReceiver callPhoneStateReceiver;
    public static Application contextApp;
    private static BdLocationUtil bdLocationUtil;
    public static PolicyManager policyManager;
    public static Context getContextApp() {
        return ourInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setContextApp(this);
        setPolicyManager();
        ourInstance = getApplicationContext();
        if (LOCK.equals(StorageUtil.get(IS_LOCK,UN_LOCK))){
            startLockActivity();
        }
        startPollAlarmReceiver(true);
        startBasisReceiver(FIVE_SECOND);
        contractStatusCheck();
        getBdLocationUtil();
    }
    private void setContextApp(Application application) {
        contextApp = application;
    }

    /**
     * 添加应用保活白名单
     */
    private void setPolicyManager(){
        policyManager = PolicyManager.getInstance(contextApp);
        if (PolicyManager.STATUS_ENABLE == policyManager.zsdkGetAppKeepaliveStatus()){
            policyManager.zsdkSetAppKeepaliveStatus(PolicyManager.STATUS_ENABLE);
            policyManager.zsdkAddKeepaliveApp(contextApp.getPackageName());
        }
        MdmUtil.setAppUninstallationPolicies(1,getPackageName());
        /*policyManager.setAppPermission(getPackageName(),"[" +
                "{\"permission\": \"RECEIVE_BOOT_COMPLETED\", \"mode\": \"ALLOWED\"}," +
                "{\"permission\": \"READ_PHONE_STATE\", \"mode\": \"ALLOWED\"}," +
                "{\"permission\": \"WAKE_LOCK\", \"mode\": \"ALLOWED\"}" +
                "{\"permission\": \"ACCESS_COARSE_LOCATION\", \"mode\": \"ALLOWED\"}" +
                "{\"permission\": \"ACCESS_FINE_LOCATION\", \"mode\": \"ALLOWED\"}" +
                "{\"permission\": \"ACCESS_WIFI_STATE\", \"mode\": \"ALLOWED\"}" +
                "{\"permission\": \"ACCESS_NETWORK_STATE\", \"mode\": \"ALLOWED\"}" +
                "{\"permission\": \"CHANGE_WIFI_STATE\", \"mode\": \"ALLOWED\"}" +
                "{\"permission\": \"SIM_STATE_CHANGED\", \"mode\": \"ALLOWED\"}" +
                "{\"permission\": \"SYSTEM_ALERT_WINDOW\", \"mode\": \"ALLOWED\"}" +
                "]");*/
    }
    private void contractStatusCheck() {
        /* if (TaskUtil.isContractFinish()) {
            TaskUtil.removeActiveAdmin();
            return;
        }
        VendorCountryUtil.getInstance().init();
        PollAlarmReceiver.setCountNumber(0);
        TaskUtil.startAlarm();
        readPubKey(); */
        registerReceiver();
        //initWhenBoot();
        initContractBroadcast();
    }

    private void initContractBroadcast() {
//        mDevicePolicyManager = DevicePolicyManager.getInstance(this);
//        securityPolicy = mDevicePolicyManager.getSecurityPolicy();
        this.mLocalBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (AppConstants.CONTRACT_COMPLETED_ACTION.equals(intent.getAction())) {
                    SGTApplication.this.unRegisterReceiver();
                }
            }
        };
       // registerContractBroadcast();
    }

    private void registerContractBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstants.CONTRACT_COMPLETED_ACTION);
        contextApp.registerReceiver(this.mLocalBroadcastReceiver, intentFilter);
    }

    public static BdLocationVo getBdLocationUtil() {
        bdLocationUtil = BdLocationUtil.getInstance(contextApp);
        return bdLocationUtil.getLocation();
    }

    private void registerReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
        simStateReceiver = new SimStateReceiver();
        registerReceiver(simStateReceiver, intentFilter);

        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangedReceiver = new NetworkChangedReceiver();
        registerReceiver(networkChangedReceiver, intentFilter2);

        IntentFilter intentFilter3 = new IntentFilter();
        intentFilter3.addAction("android.intent.action.SCREEN_ON");
        intentFilter3.addAction("android.intent.action.SCREEN_OFF");
        mScreenStatusReceiver = new ScreenStatusReceiver();
        registerReceiver(mScreenStatusReceiver, intentFilter3);

        IntentFilter intentFilter4 = new IntentFilter();
        intentFilter4.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter4.addAction("android.intent.action.PACKAGE_REMOVED");
        appInstallReceiver = new AppInstallReceiver();
        registerReceiver(appInstallReceiver, intentFilter4);

        IntentFilter intentFilter5 = new IntentFilter();
        intentFilter5.addAction("android.intent.action.ACTION_POWER_CONNECTED");
        intentFilter5.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
        batteryBroadcastReceiver = new BatteryBroadcastReceiver();
        registerReceiver(batteryBroadcastReceiver, intentFilter5);

        //打电话广播
        IntentFilter callPhoneStateIntentFilter = new IntentFilter();
        callPhoneStateIntentFilter.addAction("android.intent.action.PHONE_STATE");
        callPhoneStateReceiver = new CallPhoneStateReceiver();
        registerReceiver(callPhoneStateReceiver, callPhoneStateIntentFilter);
    }

    public void unRegisterReceiver() {
        LogUtils.info(TAG, "unRegisterReceiver when contract completed");
        if (simStateReceiver != null) {
            unregisterReceiver(simStateReceiver);
        }
        if (networkChangedReceiver != null) {
            unregisterReceiver(networkChangedReceiver);
        }
        if (mScreenStatusReceiver != null) {
            unregisterReceiver(mScreenStatusReceiver);
        }
        if (appInstallReceiver != null) {
            unregisterReceiver(appInstallReceiver);
        }
        if (batteryBroadcastReceiver != null) {
            unregisterReceiver(batteryBroadcastReceiver);
        }
        if (callPhoneStateReceiver != null) {
            unregisterReceiver(callPhoneStateReceiver);
        }
    }
    private void initWhenBoot() {
       /* if (Settings.Global.getInt(contextApp.getContentResolver(), "device_provisioned", 0) == 0) {
            getContentResolver().registerContentObserver(Settings.Secure.getUriFor("device_provisioned"), true, this.mContentObserver);
            return;
        }*/
        shouldShowLockPage();
      /*  HwPushUtil.getToken();
        startPrivacyDialog();*/
    }


    /**
     *
     * access modifiers changed from: private
     */
    public void shouldShowLockPage() {
//        whiteList = new ArrayList<>();
//        whiteList = securityPolicy.getNetworkAccessWhitelist();
//        if (whiteList.size() > 0) {
//            LogUtils.info(TAG, "shouldShowLockPage when app restart");
//            TaskUtil.startLockActivity();
//        }
    }

}
