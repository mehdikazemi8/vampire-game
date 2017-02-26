package ir.ugstudio.vampire.views.activities.register.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.async.GetQuotes;
import ir.ugstudio.vampire.async.GetStoreItems;
import ir.ugstudio.vampire.managers.CacheHandler;
import ir.ugstudio.vampire.managers.UserHandler;
import ir.ugstudio.vampire.models.User;
import ir.ugstudio.vampire.utils.Utility;
import ir.ugstudio.vampire.views.activities.main.MainActivity;
import ir.ugstudio.vampire.views.custom.CustomButton;
import ir.ugstudio.vampire.views.custom.CustomEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private CustomButton login;
    private CustomEditText username;
    private CustomEditText password;
    private ProgressDialog progressDialog;

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
        login = (CustomButton) view.findViewById(R.id.login);
        username = (CustomEditText) view.findViewById(R.id.username);
        password = (CustomEditText) view.findViewById(R.id.password);
    }

    private void configure() {
        login.setOnClickListener(this);
    }

    private boolean validForm() {
        if (username.getText().toString().trim().length() == 0) {
            Utility.makeToast(getActivity(), getString(R.string.toast_validate_form_username), Toast.LENGTH_LONG);
            return false;
        } else if (password.getText().toString().trim().length() == 0) {
            Utility.makeToast(getActivity(), getString(R.string.toast_validate_form_password), Toast.LENGTH_LONG);
            return false;
        }
        return true;
    }

    private void doLogin() {
        if (!validForm()) {
            return;
        }

        progressDialog = ProgressDialog.show(getActivity(), "", "در حال ارسال درخواست", true);
        progressDialog.show();

        String usernameStr = username.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();

        Call<User> call = VampireApp.createUserApi().login(usernameStr, passwordStr);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (response.isSuccessful()) {
                    startMainActivity(response.body());
                } else {
                    progressDialog.dismiss();
                    Utility.makeToast(getActivity(), getString(R.string.toast_login_401), Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressDialog.dismiss();
                Utility.makeToast(getActivity(), getString(R.string.toast_please_try_again_later), Toast.LENGTH_SHORT);
            }
        });
    }

    private void startMainActivity(User user) {
        // get profile, save it and continue to maps activity
        UserHandler.writeUser(getActivity(), user);
        CacheHandler.setUser(user);

        new GetQuotes(getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        GetStoreItems.run(getActivity());

        startActivity(new Intent(getActivity(), MainActivity.class));
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
