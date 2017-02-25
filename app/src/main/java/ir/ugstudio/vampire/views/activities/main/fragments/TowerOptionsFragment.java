package ir.ugstudio.vampire.views.activities.main.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.events.TowerAddEvent;
import ir.ugstudio.vampire.events.TowerCollectCoinsEvent;
import ir.ugstudio.vampire.events.TowerWatchEvent;
import ir.ugstudio.vampire.managers.CacheHandler;
import ir.ugstudio.vampire.utils.Consts;
import ir.ugstudio.vampire.utils.introduction.MissionHunter;
import ir.ugstudio.vampire.utils.introduction.MissionVampire;
import ir.ugstudio.vampire.utils.introduction.TowerHunter;
import ir.ugstudio.vampire.utils.introduction.TowerVampire;
import ir.ugstudio.vampire.views.BaseFragment;
import ir.ugstudio.vampire.views.activities.main.MainActivity;
import ir.ugstudio.vampire.views.activities.main.adapters.TowerInfoAdapter;

public class TowerOptionsFragment extends BaseFragment {

    @BindView(R.id.user_towers)
    RecyclerView userTowers;

    private TowerInfoAdapter adapter;
    private Unbinder unbinder;

    public static TowerOptionsFragment getInstance() {
        return new TowerOptionsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tower_options, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GridLayoutManager layoutManager
                = new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false);
        userTowers.setLayoutManager(layoutManager);
        adapter = new TowerInfoAdapter(CacheHandler.getUser().getTowersList());
        userTowers.setAdapter(adapter);

        if (CacheHandler.getUser().getRole().equals(Consts.ROLE_HUNTER)) {
            ((MainActivity) getActivity()).openHintFragment(new TowerHunter());
        } else {
            ((MainActivity) getActivity()).openHintFragment(new TowerVampire());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.add_tower)
    public void addTower() {
        getFragmentManager().popBackStack();
        getFragmentManager().popBackStack();
        EventBus.getDefault().post(new TowerAddEvent());
    }

    @OnClick(R.id.watch_my_towers)
    public void watchMyTowers() {
        getFragmentManager().popBackStack();
        getFragmentManager().popBackStack();
        EventBus.getDefault().post(new TowerWatchEvent());
    }

    @OnClick(R.id.collect_coin_from_my_towers)
    public void collectCoins() {
        getFragmentManager().popBackStack();
        getFragmentManager().popBackStack();
        EventBus.getDefault().post(new TowerCollectCoinsEvent());
    }
}
