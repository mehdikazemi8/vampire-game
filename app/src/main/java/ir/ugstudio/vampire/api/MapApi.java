package ir.ugstudio.vampire.api;

import ir.ugstudio.vampire.models.MapResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MapApi {
    @FormUrlEncoded
    @POST("appmapreq")
    Call<MapResponse> getMap(
            @Field("token") String token,
            @Field("lat") Double lat,
            @Field("lng") Double lng
    );
}