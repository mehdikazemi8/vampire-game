package ir.ugstudio.vampire.views.activities.main.fragments;

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
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.events.StartNearestMissionEvent;
import ir.ugstudio.vampire.listeners.OnCompleteListener;
import ir.ugstudio.vampire.managers.CacheHandler;
import ir.ugstudio.vampire.managers.UserHandler;
import ir.ugstudio.vampire.models.StoreItemVirtual;
import ir.ugstudio.vampire.models.nearest.MostWantedList;
import ir.ugstudio.vampire.models.nearest.Target;
import ir.ugstudio.vampire.utils.Consts;
import ir.ugstudio.vampire.utils.introduction.MissionHunter;
import ir.ugstudio.vampire.utils.introduction.MissionVampire;
import ir.ugstudio.vampire.views.BaseFragment;
import ir.ugstudio.vampire.views.activities.main.MainActivity;
import ir.ugstudio.vampire.views.activities.main.adapters.MostWantedViewAdapter;
import ir.ugstudio.vampire.views.custom.CustomTextView;
import ir.ugstudio.vampire.views.dialogs.IntroduceVirtualItemDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MissionOptionsFragment extends BaseFragment {

    @BindView(R.id.most_wanted_list)
    RecyclerView mostWantedList;

    @BindView(R.id.mission_player)
    ImageView missionPlayer;

    @BindView(R.id.empty_most_wanted_list_message)
    CustomTextView emptyMostWantedListMessage;

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

        if (CacheHandler.getUser().getRole().equals("hunter")) {
            Picasso.with(getActivity()).load(R.drawable.vamp1001).into(missionPlayer);
        }
        getMostWantedList();


        if (CacheHandler.getUser().getRole().equals(Consts.ROLE_HUNTER)) {
            ((MainActivity) getActivity()).openHintFragment(new MissionHunter(), true);
            emptyMostWantedListMessage.setText(getString(R.string.empty_most_wanted_message_hunter));
        } else {
            ((MainActivity) getActivity()).openHintFragment(new MissionVampire(), true);
            emptyMostWantedListMessage.setText(getString(R.string.empty_most_wanted_message_vampire));
        }
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
                } else {
                    emptyMostWantedListMessage.setVisibility(View.VISIBLE);
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

    private void showIntroduceDialog(final String itemId, Integer imageType) {
        IntroduceVirtualItemDialog dialog = new IntroduceVirtualItemDialog(
                getActivity(),
                new StoreItemVirtual(itemId, imageType),
                new OnCompleteListener() {
                    @Override
                    public void onComplete(Integer state) {
                        if (state.equals(1)) {
                            EventBus.getDefault().post(new StartNearestMissionEvent(itemId));
                        }
                    }
                }
        );
        dialog.show();

        try {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @OnClick({
            R.id.mission_sheep, R.id.mission_tower, R.id.mission_player,
            R.id.mission_sheep_back, R.id.mission_tower_back, R.id.mission_player_back
    })
    public void startMission(View view) {
        Log.d("TAG", "aaa" + (R.id.mission_sheep == view.getId()));
        Log.d("TAG", "aaa" + (R.id.mission_tower == view.getId()));
        Log.d("TAG", "aaa" + (R.id.mission_player == view.getId()));

        switch (view.getId()) {
            case R.id.mission_sheep:
            case R.id.mission_sheep_back:
                showIntroduceDialog(Consts.NEAREST_SHEEP, 5);
                break;

            case R.id.mission_tower:
            case R.id.mission_tower_back:
                showIntroduceDialog(Consts.NEAREST_TOWER, 6);
                break;

            case R.id.mission_player:
            case R.id.mission_player_back:
                showIntroduceDialog(Consts.NEAREST_PLAYER, 7);
                break;
        }
    }
}
