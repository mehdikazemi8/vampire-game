package ir.ugstudio.vampire.views.activities.main.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.managers.UserManager;
import ir.ugstudio.vampire.models.Ranklist;
import ir.ugstudio.vampire.models.User;
import ir.ugstudio.vampire.views.activities.main.adapters.RankViewAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RanklistFragment extends Fragment {
    private RecyclerView ranklist;
    private RankViewAdapter adapter;

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
        getRanklist();
    }

    private void find(View view) {
        ranklist = (RecyclerView) view.findViewById(R.id.ranklist);
    }

    private void configure() {
        ranklist.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void getRanklist() {
        Call<Ranklist> call = VampireApp.createUserApi().getRanklist(UserManager.readToken(getActivity()));
        call.enqueue(new Callback<Ranklist>() {
            @Override
            public void onResponse(Call<Ranklist> call, Response<Ranklist> response) {
                if (response.isSuccessful()) {
                    for(User user : response.body().getTop()) {
                        Log.d("TAG", "rank " + user.getUsername());
                    }
                    response.body().getTop().addAll(response.body().getNear());
                    adapter = new RankViewAdapter(getContext(), response.body().getTop());
                    ranklist.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<Ranklist> call, Throwable t) {
                Log.d("TAG", "onFailure " + t.getMessage());
            }
        });
    }
}
