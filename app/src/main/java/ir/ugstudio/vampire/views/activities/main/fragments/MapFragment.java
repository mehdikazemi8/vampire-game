package ir.ugstudio.vampire.views.activities.main.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.async.GetDirection;
import ir.ugstudio.vampire.async.GetNearest;
import ir.ugstudio.vampire.async.GetPlaces;
import ir.ugstudio.vampire.async.GetProfile;
import ir.ugstudio.vampire.events.FinishHealMode;
import ir.ugstudio.vampire.events.FinishNearestMissionEvent;
import ir.ugstudio.vampire.events.GetProfileEvent;
import ir.ugstudio.vampire.events.NearestResponseEvent;
import ir.ugstudio.vampire.events.OpenIntroductionFragment;
import ir.ugstudio.vampire.events.RefreshAroundTowerEvent;
import ir.ugstudio.vampire.events.ShowTabEvent;
import ir.ugstudio.vampire.events.StartNearestMissionEvent;
import ir.ugstudio.vampire.events.TowerAddEvent;
import ir.ugstudio.vampire.events.TowerCollectCoinsEvent;
import ir.ugstudio.vampire.events.TowerWatchEvent;
import ir.ugstudio.vampire.listeners.OnCompleteListener;
import ir.ugstudio.vampire.managers.AnalyticsManager;
import ir.ugstudio.vampire.managers.CacheHandler;
import ir.ugstudio.vampire.managers.UserHandler;
import ir.ugstudio.vampire.models.MapResponse;
import ir.ugstudio.vampire.models.Place;
import ir.ugstudio.vampire.models.PlacesResponse;
import ir.ugstudio.vampire.models.Tower;
import ir.ugstudio.vampire.models.User;
import ir.ugstudio.vampire.utils.Consts;
import ir.ugstudio.vampire.utils.FontHelper;
import ir.ugstudio.vampire.utils.Utility;
import ir.ugstudio.vampire.utils.VampireLocationManager;
import ir.ugstudio.vampire.views.BaseFragment;
import ir.ugstudio.vampire.views.activities.main.MainActivity;
import ir.ugstudio.vampire.views.custom.IconButton;
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
    private final int MIN_ZOOM = 15;
    private final int MAX_ZOOM = 16;
    private final int MIN_ZOOM_HEAL_MODE = 15;
    private final int MAX_ZOOM_HEAL_MODE = 16;
    private final float SHOW_IRAN_ZOOM_LEVEL = 4.75f;
    private final LatLng CENTER_OF_IRAN = new LatLng(32.418920, 53.344186);

    boolean addingTowerMode = false;
    boolean healMode = false;
    boolean collectCoinsMode = false;
    boolean watchMyTowersMode = false;
    boolean missionMode = false;

    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient = null;
    private Queue<Tower> towersToCollectCoin = new LinkedList<>();
    private Queue<Tower> towersToWatch = new LinkedList<>();
    private Tower nowOnThisTower = null;
    private MapView mapView;

    private HealDialog healDialog = null;
    private int healDialogShowTimes = 0;

    private Circle innerCircle = null;
    private Circle outerCircle = null;

    private Circle innerCircleTower = null;
    private Circle outerCircleTower = null;
    private Marker myMarker = null;

    private TextView coin;
    private TextView score;
    private TextView rank;

    private LinearLayout arrow;

    private FloatingActionButton cancelButton;
    private FloatingActionButton showNextTower;

//    private FloatingActionButton addTower;
//    private FloatingActionButton watchMyTowers;
//    private FloatingActionButton collectCoinFromMyTowers;

    private FloatingActionButton actionsButton;
    private IconButton showIntro;

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

//        onBringToFront();
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

        if (CacheHandler.getUser().getLifestat().equals(Consts.LIFESTAT_DEAD)) {
            GetPlaces.run(getActivity());
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getTitle().equals(CacheHandler.getUser().getUsername())) {
            return true;
        }

        if (marker.getTitle().equals("SampleTower")) {
            addTowerNow(marker.getPosition());
            return true;
        }

        if (marker.getTag() instanceof User) {
            if (watchMyTowersMode) {
                AttackDialog dialog = new AttackDialog(getActivity(), (User) marker.getTag(), nowOnThisTower);
                dialog.show();
            } else {
                AttackDialog dialog = new AttackDialog(getActivity(), (User) (marker.getTag()));
                dialog.show();
            }

        } else if (marker.getTag() instanceof Place) {
            handleHealNow((Place) marker.getTag());
        } else if (marker.getTag() instanceof Tower) {
            Tower tower = (Tower) marker.getTag();
            if (collectCoinsMode) {
                collectCoinsOfThisTower(tower);
            } else if (watchMyTowersMode) {
                TowerDialog dialog = new TowerDialog(getActivity(), (Tower) marker.getTag());
                dialog.show();
            } else {
                TowerDialog dialog = new TowerDialog(getActivity(), (Tower) marker.getTag());
                dialog.show();
            }
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
        Log.d("TAG", "changed onLocationChanged " + location.getLatitude() + " " + location.getLongitude());
        CacheHandler.setLastLocation(location);
        requestForMap(location.getLatitude(), location.getLongitude(), false);
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TAG", "MapFragment onResume");
        mapView.onResume();

        onBringToFront();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.collect_coin_from_my_towers:
//                startCollectCoinsMode();
//                break;
//
//            case R.id.watch_my_towers:
//                startWatchMyTowersMode();
//                break;
//
//            case R.id.add_tower:
//                handleAddTower();
//                break;

            case R.id.show_next_tower:
                showNextTowerToWatch();
                break;

            case R.id.cancel_button:
                actionCanceled();
                break;

            case R.id.show_intro:
                EventBus.getDefault().post(new OpenIntroductionFragment());
                break;

            case R.id.actions_button:
                ((MainActivity) (getActivity())).openActionsFragment();
                break;
        }
    }

    @Override
    public void onCameraMove() {
        Log.d("TAG", "onCameraMove");
        showTargetOfCamera();
    }

    @Override
    public void onBringToFront() {
        super.onBringToFront();
        if (!VampireLocationManager.isGPSEnabled(getActivity())) {
            turnOnGPS();
        }
    }

    private void find(View view) {
        coin = (TextView) view.findViewById(R.id.coin);
        score = (TextView) view.findViewById(R.id.score);
        rank = (TextView) view.findViewById(R.id.rank);

        cancelButton = (FloatingActionButton) view.findViewById(R.id.cancel_button);
        showNextTower = (FloatingActionButton) view.findViewById(R.id.show_next_tower);
//        addTower = (FloatingActionButton) view.findViewById(R.id.add_tower);
//        collectCoinFromMyTowers = (FloatingActionButton) view.findViewById(R.id.collect_coin_from_my_towers);
//        watchMyTowers = (FloatingActionButton) view.findViewById(R.id.watch_my_towers);
        actionsButton = (FloatingActionButton) view.findViewById(R.id.actions_button);

        coinIcon = (TextView) view.findViewById(R.id.coin_icon);
        scoreIcon = (TextView) view.findViewById(R.id.score_icon);
        rankIcon = (TextView) view.findViewById(R.id.rank_icon);

        showIntro = (IconButton) view.findViewById(R.id.show_intro);

        arrow = (LinearLayout) view.findViewById(R.id.arrow);
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

        updateView(CacheHandler.getUser());

        showNextTower.setOnClickListener(this);
//        addTower.setOnClickListener(this);
//        watchMyTowers.setOnClickListener(this);
//        collectCoinFromMyTowers.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        showIntro.setOnClickListener(this);
        actionsButton.setOnClickListener(this);
    }

    private void collectCoinsOfThisTower(final Tower tower) {
        Call<ResponseBody> call = VampireApp.createMapApi().collectTowerCoins(CacheHandler.getUser().getToken(), tower.get_id());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    GetProfile.run(getActivity());

                    Utility.makeToast(getActivity(), String.format(getString(R.string.toast_collect_coin_ok), tower.getCoin()), Toast.LENGTH_SHORT);

                    showNextTowerToCollect();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void helpMeWatchThisTower(Tower tower) {

        //showNextTowerToWatch();
    }

    private void handleMeWhenHealMode(double lat, double lng) {
        startHealDialog();
        addMeToMap(lat, lng, MIN_ZOOM_HEAL_MODE, true);

        LatLng newPlace = new LatLng(lat, lng);
        if (outerCircle == null) {
            outerCircle = googleMap.addCircle(new CircleOptions()
                    .center(newPlace)
                    .radius(CacheHandler.getUser().getSightRange())
                    .fillColor(Color.parseColor("#33AAAAAA"))
                    .strokeColor(Color.parseColor("#00FFFFFF"))
            );

            innerCircle = googleMap.addCircle(new CircleOptions()
                    .center(newPlace)
                    .radius(CacheHandler.getUser().getAttackRange())
                    .fillColor(Color.parseColor("#44AAAAAA"))
                    .strokeColor(Color.parseColor("#00FFFFFF"))
            );
        } else {
            innerCircle.setCenter(newPlace);
            outerCircle.setCenter(newPlace);
        }
    }

    private void requestForMap(final double lat, final double lng, boolean forceRequest) {
        if (googleMap == null) {
            return;
        }

        if (addingTowerMode) {
            return;
        }

        if (healMode) {
            handleMeWhenHealMode(lat, lng);
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

//        googleMap.clear();
        sampleTowerMarker = null;

        if (addingTowerMode) {
            addMeToMap(lat, lng, MIN_ZOOM, true);
        } else {
            addMeToMap(lat, lng, MAX_ZOOM, true);
        }

        LatLng newPlace = new LatLng(lat, lng);
        if (outerCircle == null) {
            outerCircle = googleMap.addCircle(new CircleOptions()
                    .center(newPlace)
                    .radius(CacheHandler.getUser().getSightRange())
                    .fillColor(Color.parseColor("#33AAAAAA"))
                    .strokeColor(Color.parseColor("#00FFFFFF"))
            );

            innerCircle = googleMap.addCircle(new CircleOptions()
                    .center(newPlace)
                    .radius(CacheHandler.getUser().getAttackRange())
                    .fillColor(Color.parseColor("#44AAAAAA"))
                    .strokeColor(Color.parseColor("#00FFFFFF"))
            );
        } else {
            innerCircle.setCenter(newPlace);
            outerCircle.setCenter(newPlace);
        }

        Call<MapResponse> call = VampireApp.createMapApi().getMap(
                UserHandler.readToken(getActivity()),
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

        if (missionMode) {
            GetDirection.run(getActivity());
        }
    }

    private void refreshMap(MapResponse response) {
        lastResponse = response;

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
                if (foundTagUser) {
                    continue;
                }
                for (User hunter : hunters) {
                    if (hunter.getUsername().equals(tagUser.getUsername())) {
                        tagUser.setGeo(hunter.getGeo());
                        oldMarker.setPosition(new LatLng(tagUser.getGeo().get(0), tagUser.getGeo().get(1)));
                        hunters.remove(hunter);
                        foundTagUser = true;

                        Log.d("abcde", "yy " + tagUser.getLifestat() + " " + hunter.getLifestat());
                        if (tagUser.getLifestat().equals("alive") && hunter.getLifestat().equals("dead")) {
                            oldMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.hunter_black));
                        }
                        break;
                    }
                }
                if (foundTagUser) {
                    continue;
                }
                for (User vampire : vampires) {
                    if (vampire.getUsername().equals(tagUser.getUsername())) {
                        tagUser.setGeo(vampire.getGeo());
                        oldMarker.setPosition(new LatLng(tagUser.getGeo().get(0), tagUser.getGeo().get(1)));
                        vampires.remove(vampire);
                        foundTagUser = true;

                        Log.d("abcde", "xx " + tagUser.getLifestat() + " " + vampire.getLifestat());
                        if (tagUser.getLifestat().equals("alive") && vampire.getLifestat().equals("dead")) {
                            oldMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.vampire_black));
                        }
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
                if (CacheHandler.getUser().getRole().equals("vampire")) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.vampire_black));
                } else {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.vampire_red));
                }
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
                if (CacheHandler.getUser().getRole().equals("hunter")) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.hunter_black));
                } else {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.hunter_red));
                }
            }
            marker = googleMap.addMarker(markerOptions);
            marker.setTag(hunter);
            markers.add(marker);
        }
    }

    private User getUser(String username) {
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

    private void revertButtonsState(boolean buttonsVisible, boolean isAddTowerMode) {
        Log.d("TAG", "revertButtonsState " + buttonsVisible);
        if (!buttonsVisible) {
            actionsButton.setVisibility(View.INVISIBLE);
//            collectCoinFromMyTowers.setVisibility(View.INVISIBLE);
//            watchMyTowers.setVisibility(View.INVISIBLE);

            cancelButton.setVisibility(View.VISIBLE);
            if (!isAddTowerMode)
                showNextTower.setVisibility(View.VISIBLE);
        } else {
            actionsButton.setVisibility(View.VISIBLE);
//            collectCoinFromMyTowers.setVisibility(View.VISIBLE);
//            watchMyTowers.setVisibility(View.VISIBLE);

            cancelButton.setVisibility(View.INVISIBLE);
            showNextTower.setVisibility(View.INVISIBLE);
        }
    }

    private void redirectToStore() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setMessage("ساختن برج نیازمند ۴۰۰۰ سکه می باشد.\nمتاسفانه سکه\u200Cي کافی برای ساخت برج نداری، دوس داری سکه خریداری کنی؟")
                .setPositiveButton("بله", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EventBus.getDefault().post(new ShowTabEvent(1));
                    }
                })
                .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
        FontHelper.setKoodakFor(getActivity(), (TextView) dialog.findViewById(android.R.id.message));
    }

    private void handleAddTower() {
        revertButtonsState(false, true);

        Log.d("TAG", "handleAddTower " + googleMap.getCameraPosition().target);

        if (VampireLocationManager.isGPSEnabled(getActivity())) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(CacheHandler.getLastLocation().getLatitude(), CacheHandler.getLastLocation().getLongitude()), MIN_ZOOM));
        } else {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(CENTER_OF_IRAN.latitude, CENTER_OF_IRAN.longitude), SHOW_IRAN_ZOOM_LEVEL));
        }

        addingTowerMode = true;
        for (Marker marker : markers) {
            if (marker.getTitle().equals("Tower")) {
                googleMap.addCircle(new CircleOptions()
                        .center(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude))
                        .radius(CacheHandler.getUser().getAttackRange())
                        .fillColor(Color.parseColor("#33AA5555"))
                        .strokeColor(Color.parseColor("#00FFFFFF"))
                );
            } else {
                marker.remove();
            }
        }
        markers.clear();
//        requestForMap(CacheHandler.getLastLocation().getLatitude(), CacheHandler.getLastLocation().getLongitude(), true);
    }

    private void startCollectCoinsMode() {
        User user = CacheHandler.getUser();
        towersToCollectCoin.clear();

        if (user.getTowersList() == null || user.getTowersList().size() == 0) {
            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setMessage(getString(R.string.toast_have_no_tower_to_collect_coin))
                    .setPositiveButton("باشه", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    }).show();
            FontHelper.setKoodakFor(getActivity(), (TextView) dialog.findViewById(android.R.id.message));
            return;
        }

        Log.d("TAG", "startCollectCoinsMode " + user.getTowersList());
        Log.d("TAG", "startCollectCoinsMode " + user.getTowersList().size());

        for (Tower tower : user.getTowersList()) {
            if (tower.getCoin() != 0) {
                towersToCollectCoin.add(tower);
            }
        }

        if (towersToCollectCoin.size() > 0) {
            revertButtonsState(false, false);
            collectCoinsMode = true;
            clearGoogleMap();
            showNextTowerToCollect();
        } else {
            Utility.makeToast(getActivity(), getString(R.string.toast_collect_coin_come_back_later), Toast.LENGTH_LONG);
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

        marker.showInfoWindow();

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(towerPlace, MIN_ZOOM));
    }

    private void finishCollectCoinsMode() {
        collectCoinsMode = false;
        revertButtonsState(true, false);
        clearGoogleMap();
        requestForMap(CacheHandler.getLastLocation().getLatitude(), CacheHandler.getLastLocation().getLongitude(), false);
    }

    private void actionCanceled() {
        watchMyTowersMode = healMode = addingTowerMode = collectCoinsMode = false;
        revertButtonsState(true, false);
        clearGoogleMap();
        requestForMap(CacheHandler.getLastLocation().getLatitude(), CacheHandler.getLastLocation().getLongitude(), true);
    }

    private void startMissionMode() {
        missionMode = true;
        actionsButton.setVisibility(View.INVISIBLE);
        openMissionInfoFragment();
    }

    private void finishMissionMode() {
        MissionInfoFragment fragment = (MissionInfoFragment) getActivity().getSupportFragmentManager().findFragmentByTag(MissionInfoFragment.class.getCanonicalName());
        if (fragment != null) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }

        missionMode = false;
        arrow.setVisibility(View.INVISIBLE);
        actionsButton.setVisibility(View.VISIBLE);
    }

    private void openMissionInfoFragment() {
        MissionInfoFragment fragment = MissionInfoFragment.getInstance();
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.mission_fragment_holder, fragment, fragment.getClass().getCanonicalName())
                .commit();
    }

    private void startWatchMyTowersMode() {
        User user = CacheHandler.getUser();
        towersToWatch.clear();

        if (user.getTowersList() == null || user.getTowersList().size() == 0) {
            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setMessage(getString(R.string.toast_have_no_tower_to_watch))
                    .setPositiveButton("باشه", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    }).show();
            FontHelper.setKoodakFor(getActivity(), (TextView) dialog.findViewById(android.R.id.message));
            return;
        }

        revertButtonsState(false, false);
        for (Tower tower : user.getTowersList()) {
            towersToWatch.add(tower);
        }

        clearGoogleMap();
        watchMyTowersMode = true;
        showNextTowerToWatch();
    }

    private void finishWatchMyTowersMode() {
        revertButtonsState(true, false);
        innerCircleTower = outerCircleTower = null;
        clearGoogleMap();
        nowOnThisTower = null;
        watchMyTowersMode = false;
        requestForMap(CacheHandler.getLastLocation().getLatitude(), CacheHandler.getLastLocation().getLongitude(), false);
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

        if (innerCircleTower == null) {
            innerCircleTower = googleMap.addCircle(new CircleOptions()
                    .center(towerPlace)
                    .radius(200)
                    .fillColor(Color.parseColor("#33AAAAAA"))
                    .strokeColor(Color.parseColor("#00FFFFFF"))
            );

            outerCircleTower = googleMap.addCircle(new CircleOptions()
                    .center(towerPlace)
                    .radius(400)
                    .fillColor(Color.parseColor("#44AAAAAA"))
                    .strokeColor(Color.parseColor("#00FFFFFF"))
            );
        } else {
            innerCircleTower.setCenter(towerPlace);
            outerCircleTower.setCenter(towerPlace);
        }

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(towerPlace, MIN_ZOOM));

        showMapAroundTheTower(nextTower);
    }

    private void showMapAroundTheTower(Tower tower) {
        Call<MapResponse> call = VampireApp.createMapApi().getMapAroundTower(
                UserHandler.readToken(getActivity()),
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

    private void clearGoogleMap() {
        googleMap.clear();
        markers.clear();
        innerCircle = outerCircle = null;
    }

    public void addTowerNow(LatLng position) {
        Log.d("TAG", "abcd " + position.latitude + " " + position.longitude);
        if (addingTowerMode) {
            addingTowerMode = false;

            Call<ResponseBody> call = VampireApp.createMapApi().addTower(CacheHandler.getUser().getToken(), position.latitude, position.longitude);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    revertButtonsState(true, false);
                    clearGoogleMap();

                    if (response.isSuccessful()) {
                        String result = Utility.extractResult(response.body());

                        switch (result) {
                            case Consts.RESULT_OK:
                                Utility.makeToast(getActivity(), getString(R.string.toast_add_tower_ok), Toast.LENGTH_LONG);
                                GetProfile.run(getActivity());
                                break;

                            case Consts.RESULT_NOT_ENOUGH_MONEY:
                                redirectToStore();
                                break;

                            case Consts.RESULT_NOT_IN_RANGE:
                                if (VampireLocationManager.isGPSEnabled(getActivity())) {
                                    Utility.makeToast(getActivity(), getString(R.string.toast_add_tower_not_in_range), Toast.LENGTH_LONG);
                                } else {
                                    Utility.makeToast(getActivity(), getString(R.string.toast_add_tower_not_in_range_gps_disabled), Toast.LENGTH_LONG);
                                }
                                break;

                            case Consts.RESULT_RANGE_CONFLICT:
                                Utility.makeToast(getActivity(), getString(R.string.toast_add_tower_range_conflict), Toast.LENGTH_LONG);
                                break;
                        }
                    } else {
                        Utility.makeToast(getActivity(), getString(R.string.toast_please_try_again_later), Toast.LENGTH_SHORT);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    revertButtonsState(true, false);
                    clearGoogleMap();

                    Utility.makeToast(getActivity(), getString(R.string.toast_please_try_again_later), Toast.LENGTH_SHORT);
                }
            });
        }
    }

    private void showTargetOfCamera() {
        if (addingTowerMode) {
            if (sampleTowerMarker == null) {
                sampleTower.position(googleMap.getCameraPosition().target);
                sampleTower.title("SampleTower");
                sampleTower.icon(BitmapDescriptorFactory.fromResource(R.drawable.tower_add));
                sampleTowerMarker = googleMap.addMarker(sampleTower);
            } else {
                sampleTowerMarker.setPosition(googleMap.getCameraPosition().target);
            }
        }
    }

    private void finishHealMode() {
        healDialogShowTimes = 10000;
        healMode = false;
        googleMap.setMinZoomPreference(MIN_ZOOM);
        googleMap.setMaxZoomPreference(MAX_ZOOM);
        clearGoogleMap();
        requestForMap(CacheHandler.getLastLocation().getLatitude(), CacheHandler.getLastLocation().getLongitude(), true);
    }

    private void startHealMode(List<Place> places) {
        Log.d("TAG", "startHealMode");
        healMode = true;

        googleMap.setMinZoomPreference(MIN_ZOOM_HEAL_MODE);
        googleMap.setMaxZoomPreference(MAX_ZOOM_HEAL_MODE);

        clearGoogleMap();

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
        addMeToMap(CacheHandler.getLastLocation().getLatitude(), CacheHandler.getLastLocation().getLongitude(), MIN_ZOOM_HEAL_MODE, true);
    }

    private void addMeToMap(double lat, double lng, float zoomLevel, final boolean putDot) {
        LatLng newPlace = new LatLng(lat, lng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(newPlace).title(CacheHandler.getUser().getUsername());

        if (putDot) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.navigator));
        }

        if (myMarker == null) {
            myMarker = googleMap.addMarker(markerOptions);
        } else {
            myMarker.setPosition(newPlace);
        }
        myMarker.setTag(CacheHandler.getUser());

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

    private void startVirtualPurchase(String itemId) {
        String token = UserHandler.readToken(getActivity());
        Call<ResponseBody> call = VampireApp.createUserApi().virtualPurchase(
                token, itemId
        );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String result = Utility.extractResult(response.body());

                    switch (result) {
                        case Consts.RESULT_OK:
                            Utility.makeToast(getActivity(), getString(R.string.toast_virtual_purchase_ok), Toast.LENGTH_LONG);
                            finishHealMode();
                            GetProfile.run(getActivity());
                            break;

                        case Consts.RESULT_NOT_ENOUGH_MONEY:
                            Utility.makeToast(getActivity(), getString(R.string.toast_virtual_purchase_not_enough_money), Toast.LENGTH_LONG);
                            break;
                    }
                } else {
                    Utility.makeToast(getActivity(), getString(R.string.toast_please_try_again_later), Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Utility.makeToast(getActivity(), getString(R.string.toast_please_try_again_later), Toast.LENGTH_SHORT);
            }
        });
    }

    private void startHealDialog() {
        if (healDialog == null) {
            healDialog = new HealDialog(getActivity(), new OnCompleteListener() {
                @Override
                public void onComplete(Integer state) {
                    if (state == 1) {
                        startVirtualPurchase(Consts.VIRTUAL_STORE_HEAL);
                    } else {
                        healDialogShowTimes = 10000;
                    }
                }
            });
            healDialog.show();
            healDialogShowTimes++;
        } else {
            if (!healDialog.isShowing() && healDialogShowTimes < 2) {
                healDialog.show();
                healDialogShowTimes++;
            }
        }
    }

    private void handleHealNow(Place place) {
        Call<ResponseBody> call = VampireApp.createUserApi().healMe(
                CacheHandler.getUser().getToken(),
                CacheHandler.getUser().getGeo().get(0),
                CacheHandler.getUser().getGeo().get(1),
                place.getPlaceId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    String result = Utility.extractResult(response.body());

                    switch (result) {
                        case Consts.RESULT_OK:
                            Utility.makeToast(getActivity(), getString(R.string.toast_heal_ok), Toast.LENGTH_LONG);
                            finishHealMode();
                            break;

                        case Consts.RESULT_NOT_NEEDED:
                            Utility.makeToast(getActivity(), getString(R.string.toast_heal_not_needed), Toast.LENGTH_LONG);
                            finishHealMode();
                            break;

                        case Consts.RESULT_NOT_IN_RANGE:
                            Utility.makeToast(getActivity(), getString(R.string.toast_heal_not_in_range), Toast.LENGTH_LONG);
                            break;
                    }
                } else {
                    Utility.makeToast(getActivity(), getString(R.string.toast_please_try_again_later), Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Utility.makeToast(getActivity(), getString(R.string.toast_please_try_again_later), Toast.LENGTH_SHORT);
            }
        });
    }

    private void turnOnGPS() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setMessage("لطفا GPS خود را روشن کنید.\nوقتی GPS روشن نباشه نمی تونی بازی کنی. :(")
                .setPositiveButton("باشه", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("بی‌خیال", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (googleMap != null) {
                            googleMap.setMinZoomPreference(SHOW_IRAN_ZOOM_LEVEL);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(CENTER_OF_IRAN, SHOW_IRAN_ZOOM_LEVEL));
                        }
                    }
                }).show();
        dialog.setCancelable(false);
        FontHelper.setKoodakFor(getActivity(), (TextView) dialog.findViewById(android.R.id.message));
    }

    @Subscribe
    public void onEvent(TowerAddEvent event) {
        handleAddTower();
        AnalyticsManager.logEvent(AnalyticsManager.FAB_TAPPED, "add_tower");
    }

    @Subscribe
    public void onEvent(TowerWatchEvent event) {
        startWatchMyTowersMode();
        AnalyticsManager.logEvent(AnalyticsManager.FAB_TAPPED, "watch_towers");
    }

    @Subscribe
    public void onEvent(TowerCollectCoinsEvent event) {
        startCollectCoinsMode();
        AnalyticsManager.logEvent(AnalyticsManager.FAB_TAPPED, "collect_coin");
    }

    @Subscribe
    public void onEvent(PlacesResponse response) {
        Log.d("TAG", "onEvent PlacesResponse " + response.getPlaces().size());
        startHealMode(response.getPlaces());
    }

    @Subscribe
    public void onEvent(StartNearestMissionEvent event) {
        getActivity().getSupportFragmentManager().popBackStack();
        getActivity().getSupportFragmentManager().popBackStack();

        GetNearest.run(getActivity(), event.getTargetType());
    }

    @Subscribe
    public void onEvent(FinishNearestMissionEvent event) {
        finishMissionMode();
    }

    @Subscribe
    public void onEvent(NearestResponseEvent event) {
        Log.d("TAG", "NearestResponseEvent " + event.getFoundNearest());
        if (event.getFoundNearest()) {
            Log.d("TAG", "NearestResponseEvent " + event.getNearestObject().getTarget().getType());
//            String msg = "" + event.getNearestObject().getDistance() + " " + event.getNearestObject().getDirection();
//            Utility.makeToast(getActivity(), msg, Toast.LENGTH_LONG);

            float degrees = event.getNearestObject().getDirection().floatValue() * (float) (180 / Math.PI);
            degrees *= -1;
            arrow.setVisibility(View.VISIBLE);
            arrow.setRotation(degrees);

            startMissionMode();

        } else {
            Utility.makeToast(getActivity(), "Not Found", Toast.LENGTH_LONG);
        }
    }

    @Subscribe
    public void onEvent(GetProfileEvent event) {
        Log.d("TAG", "onEvent GetProfileEvent " + event.isSuccessfull());
        if (event.isSuccessfull()) {
            updateView(event.getUser());
        }
    }

    @Subscribe
    public void onEvent(RefreshAroundTowerEvent event) {
        if (nowOnThisTower != null) {
            showMapAroundTheTower(nowOnThisTower);
        }
    }

    @Subscribe
    public void onEvent(FinishHealMode event) {
        finishHealMode();
    }


}
