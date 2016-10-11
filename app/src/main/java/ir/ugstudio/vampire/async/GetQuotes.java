package ir.ugstudio.vampire.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.managers.CacheManager;
import ir.ugstudio.vampire.managers.UserManager;
import ir.ugstudio.vampire.models.QuotesResponse;
import ir.ugstudio.vampire.utils.Consts;
import ir.ugstudio.vampire.utils.MemoryCache;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetQuotes extends AsyncTask<Void, Void, Void> {
    private Context context;

    public GetQuotes(Context context) {
        this.context = context;
        Log.d("TAG", "ddd GetQuotes");
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d("TAG", "ddd doInBackground");

        Call<QuotesResponse> call = VampireApp.createMapApi().getQuotes(UserManager.readToken(context));
        call.enqueue(new Callback<QuotesResponse>() {
            @Override
            public void onResponse(Call<QuotesResponse> call, Response<QuotesResponse> response) {
                if(response.isSuccessful()) {
                    CacheManager.setQuotes(response.body());
                    Log.d("TAG", "ddd isSuccessful");
                    for(String str : response.body().getQuotes()) {
                        Log.d("TAG", "ddd " + str);
                    }
                } else {
                    Log.d("TAG", "ddd not " + response.message());
                }
            }

            @Override
            public void onFailure(Call<QuotesResponse> call, Throwable t) {
                Log.d("TAG", "ddd fail " + t.getMessage());
            }
        });
        return null;
    }
}
