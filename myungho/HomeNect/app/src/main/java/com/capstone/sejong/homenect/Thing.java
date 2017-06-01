package com.capstone.sejong.homenect;

/**
 * Created by 12aud on 2017-03-31.
 */

public class Thing {
    private String name; // things 이름 or 맥주소
    private boolean status; // On/off 여부
    private boolean wifiStatus; // wifi 연결상태 여부
    private boolean timerStatus; // Timer On/Off 여부
    private String timerTime; // Timer 시간
    private boolean timerTimeControl; // 예약시간에 어떤 작동을 할 것인지 (on/ off)
    private boolean gpsStatus; // gps on/off 여부
    private String macAddr; // mac address

    /**
     *
     * @param name Thing이름
     * @param macAddr Thing 맥 주소
     * @param status Thing 상태(on/off)
     * @param wifiStatus wifi 연결상태
     * @param timerStatus 예약제어 상태(on/off)
     * @param timerTime 예약 시간 (HH:mm)
     * @param timerTimeControl 예약제어 시 thing의 on/off 여부
     * @param gpsStatus 위치기반 제어 상태
     */
    Thing(String name, String macAddr, boolean status, boolean wifiStatus, boolean timerStatus, String timerTime, boolean timerTimeControl, boolean gpsStatus) {
        this.name = name;
        this.macAddr = macAddr;
        this.status = status;
        this.wifiStatus = wifiStatus;
        this.timerStatus = timerStatus;
        this.timerTime = timerTime;
        this.timerTimeControl = timerTimeControl;
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

    public void setTimerTime(String time) {
        this.timerTime = time;
    }

    public String getTimerTime() {
        return this.timerTime;
    }

    public String getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    public boolean isTimerTimeControl() {
        return timerTimeControl;
    }

    public void setTimerTimeControl(boolean timerTimeControl) {
        this.timerTimeControl = timerTimeControl;
    }
}
