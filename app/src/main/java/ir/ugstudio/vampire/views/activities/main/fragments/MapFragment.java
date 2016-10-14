package ir.ugstudio.vampire.views.activities.main.fragments;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.managers.CacheManager;
import ir.ugstudio.vampire.managers.UserManager;
import ir.ugstudio.vampire.models.MapResponse;
import ir.ugstudio.vampire.models.User;
import ir.ugstudio.vampire.views.dialogs.AttackDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment
    implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks, LocationListener {

    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient = null;
    private final int MIN_ZOOM = 16;
    private final int MAX_ZOOM = 17;
    private final int RADIUS = 100;

    public static MapFragment getInstance() {
        return new MapFragment();
    }

    private MapView mapView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();// needed to get the map to display immediately
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        SupportMapFragment mapFragment = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map));
//        SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//        mapView = (MapView) view.findViewById(R.id.map);
//        mapView.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }
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
    public boolean onMarkerClick(Marker marker) {
        Log.d("TAG", "onMarkerClick " + marker.getId() + " " + marker.getTitle());

//        AttackDialog dialog = new AttackDialog(MainActivity.this, marker.getTitle());
        AttackDialog dialog = new AttackDialog(getActivity(), "user-17");
        dialog.show();

        return false;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("TAG", "onConnected");

        if(ContextCompat.checkSelfPermission(getActivity(),
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

    private void requestForMap(final double lat, final double lng) {
        if(googleMap == null) {
            return;
        }

        Call<MapResponse> call = VampireApp.createMapApi().getMap(
                UserManager.readToken(getActivity()),
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
                .strokeColor(android.graphics.Color.RED)
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
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TAG", "MapFragment onResume");
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
}
