
package com.zt.mdm.custom.device.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.provider.CallLog;

import com.google.gson.Gson;
import com.zt.mdm.custom.device.R;
import com.zt.mdm.custom.device.SGTApplication;
import com.zt.mdm.custom.device.bean.AppsBean;
import com.zt.mdm.custom.device.utils.AlertDialogUtils;
import com.zt.mdm.custom.device.utils.LogUtils;
import org.json.JSONArray;
import org.json.JSONException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ga.mdm.PolicyManager;
import static com.zt.mdm.custom.device.SGTApplication.contextApp;
import static com.zt.mdm.custom.device.SGTApplication.getContextApp;
import static com.zt.mdm.custom.device.SGTApplication.policyManager;
import static com.zt.mdm.custom.device.util.AppConstants.CALL_WHITE_LIST;

/**
 * @author ZT
 * @date 20201027
 */
public class MdmUtil {
    private static final String TAG = "MdmUtil";
    /**
     * 通话记录的联系人
     * 通话记录的电话号码
     * 通话记录的日期
     * 通话时长
     * 通话类型
     */
    private static String[] columns = {CallLog.Calls.CACHED_NAME
            , CallLog.Calls.NUMBER
            , CallLog.Calls.DATE
            , CallLog.Calls.DURATION
            , CallLog.Calls.TYPE};

    private static final PolicyManager pm = PolicyManager.getInstance(contextApp);

    /**
     * 恢复出厂设置
     */
    public static void clearData() {
        try {
            Class vcs = Class.forName("com.vivo.services.cust.VivoCustomManager");
            Method clearData = vcs.getDeclaredMethod("clearData");
            clearData.invoke(vcs.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 软件安装信息上报控制
     * @return
     */
    public static List<String[]> getAppRunInfo(){
        return pm.getAppRunInfo();
    }

    /**
     * 获取学生机 应用基本信息
     * @return
     */
    public static String getSoftwareInfo(){
        List<String[]> appRun = pm.getAppRunInfo();
        String[][] softWares =  pm.getSoftwareInfo();
        List<AppsBean> apps = new ArrayList<>();
        AppsBean appsBean;
        for (String[] softWare:softWares) {
            appsBean = new AppsBean(softWare[0], softWare[1], softWare[2], softWare[3], softWare[4], softWare[5],softWare[6],"0");
            apps.add(appsBean);
        }
        for (int k = 0; k < apps.size(); k++) {
            for (int i = 0; i < appRun.size(); i++) {
                if (appRun.get(i)[2].equals(apps.get(k).getPackageName())){
                    apps.get(k).setRunTime(appRun.get(i)[3]);
                    //LogUtils.info(apps.get(k).getPackageName() + "appRun.get(i)[3]",appRun.get(i)[3]);
                    //LogUtils.info("apps.get(k).getRunTime()",apps.get(k).getRunTime());
                }
            }
        }
        //LogUtils.info("size" , apps.size() + "");
        return new Gson().toJson(apps);
    }

    /**
     * 启用通话白名单
     * @return true 成功 false 失败
     */
    public static boolean zsdkAddWhiteListTypeNumber(){
        pm.zsdkSetTelephonyWhiteListTypeStatus(1);
        List<String> whiteList = new Gson().fromJson(StorageUtil.get(CALL_WHITE_LIST,"").toString(),List.class);
        pm.zsdkAddWhiteListTypeNumber(whiteList,1);
        return true;
    }

    /**
     * 禁用通话白名单
     * @return true 成功 false 失败
     */
    public static boolean unZsdkAddWhiteListTypeNumber() {
        pm.zsdkSetTelephonyWhiteListTypeStatus(2);
        pm.zsdkRemoveWhiteListTypeNumber(pm.zsdkGetWhiteListTypeNumber(1),1);
        return true;
    }
    public static void showDialog(Context context){
        /*AlertDialogUtils utils = AlertDialogUtils.getInstance();
        AlertDialogUtils.showConfirmDialog(context);
//按钮点击监听
        utils.setOnButtonClickListener(new AlertDialogUtils.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(AlertDialog dialog,String transactionId) {
                dialog.dismiss();
            }

            @Override
            public void onNegativeButtonClick(AlertDialog dialog) {
                dialog.dismiss();
            }
        });*/
        List<String> phoneList = pm.zsdkGetWhiteListTypeNumber(1);
        String[] phone = phoneList.toArray(new String[phoneList.size()]);
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("选择手机号")
                .setItems(phone, new DialogInterface.OnClickListener() {//添加列表
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TaskUtil.callPhone(phone[i]);
                    }
                })
                .create();
        alertDialog.show();
    }
    /**
     * 0：黑名单(应用包名列表中的所有项均强制卸载)；
     * 1：白名单(应用包名列表中的所有项禁止卸载)。
     * appPackageNames：数组中每一项为一个 JSON 格式字符串，格式如下：{"name": "ga.gab.appstore", "thumbprint": "00112233445566778899AABBCCDDEEFF"}，或者
     * 为包名列表。
     *
     * 禁止应用卸载
     */
    public static boolean setAppUninstallationPolicies(int mode, String appPackageNames){
        String[] appPacks = appPackageNames.split("\\,");
        return pm.setAppUninstallationPolicies(mode, appPacks);
    }

    /**
     * usb 工作模式控制
     * @param mode
     * 0：不允许用户或应用修改本机时间及时间来源，并强制同步移动网络时间；
     * 1：允许用户或应用修改本机时间，以及设定时间来源。
     * @return 成功返回 true；失败返回 false
     */
    public static boolean setUserTimeMgrPolicies(int mode){
        return  pm.setUserTimeMgrPolicies(mode);
    }

    /**
     * 数据擦除
     */
    public static boolean wipeDeviceData(){
        return pm.wipeDeviceData();
    }

    /**
     * 应用运行控制
     */
    public static boolean setRunAppPolicies(String appPacks){
        String[] appNames = appPacks.split("\\,");
        return pm.setRunAppPolicies(0,appNames);
    }

    /**
     * 时间设置功能控制
     * @param mode
     * @return
     */
    public static boolean setUsbDataPolicies(int mode){
        return  pm.setUsbDataPolicies(mode);
    }

    /**
     * 获取通话时长
     */
    public static List getCallLog() {
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> map;
        Cursor cursor = SGTApplication.getContextApp().getApplicationContext().getContentResolver().query(CallLog.Calls.CONTENT_URI,
                columns,  null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
        while (cursor.moveToNext()) {
            map = new HashMap<>();
            String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));  //号码
            long dateLong = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)); //获取通话日期
            String callDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(dateLong));
            String startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(dateLong));
            int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));//获取通话时长，值为多少秒
            int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE)); //获取通话类型：1.呼入2.呼出3.未接
            LogUtils.info(TAG,"getCallLog" + startTime);
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, -1);//获取昨天时间
            Date yesterday = c.getTime();
            String newDate = new SimpleDateFormat("yyyy-MM-dd").format(yesterday);
            // LogUtils.info(TAG,callDate + ":" +  newDate);
            List<String> callDateList = Arrays.asList(callDate.split("-"));
            /*if (DateUtils.isDateOneBigger(callDate,newDate)) {
                map.put("mobile",number);
                map.put("duration",duration);
                map.put("startTime",startTime);
                map.put("type",type);
                map.put("year",callDateList.get(0));
                map.put("month",callDateList.get(1));
                map.put("day",callDateList.get(2));
                list.add(map);
                LogUtils.info(TAG,new Gson().toJson(callDateList));
            }*/
        }
        return list;
    }

    /**
     * 机卡绑定
     */
    public static void bindPhone(){
        try {
            int i = 0;
            /*vivoTelecomControl.setTelephonyPhoneState(i,i,i);
            vivoTelecomControl.setTelephonySmsState(i,i,i);
            vivoOperationControl.setBackKeyEventState(i);
            vivoOperationControl.setMenuKeyEventState(i);
            vivoOperationControl.setHomeKeyEventState(i);
            vivoOperationControl.setStatusBarState(i);
            TaskUtil.startBindActivity();*/
        } catch (Exception e) {
            LogUtils.info(TAG + "bindPhone",e.getLocalizedMessage());
        }

    }

    /**
     * 解除机卡绑定
     */
    public static void unBindPhone(){
        try {
            int i = 0;
            int k = 1;
           /* vivoTelecomControl.setTelephonyPhoneState(i,k,k);
            vivoTelecomControl.setTelephonySmsState(i,k,k);
            vivoOperationControl.setBackKeyEventState(k);
            vivoOperationControl.setMenuKeyEventState(k);
            vivoOperationControl.setHomeKeyEventState(k);
            vivoOperationControl.setStatusBarState(k);
            TaskUtil.closeBindActivity();*/
            // TaskUtil.startLockActivity();
        } catch (Exception e) {
            LogUtils.info(TAG + "unBindPhone",e.getLocalizedMessage());
        }
    }

    /**
     * 锁机
     */
    public static void lockPhone(){
        try {
            int i = 0;
            /*vivoTelecomControl.setTelephonyPhoneState(i,1,i);
            vivoTelecomControl.setTelephonySmsState(i,i,i);
            vivoOperationControl.setBackKeyEventState(i);
            vivoOperationControl.setMenuKeyEventState(i);
            vivoOperationControl.setHomeKeyEventState(i);
            vivoOperationControl.setStatusBarState(i);*/
            TaskUtil.startLockActivity();
        } catch (Exception e){
            LogUtils.info(TAG + "lockPhone",e.getLocalizedMessage());
        }
    }

    /**
     * 解锁
     */
    public static void unLockPhone(){
        try {
            int i = 1;
            /*vivoTelecomControl.setTelephonyPhoneState(0,i,i);
            vivoTelecomControl.setTelephonySmsState(0,i,i);
            vivoOperationControl.setBackKeyEventState(i);
            vivoOperationControl.setMenuKeyEventState(i);
            vivoOperationControl.setHomeKeyEventState(i);
            vivoOperationControl.setStatusBarState(i);
            TaskUtil.closeLockActivity();*/
        } catch (Exception e) {
            LogUtils.info(TAG + "unLockPhone",e.getLocalizedMessage());
        }
    }

    /**
     * 添加安装应用白名单
     * @param pkgList 包名List
     */
    public static void addInstallWhiteList(List<String> pkgList) throws JSONException {
        try {
            List<Map<String,String>> whiteList = new ArrayList();
            whiteList.add(addWhiteList(contextApp.getPackageName()));
            if (pkgList.size() >= 1) {
                for (String pkgName : pkgList) {
                    whiteList.add(addWhiteList(pkgName));
                }
            } else {
                whiteList.add(addWhiteList("com.baidu.searchbox.lite"));
                whiteList.add(addWhiteList("com.tencent.wework"));
                /*whiteList.add(addWhiteList("com.ss.android.article.lite"));
                whiteList.add(addWhiteList("com.ss.android.article.news"));
                whiteList.add(addWhiteList("com.ss.android.ugc.aweme"));
                whiteList.add(addWhiteList("us.zoom.videomeetings"));*/
            }
            JSONArray param;
            param = new JSONArray(new Gson().toJson(whiteList));
            String[] whiteArr = new String[param.length()];
            //关联模板的id集合
            for (int i = 0; i < whiteArr.length; i++) {
                whiteArr[i] = param.get(i).toString();
            }
            Boolean flag = pm.setAppInstallationPolicies(1, whiteArr);
            LogUtils.info("setAppInstallationPolicies  flag=" + flag ,"  whiteArr:" + new Gson().toJson(whiteArr));
            LogUtils.info("getAppInstallationPolicies:" ,new Gson().toJson(pm.getAppInstallationPolicies()));

        } catch (Exception e){
            LogUtils.info(TAG + "addInstallWhiteList",e.getLocalizedMessage());
        }
    }

    public static Map addWhiteList(String pkgName){
        Map<String, String> whiteMap =  new HashMap<>();
        whiteMap.put("name", pkgName);
        whiteMap.put("thumbprint", "00112233445566778899AABBCCDDEEFF");
        return whiteMap;
    }

    /**
     * 移除安装应用白名单
     */
    public static void clearInstallWhiteList(){
        try{
            /*vivoApplicationControl.setInstallPattern(0);
            vivoApplicationControl.clearInstallWhiteList();*/
            // LogUtils.info("com.siyu.mdm.custom.device",vivoApplicationControl.getInstallWhiteList().toString());
        } catch (Exception e){
            LogUtils.info(TAG + "clearInstallWhiteList",e.getLocalizedMessage());
        }
    }

    /**
     * 获取IccId
     */
    public static List<String> getPhoneIccids(){
        try{
            //return vivoDeviceInfoControl.getPhoneIccids();
        } catch (Exception e){
            LogUtils.info(TAG + "getPhoneIccids",e.getLocalizedMessage());
            return new ArrayList<>();
        }

        return null;
    }
    /**
     * 获取imei
     */
    public static String getPhoneImeis(){
        try{
            return null;
            //return vivoDeviceInfoControl.getPhoneImeis().get(0);
        } catch (Exception e){
            LogUtils.info(TAG + "getPhoneImeis",e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * 静默安装
     * @param path 安装路径
     * @param pkg 包名
     */
    public static void installPackage(final String path, String pkg){
        policyManager.installPackage(path);
        /*try{
            vivoApplicationControl.installPackageWithObserver(path,2,pkg,new CustPackageInstallObserver(){
                @Override
                public void onPackageInstalled(String basePackageName, int returnCode, String msg, Bundle extras) {
                    super.onPackageInstalled(basePackageName, returnCode, msg, extras);
                    LogUtils.info("",basePackageName + returnCode +msg);
                    TaskUtil.delete(path);
                }
            });
        } catch (Exception e){
            LogUtils.info(TAG + "installPackage",e.getLocalizedMessage());
        }*/
    }

    /**
     * 禁止卸载
     */
    public static void setUninstall(){
        try{
           /* if (vivoApplicationControl.getUninstallPattern() != UNINSTALL_PATTERN){
                vivoApplicationControl.setUninstallPattern(UNINSTALL_PATTERN);
            }
            //LogUtils.info("setUninstall",vivoApplicationControl.getUninstallPattern() + "");
            List<String> pakList = new ArrayList<>();
            pakList.add(SGTApplication.getContextApp().getPackageName());
            vivoApplicationControl.addUninstallBlackList(pakList);*/
            //LogUtils.info(TAG,vivoApplicationControl.getUninstallPattern() + "addUninstallBlackList" + vivoApplicationControl.getUninstallBlackList());
        } catch (Exception e){
            LogUtils.info(TAG + "installPackage",e.getLocalizedMessage());
        }
    }

    /**
     * 静默卸载
     * @param pkgName 包名
     */
    public static void uninstallPackage(String pkgName){
        policyManager.uninstallPackage(pkgName);
        try{
           /* LogUtils.info("deletePackage",pkgName);
            vivoApplicationControl.deletePackageWithObserver(pkgName,2,new CustPackageDeleteObserver(){
                @Override
                public void onPackageDeleted(String basePackageName, int returnCode, String msg) {
                    super.onPackageDeleted(basePackageName, returnCode, msg);
                    LogUtils.info("onPackageDeleted",basePackageName+returnCode+msg);
                }
            });*/
        } catch (Exception e){
            LogUtils.info(TAG + "installPackage",e.getLocalizedMessage());
        }
    }

    /**
     * 静默卸载 deletePackage
     * @param pkgName 包名
     */
    public static void deletePackage(String pkgName){
        try{
            LogUtils.info("deletePackage",pkgName);
            //  vivoApplicationControl.deletePackage(pkgName,1);
        } catch (Exception e){
            LogUtils.info(TAG + "installPackage",e.getLocalizedMessage());
        }
    }

}
