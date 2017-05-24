package com.capstone.sejong.homenect;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by 12aud on 2017-05-19.
 */

public interface ApiService {
    public static final String API_URL = "http://jsonplaceholder.typicode.com/";

/*
    @GET("comments")
    Call<ResponseBody>getComment(@Query("postId")int postId); // get

    @POST("comments")
    Call<ResponseBody>getPostComment(@Query("postId")int postId); // post

    //String version
    @GET("comments")
    Call<ResponseBody>getCommentStr(@Query("postId")String postId);
*/
/*
    @POST("ap/info")
    Call<ApInfo>sendApInfo(@Body ApInfo params);*/

    @POST("ap/info")
    Call<ApInfo> sendApInfo(@Body String body);

/*
    @FormUrlEncoded
    @POST("ap/info")
    public void sendApInfoooo(@Field("ssid") String ssid, @Field("pwd") String pwd, @Field("topic") String topic);*/
}
