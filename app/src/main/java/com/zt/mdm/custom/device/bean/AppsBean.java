package com.zt.mdm.custom.device.bean;

/**
 * @author ZT
 * @date
 */
public class AppsBean {
    public String appName;
    public String packageName;
    public String firstInstallTime;
    public String lastUpdateTime;
    public String versionName;
    public String versionCode;
    public String developer;
    public String runTime;

    public AppsBean(String appName, String packageName, String firstInstallTime, String lastUpdateTime, String versionName, String versionCode, String developer, String runTime){
        this.appName = appName;
        this.packageName = packageName;
        this.firstInstallTime = firstInstallTime;
        this.lastUpdateTime = lastUpdateTime;
        this.versionName = versionName;
        this.versionCode = versionCode;
        this.developer = developer;
        this.runTime = runTime;
    }
    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getFirstInstallTime() {
        return firstInstallTime;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getVersionCode() {
        return versionCode;
    }


    public String getDeveloper() {
        return developer;
    }

    public String getRunTime() {
        return runTime;
    }

    public void setRunTime(String runTime) {
        this.runTime = runTime;
    }
}
