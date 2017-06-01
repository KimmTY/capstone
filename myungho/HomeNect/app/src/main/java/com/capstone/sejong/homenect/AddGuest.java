package com.capstone.sejong.homenect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

public class AddGuest extends AppCompatActivity {

    Button btn_ok;
    Button btn_cancel;
    EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_guest);

        btn_ok = (Button) findViewById(R.id.btn_ok_phone);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatePhoneNumber(et.getText().toString())) { // 유효한 핸드폰 번호일 경우

                    Intent intent = new Intent();
                    intent.putExtra("guest", et.getText().toString()); // 핸드폰 번호 넘겨주기
                    setResult(RESULT_OK, intent);
                    Toast.makeText(getApplicationContext(), "등록 완료", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "유효하지 않은 핸드폰 번호입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_cancel = (Button) findViewById(R.id.btn_cancel_phone);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        et = (EditText) findViewById(R.id.et_phone);

    }

    private boolean validatePhoneNumber(String phoneNo) {
        if (Pattern.matches("^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$", phoneNo)) {
            return true;
        } else {
            return false;
        }
    }
}
