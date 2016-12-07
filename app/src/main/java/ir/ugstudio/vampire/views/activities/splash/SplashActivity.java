package ir.ugstudio.vampire.views.activities.splash;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.async.GetProfile;
import ir.ugstudio.vampire.async.GetQuotes;
import ir.ugstudio.vampire.async.GetStoreItems;
import ir.ugstudio.vampire.events.GetProfileEvent;
import ir.ugstudio.vampire.managers.UserHandler;
import ir.ugstudio.vampire.models.User;
import ir.ugstudio.vampire.views.activities.main.MainActivity;
import ir.ugstudio.vampire.views.activities.register.RegisterActivity;

public class SplashActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        getProfile();
    }

    private void getProfile() {
        String token = UserHandler.readToken(SplashActivity.this);

        if (token == null || token.isEmpty()) {
            startRegisterActivity();
        } else {
            GetProfile.run(SplashActivity.this);
            GetStoreItems.run(SplashActivity.this);
        }
    }

    private void startMainActivity(User user) {

        new GetQuotes(SplashActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        new GetPlaces(SplashActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        // get profile, save it and continue to maps activity
        UserHandler.writeUser(SplashActivity.this, user);

        Log.d("TAG", "startMainActivity " + user.serialize());

        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    private void startRegisterActivity() {
        UserHandler.clearUser(SplashActivity.this);
//        start register activity
        startActivity(new Intent(SplashActivity.this, RegisterActivity.class));
        finish();
    }

    @Subscribe
    public void onEvent(GetProfileEvent event) {
        Log.d("TAG", "onEvent GetProfileEvent " + event.isSuccessfull());
        if (event.isSuccessfull()) {
            startMainActivity(event.getUser());
        } else {
            startRegisterActivity();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
