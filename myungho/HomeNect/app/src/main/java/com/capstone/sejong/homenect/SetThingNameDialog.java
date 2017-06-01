package com.capstone.sejong.homenect;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetThingNameDialog extends AppCompatActivity {

    EditText et_thing_name; // thing name EditText 창
    Button btn_ok;
    Button btn_cancel;

    SharedPreferences pref;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_thing_name_dialog);

        pref = new SharedPreferences(this);
        handler = new Handler();
        btn_ok = (Button)findViewById(R.id.btn_ok_thingName);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_thing_name.getText().toString() == ""){ // text 창이 비어있는 경우
                    Toast.makeText(SetThingNameDialog.this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else{
                    String thingName = et_thing_name.getText().toString();
                    for(int i = 0; i < 3; i++){
                        if(pref.getValue("thing" + i, "thingName", "HomeNect") == "thingName"){
                            pref.putValue("thing" + i, thingName, "HomeNect"); // HomeNect file 안에 thingName 저장

                            pref.putValue("thingName", thingName, thingName); // 사용자가 지정한 ThingName으로 file 생성
                            pref.putValue("macAddr", pref.getValue("tempMacAddr", "mac", "HomeNect"), thingName); // homeNect file안에 맥어드레스 저장
                            pref.putValue("timerStatus", false, thingName);
                            pref.putValue("gpsStatus", false, thingName);
                            pref.putValue("time", "", thingName);
                            pref.putValue("timerTimeControl", false, thingName);
                            break;
                        }
                    }

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1500);
                }
            }
        });

        btn_cancel = (Button)findViewById(R.id.btn_cancel_thingName);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SetThingNameDialog.this, "등록이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        et_thing_name = (EditText) findViewById(R.id.et_thingName);
    }
}
