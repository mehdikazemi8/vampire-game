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
import ir.ugstudio.vampire.models.nearest.NearestObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetDirection {
    public static void run(final Context context) {
        final NearestObject nearestObject = SharedPrefHandler.readMissionObject(context);
        Location lastLocation = CacheHandler.getLastLocation();
        if (nearestObject == null || lastLocation == null) {
//            EventBus.getDefault().post(new DirectionResponseEvent());
            Log.d("TAG", "directionxxx NULLL");
        } else {
            Call<NearestObject> call = VampireApp.createMapApi().getDirection(
                    UserHandler.readToken(context),
                    nearestObject.getTarget().getType(),
                    nearestObject.getTarget().getId(),
                    lastLocation.getLatitude(),
                    lastLocation.getLongitude()
            );

            call.enqueue(new Callback<NearestObject>() {
                @Override
                public void onResponse(Call<NearestObject> call, Response<NearestObject> response) {
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
                public void onFailure(Call<NearestObject> call, Throwable t) {
                    Log.d("TAG", "directionxxx fail " + t.getMessage());
                }
            });
        }

    }

    private static void updatePreferenceObject(Context context, NearestObject responseObjec) {
        NearestObject preferenceObject = SharedPrefHandler.readMissionObject(context);
        if (preferenceObject == null) {
            preferenceObject = responseObjec;
        } else {
            preferenceObject.setDirection(responseObjec.getDirection());
            preferenceObject.setDistance(responseObjec.getDistance());
        }
        SharedPrefHandler.writeMissionObject(context, preferenceObject);
    }
}
