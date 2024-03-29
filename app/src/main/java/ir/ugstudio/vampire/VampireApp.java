package ir.ugstudio.vampire;

import android.app.Application;

import com.google.firebase.analytics.FirebaseAnalytics;

import ir.ugstudio.vampire.api.MapApi;
import ir.ugstudio.vampire.api.UserApi;
import ir.ugstudio.vampire.utils.Config;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class VampireApp extends Application {

    public static FirebaseAnalytics firebaseAnalytics;

    private static Retrofit retrofit;

    private static Retrofit getRetrofit() {
        return new Retrofit.Builder().baseUrl(Config.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static MapApi createMapApi() {
        return retrofit.create(MapApi.class);
    }

    public static UserApi createUserApi() {
        return retrofit.create(UserApi.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        retrofit = getRetrofit();
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }
}