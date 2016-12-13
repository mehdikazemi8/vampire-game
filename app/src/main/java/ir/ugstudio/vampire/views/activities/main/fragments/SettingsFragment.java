package ir.ugstudio.vampire.views.activities.main.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.viewpagerindicator.CirclePageIndicator;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.async.GetProfile;
import ir.ugstudio.vampire.managers.AvatarManager;
import ir.ugstudio.vampire.managers.CacheHandler;
import ir.ugstudio.vampire.managers.SharedPrefManager;
import ir.ugstudio.vampire.views.BaseFragment;
import ir.ugstudio.vampire.views.custom.CustomButton;
import ir.ugstudio.vampire.views.custom.avatar.AvatarSelectionDialog;
import ir.ugstudio.vampire.views.custom.avatar.CustomPagerAdapter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends BaseFragment implements View.OnClickListener {

    private ViewPager viewPager;
    private CustomPagerAdapter pagerAdapter;
    private CirclePageIndicator indicator;
    private ProgressDialog progressDialog;

    private int avatarInt = -1;

    private Button logout;
    private Button selectAvatar;
    private Button rate;
    private Button share;
    private CustomButton changeAvatarBtn;

    public static SettingsFragment getInstance() {
        return new SettingsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        find(view);
        configure();
        configureViewPager(CacheHandler.getUser().getAvatar() / 1000);
    }

    private void find(View view) {
        logout = (Button) view.findViewById(R.id.logout);
        selectAvatar = (Button) view.findViewById(R.id.select_avatar);
        share = (Button) view.findViewById(R.id.share);
        rate = (Button) view.findViewById(R.id.rate);
        changeAvatarBtn = (CustomButton) view.findViewById(R.id.change_avatar);

        viewPager = (ViewPager) view.findViewById(R.id.viewpager_avatars);
        indicator = (CirclePageIndicator) view.findViewById(R.id.indicator_avatars);
    }

    private void configure() {
        logout.setOnClickListener(this);
        selectAvatar.setOnClickListener(this);
        share.setOnClickListener(this);
        rate.setOnClickListener(this);
        changeAvatarBtn.setOnClickListener(this);
    }

    private void configureViewPager(int type) {
        avatarInt = CacheHandler.getUser().getAvatar();

        pagerAdapter = new CustomPagerAdapter(getActivity(), type);
        viewPager.setAdapter(pagerAdapter);
        indicator.setViewPager(viewPager);

        viewPager.setCurrentItem(avatarInt % (type * 1000));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("TAG", "selectedyyy " + viewPager.getCurrentItem());
//                avatarInt = viewPager.getCurrentItem();
                avatarInt = AvatarManager.getAvatarIndexInThousand(CacheHandler.getUser().getRole(), viewPager.getCurrentItem());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void logout() {
        SharedPrefManager.clearAll(getActivity());
        getActivity().finish();
    }

    private void rateApp() {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setData(Uri.parse("bazaar://details?id=" + getActivity().getPackageName()));
        intent.setPackage("com.farsitel.bazaar");
        startActivity(intent);
    }

    private void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String str = "http://cafebazaar.ir/app/" + getActivity().getPackageName();
        str = str + "\n\n" + String.format(getString(R.string.message_share), CacheHandler.getUser().getUsername());
        sendIntent.putExtra(Intent.EXTRA_TEXT, str);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void selectAvatar() {
        AvatarSelectionDialog dialog = new AvatarSelectionDialog();
        dialog.show(getFragmentManager(), "DIALOG_AVATAR_CHOOSER");
    }

    private void changeAvatar() {
        progressDialog = ProgressDialog.show(getActivity(), "", "لطفا چند لحظه صبر کنید", true);
        progressDialog.show();

        Call<ResponseBody> call = VampireApp.createUserApi().changeAvatar(CacheHandler.getUser().getToken(), avatarInt);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();

                // todo, don't ask server, update yourself now
                if (response.isSuccessful()) {
                    GetProfile.run(getActivity());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logout:
                logout();
                break;

            case R.id.select_avatar:
                selectAvatar();
                break;

            case R.id.share:
                shareApp();
                break;

            case R.id.rate:
                rateApp();
                break;

            case R.id.change_avatar:
                changeAvatar();
                break;
        }
    }

    @Override
    public void onBringToFront() {
        super.onBringToFront();
        Log.d("TAG", "onBringToFront SettingsFragment");
    }
}
