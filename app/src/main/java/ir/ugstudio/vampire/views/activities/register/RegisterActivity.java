package ir.ugstudio.vampire.views.activities.register;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.views.activities.register.fragments.LoginFragment;
import ir.ugstudio.vampire.views.activities.register.fragments.RegisterFragment;

public class RegisterActivity extends FragmentActivity implements View.OnClickListener {

    private Button login;
    private Button register;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        find();
        configure();
    }

    private void find() {
        login = (Button) findViewById(R.id.registered_before);
        register = (Button) findViewById(R.id.new_user);
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
}
