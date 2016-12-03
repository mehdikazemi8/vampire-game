package ir.ugstudio.vampire.views.activities.main.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.managers.VampirePreferenceManager;
import ir.ugstudio.vampire.views.BaseFragment;

public class SettingsFragment extends BaseFragment implements View.OnClickListener {

    private Button logout;

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
    }

    private void configure() {
        logout.setOnClickListener(this);
    }

    private void logout() {
        VampirePreferenceManager.clearAll(getActivity());
        getActivity().finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logout:
                logout();
                break;
        }
    }

    @Override
    public void onBringToFront() {
        super.onBringToFront();
        Log.d("TAG", "onBringToFront SettingsFragment");
    }
}
