package ir.ugstudio.vampire.views.activities.main.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.managers.CacheHandler;
import ir.ugstudio.vampire.managers.UserHandler;
import ir.ugstudio.vampire.models.Ranklist;
import ir.ugstudio.vampire.models.User;
import ir.ugstudio.vampire.utils.Consts;
import ir.ugstudio.vampire.utils.introduction.HunterRanklist;
import ir.ugstudio.vampire.utils.introduction.VampireRanklist;
import ir.ugstudio.vampire.views.BaseFragment;
import ir.ugstudio.vampire.views.activities.main.MainActivity;
import ir.ugstudio.vampire.views.activities.main.adapters.RankViewAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RanklistFragment extends BaseFragment {

    private List<User> players = new ArrayList<>();
    private RecyclerView ranklist;
    private RankViewAdapter adapter;

    private ProgressBar progressbar;

    public static RanklistFragment getInstance() {
        return new RanklistFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ranklist, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        find(view);
        configure();
    }

    private void find(View view) {
        ranklist = (RecyclerView) view.findViewById(R.id.ranklist);
        progressbar = (ProgressBar) view.findViewById(R.id.progressbar);
    }

    private void configure() {
        ranklist.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new RankViewAdapter(getActivity(), players);
        ranklist.setAdapter(adapter);
    }

    private void getRanklist() {
        Call<Ranklist> call = VampireApp.createUserApi().getRanklist(UserHandler.readToken(getActivity()));
        call.enqueue(new Callback<Ranklist>() {
            @Override
            public void onResponse(Call<Ranklist> call, Response<Ranklist> response) {
                progressbar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    players = response.body().getTop();
                    players.addAll(response.body().getNear());
                    ((RankViewAdapter) ranklist.getAdapter()).update(players);

                    if (CacheHandler.getUser().getRole().equals(Consts.ROLE_HUNTER)) {
                        ((MainActivity) getActivity()).openHintFragment(new HunterRanklist());
                    } else {
                        ((MainActivity) getActivity()).openHintFragment(new VampireRanklist());
                    }
                }
            }

            @Override
            public void onFailure(Call<Ranklist> call, Throwable t) {
                progressbar.setVisibility(View.GONE);
                Log.d("TAG", "onFailure " + t.getMessage());
            }
        });
    }

    @Override
    public void onBringToFront() {
        super.onBringToFront();
        Log.d("TAG", "onBringToFront RanklistFragment");

        if (progressbar != null)
            progressbar.setVisibility(View.VISIBLE);

        players = new ArrayList<>();
        if (ranklist != null) {
            // why the ranklist maybe null? !!
            ((RankViewAdapter) ranklist.getAdapter()).update(players);
        }

        getRanklist();
    }
}
