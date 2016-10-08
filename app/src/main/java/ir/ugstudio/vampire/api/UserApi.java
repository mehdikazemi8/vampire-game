package ir.ugstudio.vampire.api;

import ir.ugstudio.vampire.models.MapResponse;
import ir.ugstudio.vampire.models.User;
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
}
