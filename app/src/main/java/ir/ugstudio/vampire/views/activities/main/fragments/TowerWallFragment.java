package ir.ugstudio.vampire.views.activities.main.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.events.FCMNewMessage;
import ir.ugstudio.vampire.managers.AnalyticsManager;
import ir.ugstudio.vampire.managers.CacheHandler;
import ir.ugstudio.vampire.managers.UserHandler;
import ir.ugstudio.vampire.models.Tower;
import ir.ugstudio.vampire.models.TowerMessage;
import ir.ugstudio.vampire.utils.Utility;
import ir.ugstudio.vampire.utils.introduction.TowerWall;
import ir.ugstudio.vampire.views.BaseFragment;
import ir.ugstudio.vampire.views.activities.main.MainActivity;
import ir.ugstudio.vampire.views.activities.main.adapters.MessageViewAdapter;
import ir.ugstudio.vampire.views.custom.CustomButton;
import ir.ugstudio.vampire.views.custom.CustomEditText;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TowerWallFragment extends BaseFragment implements View.OnClickListener {

    private Tower tower = null;

    private CustomEditText message;
    private LinearLayout sendMessageSection;
    private RecyclerView messagesList;
    private CustomButton submitMessage;

    private MessageViewAdapter messageAdapter;

    public static TowerWallFragment getInstance() {
        return new TowerWallFragment();
    }

    private void readArguments() {
        tower = (Tower) getArguments().getSerializable("tower");
        Log.d("TAG", "readArguments " + tower.get_id());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        readArguments();
        return inflater.inflate(R.layout.fragment_tower_wall, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        find(view);
        configure();
        getTower(tower.get_id());

        AnalyticsManager.logEvent(AnalyticsManager.VIEW_SCREEN, "TowerWallFragment");

        ((MainActivity) getActivity()).openHintFragment(new TowerWall(), true);
    }

    private void find(View view) {
        submitMessage = (CustomButton) view.findViewById(R.id.submit_message);
        message = (CustomEditText) view.findViewById(R.id.message);
        sendMessageSection = (LinearLayout) view.findViewById(R.id.send_message_section);
        messagesList = (RecyclerView) view.findViewById(R.id.messages);
    }

    private void configure() {
        messageAdapter = new MessageViewAdapter(tower.getWall());
        messagesList.setLayoutManager(new LinearLayoutManager(getContext()));
        messagesList.setAdapter(messageAdapter);

        showLastItem();

        submitMessage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit_message:
                submitMessage();
                break;
        }
    }

    private boolean hasEnteredMessage() {
        if (message.getText().toString().trim().length() == 0) {
            Utility.makeToast(getActivity(), getString(R.string.toast_enter_message), Toast.LENGTH_SHORT);
            return false;
        }
        return true;
    }

    private void submitMessage() {
        if (!hasEnteredMessage()) {
            return;
        }

        final String messageStr = message.getText().toString().trim();

        Call<ResponseBody> call = VampireApp.createMapApi().sendMessageToTower(
                UserHandler.readToken(getContext()),
                tower.get_id(),
                messageStr
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    message.setText("");
                    updateWallMessages(messageStr);
                } else {
                    Utility.makeToast(getActivity(), getString(R.string.toast_please_try_again_later), Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Utility.makeToast(getActivity(), getString(R.string.toast_please_try_again_later), Toast.LENGTH_SHORT);
            }
        });
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
    public void onEvent(FCMNewMessage event) {
        Log.d("TAG", "onMessageReceived " + event.getTowerId());
        getTower(event.getTowerId());
    }

    private void updateWallMessages(String messageStr) {
        tower.getWall().add(new TowerMessage(CacheHandler.getUser().getUsername(), messageStr, CacheHandler.getUser().getAvatar()));
        messageAdapter.notifyDataSetChanged();
        showLastItem();
    }

    private void showLastItem() {
        int lastPos = messageAdapter.getItemCount() - 1;
        messagesList.scrollToPosition(lastPos);
    }

    private void updateWallFromServer(Tower newTower) {
        for (TowerMessage newMessage : newTower.getWall()) {
            if (!tower.getWall().contains(newMessage)) {
                tower.getWall().add(newMessage);
            }
        }
        messageAdapter.notifyDataSetChanged();
        showLastItem();
    }

    private void getTower(String towerId) {
        Call<Tower> call = VampireApp.createMapApi().getTower(
                UserHandler.readToken(getActivity()),
                towerId
        );
        call.enqueue(new Callback<Tower>() {
            @Override
            public void onResponse(Call<Tower> call, Response<Tower> response) {
                if (response.isSuccessful()) {
                    updateWallFromServer(response.body());
//                    updateWallMessages(response.body().getWall().get(response.body().getWall().size() - 1).getMessage());
                }
            }

            @Override
            public void onFailure(Call<Tower> call, Throwable t) {

            }
        });
    }
}
