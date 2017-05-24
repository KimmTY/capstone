package com.capstone.sejong.homenect;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        rv = (RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        things = new ArrayList<>();
        things.add(new Thing("Capstone", false, false, false, false));
        things.add(new Thing("Things", false, false, false, false));

        adapter = new RVAdapter(this, things);
        rv.setAdapter(adapter);

        retrofit = new Retrofit.Builder().baseUrl(ApiService.API_URL).build();
        apiService = retrofit.create(ApiService.class);

//        Call<ResponseBody> comment = apiService.getComment(1);
//        comment.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                // 데이터가 받아지면 실행
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                // 데이터 받기 실패한 경우
//            }
//        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.add_thing:
//                startActivity(new Intent(MainActivity.this, ScanWifi.class));
                //do add thing
                String networkSSID = "HomeNect";
                String networkPwd = "HomeNect605";

                WifiConfiguration conf = new WifiConfiguration();
                conf.SSID = "\"" + networkSSID + "\""; // Please note the quotes
                conf.preSharedKey = "\"" + networkPwd + "\"";
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE); // Open network
                WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                wifiManager.addNetwork(conf);



                startActivity(new Intent(MainActivity.this, SSidPwdDialog.class));

                break;
            case R.id.add_user:
                startActivity(new Intent(MainActivity.this, AddGuest.class));
/*                // 임시로 기기 Ap정보 보내는 코드 넣어보기
                Map<String, Object> map = new ArrayMap<>();
                //put something inside the map, could be null
                map.put("ssid", "sejong");
                map.put("password", "123123123");
                map.put("topic", "01012341234");
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(map)).toString());
                //serviceCaller is the interface initialized with retrofit.create...

                Call<ResponseBody> apInfo = apiService.sendApInfo("apInfo", body);

                apInfo.enqueue(new Callback<ResponseBody>()
                {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> rawResponse)
                    {
                        try
                        {
                            //get your response....
                            Log.d("tag", "RetroFit2.0 :RetroGetLogin: " + rawResponse.body().string());
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable)
                    {
                        // other stuff...
                    }
                });*/
                // do add user
            default:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    private void initializeThing(){
        pref = new SharedPreferences(getApplicationContext());


    }

    @Override
    public void showTimerDialog(int i) {
        position = i;
        showDialog(TIME_PICKER_DIALOG_ID);
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
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
            adapter.notifyDataSetChanged();
        }
    };

}
