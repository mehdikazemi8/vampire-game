package ir.ugstudio.vampire.views.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.async.GetProfile;
import ir.ugstudio.vampire.events.OpenTowerWallFragment;
import ir.ugstudio.vampire.events.ShowTabEvent;
import ir.ugstudio.vampire.managers.CacheHandler;
import ir.ugstudio.vampire.models.Tower;
import ir.ugstudio.vampire.utils.Consts;
import ir.ugstudio.vampire.utils.FontHelper;
import ir.ugstudio.vampire.utils.Utility;
import ir.ugstudio.vampire.views.activities.main.adapters.OwnerViewAdapter;
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
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

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

        if (CacheHandler.getUser().getRole().equals(tower.getRole())) {
            if (CacheHandler.amIOwnerOfThisTower(tower.get_id())) {
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
                CacheHandler.getUser().getToken(),
                CacheHandler.getUser().getGeo().get(0),
                CacheHandler.getUser().getGeo().get(1),
                tower.get_id()
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String result = Utility.extractResult(response.body());

                    switch (result) {
                        case Consts.RESULT_OK:
                            Utility.makeToast(getContext(), getContext().getString(R.string.toast_join_tower_ok), Toast.LENGTH_LONG);
                            GetProfile.run(getContext());
                            tower.getOwners().add(CacheHandler.getUser());
                            adapter.notifyDataSetChanged();
                            joinTowerOwners.setVisibility(View.GONE);
                            showTowerWall.setVisibility(View.VISIBLE);
                            break;

                        case Consts.RESULT_NOT_MY_ROLE:
                            Utility.makeToast(getContext(), getContext().getString(R.string.toast_join_tower_not_my_role), Toast.LENGTH_LONG);
                            break;

                        case Consts.RESULT_NOT_IN_RANGE:
                            Utility.makeToast(getContext(), getContext().getString(R.string.toast_join_tower_not_in_range), Toast.LENGTH_LONG);
                            break;

                        case Consts.RESULT_NOT_ENOUGH_MONEY:
                            redirectToStore();
                            break;
                    }
                } else {
                    Utility.makeToast(getContext(), getContext().getString(R.string.toast_please_try_again_later), Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Utility.makeToast(getContext(), getContext().getString(R.string.toast_please_try_again_later), Toast.LENGTH_SHORT);
            }
        });
    }

    private void redirectToStore() {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setMessage("برای عضو شدن توی یه برج نیازمند ۳۰۰۰ سکه هستی\nمتاسفانه سکه\u200Cي کافی برای این کار نداری، دوس داری سکه خریداری کنی؟")
                .setPositiveButton("بله", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EventBus.getDefault().post(new ShowTabEvent(1));
                        dismiss();
                    }
                })
                .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
        FontHelper.setKoodakFor(getContext(), (TextView) dialog.findViewById(android.R.id.message));
    }

    private void stealTower() {
        Call<ResponseBody> call = VampireApp.createMapApi().stealFromTower(
                CacheHandler.getUser().getToken(),
                tower.get_id()
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String result = Utility.extractResult(response.body());

                    switch (result) {
                        case Consts.RESULT_OK:
                            Utility.makeToast(getContext(), getContext().getString(R.string.toast_steal_tower_ok), Toast.LENGTH_LONG);
                            GetProfile.run(getContext());
                            coinCount.setText("0");
                            break;

                        case Consts.RESULT_NOT_IN_RANGE:
                            Utility.makeToast(getContext(), getContext().getString(R.string.toast_steal_tower_not_in_range), Toast.LENGTH_LONG);
                            break;

                        case Consts.RESULT_NO_COIN:
                            Utility.makeToast(getContext(), getContext().getString(R.string.toast_steal_tower_no_coin), Toast.LENGTH_LONG);
                            break;

                        case Consts.RESULT_SAME_ROLE:
                            break;
                    }
                } else {
                    Utility.makeToast(getContext(), getContext().getString(R.string.toast_please_try_again_later), Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Utility.makeToast(getContext(), getContext().getString(R.string.toast_please_try_again_later), Toast.LENGTH_SHORT);
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
