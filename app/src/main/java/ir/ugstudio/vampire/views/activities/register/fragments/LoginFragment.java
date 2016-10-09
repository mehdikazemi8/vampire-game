package ir.ugstudio.vampire.views.activities.register.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.managers.UserManager;
import ir.ugstudio.vampire.models.User;
import ir.ugstudio.vampire.views.activities.main.MainActivity;
import ir.ugstudio.vampire.views.activities.register.RegisterActivity;
import ir.ugstudio.vampire.views.activities.splash.SplashActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private Button login;
    private EditText username;
    private EditText password;

    public static LoginFragment getInstance() {
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        find(view);
        configure();
    }

    private void find(View view) {
        login = (Button) view.findViewById(R.id.login);
        username = (EditText) view.findViewById(R.id.username);
        password = (EditText) view.findViewById(R.id.password);
    }

    private void configure() {
        login.setOnClickListener(this);
    }

    private void doLogin() {
        String usernameStr = username.getText().toString();
        String passwordStr = password.getText().toString();

        Call<User> call = VampireApp.createUserApi().login(usernameStr, passwordStr);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()) {
                    Log.d("TAG", "dddd " + response.body().serialize());
                    startMainActivity(response.body());
                } else {
                    // todo say something to user
                    Log.d("TAG", "dddd " + response.message() + " " + response.code());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // todo say something to user

            }
        });
    }

    private void startMainActivity(User user) {
        // get profile, save it and continue to maps activity
        UserManager.writeUser(getActivity(), user);

        startActivity(new Intent(getActivity(), MainActivity.class));
        // todo check this line
        getActivity().finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                doLogin();
                break;
        }
    }
}
