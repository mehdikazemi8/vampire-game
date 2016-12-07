package ir.ugstudio.vampire.views.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.events.OpenTowerWallFragment;
import ir.ugstudio.vampire.managers.CacheManager;
import ir.ugstudio.vampire.models.Tower;
import ir.ugstudio.vampire.utils.Consts;
import ir.ugstudio.vampire.views.activities.main.adapters.OwnerViewAdapter;
import ir.ugstudio.vampire.views.activities.main.fragments.TowerWallFragment;
import ir.ugstudio.vampire.views.custom.CustomButton;
import ir.ugstudio.vampire.views.custom.CustomTextView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TowerDialog extends Dialog implements View.OnClickListener {

    private Tower tower;
    private CustomTextView title;
    private CustomTextView coinCount;
    private CustomButton joinTowerOwners;
    private CustomButton stealFromTower;
    private CustomButton showTowerWall;

    private RecyclerView ownersList;
    private OwnerViewAdapter adapter;

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

        Log.d("TAG", "metrics.widthPixels " + (metrics.widthPixels));
        Log.d("TAG", "metrics.widthPixels " + ((int) (metrics.widthPixels * 0.98)));

        getWindow().setLayout(Math.min(1000, (int) (metrics.widthPixels * 0.98)), (int) (metrics.heightPixels * 0.60));
        findControls();
        configure();
    }

    private void findControls() {
        joinTowerOwners = (CustomButton) findViewById(R.id.join_tower_button);
        stealFromTower = (CustomButton) findViewById(R.id.steal_from_tower_button);
        showTowerWall = (CustomButton) findViewById(R.id.show_tower_wall);
        coinCount = (CustomTextView) findViewById(R.id.coin_count);
        title = (CustomTextView) findViewById(R.id.title);
        ownersList = (RecyclerView) findViewById(R.id.owners);
    }

    private void configure() {
        joinTowerOwners.setOnClickListener(this);
        stealFromTower.setOnClickListener(this);
        showTowerWall.setOnClickListener(this);

        Log.d("TAG", "configurexxx " + tower.get_id());
        for (String towerID : CacheManager.getUser().getTowers()) {
            Log.d("TAG", "configurexxx " + towerID);
        }

        if (CacheManager.getUser().getRole().equals(tower.getRole())) {
            if (CacheManager.amIOwnerOfThisTower(tower.get_id())) {
                showTowerWall.setVisibility(View.VISIBLE);
            } else {
                joinTowerOwners.setVisibility(View.VISIBLE);
            }
        } else {
            stealFromTower.setVisibility(View.VISIBLE);
        }

        if (tower.getRole().equals(Consts.ROLE_HUNTER)) {
            title.setText(getContext().getString(R.string.caption_hunters_tower));
        } else {
            title.setText(getContext().getString(R.string.caption_vampires_tower));
        }
        coinCount.setText(String.valueOf(tower.getCoin()));

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        ownersList.setLayoutManager(layoutManager);
        adapter = new OwnerViewAdapter(tower.getOwners());
        ownersList.setAdapter(adapter);
    }

    private void joinTower() {
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
        Call<ResponseBody> call = VampireApp.createMapApi().stealFromTower(
                CacheManager.getUser().getToken(),
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.join_tower_button:
                joinTower();
                break;

            case R.id.steal_from_tower_button:
                stealTower();
                break;

            case R.id.show_tower_wall:
                EventBus.getDefault().post(new OpenTowerWallFragment(tower));
                dismiss();
                break;
        }
    }
}
