package com.us47codex.mvvmarch.restApi;

import com.us47codex.mvvmarch.constant.EndPoints;
import com.us47codex.mvvmarch.login.LoginParamModel;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RestApiInterface {
   /* @POST(EndPoints.RETRO_AMENITIES)
    Single<Response<ResponseBody>> getAmenities(@HeaderMap Map<String, String> headers,
                                                @Body AmenitiesModel params);*/

    @POST(EndPoints.LOGIN)
    Single<Response<ResponseBody>> userLogin(@Body LoginParamModel params);


}
