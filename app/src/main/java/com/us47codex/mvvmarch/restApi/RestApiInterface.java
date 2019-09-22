package com.us47codex.mvvmarch.restApi;

import com.us47codex.mvvmarch.constant.EndPoints;
import com.us47codex.mvvmarch.login.LoginParamModel;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Url;

public interface RestApiInterface {
   /* @POST(EndPoints.RETRO_AMENITIES)
    Single<Response<ResponseBody>> getAmenities(@HeaderMap Map<String, String> headers,
                                                @Body AmenitiesModel params);*/

    @POST()
    Single<Response<ResponseBody>> callPostApi(
            @Url String url,
            @HeaderMap Map<String, String> headers,
            @Body HashMap<String, String> params);

    @Multipart
    @POST()
    Single<Response<ResponseBody>> callMultipartApi(
            @Url String url,
            @HeaderMap Map<String, String> headers,
            @PartMap HashMap<String, RequestBody> params);

    @Multipart
    @POST()
    Single<Response<ResponseBody>> callMultipartApi1(
            @Url String url,
            @HeaderMap Map<String, String> headers,
            @PartMap HashMap<String, RequestBody> params, @PartMap HashMap<String, MultipartBody.Part> params1);

    @Multipart
    @POST()
    Single<Response<ResponseBody>> callMultipartVisitApi(
            @Url String url,
            @HeaderMap Map<String, String> headers,
            @Body HashMap<String, String> params);


    @GET()
    Single<Response<ResponseBody>> callGetApi(
            @Url String url,
            @HeaderMap Map<String, String> headers);

    @POST(EndPoints.LOGIN)
    Single<Response<ResponseBody>> userLogin(@Body LoginParamModel params);


}
