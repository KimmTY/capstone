package com.capstone.sejong.homenect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SSidPwdDialog extends AppCompatActivity {

    EditText et_ssid;
    EditText et_pwd;
    Button btn_ok;
    Button btn_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ssid_pwd_dialog);

        et_ssid = (EditText) findViewById(R.id.et_ssid);
        et_pwd = (EditText) findViewById(R.id.et_pwd);

        btn_ok = (Button) findViewById(R.id.btn_ok_ssid_pwd);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ssid = et_ssid.getText().toString();
                String pwd = et_pwd.getText().toString();
                if(ssid != "" && pwd != ""){ // 둘다 Null값이 아니면


                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://192.168.4.1/")
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    ApiService apiService = retrofit.create(ApiService.class);

                    try{
                        JSONObject paramObject = new JSONObject();
                        paramObject.put("ssid", ssid);
                        paramObject.put("pwd", pwd);
                        paramObject.put("topic", "01027655255");


                        Call<ApInfo> apInfoCall = apiService.sendApInfo(paramObject.toString());
                        apInfoCall.enqueue(new Callback<ApInfo>() {
                            @Override
                            public void onResponse(Call<ApInfo> call, Response<ApInfo> response) {
                                Toast.makeText(getApplicationContext(), "성공", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onFailure(Call<ApInfo> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });

                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "올바르지 않은 입력입니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        btn_cancel = (Button) findViewById(R.id.btn_cancel_ssid_pwd);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
