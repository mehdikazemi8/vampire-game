package ir.ugstudio.vampire.views.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.managers.CacheManager;
import ir.ugstudio.vampire.models.Tower;
import ir.ugstudio.vampire.utils.Consts;
import ir.ugstudio.vampire.utils.FontHelper;

public class TowerDialog extends Dialog implements View.OnClickListener {

    private Tower tower;
    private TextView title;
    private TextView coinCount;
    private TextView coinIcon;
    private Button joinTowerOwners;
    private Button stealFromTower;

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
        getWindow().setLayout((int) (metrics.widthPixels * 0.9), -2);
        findControls();
        configure();
    }

    private void findControls() {
        joinTowerOwners = (Button) findViewById(R.id.join_tower_button);
        stealFromTower = (Button) findViewById(R.id.steal_from_tower_button);
        coinCount = (TextView) findViewById(R.id.coin_count);
        coinIcon = (TextView) findViewById(R.id.coin_icon);
        title = (TextView) findViewById(R.id.title);
    }

    private void configure() {
        joinTowerOwners.setOnClickListener(this);
        stealFromTower.setOnClickListener(this);

        if(CacheManager.getUser().getRole().equals(tower.getRole())) {
            stealFromTower.setVisibility(View.GONE);
        } else {
            joinTowerOwners.setVisibility(View.GONE);
        }

        if(tower.getRole().equals(Consts.ROLE_HUNTER)) {
            title.setText(getContext().getString(R.string.caption_hunters_tower));
        } else {
            title.setText(getContext().getString(R.string.caption_vampires_tower));
        }

        coinIcon.setTypeface( FontHelper.getIcons(getContext()) );
        coinCount.setText(String.valueOf(tower.getCoin()));
    }

    private void joinTower() {
        Log.d("TAG", "TowerDialog joinTower");
    }

    private void stealTower() {
        Log.d("TAG", "TowerDialog stealTower");
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
        }
    }
}
