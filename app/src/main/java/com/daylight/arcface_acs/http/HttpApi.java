package com.daylight.arcface_acs.http;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 *
 * Created by Daylight on 2018/2/2.
 */

public interface HttpApi {
    @GET("user/sendIdenCode")
    Call<String> getIdenCode();

    @POST("user/regist")
    @FormUrlEncoded
    Call<String> register(@Field("iden")String iden, @Field("username") String account, @Field("password") String password);

    @GET("comm/getAll")
    Call<String> getEstate();

    @POST("buil/getByCid")
    @FormUrlEncoded
    Call<String> getBuilding(@Field("cid")int cid);

    @POST("owner/regist")
    @FormUrlEncoded
    Call<String> uploadUserInfo(@Field("name")String name,@Field("idnumber")String idnumber,@Field("phone")String phone
            ,@Field("cid")String cid,@Field("bid")String bid,@Field("num")String doorNum,@Field("ids")int[] ids);

    @Multipart
    @POST("eng/checkFeature")
    Call<String> uploadImage(@Part("mid") RequestBody description,
                             @Part MultipartBody.Part file);

    @POST("user/checkLogin")
    @FormUrlEncoded
    Call<String> login(@Field("username") String username,@Field("password") String password,@Field("rememberMe")boolean isRemember);

    @POST("owner/info")
    Call<String> getUserInfo();

    @POST("eng/getImage")
    @FormUrlEncoded
    Call<String> getImage(@Field("fid")String fid);

    @POST("visitor/regist")
    @FormUrlEncoded
    Call<String> faceRegister(@Field("phone")String phone,@Field("name") String name,@Field("relationType") String type,
                              @Field("idnumber") String idnumber,@Field("allowBegin") String begin,@Field("allowEnd") String end,
                              @Field("ids") int[] ids);
}
