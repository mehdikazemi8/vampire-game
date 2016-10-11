package ir.ugstudio.vampire.views.activities.main;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.managers.CacheManager;
import ir.ugstudio.vampire.managers.UserManager;
import ir.ugstudio.vampire.models.MapResponse;
import ir.ugstudio.vampire.models.User;
import ir.ugstudio.vampire.views.dialogs.AttackDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.graphics.Color.RED;

public class MainActivity
        extends FragmentActivity
        implements
            OnMapReadyCallback,
            GoogleApiClient.ConnectionCallbacks,
            LocationListener,
            android.location.LocationListener,
            GoogleMap.OnMarkerClickListener {

    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient = null;
    private final int MIN_ZOOM = 16;
    private final int MAX_ZOOM = 17;
    private final int RADIUS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void requestForMap(final double lat, final double lng) {
        Call<MapResponse> call = VampireApp.createMapApi().getMap(
                UserManager.readToken(MainActivity.this),
                lat,
                lng
        );

        googleMap.clear();
        LatLng newPlace = new LatLng(lat, lng);
        googleMap.addMarker(new MarkerOptions().position(newPlace));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPlace, MAX_ZOOM));

        // todo: delete this
        googleMap.addMarker(new MarkerOptions().position(new LatLng(35.640225, 52.246952)).title("یه جا دیگه"));

        googleMap.addCircle(new CircleOptions()
                .center(newPlace)
                .radius(RADIUS)
                .strokeColor(RED)
        );

        call.enqueue(new Callback<MapResponse>() {
            @Override
            public void onResponse(Call<MapResponse> call, Response<MapResponse> response) {

                if(response.isSuccessful()) {

                    Log.d("TAG", "hhh " + response.body().serialize());

                    for(User user : response.body().getOpponents()) {
                        Log.d("TAG", "aaa " + user.getUsername() + " " + user.getGeo().get(0) + ", " + user.getGeo().get(1));
                        googleMap.addMarker(new MarkerOptions().position(
                                new LatLng(user.getGeo().get(0), user.getGeo().get(1))
                        ).title(user.getUsername()));
                    }
                } else {
                    Log.d("TAG", "aaa notSuccess " + response.message());
                }
            }

            @Override
            public void onFailure(Call<MapResponse> call, Throwable t) {
                Log.d("TAG", "aaa onFailure " + t.getMessage());

            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("TAG", "onConnected");

        if(ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationRequest mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(4000);

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("TAG", "changed " + location.getLatitude() + " " + location.getLongitude());

        CacheManager.setLastLocation(location);

//        googleMap.clear();
        requestForMap(location.getLatitude(), location.getLongitude());

//        LatLng newPlace = new LatLng(location.getLatitude(), location.getLongitude());
//        googleMap.addMarker(new MarkerOptions().position(newPlace));
//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPlace, MAX_ZOOM));

//        googleMap.addCircle(new CircleOptions()
//                .center(newPlace)
//                .radius(RADIUS)
//                .strokeColor(RED)
//        );
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap myMap) {
        Log.d("TAG", "onMapReady");

        googleMap = myMap;
        googleMap.setOnMarkerClickListener(this);

//        googleMap.getUiSettings().setZoomGesturesEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        googleMap.setMaxZoomPreference(MAX_ZOOM);
        googleMap.setMinZoomPreference(MIN_ZOOM);

        LatLng myPlace = new LatLng(35.702945, 51.405907);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPlace, MAX_ZOOM));

        googleMap.addMarker(new MarkerOptions().position(new LatLng(35.640225, 52.246952)).title("یه جا دیگه"));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(35.702799, 51.405684)).title("یه جا دیگه"));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(35.702801, 51.405518)).title("یه جا دیگه"));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(35.702744, 51.405556)).title("یه جا دیگه"));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(35.702456, 51.406055)).title("یه جا دیگه"));
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d("TAG", "xxxx onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d("TAG", "xxxx onProviderDisabled");
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d("TAG", "xxxx onStatusChanged");
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d("TAG", "onMarkerClick " + marker.getId() + " " + marker.getTitle());

//        AttackDialog dialog = new AttackDialog(MainActivity.this, marker.getTitle());
        AttackDialog dialog = new AttackDialog(MainActivity.this, "user-17");
        dialog.show();

        return false;
    }
}