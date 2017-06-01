package com.capstone.sejong.homenect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class AddUser extends AppCompatActivity {

    Button btn_ok;
    Button btn_cancel;
    EditText et;

    SharedPreferences pref;

    MqttAndroidClient client;
    IMqttToken token;
    //    static String MQTTHOST = "tcp://test.mosquitto.org:1883"; // localhost:port
    static String MQTTHOST = "tcp://58.236.6.251:1883"; // localhost:port
    String topicStr = "01091095924";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);


        pref = new SharedPreferences(this);

        et = (EditText) findViewById(R.id.et_add_user);

        btn_ok = (Button) findViewById(R.id.btn_ok_add_user);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pNum = et.getText().toString();
                if (validatePhoneNumber(pNum)) { // 유효한 핸드폰 번호일 경우

                    mqttPublish(pNum);
                    pref.putValue("user", pNum, "HomeNect"); // HomeNect file 안에 user 라는 key로 핸드폰번호 저장
                    Intent intent = new Intent();
                    intent.putExtra("phoneNumber", pNum);
                    setResult(RESULT_OK, intent);
                    Toast.makeText(getApplicationContext(), "등록 완료", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "유효하지 않은 핸드폰 번호입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_cancel = (Button) findViewById(R.id.btn_cancel_add_user);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private boolean validatePhoneNumber(String phoneNo) {
        if (Pattern.matches("^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$", phoneNo)) {
            return true;
        } else {
            return false;
        }
    }

    public void mqttPublish(String user) { // mqtt publish method
        String topic = "register";
        String message = "hello HomeNect!";

        JSONObject mqtt = new JSONObject(); // Json object
        JSONObject data = new JSONObject();
        JSONObject value = new JSONObject(); // value

        try {
            data.put("from", "app");
            data.put("to", "server");
            data.put("category", "register");
            value.put("phoneNumber", user);
            data.put("value",value);
            mqtt.put("data", data);

            message = mqtt.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            client.publish(topic, message.getBytes(), 0, false); // 퍼블리쉬
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume(){
        // MQTT
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(AddUser.this, MQTTHOST, clientId);

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
//        topicStr = pref.getValue("user", "01091095924", "HomeNect");
        super.onResume();
    }

    @Override
    protected void onDestroy(){
        mqttDisconn();
        super.onDestroy();
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
}
