package ir.ugstudio.vampire.views.activities.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.managers.UserManager;
import ir.ugstudio.vampire.views.activities.maps.MapsActivity;

/**
 * Created by mehdiii on 10/8/16.
 */

public class SplashActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        String token = UserManager.readToken(SplashActivity.this);
        if(false && (token == null || token.isEmpty())) {
            // start register activity
        } else {
            // get profile, save it and continue to maps activity
            startActivity(new Intent(SplashActivity.this, MapsActivity.class));
            finish();
        }
    }
}
