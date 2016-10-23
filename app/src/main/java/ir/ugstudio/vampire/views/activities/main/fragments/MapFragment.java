package ir.ugstudio.vampire.views.activities.main.fragments;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.async.GetPlaces;
import ir.ugstudio.vampire.events.GetProfileEvent;
import ir.ugstudio.vampire.managers.CacheManager;
import ir.ugstudio.vampire.managers.UserManager;
import ir.ugstudio.vampire.models.MapResponse;
import ir.ugstudio.vampire.models.Place;
import ir.ugstudio.vampire.models.PlacesResponse;
import ir.ugstudio.vampire.models.Tower;
import ir.ugstudio.vampire.models.User;
import ir.ugstudio.vampire.utils.Consts;
import ir.ugstudio.vampire.utils.FontHelper;
import ir.ugstudio.vampire.views.dialogs.HealDialog;
import ir.ugstudio.vampire.views.dialogs.AttackDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks, LocationListener, View.OnClickListener,
        GoogleMap.OnCameraMoveListener {

    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient = null;
    private final int MIN_ZOOM = 16;
    private final int MAX_ZOOM = 17;

    private final int MIN_ZOOM_HEAL_MODE = 14;
    private final int MAX_ZOOM_HEAL_MODE = 17;

    private static long lastRequestTime = 0;

    public static MapFragment getInstance() {
        return new MapFragment();
    }

    private MapView mapView;

    private TextView coin;
    private TextView score;
    private TextView rank;
    private FloatingActionButton addTower;

    boolean addingTowerMode = false;
    boolean healMode = false;

    private TextView coinIcon;
    private TextView scoreIcon;
    private TextView rankIcon;

    private MarkerOptions sampleTower = new MarkerOptions();
    private Marker sampleTowerMarker = null;
    private List<Marker> markers = new ArrayList<>();
    private MapResponse lastResponse = null;

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

        find(view);
        configure();
    }

    private void find(View view) {
        coin = (TextView) view.findViewById(R.id.coin);
        score = (TextView) view.findViewById(R.id.score);
        rank = (TextView) view.findViewById(R.id.rank);
        addTower = (FloatingActionButton) view.findViewById(R.id.add_tower);

        coinIcon = (TextView) view.findViewById(R.id.coin_icon);
        scoreIcon = (TextView) view.findViewById(R.id.score_icon);
        rankIcon = (TextView) view.findViewById(R.id.rank_icon);
    }

    private void updateView(User user) {
        coin.setText(String.valueOf(user.getCoin()));
        rank.setText(String.valueOf(user.getRank()));
        score.setText(String.valueOf(user.getScore()));
    }

    private void configure() {
        coinIcon.setTypeface(FontHelper.getIcons(getActivity()), Typeface.NORMAL);
        scoreIcon.setTypeface(FontHelper.getIcons(getActivity()), Typeface.NORMAL);
        rankIcon.setTypeface(FontHelper.getIcons(getActivity()), Typeface.NORMAL);

        updateView(CacheManager.getUser());

        addTower.setOnClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap myMap) {
        Log.d("TAG", "onMapReady");

        googleMap = myMap;
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnCameraMoveListener(this);

//        googleMap.getUiSettings().setZoomGesturesEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        googleMap.setMaxZoomPreference(MAX_ZOOM);
        googleMap.setMinZoomPreference(MIN_ZOOM);

        LatLng myPlace = new LatLng(35.702945, 51.405907);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPlace, MAX_ZOOM));

        googleMap.addMarker(new MarkerOptions().position(new LatLng(35.702456, 51.406055)).title("یه جا دیگه"));

        if (CacheManager.getUser().getLifestat().equals(Consts.LIFESTAT_DEAD)) {
            GetPlaces.run(getActivity());
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d("TAG", "onMarkerClick " + marker.getId() + " " + marker.getTitle());

        if (marker.getTitle().equals(CacheManager.getUser().getUsername())) {
            return true;
        }

        if (marker.getTitle().equals("SampleTower")) {
            addTowerNow(marker.getPosition());
            return true;
        }

        if(marker.getTag() == null) {
            AttackDialog dialog = new AttackDialog(getActivity(), getUser(marker.getTitle()));
            dialog.show();
        } else {
            handleHealNow((Place) marker.getTag());
        }
        return true;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("TAG", "onConnected");

        if (ContextCompat.checkSelfPermission(getActivity(),
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

        requestForMap(location.getLatitude(), location.getLongitude(), false);

//        LatLng newPlace = new LatLng(location.getLatitude(), location.getLongitude());
//        googleMap.addMarker(new MarkerOptions().position(newPlace));
//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPlace, MAX_ZOOM));

//        googleMap.addCircle(new CircleOptions()
//                .center(newPlace)
//                .radius(RADIUS)
//                .strokeColor(RED)
//        );
    }

    private void requestForMap(final double lat, final double lng, boolean forceRequest) {
        if (googleMap == null) {
            return;
        }

        if (addingTowerMode) {
            return;
        }

        if(healMode) {
            return;
        }

        if (System.currentTimeMillis() - lastRequestTime < 4000 && !forceRequest) {
            return;
        } else {
            lastRequestTime = System.currentTimeMillis();
        }

        googleMap.clear();
        sampleTowerMarker = null;

        if (addingTowerMode) {
            addMeToMap(lat, lng, MIN_ZOOM, true);
        } else {
            addMeToMap(lat, lng, MAX_ZOOM, true);
        }

        LatLng newPlace = new LatLng(lat, lng);

        if (!addingTowerMode) {
            googleMap.addCircle(new CircleOptions()
                    .center(newPlace)
                    .radius(CacheManager.getUser().getSightRange())
                    .fillColor(Color.parseColor("#33AAAAAA"))
                    .strokeColor(Color.parseColor("#00FFFFFF"))
            );
        }

        googleMap.addCircle(new CircleOptions()
                .center(newPlace)
                .radius(CacheManager.getUser().getAttackRange())
                .fillColor(Color.parseColor("#44AAAAAA"))
                .strokeColor(Color.parseColor("#00FFFFFF"))
        );

        Call<MapResponse> call = VampireApp.createMapApi().getMap(
                UserManager.readToken(getActivity()),
                lat,
                lng
        );
        call.enqueue(new Callback<MapResponse>() {
            @Override
            public void onResponse(Call<MapResponse> call, Response<MapResponse> response) {

                if (response.isSuccessful()) {
                    if(!healMode) {
                        refreshMap(response.body());
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

    private void refreshMap(MapResponse response) {
        lastResponse = response;
        markers.clear();

        MarkerOptions marker = new MarkerOptions();

        for (Tower tower : response.getTowers()) {
            marker.position(new LatLng(tower.getGeo().get(0), tower.getGeo().get(1)));
            marker.title("Tower");
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.tower));
            markers.add(googleMap.addMarker(marker));
        }

        if (addingTowerMode) {
            return;
        }

        for (User user : response.getVampires()) {
            marker.position(new LatLng(user.getGeo().get(0), user.getGeo().get(1)));
            marker.title(user.getUsername());
            if (user.getLifestat().equals("dead")) {
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.vampire_black));
            } else {
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.vampire_red));
            }
            markers.add(googleMap.addMarker(marker));
        }

        for (User user : response.getHunters()) {
            marker.position(new LatLng(user.getGeo().get(0), user.getGeo().get(1)));
            marker.title(user.getUsername());
            if (user.getLifestat().equals("dead")) {
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.hunter_black));
            } else {
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.hunter_red));
            }
            markers.add(googleMap.addMarker(marker));
        }
    }

    User getUser(String username) {
        if (lastResponse == null) {
            return null;
        }
        for (User user : lastResponse.getHunters()) {
            if (user.getUsername().equals(username))
                return user;
        }
        for (User user : lastResponse.getVampires()) {
            if (user.getUsername().equals(username))
                return user;
        }
        return null;
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
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(GetProfileEvent event) {
        Log.d("TAG", "onEvent GetProfileEvent " + event.isSuccessfull());
        if (event.isSuccessfull()) {
            updateView(event.getUser());
        }
    }

    private void handleAddTower() {
        addTower.setVisibility(View.INVISIBLE);

        Log.d("TAG", "" + googleMap.getCameraPosition().target);

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(CacheManager.getLastLocation().getLatitude(), CacheManager.getLastLocation().getLongitude()), MIN_ZOOM));

        addingTowerMode = true;
        for (Marker marker : markers) {
            if (marker.getTitle().equals("Tower")) {
                googleMap.addCircle(new CircleOptions()
                        .center(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude))
                        .radius(CacheManager.getUser().getAttackRange())
                        .fillColor(Color.parseColor("#33AA5555"))
                        .strokeColor(Color.parseColor("#00FFFFFF"))
                );
            } else {
                marker.remove();
            }
        }
        markers.clear();
//        requestForMap(CacheManager.getLastLocation().getLatitude(), CacheManager.getLastLocation().getLongitude(), true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_tower:
                handleAddTower();
                break;
        }
    }

    public void addTowerNow(LatLng position) {
        Log.d("TAG", "abcd " + position.latitude + " " + position.longitude);
        if (addingTowerMode) {
            addingTowerMode = false;

            Call<ResponseBody> call = VampireApp.createMapApi().addTower(CacheManager.getUser().getToken(), position.latitude, position.longitude);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    addTower.setVisibility(View.VISIBLE);

                    if (response.isSuccessful()) {
                        String result = "IOEXception";
                        try {
                            result = response.body().string();
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
                    addTower.setVisibility(View.VISIBLE);

                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showTargetOfCamera() {
        if (addingTowerMode) {
            if (sampleTowerMarker == null) {
                sampleTower.position(googleMap.getCameraPosition().target);
                sampleTower.title("SampleTower");
                sampleTowerMarker = googleMap.addMarker(sampleTower);
            } else {
                sampleTowerMarker.setPosition(googleMap.getCameraPosition().target);
            }
        }
    }

    @Override
    public void onCameraMove() {
        showTargetOfCamera();
    }

    @Subscribe
    public void onEvent(PlacesResponse response) {
        Log.d("TAG", "onEvent PlacesResponse " + response.getPlaces().size());
        startHealMode(response.getPlaces());
    }

    private void finishHealMode() {
        healMode = false;
        googleMap.setMinZoomPreference(MIN_ZOOM);
        googleMap.setMaxZoomPreference(MAX_ZOOM);

        requestForMap(CacheManager.getLastLocation().getLatitude(), CacheManager.getLastLocation().getLongitude(), false);
    }

    private void startHealMode(List<Place> places) {
        Log.d("TAG", "startHealMode");
        healMode = true;

        googleMap.setMinZoomPreference(MIN_ZOOM_HEAL_MODE);
        googleMap.setMaxZoomPreference(MAX_ZOOM_HEAL_MODE);

        googleMap.clear();
        markers.clear();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.hospital));
        for(Place place : places) {
            LatLng newPlace = new LatLng(place.getGeo().get(0), place.getGeo().get(1));
            markerOptions.position(newPlace);
            markerOptions.title(place.getPlaceId());

            Marker marker = googleMap.addMarker(markerOptions);
            marker.setTag(place);
            markers.add( marker );
        }

        addMeToMap(CacheManager.getLastLocation().getLatitude(), CacheManager.getLastLocation().getLongitude(), MIN_ZOOM_HEAL_MODE, false);
    }

    private void addMeToMap(double lat, double lng, float zoomLevel, final boolean putDot) {
        LatLng newPlace = new LatLng(lat, lng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(newPlace).title(CacheManager.getUser().getUsername());

        if(putDot) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.dot));
        }

        googleMap.addMarker(markerOptions);

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPlace, zoomLevel), new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                if(!putDot) {
                    startHealDialog();
                }
            }

            @Override
            public void onCancel() {

            }
        });
    }

    private void startHealDialog() {
        new HealDialog(getActivity()).show();
    }

    private void handleHealNow(Place place) {
        // TODO, replace my place instead of one of the hospitals
        Call<ResponseBody> call = VampireApp.createUserApi().healMe(
                CacheManager.getUser().getToken(),
                place.getGeo().get(0),
                place.getGeo().get(1),
                place.getPlaceId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    finishHealMode();
                    String result = "IOEXception";
                    try {
                        result = response.body().string();
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
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
