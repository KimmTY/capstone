package com.capstone.sejong.homenect;

/**
 * Created by 12aud on 2017-05-19.
 */

public class ApInfo {
    final String ssid;
    final String password;
    final String topic;

    ApInfo(String ssid, String password, String topic){
        this.ssid = ssid;
        this.password = password;
        this.topic = topic;
    }
}
