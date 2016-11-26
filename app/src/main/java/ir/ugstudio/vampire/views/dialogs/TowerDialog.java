package ir.ugstudio.vampire.views.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.async.GetProfile;
import ir.ugstudio.vampire.managers.CacheManager;
import ir.ugstudio.vampire.managers.UserManager;
import ir.ugstudio.vampire.models.Tower;
import ir.ugstudio.vampire.models.TowerMessage;
import ir.ugstudio.vampire.utils.Consts;
import ir.ugstudio.vampire.utils.FontHelper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TowerDialog extends Dialog implements View.OnClickListener {

    private Tower tower;
    private TextView title;
    private TextView coinCount;
    private TextView coinIcon;
    private Button joinTowerOwners;
    private Button stealFromTower;
    private Button submitMessage;
    private EditText message;

    public TowerDialog(Context context, Tower tower) {
        super(context);
        this.tower = tower;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_tower);

        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        getWindow().setLayout((int) (metrics.widthPixels * 0.9), (int) (metrics.heightPixels * 0.9));
        findControls();
        configure();
    }

    private void findControls() {
        joinTowerOwners = (Button) findViewById(R.id.join_tower_button);
        stealFromTower = (Button) findViewById(R.id.steal_from_tower_button);
        coinCount = (TextView) findViewById(R.id.coin_count);
        coinIcon = (TextView) findViewById(R.id.coin_icon);
        title = (TextView) findViewById(R.id.title);
        message = (EditText) findViewById(R.id.message);
        submitMessage = (Button) findViewById(R.id.submit_message);
    }

    private void configure() {
        joinTowerOwners.setOnClickListener(this);
        stealFromTower.setOnClickListener(this);
        submitMessage.setOnClickListener(this);

        if (CacheManager.getUser().getRole().equals(tower.getRole())) {
            stealFromTower.setVisibility(View.GONE);
            /**
             * We are the same group
             * two situations:
             *  1. Am I an owner
             *  2. I'm not an owner
             */

            Log.d("TAG", "owner?? " + tower.get_id() + " " + CacheManager.amIOwnerOfThisTower(tower.get_id()));
            if (CacheManager.amIOwnerOfThisTower(tower.get_id())) {
                joinTowerOwners.setVisibility(View.GONE);
            } else {

            }
        } else {
            joinTowerOwners.setVisibility(View.GONE);
        }

        if (tower.getRole().equals(Consts.ROLE_HUNTER)) {
            title.setText(getContext().getString(R.string.caption_hunters_tower));
        } else {
            title.setText(getContext().getString(R.string.caption_vampires_tower));
        }

        for (TowerMessage message : tower.getWall()) {
            Log.d("TAG", "message " + message.getMessage());
        }

        coinIcon.setTypeface(FontHelper.getIcons(getContext()));
        coinCount.setText(String.valueOf(tower.getCoin()));
    }

    private void joinTower() {
        Log.d("TAG", "TowerDialog joinTower");

        Log.d("TAG", "joinTower " + CacheManager.getUser().getGeo().get(0));
        Log.d("TAG", "joinTower " + CacheManager.getUser().getGeo().get(1));
        Log.d("TAG", "joinTower " + tower.get_id());

        Call<ResponseBody> call = VampireApp.createMapApi().joinTower(
                CacheManager.getUser().getToken(),
                CacheManager.getUser().getGeo().get(0),
                CacheManager.getUser().getGeo().get(1),
                tower.get_id()
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String result = "IOEXception";
                    try {
                        result = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "NOT SUCCESSFUL", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void stealTower() {
        Log.d("TAG", "TowerDialog stealTower");

        // TODO, replace towerId by real id, and place with real lat lng
        Call<ResponseBody> call = VampireApp.createMapApi().stealFromTower(
                CacheManager.getUser().getToken(),
                "580c91d94aa20a0ba14482be"
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String result = "IOEXception";
                    try {
                        result = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "NOT SUCCESSFUL", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.join_tower_button:
                joinTower();
                break;

            case R.id.steal_from_tower_button:
                stealTower();
                break;

            case R.id.submit_message:
                submitMessage();
                break;
        }
    }

    private boolean hasEnteredMessage() {
        if (message.getText().toString().trim().length() == 0) {
            Toast.makeText(getContext(), getContext().getString(R.string.toast_enter_message), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void submitMessage() {
        if (!hasEnteredMessage()) {
            return;
        }

        Call<ResponseBody> call = VampireApp.createMapApi().sendMessageToTower(
                UserManager.readToken(getContext()),
                tower.get_id(),
                message.getText().toString().trim()
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
