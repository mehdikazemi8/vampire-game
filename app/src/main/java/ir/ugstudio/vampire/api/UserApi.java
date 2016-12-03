package ir.ugstudio.vampire.api;

import ir.ugstudio.vampire.models.Ranklist;
import ir.ugstudio.vampire.models.StoreItems;
import ir.ugstudio.vampire.models.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserApi {
    @FormUrlEncoded
    @POST("getprofile")
    Call<User> getProfile(
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("loginuser")
    Call<User> login(
            @Field("username") String username, @Field("password") String password
    );

    @FormUrlEncoded
    @POST("createuser")
    Call<User> register(
            @Field("username") String username, @Field("password") String password,
            @Field("role") String playerType
    );

    @FormUrlEncoded
    @POST("getranklist")
    Call<Ranklist> getRanklist(
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("heal")
    Call<ResponseBody> healMe(
            @Field("token") String token,
            @Field("lat") Double lat,
            @Field("lng") Double lng,
            @Field("hospitalId") String hospitalId
    );

    @FormUrlEncoded
    @POST("setfcmid")
    Call<ResponseBody> sendFCMIdToServer(
            @Field("token") String token,
            @Field("fcmId") String fcmId
    );

    @FormUrlEncoded
    @POST("getstoreitems")
    Call<StoreItems> getStoreItems(
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("startrealpurchase")
    Call<ResponseBody> startRealPurchase(
            @Field("token") String token,
            @Field("itemSku") String itemSku
    );

    @FormUrlEncoded
    @POST("confirmrealpurchase")
    Call<ResponseBody> confirmRealPurchase(
            @Field("token") String token,
            @Field("orderID") String orderID,
            @Field("developerPayload") String developerPayload
    );

    @FormUrlEncoded
    @POST("finalizerealpurchase")
    Call<ResponseBody> finalizeRealPurchase(
            @Field("token") String token,
            @Field("developerPayload") String developerPayload
    );

    @FormUrlEncoded
    @POST("virtualpurchase")
    Call<ResponseBody> virtualPurchase(
            @Field("token") String token,
            @Field("itemId") String itemId
    );

    @FormUrlEncoded
    @POST("getnotification")
    Call<ResponseBody> getNotification(
            @Field("token") String token
    );
}
