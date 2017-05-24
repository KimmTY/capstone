package com.capstone.sejong.homenect;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements AdapterCallback {

    public static int TIME_PICKER_DIALOG_ID = 0;

    Retrofit retrofit; // HTTP Request를 위한 retrofit lib
    ApiService apiService; // API interface

    private List<Thing> things;
    private RecyclerView rv;
    RVAdapter adapter; //

    SharedPreferences pref; // things 데이터 저장하기 위한 내부 DB

    int position; // thing position
    int hour_x; // 예약시간
    int minute_x; // 예약 분
    boolean nearByHomeNect; // sonoff가 주변에 있다고 표시할 변수

    // Progress dialog popped up when scan wifi
    ProgressDialog mProgressDialog;
    // WifiManager variable
    WifiManager wifiManager;

    private List<ScanResult> mScanResult; // ScanResult List


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                mScanResult = wifiManager.getScanResults(); // ScanResult

                for (int i = 0; i < mScanResult.size(); ++i) {
                    ScanResult result = mScanResult.get(i);
                    if(result.SSID.toString().contains("ensharp")){
                        nearByHomeNect = true;
                        return;
                    }
                }
            } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                sendBroadcast(new Intent("wifi.ON_NETWORK_STATE_CHANGED"));
            }
        }
    };

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

        things = new ArrayList<>();
        things.add(new Thing("Capstone", false, false, false, false));
        things.add(new Thing("Things", false, false, false, false));

        adapter = new RVAdapter(this, things);
        rv.setAdapter(adapter);

        // Setup WIFI
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        // if WIFIEnabled
        if(wifiManager.isWifiEnabled() == false){
            wifiManager.setWifiEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.add_thing:
                startActivity(new Intent(MainActivity.this, ScanWifi.class));
                //do add thing
                break;
            case R.id.add_user:
                startActivity(new Intent(MainActivity.this, AddGuest.class));
                // do add user
            default:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    private void initializeThing() {
        pref = new SharedPreferences(getApplicationContext());
    }

    @Override
    public void showTimerDialog(int i) {
        position = i; // thing position
        showDialog(TIME_PICKER_DIALOG_ID); // 시간 선택 dialog 띄우기
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
            things.get(position).setTimerTime(hour_x + ":" + minute_x);
            Toast.makeText(MainActivity.this, hour_x + ":" + minute_x, Toast.LENGTH_SHORT).show();
            adapter.things.get(position).setTimerTime(hour_x + ":" + minute_x);
            adapter.notifyDataSetChanged(); // adapter refresh
        }
    };
}
