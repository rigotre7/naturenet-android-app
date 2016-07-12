package org.naturenet.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;

import org.naturenet.R;
import org.naturenet.data.model.Observation;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExploreFragment extends Fragment {
    final private static int CAMERA_REQUEST = 1;
    final private static int GALLERY_REQUEST = 2;
    final private static int GV_IMAGE_WIDTH = 240;
    final private static int GV_IMAGE_HEIGHT = 240;
    static String LATITUDE = "0";
    static String LONGITUDE = "1";
    ImageButton add_observation, add_design_idea;
    Button explore, camera, gallery, design_ideas, design_challenges;
    TextView select, add_observation_cancel, add_design_idea_cancel;
    LinearLayout dialog_add_observation, dialog_add_design_idea;
    FrameLayout floating_buttons;
    GridView gridview;
    ImageView gallery_item;
    MainActivity main;
    List<String> galleryLatestList;
    String[] galleryLatest;
    String selectedImage;
    double latValue, longValue;
    CameraPhoto cameraPhoto;
    GalleryPhoto galleryPhoto;
    MapView mMapView;
    private GoogleMap googleMap;
    public ExploreFragment() {}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = ((MainActivity) this.getActivity());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_explore, container, false);
        mMapView = (MapView) v.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        googleMap = mMapView.getMap();
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(main);
        if (status != ConnectionResult.SUCCESS)
            GooglePlayServicesUtil.getErrorDialog(status, main, 10).show();
        else {
            try {
                googleMap.setMyLocationEnabled(true);

                LocationManager locationManager = (LocationManager) main.getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                String provider = locationManager.getBestProvider(criteria, true);
//                if (ActivityCompat.checkSelfPermission(main, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(main, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Location myLocation = locationManager.getLastKnownLocation(provider);
                    googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    latValue = myLocation.getLatitude();
                    longValue = myLocation.getLongitude();
                    LatLng latLng = new LatLng(latValue, longValue);
                    MarkerOptions marker = new MarkerOptions().position(latLng).title("My Location");
                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                    googleMap.addMarker(marker);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
//                }

//                googleMap.setOnMyLocationChangeListener(this);
            } catch (SecurityException e) {}
        }
        return v;
    }
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        explore = (Button) main.findViewById(R.id.explore_b_explore);
        floating_buttons = (FrameLayout) main.findViewById(R.id.fl_floating_buttons);
        add_observation = (ImageButton) main.findViewById(R.id.floating_buttons_ib_add_observation);
        add_design_idea = (ImageButton) main.findViewById(R.id.floating_buttons_ib_add_design_idea);
        dialog_add_observation = (LinearLayout) main.findViewById(R.id.ll_dialog_add_observation);
        add_observation_cancel = (TextView) main.findViewById(R.id.dialog_add_observation_tv_cancel);
        camera = (Button) main.findViewById(R.id.dialog_add_observation_b_camera);
        gallery = (Button) main.findViewById(R.id.dialog_add_observation_b_gallery);
        select = (TextView) main.findViewById(R.id.dialog_add_observation_tv_select);
        gridview = (GridView) main.findViewById(R.id.dialog_add_observation_gv);
        gallery_item = (ImageView) main.findViewById(R.id.gallery_iv);
        dialog_add_design_idea = (LinearLayout) main.findViewById(R.id.ll_dialog_add_design_idea);
        add_design_idea_cancel = (TextView) main.findViewById(R.id.dialog_add_design_idea_tv_cancel);
        design_ideas = (Button) main.findViewById(R.id.dialog_add_design_idea_b_design_ideas);
        design_challenges = (Button) main.findViewById(R.id.dialog_add_design_idea_b_design_challenges);
        cameraPhoto = new CameraPhoto(main);
        galleryPhoto = new GalleryPhoto(main);
        explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.goToObservationActivity();
            }
        });
        add_observation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = ContextCompat.checkSelfPermission(main, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(main, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY_REQUEST);
                } else {
                    setGallery();
                }
                select.setVisibility(View.GONE);
                floating_buttons.setVisibility(View.GONE);
                explore.setVisibility(View.GONE);
                dialog_add_observation.setVisibility(View.VISIBLE);
            }
        });
        add_design_idea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floating_buttons.setVisibility(View.GONE);
                explore.setVisibility(View.GONE);
                dialog_add_design_idea.setVisibility(View.VISIBLE);
            }
        });
        add_observation_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < galleryLatest.length; i++)
                    if (galleryLatest[i].equals(selectedImage))
                        gridview.getChildAt(i).findViewById(R.id.gallery_iv).setBackgroundResource(0);
                selectedImage = null;
                select.setVisibility(View.GONE);
                floating_buttons.setVisibility(View.VISIBLE);
                explore.setVisibility(View.VISIBLE);
                dialog_add_observation.setVisibility(View.GONE);
            }
        });
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.observationPath = selectedImage;
                try {
                    Bitmap bitmap = ImageLoader.init().from(main.observationPath).requestSize(512, 512).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    main.observationBitmap = stream.toByteArray();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                main.newObservation = new Observation();
                Map<String, Double> latLong = new HashMap<String, Double>();
                double latitude = latValue;
                double longitude = longValue;
                latLong.put(LATITUDE, latitude);
                latLong.put(LONGITUDE, longitude);
                main.newObservation.setL(latLong);
                setGallery();
                main.goToAddObservationActivity();
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGallery();
                select.setVisibility(View.GONE);
                try {
                    startActivityForResult(cameraPhoto.takePhotoIntent(), CAMERA_REQUEST);
                } catch (IOException e) {
                    Toast.makeText(main, "Something Wrong while taking photo", Toast.LENGTH_SHORT).show();
                }
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGallery();
                select.setVisibility(View.GONE);
                startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST);
            }
        });
        add_design_idea_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floating_buttons.setVisibility(View.VISIBLE);
                explore.setVisibility(View.VISIBLE);
                dialog_add_design_idea.setVisibility(View.GONE);
            }
        });
        design_ideas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        design_challenges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        floating_buttons.setVisibility(View.VISIBLE);
        explore.setVisibility(View.VISIBLE);
        dialog_add_observation.setVisibility(View.GONE);
        dialog_add_design_idea.setVisibility(View.GONE);
    }
//    @Override
//    public void onMyLocationChange(Location location) {
//        double longitude = location.getLongitude();
//        double latitude = location.getLatitude();
//        Log.d("My Location", "Latitude: " + Double.toString(latitude) + ", Longitude: " + Double.toString(longitude));
//        LatLng latLng = new LatLng(latitude, longitude);
//        MarkerOptions marker = new MarkerOptions().position(latLng).title("My Location");
//        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
//        googleMap.addMarker(marker);
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
//        latValue = latitude;
//        longValue = longitude;
//    }
    private class ImageAdapter extends BaseAdapter {
        private Context mContext;
        public ImageAdapter(Context c) {
            mContext = c;
        }
        public int getCount() {
            return galleryLatest.length;
        }
        public Object getItem(int position) {
            return null;
        }
        public long getItemId(int position) {
            return 0;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) main.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gallery_gv_item, parent, false);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.gallery_iv);
            imageView.setLayoutParams(new GridView.LayoutParams(GV_IMAGE_WIDTH, GV_IMAGE_HEIGHT));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//            imageView.setImageBitmap(BitmapFactory.decodeFile(galleryLatest[position]));
            imageView.setImageBitmap(main.decodeURI(galleryLatest[position]));
            imageView.setPadding(10,10,10,10);
            imageView.setBackgroundResource(0);
            return imageView;
        }
    }
    public void setGallery() {
        galleryLatestList = main.getAllShownImagesPath();
        if (galleryLatestList.size() != 0) {
            galleryLatest = galleryLatestList.toArray(new String[galleryLatestList.size()]);
            gridview.setAdapter(new ImageAdapter(main));
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    ImageView iv = (ImageView) v.findViewById(R.id.gallery_iv);
                    if (selectedImage == null) {
                        selectedImage = galleryLatest[position];
                        iv.setBackground(getResources().getDrawable(R.drawable.border_selected_image));
                        select.setVisibility(View.VISIBLE);
                    } else if (selectedImage.equals(galleryLatest[position])) {
                        selectedImage = null;
                        iv.setBackgroundResource(0);
                        select.setVisibility(View.GONE);
                    } else {
                        for (int i=0; i<galleryLatest.length; i++)
                            if (galleryLatest[i].equals(selectedImage))
                                gridview.getChildAt(i).findViewById(R.id.gallery_iv).setBackgroundResource(0);
                        selectedImage = galleryLatest[position];
                        iv.setBackground(getResources().getDrawable(R.drawable.border_selected_image));
                        select.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == main.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                main.observationPath = cameraPhoto.getPhotoPath();
                cameraPhoto.addToGallery();
            } else if (requestCode == GALLERY_REQUEST) {
                galleryPhoto.setPhotoUri(data.getData());
                main.observationPath = galleryPhoto.getPath();
            }
            try {
                Bitmap bitmap = ImageLoader.init().from(main.observationPath).requestSize(512, 512).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                main.observationBitmap = stream.toByteArray();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            main.newObservation = new Observation();
            Map<String, Double> latLong =  new HashMap<String, Double>();
            double latitude = latValue;
            double longitude = longValue;
            latLong.put(LATITUDE, latitude);
            latLong.put(LONGITUDE, longitude);
            main.newObservation.setL(latLong);
            setGallery();
            main.goToAddObservationActivity();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if ((requestCode == GALLERY_REQUEST) && (grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            setGallery();
        } else if ((requestCode == CAMERA_REQUEST) && (grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            setGallery();
        }
    }
//    @Override
//    public boolean onMarkerClick(Marker marker) {
//        return true;
//    }
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        mMap.getUiSettings().setMapToolbarEnabled(false);
//        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//        // Default to roughly the center of the sites
//        LatLng center = new LatLng(39.196, -106.824);
//        CameraPosition position = new CameraPosition.Builder().target(center).zoom(16).build();
//        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
//        readObservations();
//    }
//    private void readObservations() {
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
//    }
}