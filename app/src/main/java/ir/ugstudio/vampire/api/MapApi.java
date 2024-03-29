package ir.ugstudio.vampire.api;

import ir.ugstudio.vampire.models.MapResponse;
import ir.ugstudio.vampire.models.PlacesResponse;
import ir.ugstudio.vampire.models.QuotesResponse;
import ir.ugstudio.vampire.models.Tower;
import ir.ugstudio.vampire.models.nearest.MostWantedList;
import ir.ugstudio.vampire.models.nearest.Target;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

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
            @Field("username") String username,
            @Field("message") String message
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

    @FormUrlEncoded
    @POST("addtower")
    Call<ResponseBody> addTower(
            @Field("token") String token,
            @Field("lat") Double lat,
            @Field("lng") Double lng
    );

    @FormUrlEncoded
    @POST("jointower")
    Call<ResponseBody> joinTower(
            @Field("token") String token,
            @Field("lat") Double lat,
            @Field("lng") Double lng,
            @Field("towerId") String towerId
    );

    @FormUrlEncoded
    @POST("stealtower")
    Call<ResponseBody> stealFromTower(
            @Field("token") String token,
            @Field("towerId") String towerId
    );

    @FormUrlEncoded
    @POST("collecttowercoin")
    Call<ResponseBody> collectTowerCoins(
            @Field("token") String token,
            @Field("towerId") String towerId
    );

    @FormUrlEncoded
    @POST("towermapview")
    Call<MapResponse> getMapAroundTower(
            @Field("token") String token,
            @Field("towerId") String towerId
    );

    @FormUrlEncoded
    @POST("towerattack")
    Call<ResponseBody> attackFromTower(
            @Field("token") String token,
            @Field("towerId") String towerId,
            @Field("username") String username
    );

    @FormUrlEncoded
    @POST("towermessage")
    Call<ResponseBody> sendMessageToTower(
            @Field("token") String token,
            @Field("towerId") String towerId,
            @Field("message") String message
    );

    @FormUrlEncoded
    @POST("gettower")
    Call<Tower> getTower(
            @Field("token") String token,
            @Field("towerId") String towerId
    );

    @FormUrlEncoded
    @POST("getnearest")
    Call<Target> getNearestObject(
            @Field("token") String token,
            @Field("targetType") String targetType,
            @Field("lat") Double lat,
            @Field("lng") Double lng
    );

    @FormUrlEncoded
    @POST("getdirection")
    Call<Target> getDirection(
            @Field("token") String token,
            @Field("targetType") String targetType,
            @Field("targetId") String targetId,
            @Field("lat") double lat,
            @Field("lng") double lng
    );

    @FormUrlEncoded
    @POST("getmostwanted")
    Call<MostWantedList> getMostWantedList(
            @Field("token") String token,
            @Field("lat") double lat,
            @Field("lng") double lng
    );
}