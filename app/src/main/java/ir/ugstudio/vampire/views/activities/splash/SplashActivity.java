package ir.ugstudio.vampire.views.activities.splash;

import android.content.Intent;
import android.content.pm.PackageInfo;
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
import ir.ugstudio.vampire.models.UpdateVersion;
import ir.ugstudio.vampire.models.User;
import ir.ugstudio.vampire.utils.VampireNetworkManager;
import ir.ugstudio.vampire.views.activities.main.MainActivity;
import ir.ugstudio.vampire.views.activities.register.RegisterActivity;
import ir.ugstudio.vampire.views.custom.CustomTextView;
import ir.ugstudio.vampire.views.dialogs.ConnectionLostDialog;
import ir.ugstudio.vampire.views.dialogs.UpdateDialog;

public class SplashActivity extends FragmentActivity {

    private final static String URL = "http://cafebazaar.ir/app/%s/?l=fa";
    private ConnectionLostDialog dialog;
    private CustomTextView message;
    private boolean turningWifiOnState = false;
    private boolean getProfileSent = false;
    private boolean continueChekingInternet = true;

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
        Log.d("TAG", "checkInternet");
        if (!continueChekingInternet) {
            return;
        }

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

    /**
     * When this method is called, we definitely have internet access
     */
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
        continueChekingInternet = false;

        new GetQuotes(SplashActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        GetStoreItems.run(SplashActivity.this);

        // todo, inja momkene user null bashe va gir kone?
        UserHandler.writeUser(SplashActivity.this, user);
        CacheHandler.setUser(user);

        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    private void startRegisterActivity() {
        continueChekingInternet = false;

        UserHandler.clearUser(SplashActivity.this);
        startActivity(new Intent(SplashActivity.this, RegisterActivity.class));
        finish();
    }

    private void showUpdateDialog(final boolean forceUpdate, final User user) {
        UpdateDialog updateDialog = new UpdateDialog(SplashActivity.this, forceUpdate, new OnCompleteListener() {
            @Override
            public void onComplete(Integer state) {
                if (state == 0) {
                    if (forceUpdate) {
                        finish();
                    } else {
                        startMainActivity(user);
                    }
                }
            }
        });
        updateDialog.show();
    }

    private boolean haveNewUpdate(UpdateVersion updateVersion) {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int myVersionCode = pInfo.versionCode;
            return myVersionCode < updateVersion.getCurrentVersion();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isForceUpdate(UpdateVersion updateVersion) {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int myVersionCode = pInfo.versionCode;
            return myVersionCode < updateVersion.getMinimumSupportVersion();
        } catch (Exception e) {
            return false;
        }
    }

    @Subscribe
    public void onEvent(GetProfileEvent event) {
        Log.d("TAG", "onEvent GetProfileEvent " + event.isSuccessfull());
        if (event.isSuccessfull()) {
            if (haveNewUpdate(event.getUser().getUpdateVersion())) {
                showUpdateDialog(isForceUpdate(event.getUser().getUpdateVersion()), event.getUser());
            } else {
                startMainActivity(event.getUser());
            }
        } else {
            startMainActivity(event.getUser());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
