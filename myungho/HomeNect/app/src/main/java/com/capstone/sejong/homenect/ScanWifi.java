package com.capstone.sejong.homenect;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class ScanWifi extends Activity implements View.OnClickListener {

    private static final String TAG = "WIFIScanner";

    // WifiManager variable
    WifiManager wifiManager;

    TextView textStatus;
    Button btnScanStart;
    Button btnScanStop;

    private int scanCount = 0;
    String text = "";
    String result = "";

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
        this.textStatus.setText("Scan count is \t" + ++this.scanCount + " times \n");
        this.textStatus.append("=======================================\n");

        for(int i = 0; i < this.mScanResult.size(); ++i) {
            ScanResult result = mScanResult.get(i);
            this.textStatus.append(i + 1 + ". SSID : " + result.SSID.toString() + "\t\t RSSI : " + result.level + " dBm\n");
        }

        this.textStatus.append("=======================================\n");
    }

    public void initWIFIScan() {
        this.scanCount = 0;
        this.text = "";
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

        // Setup UI
        textStatus = (TextView) findViewById(R.id.textStatus);
        btnScanStart = (Button) findViewById(R.id.btnScanStart);
        btnScanStop = (Button) findViewById(R.id.btnScanStop);

        // Setup OnClickListener
        btnScanStart.setOnClickListener(this);
        btnScanStop.setOnClickListener(this);

        // Setup WIFI
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        // if WIFIEnabled
        if(wifiManager.isWifiEnabled() == false){
            wifiManager.setWifiEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnScanStart){
            initWIFIScan();
        }
        if(v.getId() == R.id.btnScanStop){
            unregisterReceiver(mReceiver);
        }

    }
}
