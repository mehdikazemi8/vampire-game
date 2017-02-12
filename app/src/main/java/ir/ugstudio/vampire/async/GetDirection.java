package ir.ugstudio.vampire.async;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.events.DirectionResponseEvent;
import ir.ugstudio.vampire.managers.CacheHandler;
import ir.ugstudio.vampire.managers.SharedPrefHandler;
import ir.ugstudio.vampire.managers.UserHandler;
import ir.ugstudio.vampire.models.nearest.Target;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetDirection {
    public static void run(final Context context) {
        final Target target = SharedPrefHandler.readMissionObject(context);
        Location lastLocation = CacheHandler.getLastLocation();
        if (target == null || lastLocation == null) {
//            EventBus.getDefault().post(new DirectionResponseEvent());
            Log.d("TAG", "directionxxx NULLL");
        } else {
            Call<Target> call = VampireApp.createMapApi().getDirection(
                    UserHandler.readToken(context),
                    target.getTarget().getType(),
                    target.getTarget().getId(),
                    lastLocation.getLatitude(),
                    lastLocation.getLongitude()
            );

            call.enqueue(new Callback<Target>() {
                @Override
                public void onResponse(Call<Target> call, Response<Target> response) {
                    Log.d("TAG", "directionxxx " + response.isSuccessful());
                    if (response.isSuccessful()) {
                        Log.d("TAG", "directionxxx " + response.body().getDirection());
                        Log.d("TAG", "directionxxx " + response.body().getDistance());

//                        String msg = "" + response.body().getDistance() + " " + response.body().getDirection();
//                        Utility.makeToast(context, msg, Toast.LENGTH_SHORT);

                        updatePreferenceObject(context, response.body());
                        EventBus.getDefault().post(new DirectionResponseEvent());
                    }
                }

                @Override
                public void onFailure(Call<Target> call, Throwable t) {
                    Log.d("TAG", "directionxxx fail " + t.getMessage());
                }
            });
        }

    }

    private static void updatePreferenceObject(Context context, Target responseObjec) {
        Target preferenceObject = SharedPrefHandler.readMissionObject(context);
        if (preferenceObject == null) {
            preferenceObject = responseObjec;
        } else {
            preferenceObject.setDirection(responseObjec.getDirection());
            preferenceObject.setDistance(responseObjec.getDistance());
        }
        SharedPrefHandler.writeMissionObject(context, preferenceObject);
    }
}
