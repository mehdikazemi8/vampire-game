package ir.ugstudio.vampire.views.activities.main.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.views.dialogs.AttackDialog;

public class MapFragment extends Fragment
    implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap googleMap;
    private final int MIN_ZOOM = 16;
    private final int MAX_ZOOM = 17;

    public static MapFragment getInstance() {
        return new MapFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
}
