package ir.ugstudio.vampire.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.managers.UserManager;
import ir.ugstudio.vampire.models.Place;
import ir.ugstudio.vampire.models.PlacesResponse;
import ir.ugstudio.vampire.models.QuotesResponse;
import ir.ugstudio.vampire.utils.Consts;
import ir.ugstudio.vampire.utils.MemoryCache;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetPlaces {
    public static void run(Context context) {
        Call<PlacesResponse> call = VampireApp.createMapApi().getPlaces(UserManager.readToken(context), Consts.PLACE_HOSPITAL);
        call.enqueue(new Callback<PlacesResponse>() {
            @Override
            public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {
                if(response.isSuccessful()) {
                    EventBus.getDefault().post(response.body());
                } else {
                    Log.d("TAG", "ddd not " + response.message());
                }
            }

            @Override
            public void onFailure(Call<PlacesResponse> call, Throwable t) {
                Log.d("TAG", "ddd fail " + t.getMessage());
            }
        });
    }
}
