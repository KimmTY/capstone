package com.capstone.sejong.homenect;

/**
 * Created by 12aud on 2017-05-24.
 */

public interface AdapterCallback {
    void showTimerDialog(int position);
    void mqttPublish(String control, int position);
}
