package com.zt.mdm.custom.device.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zt.mdm.custom.device.R;
import com.zt.mdm.custom.device.util.MdmUtil;
import com.zt.mdm.custom.device.util.TaskUtil;
import com.zt.mdm.custom.device.utils.LogUtils;
import com.zt.mdm.custom.device.utils.UpdateUtil;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.zt.mdm.custom.device.SGTApplication.policyManager;
import static com.zt.mdm.custom.device.util.MdmUtil.getAppRunInfo;
import static com.zt.mdm.custom.device.util.MdmUtil.getSoftwareInfo;

/**
 * @author Z T
 * @date 20200924
 */
public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    public static MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;
        ((TextView)findViewById(R.id.text1)).setText("当前CCID SIM1:" + TaskUtil.getImsiCode() + "    SIM2: " + TaskUtil.getImsiCode2());
        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
        findViewById(R.id.btn4).setOnClickListener(this);
        findViewById(R.id.btn5).setOnClickListener(this);
        findViewById(R.id.btn6).setOnClickListener(this);
        findViewById(R.id.btn7).setOnClickListener(this);
        findViewById(R.id.btn8).setOnClickListener(this);
        findViewById(R.id.btn9).setOnClickListener(this);
        findViewById(R.id.btn10).setOnClickListener(this);
        findViewById(R.id.btn11).setOnClickListener(this);
     }

    @Override
    public void onClick(View v) {
        LogUtils.info(TAG,"onclick");
        String pkgName = "com.baidu.searchbox.lite";
        LogUtils.info(TAG,"getId");
        switch (v.getId()) {
            case R.id.btn1:
                boolean isLock = policyManager.lockDevice("你好，已锁屏！");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TaskUtil.changeDownloadImage("https://gimg2.baidu.com/image_search/src=http%3A%2F%2F1812.img.pp.sohu.com.cn%2Fimages%2Fblog%2F2009%2F11%2F18%2F18%2F8%2F125b6560a6ag214.jpg&refer=http%3A%2F%2F1812.img.pp.sohu.com.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1624698251&t=085ca3d7dbf5ca30b724da7b9f1f633b");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                LogUtils.info("ISLOCK", isLock + "");
                break;
            case R.id.btn2:
                boolean isLock2 = policyManager.unlockDevice();
                LogUtils.info("unlockDevice",isLock2 + "");
                break;
            case R.id.btn3:
                LogUtils.info( TAG, "btn3");
                MdmUtil.uninstallPackage(pkgName);
                break;
            case R.id.btn4:
                String apkUrl = "http://gdown.baidu.com/data/wisegame/4fb53174d106c3a6/04d44fb53174d106c3a6af535c969fe2.apk";
                UpdateUtil.processInstall(apkUrl,pkgName);
                break;
            case R.id.btn5:
                try {
                    MdmUtil.addInstallWhiteList(new ArrayList<>());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn6:
                policyManager.setCameraPolicies(1);
                break;
            case R.id.btn7:
                LogUtils.info("getSoftwareInfo", "==" +getSoftwareInfo());

                /*//WIFI 热点管控
                policyManager.zsdkSetWifiAPStatus(0);
                //禁用wifi
                policyManager.setWlanPolicies(0);
                //禁用蓝牙
                policyManager.setBluetoothPolicies(0,null);
                //禁用gps
                policyManager.setGpsPolicies(0);

                policyManager.setNfcPolicies(0);
                //扩展存储访问控制
                policyManager.setExternalStoragePolicies(0);*/
               /* policyManager.setCaptureScreenPolicies(0);
                LogUtils.info("policyManager.getRootState();",policyManager.getRootState() + "");
                LogUtils.info("listIccid;",new Gson().toJson(policyManager.listIccid()));
                policyManager.setVoicePolicies(0);
                policyManager.setSmsPolicies(0,"");
                LogUtils.info("getSoftwareInfo;",new Gson().toJson(policyManager.getSoftwareInfo()));
                LogUtils.info("policyManager.getAppRunInfo();",new Gson().toJson(policyManager.getAppRunInfo()));
                LogUtils.info("getDeviceInfo", new Gson().toJson(policyManager.getDeviceInfo()) + "");
           */     break;
           case R.id.btn8:
               policyManager.setCaptureScreenPolicies(1);
               policyManager.setVoicePolicies(1);
               policyManager.setSmsPolicies(1,"");

               /*//WIFI 热点管控
               policyManager.zsdkSetWifiAPStatus(1);
               //禁用wifi
               policyManager.setWlanPolicies(3);
               //禁用蓝牙
               policyManager.setBluetoothPolicies(2,null);
               //禁用gps
               policyManager.setGpsPolicies(2);

               policyManager.setNfcPolicies(2);
               //扩展存储访问控制
               policyManager.setExternalStoragePolicies(2);*/
                break;
            case R.id.btn9:
                /*policyManager.zsdkSetNavigationBarStatus(1);
                String gpsStr = policyManager.getDevicePosition();
                LogUtils.info("gpsStr",gpsStr);*/
                String apps2 = "";
                MdmUtil.setRunAppPolicies(apps2);
                break;
            case R.id.btn10:
                MdmUtil.zsdkAddWhiteListTypeNumber();
                break;
            case R.id.btn11:
                MdmUtil.unZsdkAddWhiteListTypeNumber();
                break;
        }
    }
    public static MainActivity getMainActivity() {
        return mainActivity;
    }
}
