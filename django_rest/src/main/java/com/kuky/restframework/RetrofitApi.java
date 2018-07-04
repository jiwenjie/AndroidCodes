package com.kuky.restframework;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @author kuky
 * @description
 */
public interface RetrofitApi {

    String DJANGO_BASE = "http://192.168.0.103:8080";
    String EXPERT_BASE = "http://192.168.0.103:8080";

    @GET("/api/categories/")
    Observable<ResponseBody> getCategories();

    @FormUrlEncoded
    @POST("/api/categories/")
    Observable<ResponseBody> postNewCategory(@Field("name") String name);

    @GET("/api/category/{id}")
    Observable<ResponseBody> getCategory(@Path("id") String id);

    @FormUrlEncoded
    @PUT("/api/category/{id}/")
    Observable<ResponseBody> updateCategory(@Path("id") String id,
                                            @Field("name") String name);

    @HTTP(method = "DELETE", path = "/api/category/{id}/", hasBody = true)
    Observable<ResponseBody> deleteCategory(@Path("id") String id);

    @FormUrlEncoded
    @POST("/user/login/")
    Observable<UserLogin> userLogin(@Field("username") String username,
                                    @Field("password") String password);

    @FormUrlEncoded
    @PUT("/user/reset_pass/")
    Observable<ResponseBody> reset(@Field("old_password") String old_password,
                                   @Field("new_password") String new_password,
                                   @Field("confirm_password") String confirm_password,
                                   @Header("Authorization") String token);

    @Multipart
    @POST("/user/upload_avatar/")
    Observable<ResponseBody> avatarUpload(@Part MultipartBody.Part avatar,
                                          @Part("suffix") String suffix,
                                          @Header("Authorization") String token);

    @GET("/user/get_info/{pk}/")
    Observable<User> getUserDetail(@Path("pk") int pk,
                                   @Header("Authorization") String token);

    @FormUrlEncoded
    @PUT("/user/modified/{pk}/")
    Observable<ResponseBody> modifiedUser(@Path("pk") int pk,
                                          @Header("Authorization") String token,
                                          @Field("username") String username,
                                          @Field("phone_num") String phone_num,
                                          @Field("qq") String qq,
                                          @Field("we_chat") String we_chat);
}
