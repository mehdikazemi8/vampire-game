package ir.ugstudio.vampire.api;

import java.util.List;

import ir.ugstudio.vampire.models.MapResponse;
import ir.ugstudio.vampire.models.PlacesResponse;
import ir.ugstudio.vampire.models.QuotesResponse;
import okhttp3.ResponseBody;
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

    @FormUrlEncoded
    @POST("appattack")
    Call<ResponseBody> attack(
            @Field("token") String token,
            @Field("lat") Double lat,
            @Field("lng") Double lng,
            @Field("username") String username
    );

    @FormUrlEncoded
    @POST("getquote")
    Call<QuotesResponse> getQuotes(
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("getplace")
    Call<PlacesResponse> getPlaces(
            @Field("token") String token,
            @Field("placeType") String placeType
    );
}