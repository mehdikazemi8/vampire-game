package ir.ugstudio.vampire.views.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.Random;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.async.GetProfile;
import ir.ugstudio.vampire.events.RefreshAroundTowerEvent;
import ir.ugstudio.vampire.managers.AnalyticsManager;
import ir.ugstudio.vampire.managers.AvatarManager;
import ir.ugstudio.vampire.managers.CacheHandler;
import ir.ugstudio.vampire.managers.UserHandler;
import ir.ugstudio.vampire.models.QuotesResponse;
import ir.ugstudio.vampire.models.Tower;
import ir.ugstudio.vampire.models.User;
import ir.ugstudio.vampire.utils.Consts;
import ir.ugstudio.vampire.utils.FontHelper;
import ir.ugstudio.vampire.utils.Utility;
import ir.ugstudio.vampire.views.custom.CustomTextView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttackDialog extends Dialog implements View.OnClickListener {

    private final int NUMBER_OF_RADIO = 3;
    private User user = null;
    private String messageStr = null;
    private RadioButton[] quotesRadio = new RadioButton[NUMBER_OF_RADIO];

    private CustomTextView role;
    private CustomTextView username;
    private CustomTextView coin;
    private CustomTextView score;
    private RadioGroup radioGroup;

    private FrameLayout attackButton;
    private boolean doIAttackFromTower = false;
    private Tower tower;

    private ImageView avatar;
    private boolean isSheep = false;

    public AttackDialog(Context context, User user) {
        super(context);
        this.user = user;
        isSheep = user.getRole().equals("sheep");
    }

    public AttackDialog(Context context, User user, Tower tower) {
        super(context);
        this.user = user;
        this.tower = tower;
        this.doIAttackFromTower = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_attack);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();

        if (isSheep) {
            getWindow().setLayout(Math.min(1000, (int) (metrics.widthPixels * 0.98)), (int) (metrics.heightPixels * 0.90));
        } else {
            getWindow().setLayout(Math.min(1000, (int) (metrics.widthPixels * 0.98)), (int) (metrics.heightPixels * 0.90));
        }

        findControls();
        configure();

        AnalyticsManager.logEvent(AnalyticsManager.ATTACK_DIALOG, user.getRole());
    }

    private void findControls() {
        attackButton = (FrameLayout) findViewById(R.id.attack_button);

        username = (CustomTextView) findViewById(R.id.username);
        coin = (CustomTextView) findViewById(R.id.coin);
        score = (CustomTextView) findViewById(R.id.score);
        role = (CustomTextView) findViewById(R.id.role);

        avatar = (ImageView) findViewById(R.id.avatar);

        quotesRadio[0] = (RadioButton) findViewById(R.id.quote_0);
        quotesRadio[1] = (RadioButton) findViewById(R.id.quote_1);
        quotesRadio[2] = (RadioButton) findViewById(R.id.quote_2);

        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
    }

    private void configure() {
        FontHelper.setKoodakFor(getContext(), quotesRadio[0], quotesRadio[1], quotesRadio[2]);

        if (isSheep) {
            Picasso.with(getContext()).load(R.drawable.sheep_large).into(avatar);
            radioGroup.setVisibility(View.GONE);
        } else {
            Picasso.with(getContext()).load(AvatarManager.getResourceId(getContext(), user.getAvatar())).into(avatar);
        }

        attackButton.setOnClickListener(this);

        username.setText(user.getUsername());
        coin.setText(String.valueOf(user.getCoin()));
        score.setText(String.valueOf(user.getScore()));

        if (user.getRole().equals("hunter")) {
            role.setText("شکارچی");
        } else if (user.getRole().equals("vampire")) {
            role.setText("خون‌آشام");
        } else {
//            role.setText("");
            role.setVisibility(View.GONE);
        }

        messageStr = null;
        final QuotesResponse quotes = CacheHandler.getQuotes();
        Collections.shuffle(quotes.getQuotes(), new Random(System.nanoTime()));
        for (int i = 0; i < NUMBER_OF_RADIO; i++) {
            final int idx = i;
            quotesRadio[i].setText(quotes.getQuotes().get(i));

            quotesRadio[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    messageStr = quotes.getQuotes().get(idx);
                }
            });
        }

        if (isSheep) {
            for (RadioButton radioButton : quotesRadio) {
                radioButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void attack() {
        if (messageStr == null && !isSheep) {
            Utility.makeToast(getContext(), getContext().getString(R.string.choose_attack_message), Toast.LENGTH_LONG);
            return;
        }

        Location lastLocation = CacheHandler.getLastLocation();
        Call<ResponseBody> call = VampireApp.createMapApi().attack(
                UserHandler.readToken(getContext()),
                lastLocation.getLatitude(),
                lastLocation.getLongitude(),
                user.getUsername(),
                messageStr
        );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    String result = Utility.extractResult(response.body());

                    switch (result) {
                        case Consts.RESULT_OK:
                            GetProfile.run(getContext());
                            Utility.makeToast(getContext(), getContext().getString(R.string.toast_attack_ok), Toast.LENGTH_LONG);
                            break;

                        case Consts.RESULT_NOT_ALIVE:
                            Utility.makeToast(getContext(), getContext().getString(R.string.toast_attack_not_alive), Toast.LENGTH_LONG);
                            break;

                        case Consts.RESULT_NOT_IN_RANGE:
                            Utility.makeToast(getContext(), getContext().getString(R.string.toast_attack_not_in_range), Toast.LENGTH_LONG);
                            break;

                        case Consts.RESULT_SAME_ROLE:
                            Utility.makeToast(getContext(), getContext().getString(R.string.toast_attack_same_role), Toast.LENGTH_LONG);
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

        dismiss();
    }

    private void attackFromTower() {
        Call<ResponseBody> call = VampireApp.createMapApi().attackFromTower(
                UserHandler.readToken(getContext()),
                tower.get_id(),
                user.getUsername()
        );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String result = Utility.extractResult(response.body());

                    switch (result) {
                        case Consts.RESULT_OK:
                            Utility.makeToast(getContext(), getContext().getString(R.string.toast_attack_ok), Toast.LENGTH_LONG);
                            GetProfile.run(getContext());
                            EventBus.getDefault().post(new RefreshAroundTowerEvent());
                            break;

                        case Consts.RESULT_NOT_ALIVE:
                            Utility.makeToast(getContext(), getContext().getString(R.string.toast_attack_not_alive), Toast.LENGTH_LONG);
                            break;

                        case Consts.RESULT_NOT_IN_RANGE:
                            Utility.makeToast(getContext(), getContext().getString(R.string.toast_attack_not_in_range), Toast.LENGTH_LONG);
                            break;

                        case Consts.RESULT_SAME_ROLE:
                            Utility.makeToast(getContext(), getContext().getString(R.string.toast_attack_same_role), Toast.LENGTH_LONG);
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

        dismiss();
    }

    private void handleAttack() {
        if (doIAttackFromTower) {
            attackFromTower();
        } else {
            attack();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.attack_button:
                handleAttack();
                break;
        }
    }
}
