package com.zt.mdm.custom.device.bean;

import static com.zt.mdm.custom.device.util.AppConstants.defaultL;

/**
 * Created by zhoutao on 15/1/13.
 * 百度地图定位实体类
 */
public class BdLocationVo {
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }



    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    private String time;//时间

    public Double getLatitude() {
        if (defaultL.equals(latitude)) {
            latitude = 0.00;
        }
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLontitude() {
        if (defaultL.equals(lontitude)) {
            lontitude = 0.00;
        }
        return lontitude;
    }

    public void setLontitude(Double lontitude) {
        this.lontitude = lontitude;
    }

    private Double latitude;//纬度
    private Double lontitude;//经度
    private String addr;//地址
    private String city;//城市
}
