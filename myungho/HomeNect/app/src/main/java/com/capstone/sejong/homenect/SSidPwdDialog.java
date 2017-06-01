package com.capstone.sejong.homenect;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SSidPwdDialog extends AppCompatActivity {

    EditText et_ssid;
    EditText et_pwd;
    Button btn_ok;
    Button btn_cancel;

    SharedPreferences pref;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ssid_pwd_dialog);

        pref = new SharedPreferences(this);

        handler = new Handler();

        et_ssid = (EditText) findViewById(R.id.et_ssid);
        et_pwd = (EditText) findViewById(R.id.et_pwd);

        btn_ok = (Button) findViewById(R.id.btn_ok_ssid_pwd);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ssid = et_ssid.getText().toString();
                String pwd = et_pwd.getText().toString();
                if(ssid != "" && pwd != ""){ // 둘다 Null값이 아니면

                    /*JsonObject obj = new JsonObject();
                    obj.add

                    ApiGenerator.getInstance().sendApInfo(new JsonObject())
*/
                        ApiGenerator.getInstance().sendApInfo(ssid, pwd, "01091095924").enqueue(new Callback<ApInfo>() {
                            @Override
                            public void onResponse(Call<ApInfo> call, Response<ApInfo> response) {
                                if(response.isSuccessful()){
//                                    ApInfo apInfo = response.body();
                                    Gson gson = new GsonBuilder().create();
                                    String encodedValues = null;
                                    int commaIndex;
                                    StringBuilder encodedValue = null;
                                    String[] values = new String[3];
                                    String endValue;
                                    try {
                                        JSONObject jjjsonObj = new JSONObject(gson.toJson(response.raw().request().body()).toString());
                                        encodedValues = jjjsonObj.getString("encodedValues");
//                                        JSONObject jsonObj = new JSONObject(encodedValues);
//                                        encodedValue = new StringBuilder(jsonObj.toString());
//                                        encodedValue.substring(encodedValue.indexOf(","));
//                                        encodedValue.substring(encodedValue.toString().indexOf(','));
/*                                        encodedValues = jsonObj.toString().substring(jsonObj.toString().indexOf(","));
                                        encodedValues = encodedValues.substring(encodedValues.charAt(','));
                                        encodedValues = encodedValues.replaceAll("\"", "");*/
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    StringBuilder s = new StringBuilder(encodedValues);
                                    values = encodedValues.split("\",\"");
                                    endValue = values[1];
                                    Toast.makeText(SSidPwdDialog.this, endValue, Toast.LENGTH_SHORT).show();
                                    pref.putValue("tempMacAddr", "5C:CF:7F:A7:A0:6D", "HomeNect"); // 임시 맥 주소
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(new Intent(SSidPwdDialog.this, SetThingNameDialog.class));
                                            finish();
                                        }
                                    }, 1000);
                                    /*try {
                                        JSONObject jjsonObject = new JSONObject(response.body().toString());
                                        String result = jjsonObject.getString("ssid");
                                        String macAddr = jjsonObject.getString("pwd");
                                        Toast.makeText(getApplicationContext(), response.body().toString() // 맥 어드레스 받기
                                                , Toast.LENGTH_SHORT).show();
                                        if(result == "success"){
                                            Toast.makeText(getApplicationContext(), "맥어드레스 잘받음" // 맥 어드레스 받기
                                                    , Toast.LENGTH_SHORT).show();
                                            pref.putValue("tempMacAddr", macAddr, "HomeNect"); // 임시 맥 어드레스
                                            startActivity(new Intent(SSidPwdDialog.this, SetThingNameDialog.class));
                                            finish();
                                        } else {
                                            Toast.makeText(getApplicationContext(), response.body().toString() + "ERROR" // 에러
                                                    , Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }*/
                                }
                            }

                            @Override
                            public void onFailure(Call<ApInfo> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), t.getMessage() + "실패", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
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
