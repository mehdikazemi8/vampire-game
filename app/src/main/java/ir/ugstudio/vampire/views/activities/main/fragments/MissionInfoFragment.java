package ir.ugstudio.vampire.views.activities.main.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.events.DirectionResponseEvent;
import ir.ugstudio.vampire.events.FinishNearestMissionEvent;
import ir.ugstudio.vampire.managers.AvatarManager;
import ir.ugstudio.vampire.managers.SharedPrefHandler;
import ir.ugstudio.vampire.models.nearest.Target;
import ir.ugstudio.vampire.utils.Consts;
import ir.ugstudio.vampire.views.BaseFragment;
import ir.ugstudio.vampire.views.custom.CustomTextView;

public class MissionInfoFragment extends BaseFragment {

    @BindView(R.id.username)
    CustomTextView username;
    @BindView(R.id.coin)
    CustomTextView coin;
    @BindView(R.id.distance)
    CustomTextView distance;
    @BindView(R.id.avatar)
    ImageView avatar;

    private Unbinder unbinder;

    public static MissionInfoFragment getInstance() {
        return new MissionInfoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mission_info, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateViewsData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(DirectionResponseEvent event) {
        updateViewsData();
    }

    private void updateViewsData() {
        Target target = SharedPrefHandler.readMissionObject(getActivity());
        Log.d("TAG", "updateViewsData " + target.serialize());

        if (target == null) {
            return;
        }

        // todo, optimize this
        coin.setText(String.valueOf(target.getTarget().getCoin()));
        distance.setText(String.format(getString(R.string.template_nearest_distance), target.getDistance().intValue()));

        if (target.getTarget().getType().equals(Consts.TARGET_TYPE_PLAYER)) {
            username.setText(target.getTarget().getUsername());
            Picasso.with(getActivity()).load(AvatarManager.getResourceId(getActivity(), target.getTarget().getAvatar())).into(avatar);
        } else if (target.getTarget().getType().equals(Consts.TARGET_TYPE_SHEEP)) {
            Picasso.with(getActivity()).load(R.drawable.sheep_large).into(avatar);
        } else {
            Picasso.with(getActivity()).load(R.drawable.tower).into(avatar);
        }
    }

    @OnClick(R.id.cancel_mission)
    public void cancelMission() {
        EventBus.getDefault().post(new FinishNearestMissionEvent());
    }
}
