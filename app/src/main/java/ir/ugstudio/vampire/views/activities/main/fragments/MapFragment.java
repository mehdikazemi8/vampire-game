package ir.ugstudio.vampire.views.activities.main.fragments;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.async.GetPlaces;
import ir.ugstudio.vampire.async.GetProfile;
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
import ir.ugstudio.vampire.views.BaseFragment;
import ir.ugstudio.vampire.views.dialogs.AttackDialog;
import ir.ugstudio.vampire.views.dialogs.HealDialog;
import ir.ugstudio.vampire.views.dialogs.TowerDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends BaseFragment
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks, LocationListener, View.OnClickListener,
        GoogleMap.OnCameraMoveListener {

    private static long lastRequestTime = 0;
    private final int MIN_ZOOM = 16;
    private final int MAX_ZOOM = 17;
    private final int MIN_ZOOM_HEAL_MODE = 14;
    private final int MAX_ZOOM_HEAL_MODE = 17;
    boolean addingTowerMode = false;
    boolean healMode = false;
    boolean collectCoinsMode = false;
    boolean watchMyTowersMode = false;
    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient = null;
    private Queue<Tower> towersToCollectCoin = new LinkedList<>();
    private Queue<Tower> towersToWatch = new LinkedList<>();
    private Tower nowOnThisTower = null;
    private MapView mapView;
    private TextView coin;
    private TextView score;
    private TextView rank;

    private FloatingActionButton cancelButton;
    private FloatingActionButton addTower;
    private FloatingActionButton watchMyTowers;
    private FloatingActionButton collectCoinFromMyTowers;

    private TextView coinIcon;
    private TextView scoreIcon;
    private TextView rankIcon;
    private MarkerOptions sampleTower = new MarkerOptions();
    private Marker sampleTowerMarker = null;
    private List<Marker> markers = new ArrayList<>();
    private MapResponse lastResponse = null;

    public static MapFragment getInstance() {
        return new MapFragment();
    }

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

        cancelButton = (FloatingActionButton) view.findViewById(R.id.cancel_button);
        addTower = (FloatingActionButton) view.findViewById(R.id.add_tower);
        collectCoinFromMyTowers = (FloatingActionButton) view.findViewById(R.id.collect_coin_from_my_towers);
        watchMyTowers = (FloatingActionButton) view.findViewById(R.id.watch_my_towers);

        coinIcon = (TextView) view.findViewById(R.id.coin_icon);
        scoreIcon = (TextView) view.findViewById(R.id.score_icon);
        rankIcon = (TextView) view.findViewById(R.id.rank_icon);
    }

    private void updateView(User user) {
        // todo, when goes to cafe for rating and comes back it crashes
        FontHelper.setKoodakFor(getActivity(), coin, rank, score);
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
        watchMyTowers.setOnClickListener(this);
        collectCoinFromMyTowers.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap myMap) {
        Log.d("TAG", "onMapReady");

        googleMap = myMap;
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnCameraMoveListener(this);

        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getActivity(), R.raw.style_json));
            if (!success) {
                Log.e("MapsActivityRaw", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivityRaw", "Can't find style.", e);
        }

//        googleMap.getUiSettings().setZoomGesturesEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        googleMap.setMaxZoomPreference(MAX_ZOOM);
        googleMap.setMinZoomPreference(MIN_ZOOM);

        LatLng myPlace = new LatLng(35.702945, 51.405907);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPlace, MAX_ZOOM));
//        googleMap.addMarker(new MarkerOptions().position(new LatLng(35.702456, 51.406055)).title("مرکز فرماندهی"));

        if (CacheManager.getUser().getLifestat().equals(Consts.LIFESTAT_DEAD)) {
            GetPlaces.run(getActivity());
        }
    }

    private void collectCoinsOfThisTower(Tower tower) {
        Call<ResponseBody> call = VampireApp.createMapApi().collectTowerCoins(CacheManager.getUser().getToken(), tower.get_id());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String message = "خطا در ارتباط با سرور";
                    try {
                        message = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    GetProfile.run(getActivity());
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    showNextTowerToCollect();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d("TAG", "onMarkerClick " + marker.getId() + " " + marker.getTitle() + " " + watchMyTowersMode);

        if (marker.getTitle().equals(CacheManager.getUser().getUsername())) {
            return true;
        }

        if (marker.getTitle().equals("SampleTower")) {
            addTowerNow(marker.getPosition());
            return true;
        }

        if (marker.getTag() instanceof User) {
            Log.d("TAG", "onMarkerClick USER");
            if (watchMyTowersMode) {
                AttackDialog dialog = new AttackDialog(getActivity(), (User) marker.getTag(), nowOnThisTower);
                dialog.show();
            } else {
                AttackDialog dialog = new AttackDialog(getActivity(), getUser(marker.getTitle()));
                dialog.show();
            }

        } else if (marker.getTag() instanceof Place) {
            Log.d("TAG", "onMarkerClick Place");
            handleHealNow((Place) marker.getTag());
        } else if (marker.getTag() instanceof Tower) {
            Tower tower = (Tower) marker.getTag();
            Log.d("TAG", "onMarkerClick TOWER " + tower.getRole() + " " + CacheManager.getUser().getRole());

            if (collectCoinsMode) {
                collectCoinsOfThisTower(tower);
            } else if (watchMyTowersMode) {
                helpMeWatchThisTower(tower);
            } else {
                TowerDialog dialog = new TowerDialog(getActivity(), (Tower) marker.getTag());
                dialog.show();
            }
        }

        return true;
    }

    private void helpMeWatchThisTower(Tower tower) {
        showNextTowerToWatch();
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
        Log.d("TAG", "changed onLocationChanged " + location.getLatitude() + " " + location.getLongitude());
        CacheManager.setLastLocation(location);
        requestForMap(location.getLatitude(), location.getLongitude(), false);
    }

    private void requestForMap(final double lat, final double lng, boolean forceRequest) {
        if (googleMap == null) {
            return;
        }

        if (addingTowerMode) {
            return;
        }

        if (healMode) {
            return;
        }

        if (collectCoinsMode) {
            return;
        }

        if (watchMyTowersMode) {
            return;
        }

        if (System.currentTimeMillis() - lastRequestTime < 4000 && !forceRequest) {
            return;
        } else {
            lastRequestTime = System.currentTimeMillis();
        }

        // todo must clear or not 6 december
//        googleMap.clear();
        sampleTowerMarker = null;

        if (addingTowerMode) {
            addMeToMap(lat, lng, MIN_ZOOM, true);
        } else {
            addMeToMap(lat, lng, MAX_ZOOM, true);
        }

        LatLng newPlace = new LatLng(lat, lng);

        googleMap.addCircle(new CircleOptions()
                .center(newPlace)
                .radius(CacheManager.getUser().getSightRange())
                .fillColor(Color.parseColor("#33AAAAAA"))
                .strokeColor(Color.parseColor("#00FFFFFF"))
        );

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
                    if (!healMode && !watchMyTowersMode && !collectCoinsMode) {
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

        // // TODO: 12/6/16
        /**
         * oon googleMap.clear() ro az tooye requestForMap bardashtim, hala bayad diff begirim ba chizayee
         * ke jadid oomade
         * */
        //markers.clear();

        MarkerOptions markerOptions = new MarkerOptions();
        Marker marker = null;
        for (Tower tower : response.getTowers()) {
            markerOptions.position(new LatLng(tower.getGeo().get(0), tower.getGeo().get(1)));
            markerOptions.title("Tower");
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.tower));

            marker = googleMap.addMarker(markerOptions);
            marker.setTag(tower);
            markers.add(marker);
        }

        if (addingTowerMode) {
            return;
        }

        List<User> sheeps = response.getSheeps();
        List<User> hunters = response.getHunters();
        List<User> vampires = response.getVampires();

        for (Marker oldMarker : markers) {
            if (oldMarker.getTag() instanceof User) {
                boolean foundTagUser = false;
                User tagUser = (User) oldMarker.getTag();
                for (User sheep : sheeps) {
                    if (sheep.getUsername().equals(tagUser.getUsername())) {
                        tagUser.setGeo(sheep.getGeo());
                        oldMarker.setPosition(new LatLng(tagUser.getGeo().get(0), tagUser.getGeo().get(1)));
                        foundTagUser = true;
                        sheeps.remove(sheep);
                        break;
                    }
                }
                if (foundTagUser)
                    continue;
                for (User hunter : hunters) {
                    if (hunter.getUsername().equals(tagUser.getUsername())) {
                        tagUser.setGeo(hunter.getGeo());
                        oldMarker.setPosition(new LatLng(tagUser.getGeo().get(0), tagUser.getGeo().get(1)));
                        foundTagUser = true;
                        hunters.remove(hunter);
                        break;
                    }
                }
                if (foundTagUser)
                    continue;
                for (User vampire : vampires) {
                    if (vampire.getUsername().equals(tagUser.getUsername())) {
                        tagUser.setGeo(vampire.getGeo());
                        oldMarker.setPosition(new LatLng(tagUser.getGeo().get(0), tagUser.getGeo().get(1)));
                        foundTagUser = true;
                        vampires.remove(vampire);
                        break;
                    }
                }

                if (!foundTagUser) {
                    oldMarker.remove();
                }
            }
        }

        // todo check this again
        for (User sheep : sheeps) {
            markerOptions.position(new LatLng(sheep.getGeo().get(0), sheep.getGeo().get(1)));
            markerOptions.title(sheep.getUsername());
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.sheep));

            marker = googleMap.addMarker(markerOptions);
            marker.setTag(sheep);
            markers.add(marker);
        }

        for (User vampire : vampires) {
            markerOptions.position(new LatLng(vampire.getGeo().get(0), vampire.getGeo().get(1)));
            markerOptions.title(vampire.getUsername());
            if (vampire.getLifestat().equals("dead")) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.vampire_black));
            } else {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.vampire_red));
            }

            marker = googleMap.addMarker(markerOptions);
            marker.setTag(vampire);
            markers.add(marker);
        }

//        for (User user : response.getSheeps()) {
//            markerOptions.position(new LatLng(user.getGeo().get(0), user.getGeo().get(1)));
//            markerOptions.title(user.getUsername());
//            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.sheep));
//
//            marker = googleMap.addMarker(markerOptions);
//            marker.setTag(user);
//            markers.add(marker);
//        }

        for (User hunter : hunters) {
            markerOptions.position(new LatLng(hunter.getGeo().get(0), hunter.getGeo().get(1)));
            markerOptions.title(hunter.getUsername());
            if (hunter.getLifestat().equals("dead")) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.hunter_black));
            } else {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.hunter_red));
            }
            marker = googleMap.addMarker(markerOptions);
            marker.setTag(hunter);
            markers.add(marker);
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

    private void revertButtonsState(boolean buttonsVisible) {
        Log.d("TAG", "revertButtonsState " + buttonsVisible);
        if (!buttonsVisible) {
            addTower.setVisibility(View.INVISIBLE);
            collectCoinFromMyTowers.setVisibility(View.INVISIBLE);
            watchMyTowers.setVisibility(View.INVISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
        } else {
            addTower.setVisibility(View.VISIBLE);
            collectCoinFromMyTowers.setVisibility(View.VISIBLE);
            watchMyTowers.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.INVISIBLE);
        }
    }

    private void handleAddTower() {
        revertButtonsState(false);

        Log.d("TAG", "handleAddTower " + googleMap.getCameraPosition().target);

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

    private void startCollectCoinsMode() {
        User user = CacheManager.getUser();
        towersToCollectCoin.clear();
        for (Tower tower : user.getTowersList()) {
            if (tower.getCoin() != 0) {
                towersToCollectCoin.add(tower);
            }
        }

        if (towersToCollectCoin.size() > 0) {
            revertButtonsState(false);
            collectCoinsMode = true;
            googleMap.clear();
            showNextTowerToCollect();
        } else {
            Toast.makeText(getActivity(), "همه چی رو به راهه، یه ساعت دیگه بیا", Toast.LENGTH_LONG).show();
        }
    }

    private void showNextTowerToCollect() {
        if (towersToCollectCoin.isEmpty()) {
            finishCollectCoinsMode();
            return;
        }

        Tower nextTower = towersToCollectCoin.remove();
        LatLng towerPlace = new LatLng(nextTower.getGeo().get(0), nextTower.getGeo().get(1));

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(towerPlace);
        markerOptions.title(String.valueOf(nextTower.getCoin()));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.tower));
        Marker marker = googleMap.addMarker(markerOptions);
        marker.setTag(nextTower);

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(towerPlace, MIN_ZOOM));

        Toast.makeText(getActivity(), String.valueOf(nextTower.getCoin()), Toast.LENGTH_LONG).show();
    }

    private void finishCollectCoinsMode() {
        collectCoinsMode = false;
        revertButtonsState(true);
        requestForMap(CacheManager.getLastLocation().getLatitude(), CacheManager.getLastLocation().getLongitude(), false);
    }

    private void actionCanceled() {
        watchMyTowersMode = healMode = addingTowerMode = collectCoinsMode = false;
        revertButtonsState(true);
        requestForMap(CacheManager.getLastLocation().getLatitude(), CacheManager.getLastLocation().getLongitude(), true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_tower:
                handleAddTower();
                break;

            case R.id.collect_coin_from_my_towers:
                startCollectCoinsMode();
                break;

            case R.id.watch_my_towers:
                startWatchMyTowersMode();
                break;

            case R.id.cancel_button:
                actionCanceled();
                break;
        }
    }

    private void startWatchMyTowersMode() {
        revertButtonsState(false);

        towersToWatch.clear();
        User user = CacheManager.getUser();
        for (Tower tower : user.getTowersList()) {
            towersToWatch.add(tower);
        }

        if (towersToWatch.isEmpty()) {
            Toast.makeText(getActivity(), "اول باید برج بسازی بعد می تونی مدیریت کنیشون", Toast.LENGTH_LONG).show();
            return;
        }

        googleMap.clear();
        watchMyTowersMode = true;
        showNextTowerToWatch();
    }

    private void finishWatchMyTowersMode() {
        revertButtonsState(true);
        nowOnThisTower = null;
        watchMyTowersMode = false;
        requestForMap(CacheManager.getLastLocation().getLatitude(), CacheManager.getLastLocation().getLongitude(), false);
    }

    private void showNextTowerToWatch() {
        if (towersToWatch.isEmpty()) {
            finishWatchMyTowersMode();
            return;
        }

        Tower nextTower = towersToWatch.remove();
        nowOnThisTower = nextTower;
        LatLng towerPlace = new LatLng(nextTower.getGeo().get(0), nextTower.getGeo().get(1));

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(towerPlace);
        markerOptions.title(String.valueOf(nextTower.getCoin()));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.tower));
        Marker marker = googleMap.addMarker(markerOptions);
        marker.setTag(nextTower);

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(towerPlace, MIN_ZOOM));

        showMapAroundTheTower(nextTower);
    }

    private void showMapAroundTheTower(Tower tower) {
        Call<MapResponse> call = VampireApp.createMapApi().getMapAroundTower(
                UserManager.readToken(getActivity()),
                tower.get_id()
        );

        call.enqueue(new Callback<MapResponse>() {
            @Override
            public void onResponse(Call<MapResponse> call, Response<MapResponse> response) {
                if (response.isSuccessful()) {
                    refreshMap(response.body());
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

    public void addTowerNow(LatLng position) {
        Log.d("TAG", "abcd " + position.latitude + " " + position.longitude);
        if (addingTowerMode) {
            addingTowerMode = false;

            Call<ResponseBody> call = VampireApp.createMapApi().addTower(CacheManager.getUser().getToken(), position.latitude, position.longitude);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    addTower.setVisibility(View.VISIBLE);
                    revertButtonsState(true);

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
//                    addTower.setVisibility(View.VISIBLE);
                    revertButtonsState(true);

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
                sampleTower.icon(BitmapDescriptorFactory.fromResource(R.drawable.plus_tower1));
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
        for (Place place : places) {
            LatLng newPlace = new LatLng(place.getGeo().get(0), place.getGeo().get(1));
            markerOptions.position(newPlace);
            markerOptions.title(place.getPlaceId());

            Marker marker = googleMap.addMarker(markerOptions);
            marker.setTag(place);
            markers.add(marker);
        }

        // todo why this crashes when app starts
        addMeToMap(CacheManager.getLastLocation().getLatitude(), CacheManager.getLastLocation().getLongitude(), MIN_ZOOM_HEAL_MODE, false);
    }

    private void addMeToMap(double lat, double lng, float zoomLevel, final boolean putDot) {
        LatLng newPlace = new LatLng(lat, lng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(newPlace).title(CacheManager.getUser().getUsername());

        if (putDot) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.dot));
        }

        Marker marker = googleMap.addMarker(markerOptions);
        marker.setTag(CacheManager.getUser());

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPlace, zoomLevel), new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                if (!putDot) {
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

    @Override
    public void onBringToFront() {
        super.onBringToFront();
        Log.d("TAG", "onBringToFront MapFragment");
    }
}
