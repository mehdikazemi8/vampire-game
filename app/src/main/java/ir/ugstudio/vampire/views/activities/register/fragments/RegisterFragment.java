package ir.ugstudio.vampire.views.activities.register.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.async.GetPlaces;
import ir.ugstudio.vampire.async.GetQuotes;
import ir.ugstudio.vampire.managers.UserManager;
import ir.ugstudio.vampire.models.User;
import ir.ugstudio.vampire.views.activities.main.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment implements View.OnClickListener {

    private Button register;
    private EditText username;
    private EditText password;
    private Spinner playerType;
    private boolean playerTypeSelected = false;
    private String playerTypeStr = null;

    public static RegisterFragment getInstance() {
        return new RegisterFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        find(view);
        configure();
    }

    private void find(View view) {
        register = (Button) view.findViewById(R.id.register);
        username = (EditText) view.findViewById(R.id.username);
        password = (EditText) view.findViewById(R.id.password);
        playerType = (Spinner) view.findViewById(R.id.player_type);
    }

    private void configure() {
        register.setOnClickListener(this);
        playerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                playerTypeSelected = true;
                playerTypeStr = getActivity().getResources().getStringArray(R.array.player_type_values)[i];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void doRegister() {
        String usernameStr = username.getText().toString();
        String passwordStr = password.getText().toString();

        Call<User> call = VampireApp.createUserApi().register(usernameStr, passwordStr, playerTypeStr);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()) {
                    startMainActivity(response.body());
                } else {
                    // todo
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // todo
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register:
                doRegister();
                break;
        }
    }

    private void startMainActivity(User user) {
        // get profile, save it and continue to maps activity
        UserManager.writeUser(getActivity(), user);

        new GetQuotes(getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new GetPlaces(getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        Log.d("TAG", "ffff " + user.serialize());

        startActivity(new Intent(getActivity(), MainActivity.class));
        // todo check this line
        getActivity().finish();
    }
}
