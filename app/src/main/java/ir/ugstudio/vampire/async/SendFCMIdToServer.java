package ir.ugstudio.vampire.async;

import android.util.Log;

import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.managers.CacheManager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendFCMIdToServer {
    public static void run(String FCMId) {
        if(FCMId == null || CacheManager.getUser() == null) {
            return;
        }

        Call<ResponseBody> call = VampireApp.createUserApi().sendFCMIdToServer(
                CacheManager.getUser().getToken(),
                FCMId
        );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("TAG", "sendServer " + response.message());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
