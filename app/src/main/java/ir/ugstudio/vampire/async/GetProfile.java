package ir.ugstudio.vampire.async;

import android.content.Context;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.events.GetProfileEvent;
import ir.ugstudio.vampire.managers.CacheHandler;
import ir.ugstudio.vampire.managers.UserHandler;
import ir.ugstudio.vampire.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetProfile {
    public static void run(final Context context) {
        String token = UserHandler.readToken(context);

        Call<User> call = VampireApp.createUserApi().getProfile(token);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d("TAG", "GetProfile response " + response.message());
                if (response.isSuccessful()) {
                    CacheHandler.setUser(response.body());
                    UserHandler.writeUser(context, response.body());

                    EventBus.getDefault().post(new GetProfileEvent(response.body(), true));
                } else {
                    EventBus.getDefault().post(new GetProfileEvent(response.body(), false));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                EventBus.getDefault().post(new GetProfileEvent(null, false));
            }
        });
    }
}
