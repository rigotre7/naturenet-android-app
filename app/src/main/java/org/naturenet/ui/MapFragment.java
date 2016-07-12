package org.naturenet.ui;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.common.collect.Lists;

import org.naturenet.BuildConfig;
import org.naturenet.R;
import org.naturenet.data.model.Observation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private Logger mLogger = LoggerFactory.getLogger(MapFragment.class);
    private MapView mMapView;
    private GoogleMap mMap;
    private List<Observation> mVisibleObservations = Lists.newArrayList();
    public MapFragment() {}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//         SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        // mapFragment.getMapAsync(this);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = (MapView) root.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try { MapsInitializer.initialize(getActivity().getApplicationContext()); }
        catch (Exception e) { mLogger.error("Failed to initialize map view: {}", e); }
        mMapView.getMapAsync(this);
//        root.findViewById(R.id.explore_b_explore).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((MainActivity)getActivity()).showGallery();
//            }
//        });
        return root;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // Default to roughly the center of the sites
        LatLng center = new LatLng(39.196, -106.824);
        CameraPosition position = new CameraPosition.Builder().target(center).zoom(16).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        readObservations();
    }
    private void readObservations() {
//        mLogger.info("Getting observations");
//        mFirebase.child(Observation.NODE_NAME).orderByChild("updated_at").limitToFirst(10).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                for(DataSnapshot child : snapshot.getChildren()) {
//                    Observation obs = child.getValue(Observation.class);
//                    mVisibleObservations.add(obs);
//                    MarkerOptions mark = new MarkerOptions().position(obs.getLocation()).icon(BitmapDescriptorFactory.defaultMarker());
//                    mMap.addMarker(mark);
//                }
//            }
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//                mLogger.error("Failed to read Observations: {}", firebaseError);
//                Toast.makeText(getActivity(), "Could not get observations", Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}