package ir.ugstudio.vampire.views.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.Collections;
import java.util.Random;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.async.GetProfile;
import ir.ugstudio.vampire.managers.AvatarManager;
import ir.ugstudio.vampire.managers.CacheManager;
import ir.ugstudio.vampire.managers.UserManager;
import ir.ugstudio.vampire.models.QuotesResponse;
import ir.ugstudio.vampire.models.Tower;
import ir.ugstudio.vampire.models.User;
import ir.ugstudio.vampire.utils.FontHelper;
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

    private Button attackButton;
    private boolean doIattackFromTower = false;
    private Tower tower;

    private ImageView avatar;

    public AttackDialog(Context context, User user) {
        super(context);
        this.user = user;
    }

    public AttackDialog(Context context, User user, Tower tower) {
        super(context);
        this.user = user;
        this.tower = tower;
        this.doIattackFromTower = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_attack);

        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        getWindow().setLayout((int) (metrics.widthPixels * 0.98), (int) (metrics.heightPixels * 0.95));
        findControls();
        configure();
    }

    private void findControls() {
        attackButton = (Button) findViewById(R.id.attack_button);

        username = (CustomTextView) findViewById(R.id.username);
        coin = (CustomTextView) findViewById(R.id.coin);
        score = (CustomTextView) findViewById(R.id.score);
        role = (CustomTextView) findViewById(R.id.role);

        avatar = (ImageView) findViewById(R.id.avatar);

        quotesRadio[0] = (RadioButton) findViewById(R.id.quote_0);
        quotesRadio[1] = (RadioButton) findViewById(R.id.quote_1);
        quotesRadio[2] = (RadioButton) findViewById(R.id.quote_2);
    }

    private void configure() {
        FontHelper.setKoodakFor(getContext(), quotesRadio[0], quotesRadio[1], quotesRadio[2], attackButton);

        avatar.setBackgroundResource(AvatarManager.getResourceId(getContext(), user.getAvatar()));
        attackButton.setOnClickListener(this);

        username.setText(user.getUsername());
        coin.setText(String.valueOf(user.getCoin()));
        score.setText(String.valueOf(user.getScore()));
        role.setText(user.getRole().equals("hunter") ? "شکارچی" : "خون‌آشام");

        messageStr = null;
        final QuotesResponse quotes = CacheManager.getQuotes();
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
    }

    private void attack() {
        if (messageStr == null) {
            Toast.makeText(getContext(), getContext().getString(R.string.choose_attack_message), Toast.LENGTH_SHORT).show();
            return;
        }
        Location lastLocation = CacheManager.getLastLocation();
        Call<ResponseBody> call = VampireApp.createMapApi().attack(
                UserManager.readToken(getContext()),
                lastLocation.getLatitude(),
                lastLocation.getLongitude(),
                user.getUsername(),
                messageStr
        );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("TAG", "attack " + response.message());
                if (response.isSuccessful()) {
                    GetProfile.run(getContext());

                    Log.d("TAG", "xxx " + response.message());
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
                Log.d("TAG", "xxx " + t.getMessage());
            }
        });

        dismiss();
    }

    private void attackFromTower() {
        Call<ResponseBody> call = VampireApp.createMapApi().attackFromTower(
                UserManager.readToken(getContext()),
                tower.get_id(),
                user.getUsername()
        );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("TAG", "attack " + response.message());
                if (response.isSuccessful()) {
//                     TODO, do I have to update user
                    GetProfile.run(getContext());

                    Log.d("TAG", "xxx " + response.message());
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
                Log.d("TAG", "xxx " + t.getMessage());
            }
        });

        dismiss();
    }

    private void handleAttack() {
        if (doIattackFromTower) {
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
