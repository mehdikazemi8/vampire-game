package ir.ugstudio.vampire.views.activities.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.managers.UserManager;
import ir.ugstudio.vampire.models.User;
import ir.ugstudio.vampire.utils.Consts;
import ir.ugstudio.vampire.utils.MemoryCache;
import ir.ugstudio.vampire.views.activities.main.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mehdiii on 10/8/16.
 */

public class SplashActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        getProfile();
    }

    private void getProfile() {
        String token = UserManager.readToken(SplashActivity.this);
        // todo delete this
//        token = "de79d0e494db52ef52a8eee17bda5c9419de370b";

        if(false && (token == null || token.isEmpty())) {
            startRegisterActivity();
        } else {
            Call<User> call = VampireApp.createUserApi().getProfile(token);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if(response.isSuccessful()) {
                        startMainActivity(response.body());
                    } else {
                        startRegisterActivity();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    startRegisterActivity();
                }
            });
        }
    }

    private void startMainActivity(User user) {
        // get profile, save it and continue to maps activity
        UserManager.writeUser(SplashActivity.this, user);

        Log.d("TAG", "startMainActivity " + user.serialize());

        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    private void startRegisterActivity() {
        UserManager.clearUser(SplashActivity.this);
//        start register activity
//        startActivity(new Intent(SplashActivity.this, RegisterActivity.class));
//        finish();
    }
}
