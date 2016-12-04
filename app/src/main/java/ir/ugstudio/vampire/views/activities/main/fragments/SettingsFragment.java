package ir.ugstudio.vampire.views.activities.main.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.managers.CacheManager;
import ir.ugstudio.vampire.managers.VampirePreferenceManager;
import ir.ugstudio.vampire.views.BaseFragment;
import ir.ugstudio.vampire.views.custom.avatar.AvatarSelectionDialog;

public class SettingsFragment extends BaseFragment implements View.OnClickListener {

    private Button logout;
    private Button selectAvatar;
    private Button rate;
    private Button share;

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
    }

    private void find(View view) {
        logout = (Button) view.findViewById(R.id.logout);
        selectAvatar = (Button) view.findViewById(R.id.select_avatar);
        share = (Button) view.findViewById(R.id.share);
        rate = (Button) view.findViewById(R.id.rate);
    }

    private void configure() {
        logout.setOnClickListener(this);
        selectAvatar.setOnClickListener(this);
        share.setOnClickListener(this);
        rate.setOnClickListener(this);
    }

    private void logout() {
        VampirePreferenceManager.clearAll(getActivity());
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
        str = str + "\n\n" + String.format(getString(R.string.message_share), CacheManager.getUser().getUsername());
        sendIntent.putExtra(Intent.EXTRA_TEXT, str);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void selectAvatar() {
        AvatarSelectionDialog dialog = new AvatarSelectionDialog();
        dialog.show(getFragmentManager(), "DIALOG_AVATAR_CHOOSER");
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
        }
    }

    @Override
    public void onBringToFront() {
        super.onBringToFront();
        Log.d("TAG", "onBringToFront SettingsFragment");
    }
}
