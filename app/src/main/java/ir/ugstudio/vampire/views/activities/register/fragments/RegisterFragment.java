package ir.ugstudio.vampire.views.activities.register.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.viewpagerindicator.CirclePageIndicator;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.async.GetQuotes;
import ir.ugstudio.vampire.async.GetStoreItems;
import ir.ugstudio.vampire.managers.AvatarManager;
import ir.ugstudio.vampire.managers.CacheHandler;
import ir.ugstudio.vampire.managers.UserHandler;
import ir.ugstudio.vampire.models.User;
import ir.ugstudio.vampire.utils.FontHelper;
import ir.ugstudio.vampire.views.activities.main.MainActivity;
import ir.ugstudio.vampire.views.custom.CustomButton;
import ir.ugstudio.vampire.views.custom.avatar.CustomPagerAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment implements View.OnClickListener {

    private ViewPager viewPager;
    private CustomPagerAdapter pagerAdapter;
    private CirclePageIndicator indicator;
    private ProgressDialog progressDialog;

    private CustomButton register;
    private EditText username;
    private EditText password;
    private MaterialSpinner playerType;
    private int avatarInt = -1;
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
        configureViewPager(0);
    }

    private void configureViewPager(int type) {
        if (type == 0) {
            avatarInt = -1;
        } else {
            avatarInt = 0;
        }

        pagerAdapter = new CustomPagerAdapter(getActivity(), type);
        viewPager.setAdapter(pagerAdapter);
        indicator.setViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("TAG", "selectedyyy " + viewPager.getCurrentItem());
                avatarInt = viewPager.getCurrentItem();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void find(View view) {
        register = (CustomButton) view.findViewById(R.id.register);
        username = (EditText) view.findViewById(R.id.username);
        password = (EditText) view.findViewById(R.id.password);
        playerType = (MaterialSpinner) view.findViewById(R.id.player_type);

        viewPager = (ViewPager) view.findViewById(R.id.viewpager_avatars);
        indicator = (CirclePageIndicator) view.findViewById(R.id.indicator_avatars);
    }

    private void configure() {
        register.setOnClickListener(this);

        FontHelper.setKoodakFor(getActivity(), playerType);

        playerType.setItems(getActivity().getResources().getStringArray(R.array.player_type_keys));
        playerType.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                playerTypeStr = getActivity().getResources().getStringArray(R.array.player_type_values)[position];
                configureViewPager(position);
            }
        });
    }

    private boolean validUsername(String str) {
        return str.matches("[A-Za-z0-9-_]+");
    }

    private boolean validForm() {
        if (username.getText().toString().trim().length() == 0) {
            Toast.makeText(getActivity(), "لطفا نام کاربری خود را انتخاب کنید", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!validUsername(username.getText().toString().trim())) {
            Toast.makeText(getActivity(), "برای نام کاربری فقط حروف انگلیسی و رقم استفاده کنید", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.getText().toString().trim().length() == 0) {
            Toast.makeText(getActivity(), "لطفا رمز عبور خود را انتخاب کنید", Toast.LENGTH_SHORT).show();
            return false;
        } else if (avatarInt == -1) {
            Toast.makeText(getActivity(), "لطفا نوع شخصیت خود را انتخاب کنید", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void doRegister() {
        if (!validForm()) {
            return;
        }

        progressDialog = ProgressDialog.show(getActivity(), "", "در حال ارسال درخواست", true);
        progressDialog.show();

        String usernameStr = username.getText().toString();
        String passwordStr = password.getText().toString();

        Call<User> call = VampireApp.createUserApi().register(usernameStr, passwordStr, playerTypeStr, AvatarManager.getAvatarIndex(playerTypeStr, avatarInt));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
//                progressDialog.dismiss();
                if (response.code() == 409) {
                    Toast.makeText(getActivity(), "نام کاربری تکراری است", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.isSuccessful()) {
                    startMainActivity(response.body());
                } else {
                    Toast.makeText(getActivity(), "مشکلی پیش آمده لطفا دوباره امتحان کنید", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
//                progressDialog.dismiss();
                Toast.makeText(getActivity(), "مشکلی پیش آمده لطفا دوباره امتحان کنید", Toast.LENGTH_SHORT).show();
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
        UserHandler.writeUser(getActivity(), user);
        CacheHandler.setUser(user);

        new GetQuotes(getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        GetStoreItems.run(getActivity());

        startActivity(new Intent(getActivity(), MainActivity.class));
        // todo check this line
        getActivity().finish();
    }

}
