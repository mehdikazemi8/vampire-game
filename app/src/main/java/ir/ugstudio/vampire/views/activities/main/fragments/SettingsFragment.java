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
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.async.GetProfile;
import ir.ugstudio.vampire.managers.AnalyticsManager;
import ir.ugstudio.vampire.managers.AvatarManager;
import ir.ugstudio.vampire.managers.CacheHandler;
import ir.ugstudio.vampire.managers.SharedPrefManager;
import ir.ugstudio.vampire.views.BaseFragment;
import ir.ugstudio.vampire.views.custom.CustomButton;
import ir.ugstudio.vampire.views.custom.CustomTextView;
import ir.ugstudio.vampire.views.custom.avatar.CustomPagerAdapter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends BaseFragment implements View.OnClickListener {

    CustomTextView username;
    ImageView roleAvatar;
    private ViewPager viewPager;
    private CustomPagerAdapter pagerAdapter;
    private CirclePageIndicator indicator;
    private ProgressDialog progressDialog;
    private int avatarInt = -1;
    private CustomButton logout;
    private CustomButton rate;
    private CustomButton share;
    private CustomButton changeAvatarBtn;
    private CustomButton telegramContact;

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
        logout = (CustomButton) view.findViewById(R.id.logout);
        share = (CustomButton) view.findViewById(R.id.share);
        rate = (CustomButton) view.findViewById(R.id.rate);
        changeAvatarBtn = (CustomButton) view.findViewById(R.id.change_avatar);
        telegramContact = (CustomButton) view.findViewById(R.id.telegram_contact);

        username = (CustomTextView) view.findViewById(R.id.username);
        roleAvatar = (ImageView) view.findViewById(R.id.role_avatar);

        viewPager = (ViewPager) view.findViewById(R.id.viewpager_avatars);
        indicator = (CirclePageIndicator) view.findViewById(R.id.indicator_avatars);

    }

    private void configure() {
        logout.setOnClickListener(this);
        share.setOnClickListener(this);
        rate.setOnClickListener(this);
        changeAvatarBtn.setOnClickListener(this);
        telegramContact.setOnClickListener(this);

        username.setText(CacheHandler.getUser().getUsername());
        if (CacheHandler.getUser().getRole().equals("hunter")) {
            Picasso.with(getActivity()).load(R.drawable.role_hunter).into(roleAvatar);
        } else {
            Picasso.with(getActivity()).load(R.drawable.role_vampire).into(roleAvatar);
        }
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
        AnalyticsManager.logEvent(AnalyticsManager.RATE_APP, "" + CacheHandler.getUser().getUsername());

        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setData(Uri.parse("bazaar://details?id=" + getActivity().getPackageName()));
        intent.setPackage("com.farsitel.bazaar");
        startActivity(intent);
    }

    private void shareApp() {
        AnalyticsManager.logEvent(AnalyticsManager.SHARE_APP, "" + CacheHandler.getUser().getUsername());

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String str = "http://cafebazaar.ir/app/" + getActivity().getPackageName();
        str = str + "\n\n" + String.format(getString(R.string.message_share), CacheHandler.getUser().getUsername());
        sendIntent.putExtra(Intent.EXTRA_TEXT, str);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

//    private void selectAvatar() {
//        AvatarSelectionDialog dialog = new AvatarSelectionDialog();
//        dialog.show(getFragmentManager(), "DIALOG_AVATAR_CHOOSER");
//    }

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

    private void openTelegram() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String str = "https://telegram.me/Frshd_Jfri";
        sendIntent.putExtra(Intent.EXTRA_TEXT, str);
        sendIntent.setPackage("org.telegram.messenger");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.telegram_contact:
                openTelegram();
                break;

            case R.id.logout:
                logout();
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
