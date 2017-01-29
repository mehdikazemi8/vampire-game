package ir.ugstudio.vampire.views.activities.main.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.utils.FontHelper;
import ir.ugstudio.vampire.views.BaseFragment;

public class MissionOptionsFragment extends BaseFragment {

    private Unbinder unbinder;

    public static MissionOptionsFragment getInstance() {
        return new MissionOptionsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mission_options, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
    }

    @OnClick({R.id.mission_sheep, R.id.mission_tower, R.id.mission_player})
    public void startMission(View view) {
        Log.d("TAG", "aaa" + (R.id.mission_sheep == view.getId()));
        Log.d("TAG", "aaa" + (R.id.mission_tower == view.getId()));
        Log.d("TAG", "aaa" + (R.id.mission_player == view.getId()));

        String message = "";
        switch (view.getId()) {
            case R.id.mission_sheep:
                message = "برای پیدا کردن یک گوسفند که تو نزدیکیات هست باید ۵۰ سکه بپردازی";
                break;

            case R.id.mission_tower:
                message = "برای پیدا کردن یک برج باید ۲۰۰ سکه بپردازی";
                break;

            case R.id.mission_player:
                message = "برای پیدا کردن یک خون آشام باید ۱۰۰ سکه بپردازی";
                break;
        }

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("حله بریم", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("فعلا نه", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
        dialog.setCancelable(true);
        FontHelper.setKoodakFor(getActivity(), (TextView) dialog.findViewById(android.R.id.message));
    }
}
