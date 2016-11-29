package ir.ugstudio.vampire.async;

import android.content.Context;
import android.util.Log;

import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.managers.SharedPrefManager;
import ir.ugstudio.vampire.managers.UserManager;
import ir.ugstudio.vampire.models.StoreItems;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetStoreItems {
    public static void run(final Context context) {
        Log.d("TAG", "HeRE GetStoreItems");
        String token = UserManager.readToken(context);

        Call<StoreItems> call = VampireApp.createUserApi().getStoreItems(token);
        call.enqueue(new Callback<StoreItems>() {
            @Override
            public void onResponse(Call<StoreItems> call, Response<StoreItems> response) {
                Log.d("TAG", "GetStoreItems " + response.message());
                if (response.isSuccessful()) {
                    SharedPrefManager.writeStoreItems(context, response.body());
                }
            }

            @Override
            public void onFailure(Call<StoreItems> call, Throwable t) {
                Log.d("TAG", "GetStoreItemsss " + t.getMessage());
            }
        });
    }
}
