package ir.ugstudio.vampire.views.activities.splash;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.async.GetProfile;
import ir.ugstudio.vampire.async.GetQuotes;
import ir.ugstudio.vampire.async.GetStoreItems;
import ir.ugstudio.vampire.events.GetProfileEvent;
import ir.ugstudio.vampire.listeners.OnCompleteListener;
import ir.ugstudio.vampire.managers.CacheHandler;
import ir.ugstudio.vampire.managers.UserHandler;
import ir.ugstudio.vampire.models.User;
import ir.ugstudio.vampire.utils.VampireNetworkManager;
import ir.ugstudio.vampire.views.activities.main.MainActivity;
import ir.ugstudio.vampire.views.activities.register.RegisterActivity;
import ir.ugstudio.vampire.views.custom.CustomTextView;
import ir.ugstudio.vampire.views.dialogs.ConnectionLostDialog;

public class SplashActivity extends FragmentActivity {

    ConnectionLostDialog dialog;
    boolean turningWifiOnState = false;
    boolean getProfileSent = false;
    private CustomTextView message;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        find();
        configure();
        checkInternet();
    }

    private void find() {
        message = (CustomTextView) findViewById(R.id.message);
    }

    private void configure() {
        dialog = new ConnectionLostDialog(SplashActivity.this);
        dialog.setOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(Integer state) {
                Log.d("TAG", "onComplete " + state);
                switch (state) {
                    case 0:
                        startAppFromHere();
                        dialog.dismiss();
                        break;

                    case 1:
                        VampireNetworkManager.changeWifiState(SplashActivity.this, true);
                        turningWifiOnState = true;
                        message.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                        break;

                    case 2:
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                        dialog.dismiss();
                        break;
                }
            }
        });
    }

    private void checkInternet() {
        startAppFromHere();

        final long PERIOD = 2000;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkInternet();
            }
        }, PERIOD);
    }

    private void startAppFromHere() {
        if (VampireNetworkManager.isNetworkAvailable(SplashActivity.this)) {
            decideBetweenRegisterAndMain();
        } else {
            showTurnNetworkOnDialog();
        }
    }

    /**
     * 0 : retry
     * 1 : wifi
     * 2 : 3G
     */
    private void showTurnNetworkOnDialog() {
        if (!dialog.isShowing()) {
            if (!turningWifiOnState) {
                if (!this.isFinishing()) {
                    dialog.show();
                }
            }
        }
    }

    private void decideBetweenRegisterAndMain() {
        String token = UserHandler.readToken(SplashActivity.this);
        if (token == null || token.isEmpty()) {
            startRegisterActivity();
        } else {
            if (!getProfileSent) {
                getProfileSent = true;
                message.setText(getString(R.string.connection_lost_message_get_profile));
                message.setVisibility(View.VISIBLE);
                GetProfile.run(SplashActivity.this);
            }
        }
    }

    private void startMainActivity(User user) {
        new GetQuotes(SplashActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        GetStoreItems.run(SplashActivity.this);

        UserHandler.writeUser(SplashActivity.this, user);
        CacheHandler.setUser(user);

        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    private void startRegisterActivity() {
        UserHandler.clearUser(SplashActivity.this);
        startActivity(new Intent(SplashActivity.this, RegisterActivity.class));
        finish();
    }

    @Subscribe
    public void onEvent(GetProfileEvent event) {
        Log.d("TAG", "onEvent GetProfileEvent " + event.isSuccessfull());
        if (event.isSuccessfull()) {
            startMainActivity(event.getUser());
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
