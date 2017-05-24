package com.capstone.sejong.homenect;

/**
 * Created by 12aud on 2017-05-19.
 */

public class ApInfo {
    String ssid;
    String pwd;
    String topic;

    ApInfo(String ssid, String pwd, String topic){
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
}
