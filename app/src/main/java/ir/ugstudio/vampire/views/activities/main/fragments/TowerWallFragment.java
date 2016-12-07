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

import java.io.IOException;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.managers.CacheManager;
import ir.ugstudio.vampire.managers.UserManager;
import ir.ugstudio.vampire.models.Tower;
import ir.ugstudio.vampire.models.TowerMessage;
import ir.ugstudio.vampire.views.BaseFragment;
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
            Toast.makeText(getContext(), getContext().getString(R.string.toast_enter_message), Toast.LENGTH_SHORT).show();
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
                UserManager.readToken(getContext()),
                tower.get_id(),
                messageStr
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String result = "IOEXception";
                    try {
                        result = response.body().string();
                        Log.d("TAG", "xxx " + result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                    message.setText("");
                    tower.getWall().add(new TowerMessage(CacheManager.getUser().getUsername(), messageStr, CacheManager.getUser().getAvatar()));
                    messageAdapter.notifyDataSetChanged();
                    int lastPos = messageAdapter.getItemCount() - 1;
                    messagesList.scrollToPosition(lastPos);
                } else {
                    Toast.makeText(getContext(), "NOT SUCCESSFUL", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("TAG", "yyy " + t.getMessage());
            }
        });
    }
}
