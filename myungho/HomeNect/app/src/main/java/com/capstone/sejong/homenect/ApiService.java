package com.capstone.sejong.homenect;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by 12aud on 2017-05-19.
 */

public interface ApiService {
/*
    @POST("ap/info")
    Call<ApInfo> sendApInfo(@Body ApInfo params);
*/

    @POST("ap/info")
    @FormUrlEncoded
    Call<ApInfo> sendApInfo(@Field("ssid") String ssid,
                            @Field("pwd") String pwd,
                            @Field("topic") String topic);

/*
    @Headers( "Content-Type: application/json" )
    @POST("ap/info")
    Call<JsonElement> sendApInfo(@Body JsonObject body);
*/

/*    @POST("ap/info")
    Call<ApInfo> sendApInfo(@Body String body);*/

/*
    @FormUrlEncoded
    @POST("ap/info")
    public void sendApInfoooo(@Field("ssid") String ssid, @Field("pwd") String pwd, @Field("topic") String topic);*/
}
