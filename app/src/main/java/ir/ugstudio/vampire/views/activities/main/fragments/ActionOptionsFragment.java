package ir.ugstudio.vampire.views.activities.main.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.views.BaseFragment;
import ir.ugstudio.vampire.views.activities.main.MainActivity;

public class ActionOptionsFragment extends BaseFragment {

    private Unbinder unbinder;

    public static ActionOptionsFragment getInstance() {
        return new ActionOptionsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_action_options, container, false);
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

    @OnClick(R.id.tower_actions)
    public void towerActions() {
        ((MainActivity) (getActivity())).openTowerOptionsFragment();
    }

    @OnClick(R.id.mission_actions)
    public void missionActions() {
        ((MainActivity) (getActivity())).openMissionOptionsFragment();
    }
}
