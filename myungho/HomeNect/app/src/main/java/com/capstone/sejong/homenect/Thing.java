package com.capstone.sejong.homenect;

/**
 * Created by 12aud on 2017-03-31.
 */

public class Thing {
    private String name; // things 이름 or 맥주소
    private boolean status; // On/off 여부
    private boolean wifiStatus; // wifi 연결상태 여부
    private boolean timerStatus; // Timer On/Off 여부
    private boolean gpsStatus; // gps on/off 여부

    Thing(String name, boolean status, boolean wifiStatus, boolean timerStatus, boolean gpsStatus){
        this.name = name;
        this.status = status;
        this.wifiStatus = wifiStatus;
        this.timerStatus = timerStatus;
        this.gpsStatus = gpsStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isWifiStatus() {
        return wifiStatus;
    }

    public void setWifiStatus(boolean wifiStatus) {
        this.wifiStatus = wifiStatus;
    }

    public boolean isTimerStatus() {
        return timerStatus;
    }

    public void setTimerStatus(boolean timerStatus) {
        this.timerStatus = timerStatus;
    }

    public boolean isGpsStatus() {
        return gpsStatus;
    }

    public void setGpsStatus(boolean gpsStatus) {
        this.gpsStatus = gpsStatus;
    }
}
