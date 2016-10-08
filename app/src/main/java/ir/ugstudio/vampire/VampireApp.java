package ir.ugstudio.vampire;

import android.app.Application;

import ir.ugstudio.vampire.api.MapApi;
import ir.ugstudio.vampire.utils.Config;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class VampireApp extends Application {

    private static Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();

        retrofit = getRetrofit();
    }

    private static Retrofit getRetrofit() {
        return new Retrofit.Builder().baseUrl(Config.BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    public static MapApi createMapApi() {
        return retrofit.create(MapApi.class);
    }
}