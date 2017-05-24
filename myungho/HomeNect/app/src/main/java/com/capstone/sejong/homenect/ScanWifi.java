package com.capstone.sejong.homenect;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import java.util.List;

public class ScanWifi extends AppCompatActivity{

    // WifiManager variable
    WifiManager wifiManager;
    boolean mResult;

    private List<ScanResult> mScanResult; // ScanResult List

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
                getWIFIScanResult(); // get WIFISCanResult
                wifiManager.startScan();
            } else if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
                sendBroadcast(new Intent("wifi.ON_NETWORK_STATE_CHANGED"));
            }
        }
    };

    public void getWIFIScanResult() {
        mScanResult = wifiManager.getScanResults(); // ScanResult

        for(int i = 0; i < this.mScanResult.size(); ++i) {
            ScanResult result = mScanResult.get(i);
            if(result.SSID.toString().contains("HomeNect")){
                mResult = true;
                showInputSSidandPwd();
                finish();
            }
//            this.textStatus.append(i + 1 + ". SSID : " + result.SSID.toString() + "\t\t RSSI : " + result.level + " dBm\n"); ssid 이름이랑 dBm값
        }
    }

    public void initWIFIScan() {
        IntentFilter filter = new IntentFilter("android.net.wifi.SCAN_RESULTS");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        this.registerReceiver(this.mReceiver, filter);
        this.wifiManager.startScan();
        Log.d("WIFIScanner", "initWIFIScan()");
    }

    @SuppressLint("WifiManagerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_wifi);

        mResult = false;

        // Setup WIFI
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        // if WIFIEnabled
        if(wifiManager.isWifiEnabled() == false){
            wifiManager.setWifiEnabled(true);
        }

        initWIFIScan();
    }

    @Override
    protected void onDestroy()
    {
        unregisterReceiver(mReceiver); // mReceiver 제거
        if(!mResult){
            Toast.makeText(getApplicationContext(), "주변에 HomeNect가 없습니다!", Toast.LENGTH_LONG).show();
        }
        super.onDestroy();
    }

    public void showInputSSidandPwd() {
        String networkSSID = "HomeNect";
        String networkPwd = "HomeNect605";

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\""; // Please note the quotes
        conf.preSharedKey = "\"" + networkPwd + "\"";
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE); // Open network
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(conf); // thing 을 wifi로 해서 연결

        startActivity(new Intent(ScanWifi.this, SSidPwdDialog.class)); // show dialog
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        return false;
    }
}
