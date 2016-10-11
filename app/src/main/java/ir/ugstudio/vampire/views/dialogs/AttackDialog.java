package ir.ugstudio.vampire.views.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.managers.CacheManager;
import ir.ugstudio.vampire.managers.UserManager;
import ir.ugstudio.vampire.models.QuotesResponse;
import ir.ugstudio.vampire.utils.MemoryCache;
import ir.ugstudio.vampire.views.activities.main.MainActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttackDialog extends Dialog implements View.OnClickListener {

    private String usernameStr = null;
    private String messageStr = null;

    private TextView username;
    private Button attackButton;
    private Spinner quotesSpinner;

    public AttackDialog(Context context, String usernameStr) {
        super(context);
        this.usernameStr = usernameStr;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_attack);

        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        getWindow().setLayout((int) (metrics.widthPixels * 0.9), -2);
        findControls();
        configure();
    }

    private void findControls() {
        attackButton = (Button) findViewById(R.id.attack_button);
        quotesSpinner = (Spinner) findViewById(R.id.quotes);
        username = (TextView) findViewById(R.id.username);
    }

    private void configure() {
        attackButton.setOnClickListener(this);
        username.setText(usernameStr);

        // configure spinner
        final QuotesResponse quotes = CacheManager.getQuotes();
        // todo null pointer??
        messageStr = quotes.getQuotes().get(0);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, quotes.getQuotes());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quotesSpinner.setAdapter(adapter);
        quotesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                messageStr = quotes.getQuotes().get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void attack() {
        Location lastLocation = CacheManager.getLastLocation();
        Call<ResponseBody> call = VampireApp.createMapApi().attack(
                UserManager.readToken(getContext()),
                lastLocation.getLatitude(),
                lastLocation.getLongitude(),
                usernameStr,
                messageStr
        );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.attack_button:
                attack();
                break;
        }
    }
}
