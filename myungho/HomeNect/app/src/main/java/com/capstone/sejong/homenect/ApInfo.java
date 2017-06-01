package com.capstone.sejong.homenect;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by 12aud on 2017-05-19.
 */

public class ApInfo {
    @SerializedName("ssid")
    @Expose
    String ssid;
    @SerializedName("pwd")
    @Expose
    String pwd;
    @SerializedName("topic")
    @Expose
    String topic;
//    @SerializedName("macAddr")
//    @Expose
//    String macAddr;
//    @SerializedName("result")
//    @Expose
//    String result;

    ApInfo(String ssid, String pwd, String topic) {
        this.ssid = ssid;
        this.pwd = pwd;
        this.topic = topic;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

/*
    public String getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }
*/

    @Override
    public String toString() {
        return "ApInfo{" +
                "macAddr='" + ssid + '\'' +
                ", result='" + pwd +
                '}';
    }
}
