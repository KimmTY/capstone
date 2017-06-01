package com.capstone.sejong.homenect.STT;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.sejong.homenect.R;
import com.capstone.sejong.homenect.SharedPreferences;
import com.dnkilic.waveform.WaveView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;
import org.json.JSONObject;

public class SttActivity extends AppCompatActivity {

    public static WaveView waveView;
    Handler handler;
    SharedPreferences pref;
    String[] thingNames;
    StringBuilder things;
    TextView tv_thingName;

    //MQTT
    MqttAndroidClient client;
    IMqttToken token;
    //    static String MQTTHOST = "tcp://test.mosquitto.org:1883"; // localhost:port
    static String MQTTHOST = "tcp://58.236.6.251:1883"; // localhost:port
    String topicStr = "01091095924";

    LocalBroadcastManager mLocalBroadcastManager;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("com.sttactivity.action.close")){
                finish();
            }else if(intent.getAction().equals("mqtt.publish.control.thing")){

                // MQTT
                String clientId = MqttClient.generateClientId();
                client = new MqttAndroidClient(SttActivity.this, MQTTHOST, clientId);

                if(!client.isConnected()){
                    try {
                        // IMqttToken token = client.connect(options);
                        token = client.connect();
                        token.setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                // 성공
                                Toast.makeText(getApplicationContext(), "Mqtt connected!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                // 실패
                                Toast.makeText(getApplicationContext(), "Mqtt connection failed", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                topicStr = pref.getValue("user", "01091095924", "HomeNect");
                String thingName = intent.getStringExtra("thingName");
                String opCode = "0";
                if(intent.getStringExtra("control").equals("on")){
                    opCode = "1";
                }
                mqttPublish(thingName, opCode);

                mqttDisconn();
            }
        }
    };

    public void mqttPublish(String thingName, String opCode){
        String macAddr = pref.getValue("macAddr", "macAddr", thingName);
        String message = "hello HomeNect!";
        try {
            JSONObject mqtt = new JSONObject(); // Json object
            JSONObject data = new JSONObject();
            JSONObject value = new JSONObject(); // value

            data.put("from", "app");
            data.put("to", "thing");
            data.put("category", "control");
            value.put("macAddr", macAddr);
            value.put("opCode", opCode);
            data.put("value",value);

            mqtt.put("data", data);

            message = mqtt.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            client.publish(topicStr, message.getBytes(), 0, false); // 퍼블리쉬
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_stt);

        thingNames = new String[4];
        things = new StringBuilder();
        pref = new SharedPreferences(this);
        for(int i = 0; i < 4; i++){
            // for 문
            String temp = pref.getValue("thing" + i, "thingEmpty", "HomeNect");
            if(temp != "thingEmpty"){
                thingNames[i] = temp;
                if(i == 0){
                    things.append(temp);
                } else {
                    things.append("\n" + temp);
                }
            } else {
                break;
            }
        }

        waveView = (WaveView) findViewById(R.id.waveView_stt);
        tv_thingName = (TextView) findViewById(R.id.tv_thing_name);
        tv_thingName.setText(things.toString());
        handler = new Handler();

        request_read_audio(); // permission request

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.sttactivity.action.close");
        mIntentFilter.addAction("mqtt.publish.control.thing");
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, mIntentFilter);

        startService(new Intent(getApplicationContext(), SpottingService.class)); // Start CMUSphinx by Service
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8),(int) (height * .4));

//        waveView.initialize(dm);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                waveView.initialize(dm);
            }
        }, 2000);
    }
    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        try {
            // stop all Services
            stopService(new Intent(getApplicationContext(), SpottingService.class));
            stopService(new Intent(getApplicationContext(), SttService.class));
            waveView.stop();
        } catch (Exception e) {
        }
        mqttDisconn();
        super.onDestroy();
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
    }
    public void mqttDisconn() {
        if(client == null){
            return;
        }
        if(!client.isConnected()){
            return;
        }
        try {
            IMqttToken token = client.disconnect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(getApplicationContext(), "Mqtt disconnected!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(getApplicationContext(), "Mqtt could not disconnect..", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    // permission/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    request_read_audio();
                } else {
                    finish();
                }
            }
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    request_write_external_storage();
                } else {
                    finish();
                }
            }
        }
    }

    public void request_read_audio() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            }
        } else {
        }
    }

    public void request_write_external_storage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CHANGE_WIFI_STATE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            }
        } else {
        }
    }
}
