package ir.ugstudio.vampire.async;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.events.NearestResponseEvent;
import ir.ugstudio.vampire.managers.CacheHandler;
import ir.ugstudio.vampire.managers.SharedPrefHandler;
import ir.ugstudio.vampire.models.nearest.Target;
import ir.ugstudio.vampire.utils.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetNearest {
    public static void run(final Context context, final String targetType) {
        Location lastLocation = CacheHandler.getLastLocation();
        if (lastLocation == null) {
            return;
        }

        Call<Target> call = VampireApp.createMapApi().getNearestObject(
                CacheHandler.getUser().getToken(),
                targetType,
                lastLocation.getLatitude(),
                lastLocation.getLongitude()
        );

        call.enqueue(new Callback<Target>() {
            @Override
            public void onResponse(Call<Target> call, Response<Target> response) {
                Log.d("TAG", "onResponse " + response.message());
                if (response.isSuccessful()) {
                    Log.d("TAG", "onResponse " + response.body().getDistance());
                    Log.d("TAG", "onResponse " + response.body().getDirection());
                    Log.d("TAG", "onResponse " + response.body().getTarget().getId());
                    Log.d("TAG", "onResponse " + response.body().getTarget().getCoin());
                    Log.d("TAG", "onResponse " + response.body().getTarget().getUsername());
                    Log.d("TAG", "onResponse " + response.body().getTarget().getAvatar());

                    response.body().getTarget().setType(targetType);

                    SharedPrefHandler.writeMissionObject(context, response.body());
                } else {
                    Utility.makeToast(context, "الان که چشمات نمیبینه که بخوای بزنیش", Toast.LENGTH_LONG);
                }

                EventBus.getDefault().post(new NearestResponseEvent(response.body(), response.isSuccessful()));
            }

            @Override
            public void onFailure(Call<Target> call, Throwable t) {
                Log.d("TAG", "onResponse onFailure " + t.getMessage());
            }
        });
    }
}
