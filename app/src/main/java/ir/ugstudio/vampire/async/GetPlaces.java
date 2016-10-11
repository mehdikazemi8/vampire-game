package ir.ugstudio.vampire.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.managers.UserManager;
import ir.ugstudio.vampire.models.Place;
import ir.ugstudio.vampire.models.PlacesResponse;
import ir.ugstudio.vampire.models.QuotesResponse;
import ir.ugstudio.vampire.utils.Consts;
import ir.ugstudio.vampire.utils.MemoryCache;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetPlaces extends AsyncTask<Void, Void, Void> {
    private Context context;

    public GetPlaces(Context context) {
        this.context = context;
        Log.d("TAG", "ddd GetPlaces");
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d("TAG", "ddd doInBackground");

        Call<PlacesResponse> call = VampireApp.createMapApi().getPlaces(UserManager.readToken(context), Consts.PLACE_HOSPITAL);
        call.enqueue(new Callback<PlacesResponse>() {
            @Override
            public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {
                if(response.isSuccessful()) {

                    Log.d("TAG", "ddd isSuccessful");
                    for(Place place : response.body().getPlaces()) {
                        Log.d("TAG", "ddd " + place.getGeo().get(0));
                    }
                } else {
                    Log.d("TAG", "ddd not " + response.message());
                }
            }

            @Override
            public void onFailure(Call<PlacesResponse> call, Throwable t) {
                Log.d("TAG", "ddd fail " + t.getMessage());
            }
        });
        return null;
    }
}
