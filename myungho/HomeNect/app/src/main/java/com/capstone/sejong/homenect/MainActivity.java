package com.capstone.sejong.homenect;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TimePicker;
import android.widget.Toast;

import com.capstone.sejong.homenect.STT.SttActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterCallback{

    public static int TIME_PICKER_DIALOG_ID = 0;
    public static int REQUEST_ADD_USER = 10;

    private List<Thing> things;
    private RecyclerView rv;
    RVAdapter adapter; //

    SharedPreferences pref; // things 데이터 저장하기 위한 내부 DB

    int position; // thing position
    int hour_x; // 예약시간
    int minute_x; // 예약 분

    // Progress dialog popped up when scan wifi
    ProgressDialog mProgressDialog;

    //MQTT
    MqttAndroidClient client;
    IMqttToken token;
//    static String MQTTHOST = "tcp://test.mosquitto.org:1883"; // localhost:port
    static String MQTTHOST = "tcp://58.236.6.251:1883"; // localhost:port
    String topicStr = "01091095924";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("HomeNect");

        rv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        request_access_wifi();

        things = new ArrayList<>();

        pref = new SharedPreferences(this);
//        things.add(new Thing("Things", "5C:CF:7F:A7:A7:15" , false, false, false, "", false, false));


        topicStr = pref.getValue("user", "01091095924", "HomeNect");

        adapter = new RVAdapter(this, things);
        rv.setAdapter(adapter);
/*
        pref.removeAllPreferences("HomeNect");
        pref.removeAllPreferences("light");
*/

    }

    @Override
    protected void onResume(){
        topicStr = pref.getValue("user", "01091095924", "HomeNect");
        for(int i = 0; i < 3; i++){
            if(pref.getValue("thing" + i, "EmptyThing", "HomeNect") == "EmptyThing"){
                break;
            } else {
                String thingName = pref.getValue("thing" + i, "Name", "HomeNect"); // thing 이름

                String thingMacAddr = pref.getValue("macAddr", "mac", thingName); // thing mac주소
                boolean timerStatus = pref.getValue("timerStatus", false, thingName); // thing 예약제어 상태
                String time = pref.getValue("time", "time", thingName); // 예약 시간
                boolean timerTimeControl = pref.getValue("timerTimeControl", false, thingName); // 예약제어시 thing의 on/off 여부
                boolean gpsStatus = pref.getValue("gpsStatus", false, thingName); // gps on/off 여부

                if(things.size() != 0){
                    if(thingName == things.get(i).getName()){
                        things.get(i).setMacAddr(thingMacAddr);
                        things.get(i).setTimerStatus(timerStatus);
                        things.get(i).setTimerTime(time);
                        things.get(i).setTimerTimeControl(timerTimeControl);
                        things.get(i).setGpsStatus(gpsStatus);
                    }
                } else{
                    things.add(new Thing(thingName, thingMacAddr, false, false, timerStatus, time, timerTimeControl, gpsStatus));
                }
                adapter.notifyDataSetChanged();
            }
        }

        // MQTT
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(MainActivity.this, MQTTHOST, clientId);

        if(!client.isConnected() && isNetWork()){
            try {
                // IMqttToken token = client.connect(options);
                token = client.connect();
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // 성공
                        Toast.makeText(getApplicationContext(), "Mqtt connected!", Toast.LENGTH_SHORT).show();
                        mqttSubscription(); // subscribe!
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

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // new String(message.getPayload()); subscribe message
                    JSONObject obj = new JSONObject(new String(message.getPayload()));
//                    Toast.makeText(MainActivity.this, new String(message.getPayload()), Toast.LENGTH_SHORT).show();
                    String from = obj.getString("from");
                    String to = obj.getString("to");
                    if(to != "app")
                    {
                        return;
                    }
                    String category = obj.getString("category");
                    String value = obj.getString("value");

                    switch (category){
                        case "updateStatus":
                            JSONObject jobject = new JSONObject(value);
                            String getMacAddr = jobject.getString("macAddr");
                            String getStatus = jobject.getString("status");

                            boolean status = false;
                            if(getStatus == "1"){
                                status = true;
                            }

                            for(int i = 0; i < 3; i++){
                                if(pref.getValue("thing" + i, "emptyThing", "HomeNect") == "emptyThing"){
                                    return;
                                }
                                if(things.get(i).getMacAddr().equals(getMacAddr)){ // Mac 주소 검사하고
                                    things.get(i).setWifiStatus(true);
                                    if(things.get(i).isStatus() != status){ // 상태가 다를 경우에만 UI 변경
                                        things.get(i).setStatus(status);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                            break;
                        case "thingList":
                            JSONArray jarray = new JSONArray(value);
                            for(int i = 0; i <jarray.length(); i++){
                                JSONObject jObject = jarray.getJSONObject(i);
                                String thingName = jObject.getString("thingName");
                                String macAddr = jObject.getString("macAddr");
                                String getTimerStatus = jObject.getString("timerStatus");
                                String time = jObject.getString("time");
                                String getGpsStatus = jObject.getString("gpsStatus");

                                boolean timerStatus = false;
                                boolean gpsStatus = false;
                                if(getTimerStatus == "On"){
                                    timerStatus = true;
                                }
                                if(getGpsStatus == "On"){
                                    gpsStatus = true;
                                }
                                // 추가하기 전에 mac addr 겹치는지 비교먼저!
                                for(int k = 0; k < things.size(); k++){
                                    if(macAddr.equals(things.get(k).getMacAddr())){
                                        return;
                                    }
                                }
                                things.add(new Thing(thingName, macAddr, false, false, timerStatus, time, true, gpsStatus)); // Thing 추가
                                adapter.notifyDataSetChanged(); // 리스트 업데이트
                            }
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
        }
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK){
            // 등록 안한 경우
            Toast.makeText(MainActivity.this, "사용자를 등록해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(requestCode == REQUEST_ADD_USER){
//            mqttPublish(data.getStringExtra("phoneNumber"), 1);
        }

    }

    @Override
    protected void onPause() {
        mqttDisconn();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_stt:
                startActivity(new Intent(MainActivity.this, SttActivity.class));
                break;

            case R.id.add_thing:
                if(pref.getValue("user", "emptyUser", "HomeNect") == "emptyUser")
                {
                    Toast.makeText(MainActivity.this, "사용자를 등록해주세요.", Toast.LENGTH_SHORT).show();
                    startActivityForResult(new Intent(MainActivity.this, AddUser.class), REQUEST_ADD_USER);
                } else{
                    startActivity(new Intent(MainActivity.this, ScanWifi.class));
                }
                //do add thing
                break;
            case R.id.add_guest:
                if(pref.getValue("user", "emptyUser", "HomeNect") == "emptyUser")
                {
                    Toast.makeText(MainActivity.this, "사용자를 등록해주세요.", Toast.LENGTH_SHORT).show();
                    startActivityForResult(new Intent(MainActivity.this, AddUser.class), REQUEST_ADD_USER);
                } else{
                    startActivity(new Intent(MainActivity.this, AddGuest.class));
                }
                break;
                // do add user
            case R.id.add_user:
                startActivityForResult(new Intent(MainActivity.this, AddUser.class), REQUEST_ADD_USER);
            case R.id.action_gps:
                String thingName = pref.getValue("thing" + 0,"thingName","HomeNect");
                Toast.makeText(MainActivity.this, "thingName : " + thingName + "\n" +
                        "macAddr:" + pref.getValue("macAddr", "mac", thingName) + "\n" +
                        "timeStatus:" + pref.getValue("timeStatus", false, thingName) + "\n" +
                        "time:" + pref.getValue("time", "0000", thingName), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_stt, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void showTimerDialog(int i) {
        position = i; // thing position
        showDialog(TIME_PICKER_DIALOG_ID); // 시간 선택 dialog 띄우기
    }

    @Override
    public void mqttPublish(String control, int i) {
        String topic;
        String message;
        switch (control) {
            case "powerOn":
                topic = topicStr;
                message = "hello HomeNect!";
                try {
                    JSONObject mqtt = new JSONObject(); // Json object
                    JSONObject data = new JSONObject();
                    JSONObject value = new JSONObject(); // value

                    data.put("from", "app");
                    data.put("to", "thing");
                    data.put("category", "control");
                    value.put("macAddr", things.get(i).getMacAddr());
                    value.put("opCode", "1");
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
                break;
            case "powerOff":
                topic = topicStr;
                message = "hello HomeNect!";

                try {
                    JSONObject mqtt = new JSONObject(); // Json object
                    JSONObject data = new JSONObject();
                    JSONObject value = new JSONObject(); // value

                    data.put("from", "app");
                    data.put("to", "thing");
                    data.put("category", "control");
                    value.put("macAddr", things.get(i).getMacAddr());
                    value.put("opCode", "0");
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
                break;
            case "timerOn":
                topic = topicStr;
                message = "requestList";
                try {
                    JSONObject mqtt = new JSONObject(); // Json object
                    JSONObject value = new JSONObject(); // value

                    value.put("from", "app");
                    value.put("to", "server");
                    value.put("category", "thingList");

                    mqtt.put("data", value);

                    message = mqtt.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    client.publish(topic, message.getBytes(), 0, false); // 퍼블리쉬
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                break;
            case "saveSchedule":
                topic = topicStr;
                message = "ScheduleInformation";
                try{
                    JSONObject mqtt = new JSONObject();
                    JSONObject data = new JSONObject();
                    JSONObject value = new JSONObject();

                    String opCode = "0";
                    if(things.get(i).isTimerTimeControl()){
                        opCode = "1";
                    }

                    data.put("from", "app");
                    data.put("to", "server");
                    data.put("category", "saveSchedule");

                    value.put("macAddr", things.get(i).getMacAddr());
                    value.put("time", things.get(i).getTimerTime().replace(":",""));
                    value.put("opCode", opCode); // 전원을 킬것인지 끌것인지 정보

                    data.put("value", value);
                    mqtt.put("data", data);
                    message = mqtt.toString();
                }catch(JSONException e){
                    e.printStackTrace();
                }
                try {
                    client.publish(topic, message.getBytes(), 0, false); // 퍼블리쉬
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), message.getBytes().toString(),Toast.LENGTH_SHORT);
                break;
            default:
                break;
        }
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == TIME_PICKER_DIALOG_ID)
            return new TimePickerDialog(MainActivity.this, kTimePickerListener, hour_x, minute_x, false);
        return null;
    }

    protected TimePickerDialog.OnTimeSetListener kTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_x = hourOfDay; // 확인 버튼 눌렀을 때 굳굳
            minute_x = minute;
            StringBuilder time = new StringBuilder();
            time.append(hour_x + ":");
            if (minute_x < 10) {
                time.append("0" + minute_x);
            } else {
                time.append(minute_x);
            }
            things.get(position).setTimerTime(time.toString());
            Toast.makeText(MainActivity.this, time.toString(), Toast.LENGTH_SHORT).show();
            pref.putValue("time", time.toString(), things.get(position).getName());
            adapter.things.get(position).setTimerTime(time.toString());
            adapter.notifyDataSetChanged(); // adapter refresh
        }
    };

    private void mqttSubscription() {
        try {
            client.subscribe(topicStr, 0); // 섭스크라이브
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void mqttDisconn() {
        if(client == null){
            return;
        }
        if(!client.isConnected() && !isNetWork()){
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
                    request_access_wifi();
                } else {
                    finish();
                }
            }
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    request_change_wifi_state();
                } else {
                    finish();
                }
            }
            case 3: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    request_change_access_fine_location();
                } else{
                    finish();
                }
            }
        }
    }

    public void request_access_wifi() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_WIFI_STATE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, 3);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, 3);
            }
        } else {
        }
    }

    public void request_change_wifi_state() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 4);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 4);
            }
        } else {
        }
    }
    public void request_change_access_fine_location() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 5);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 5);
            }
        } else {
        }
    }
    private Boolean isNetWork(){
        ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
        boolean isMobileAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isAvailable();
        boolean isMobileConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        if ((isMobileAvailable && isMobileConnect)){
            return true;
        }else{
            return false;
        }
    }
}
