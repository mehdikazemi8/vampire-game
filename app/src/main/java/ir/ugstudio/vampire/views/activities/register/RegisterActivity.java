package ir.ugstudio.vampire.views.activities.register;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.events.CloseIntroductionFragment;
import ir.ugstudio.vampire.utils.Consts;
import ir.ugstudio.vampire.views.activities.register.fragments.LoginFragment;
import ir.ugstudio.vampire.views.activities.register.fragments.RegisterFragment;
import ir.ugstudio.vampire.views.custom.CustomButton;

public class RegisterActivity extends FragmentActivity implements View.OnClickListener {

    private CustomButton login;
    private CustomButton register;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        find();
        configure();
    }

    private void find() {
        login = (CustomButton) findViewById(R.id.registered_before);
        register = (CustomButton) findViewById(R.id.new_user);
    }

    private void configure() {
        login.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    private void loginFragment() {
        LoginFragment fragment = LoginFragment.getInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_holder, fragment, null)
                .addToBackStack(null)
                .commit();
    }

    private void registerFragment() {
        RegisterFragment fragment = RegisterFragment.getInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_holder, fragment, null)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.registered_before:
                loginFragment();
                break;

            case R.id.new_user:
                registerFragment();
                break;
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

//    @Subscribe
//    public void onEvent(OpenIntroductionFragment event) {
//        IntroductionFragment fragment = IntroductionFragment.getInstance();
//        getSupportFragmentManager()
//                .beginTransaction()
//                .add(R.id.fragment_holder, fragment, Consts.FRG_INTRO)
//                .addToBackStack(null)
//                .commit();
//    }

    @Subscribe
    public void onEvent(CloseIntroductionFragment event) {
        if (getSupportFragmentManager().findFragmentByTag(Consts.FRG_INTRO) != null) {
            getSupportFragmentManager().popBackStack();
        }
    }
}
