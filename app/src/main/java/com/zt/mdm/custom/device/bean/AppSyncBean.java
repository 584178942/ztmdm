package com.zt.mdm.custom.device.bean;

import java.util.List;

/**
 * @author ZT
 * @date
 */
public class AppSyncBean {

    /**
     * state : 50000
     * message : 同步成功，正常返回
     * sendState : null
     * data : {"sendState":"unlock","whiteList":["15514125333","18036432215"]}
     * uninstallCert : false
     * cert : null
     */

    private int state;
    private String message;
    private Object sendState;
    private DataBean data;
    private boolean uninstallCert;
    private Object cert;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getSendState() {
        return sendState;
    }

    public void setSendState(Object sendState) {
        this.sendState = sendState;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public boolean isUninstallCert() {
        return uninstallCert;
    }

    public void setUninstallCert(boolean uninstallCert) {
        this.uninstallCert = uninstallCert;
    }

    public Object getCert() {
        return cert;
    }

    public void setCert(Object cert) {
        this.cert = cert;
    }

    public static class DataBean {
        /**
         * sendState : unlock
         * whiteList : ["15514125333","18036432215"]
         */

        private String sendState;
        private List<String> whiteList;

        public String getSendState() {
            return sendState;
        }

        public void setSendState(String sendState) {
            this.sendState = sendState;
        }

        public List<String> getWhiteList() {
            return whiteList;
        }

        public void setWhiteList(List<String> whiteList) {
            this.whiteList = whiteList;
        }
    }
}
