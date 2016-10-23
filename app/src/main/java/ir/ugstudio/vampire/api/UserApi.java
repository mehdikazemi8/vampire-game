package ir.ugstudio.vampire.api;

import ir.ugstudio.vampire.models.Ranklist;
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
}
