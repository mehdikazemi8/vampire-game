package ir.ugstudio.vampire.async;

import android.location.Location;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.events.NearestResponseEvent;
import ir.ugstudio.vampire.managers.CacheHandler;
import ir.ugstudio.vampire.models.nearest.NearestObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetNearest {
    public static void run(final String targetType) {
        Location lastLocation = CacheHandler.getLastLocation();
        if (lastLocation == null) {
            return;
        }

        Call<NearestObject> call = VampireApp.createMapApi().getNearestObject(
                CacheHandler.getUser().getToken(),
                targetType,
                lastLocation.getLatitude(),
                lastLocation.getLongitude()
        );
        call.enqueue(new Callback<NearestObject>() {
            @Override
            public void onResponse(Call<NearestObject> call, Response<NearestObject> response) {
                Log.d("TAG", "onResponse " + response.message());
                if (response.isSuccessful()) {
                    Log.d("TAG", "onResponse " + response.body().getDistance());
                    Log.d("TAG", "onResponse " + response.body().getDirection());
                    Log.d("TAG", "onResponse " + response.body().getTarget().getId());
                    Log.d("TAG", "onResponse " + response.body().getTarget().getCoin());
                    Log.d("TAG", "onResponse " + response.body().getTarget().getName());
                    Log.d("TAG", "onResponse " + response.body().getTarget().getAvatar());

                    response.body().getTarget().setType(targetType);
                }

                EventBus.getDefault().post(new NearestResponseEvent(response.body(), response.isSuccessful()));
            }

            @Override
            public void onFailure(Call<NearestObject> call, Throwable t) {
                Log.d("TAG", "onResponse onFailure " + t.getMessage());
            }
        });
    }
}
