package ir.ugstudio.vampire.views.activities.register;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.views.activities.register.fragments.LoginFragment;

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
    }

    private void configure() {
        login.setOnClickListener(this);
    }

    private void loginFragment() {
        LoginFragment fragment = LoginFragment.getInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.login_fragment_holder, fragment, null)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.registered_before:
                loginFragment();
                break;
        }
    }
}
