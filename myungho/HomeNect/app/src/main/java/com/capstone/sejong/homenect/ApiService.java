package com.capstone.sejong.homenect;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

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

    @POST("ap/info")
    Call<ResponseBody>sendApInfo(@Path("ap/info") String postfix, @Body RequestBody params);
}
