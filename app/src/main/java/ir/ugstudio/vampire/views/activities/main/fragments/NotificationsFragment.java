package ir.ugstudio.vampire.views.activities.main.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.managers.UserHandler;
import ir.ugstudio.vampire.models.Notification;
import ir.ugstudio.vampire.models.NotificationList;
import ir.ugstudio.vampire.views.BaseFragment;
import ir.ugstudio.vampire.views.activities.main.adapters.NotificationViewAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends BaseFragment {

    private NotificationViewAdapter adapter;
    private List<Notification> victim = new ArrayList<>();
    private RecyclerView notifications;

    public static NotificationsFragment getInstance() {
        return new NotificationsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        find(view);
        configure();
    }

    private void find(View view) {
        notifications = (RecyclerView) view.findViewById(R.id.notifications);
    }

    private void configure() {
        notifications.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new NotificationViewAdapter(victim);
        notifications.setAdapter(adapter);
    }

    @Override
    public void onBringToFront() {
        super.onBringToFront();
        Log.d("TAG", "onBringToFront NotificationsFragment");
        callForNotifications();
    }

    private void callForNotifications() {
        String token = UserHandler.readToken(getActivity());
        Call<NotificationList> call = VampireApp.createUserApi().getNotification(token);
        call.enqueue(new Callback<NotificationList>() {
            @Override
            public void onResponse(Call<NotificationList> call, Response<NotificationList> response) {
                if (response.isSuccessful()) {
                    Log.d("TAG", "onResponse " + response.body().getVictim().size());
                    victim = response.body().getVictim();
                    ((NotificationViewAdapter) notifications.getAdapter()).update(victim);
                }
            }

            @Override
            public void onFailure(Call<NotificationList> call, Throwable t) {
                Log.d("TAG", "onResponse " + t.getMessage());
            }
        });
    }
}
