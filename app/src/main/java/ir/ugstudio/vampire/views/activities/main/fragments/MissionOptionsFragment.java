package ir.ugstudio.vampire.views.activities.main.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.events.StartNearestMissionEvent;
import ir.ugstudio.vampire.managers.CacheHandler;
import ir.ugstudio.vampire.managers.UserHandler;
import ir.ugstudio.vampire.models.nearest.MostWantedList;
import ir.ugstudio.vampire.models.nearest.Target;
import ir.ugstudio.vampire.utils.FontHelper;
import ir.ugstudio.vampire.views.BaseFragment;
import ir.ugstudio.vampire.views.activities.main.adapters.MostWantedViewAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MissionOptionsFragment extends BaseFragment {

    @BindView(R.id.most_wanted_list)
    RecyclerView mostWantedList;

    private Unbinder unbinder;
    private MostWantedViewAdapter mostWantedViewAdapter;

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

        getMostWantedList();
    }

    private void getMostWantedList() {
        Location lastLocation = CacheHandler.getLastLocation();
        if (lastLocation == null) {
            return;
        }

        Call<MostWantedList> call = VampireApp.createMapApi().getMostWantedList(
                UserHandler.readToken(getActivity()),
                lastLocation.getLatitude(),
                lastLocation.getLongitude()
        );

        call.enqueue(new Callback<MostWantedList>() {
            @Override
            public void onResponse(Call<MostWantedList> call, Response<MostWantedList> response) {
                Log.d("TAG", "MostWantedList " + response.message());

                if (response.isSuccessful()) {
                    bindListData(response.body().getMostWantedList());
                }
            }

            @Override
            public void onFailure(Call<MostWantedList> call, Throwable t) {
                Log.d("TAG", "MostWantedList fail " + t.getMessage());
            }
        });
    }

    private void bindListData(List<Target> mostWantedItems) {

        GridLayoutManager layoutManager
                = new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false);
        mostWantedList.setLayoutManager(layoutManager);
        mostWantedViewAdapter = new MostWantedViewAdapter(mostWantedItems);
        mostWantedList.setAdapter(mostWantedViewAdapter);
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

        String targetType = null;

        String message = "";
        switch (view.getId()) {
            case R.id.mission_sheep:
                message = "برای پیدا کردن یک گوسفند که تو نزدیکیات هست باید ۵۰ سکه بپردازی";
                targetType = "sheep";
                break;

            case R.id.mission_tower:
                message = "برای پیدا کردن یک برج باید ۲۰۰ سکه بپردازی";
                targetType = "tower";
                break;

            case R.id.mission_player:
                message = "برای پیدا کردن یک خون آشام باید ۱۰۰ سکه بپردازی";
                targetType = "player";
                break;
        }

        final String finalTargetType = targetType;
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("حله بریم", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EventBus.getDefault().post(new StartNearestMissionEvent(finalTargetType));
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
