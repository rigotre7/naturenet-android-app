package org.naturenet.ui;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.base.Strings;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.naturenet.CroppedCircleTransformation;
import org.naturenet.R;
import org.naturenet.data.model.Observation;
import org.naturenet.data.model.PreviewInfo;
import org.naturenet.data.model.Site;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class ExploreFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    final private static int CAMERA_REQUEST = 1;
    final private static int GALLERY_REQUEST = 2;
    static String LATITUDE = "0";
    static String LONGITUDE = "1";
    static String MY_LOCATION = "My Location";
    static String OBSERVATION = "Observation";
    ImageButton add_observation, add_design_idea;
    Button explore, camera, gallery, design_ideas, design_challenges;
    TextView preview_observer_user_name, preview_observer_affiliation, preview_observation_text, preview_likes_count, preview_comments_count, select, add_observation_cancel, add_design_idea_cancel;
    LinearLayout dialog_preview, dialog_add_observation, dialog_add_design_idea;
    FrameLayout floating_buttons;
    GridView gridview;
    ImageView preview_cancel, preview_observation_image, preview_observer_avatar, gallery_item;
    MainActivity main;
    List<Uri> recentImageGallery;
    Uri selectedImage;
    double latValue, longValue;
    CameraPhoto cameraPhoto;
    GalleryPhoto galleryPhoto;
    MapView mMapView;
    GoogleMap googleMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest locationRequest;
    Observation previewSelectedObservation;
    Transformation mAvatarTransform = new CroppedCircleTransformation();
    private Map<Marker, PreviewInfo> allMarkersMap = new HashMap<Marker, PreviewInfo>();

    public ExploreFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = ((MainActivity) this.getActivity());
        Site home = main.user_home_site;
        if(home != null) {
            latValue = home.location.get(0);
            longValue = home.location.get(1);
        } else {
            latValue = 0;
            longValue = 0;
        }
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(main)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            locationRequest = new LocationRequest();
            locationRequest.setInterval(10 * 1000);
            locationRequest.setFastestInterval(1 * 1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                    final Status status = locationSettingsResult.getStatus();
                    if (status.getStatusCode() != LocationSettingsStatusCodes.SUCCESS) {
                        zoomToUser();
                    }
                }
            });
        }
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
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                // Default to CONUS until we get location info
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(40, -96), 3));
                for (int i = 0; i < main.observations.size(); i++) {
                    final Observation observation = main.observations.get(i);
                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(observation.getL().get(LATITUDE), observation.getL().get(LONGITUDE)))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    allMarkersMap.put(marker, main.previews.get(observation));
                }
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if (!MY_LOCATION.equals(marker.getTitle())) {
                            PreviewInfo preview = allMarkersMap.get(marker);
                            for (Observation observation : main.previews.keySet()) {
                                if (main.previews.get(observation).equals(preview)) {
                                    previewSelectedObservation = observation;
                                    break;
                                }
                            }
                            Picasso.with(main).load(Strings.emptyToNull(preview.observationImageUrl)).fit().centerCrop().into(preview_observation_image);
                            Picasso.with(main).load(Strings.emptyToNull(preview.observerAvatarUrl))
                                    .transform(mAvatarTransform).fit().into(preview_observer_avatar);
                            preview_observer_user_name.setText(preview.observerName);
                            preview_observer_affiliation.setText(preview.affiliation);
                            preview_observation_text.setText(preview.observationText);
                            preview_likes_count.setText(preview.likesCount);
                            preview_comments_count.setText(preview.commentsCount);
                            floating_buttons.setVisibility(View.GONE);
                            explore.setVisibility(View.GONE);
                            dialog_preview.setVisibility(View.VISIBLE);
                        } else {
                            if (dialog_preview.getVisibility() == View.VISIBLE) {
                                floating_buttons.setVisibility(View.VISIBLE);
                                explore.setVisibility(View.VISIBLE);
                                dialog_preview.setVisibility(View.GONE);
                            }
                        }
                        return false;
                    }
                });
            }
        });
        return v;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(main, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (!googleMap.isMyLocationEnabled()) {
            if (ContextCompat.checkSelfPermission(main, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
            }

            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                    .title(MY_LOCATION));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12.0f));
        }
        latValue = location.getLatitude();
        longValue = location.getLongitude();
    }

    private void zoomToUser() {
        if(latValue != 0.0 && longValue != 0.0) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latValue, longValue), 10));
        }
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
        mMapView.onResume();
        if (mGoogleApiClient.isConnected())
            requestLocationUpdates();
        super.onResume();
    }

    @Override
    public void onPause() {
        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (dialog_preview != null) {
            dialog_preview = null;
        }
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
        dialog_preview = (LinearLayout) main.findViewById(R.id.ll_dialog_preview);
        preview_cancel = (ImageView) main.findViewById(R.id.preview_cancel);
        preview_observation_image = (ImageView) main.findViewById(R.id.preview_observation_iv);
        preview_observer_avatar = (ImageView) main.findViewById(R.id.preview_observer_avatar);
        preview_observer_user_name = (TextView) main.findViewById(R.id.preview_observer_user_name);
        preview_observer_affiliation = (TextView) main.findViewById(R.id.preview_observer_affiliation);
        preview_observation_text = (TextView) main.findViewById(R.id.preview_observation_text);
        preview_likes_count = (TextView) main.findViewById(R.id.preview_likes_count);
        preview_comments_count = (TextView) main.findViewById(R.id.preview_comments_count);
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
        preview_observation_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.previewSelectedObservation = previewSelectedObservation;
                main.goToObservationActivity();
            }
        });
        preview_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floating_buttons.setVisibility(View.VISIBLE);
                explore.setVisibility(View.VISIBLE);
                dialog_preview.setVisibility(View.GONE);
            }
        });
        add_observation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(main, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(main, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY_REQUEST);
                else
                    setGallery();
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
                if (recentImageGallery != null) {
                    int index = recentImageGallery.indexOf(selectedImage);
                    if (index >= 0) {
                        gridview.getChildAt(index).findViewById(R.id.gallery_iv).setBackgroundResource(0);
                    }
                }
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
        dialog_preview.setVisibility(View.GONE);
    }

    public void setGallery() {
        recentImageGallery = main.getRecentImagesUris();
        if (recentImageGallery.size() != 0) {
            gridview.setAdapter(new ImageGalleryAdapter(main, recentImageGallery));
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    ImageView iv = (ImageView) v.findViewById(R.id.gallery_iv);
                    if (selectedImage == null) {
                        selectedImage = recentImageGallery.get(position);
                        iv.setBackground(getResources().getDrawable(R.drawable.border_selected_image));
                        select.setVisibility(View.VISIBLE);
                    } else if (selectedImage.equals(recentImageGallery.get(position))) {
                        selectedImage = null;
                        iv.setBackgroundResource(0);
                        select.setVisibility(View.GONE);
                    } else {
                        int index = recentImageGallery.indexOf(selectedImage);
                        if (index >= 0) {
                            gridview.getChildAt(index).findViewById(R.id.gallery_iv).setBackgroundResource(0);
                        }
                        selectedImage = recentImageGallery.get(position);
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
        if (resultCode == MainActivity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                Timber.d("Camera Path: %s", cameraPhoto.getPhotoPath());
                main.observationPath = Uri.fromFile(new File(cameraPhoto.getPhotoPath()));
                cameraPhoto.addToGallery();
            } else if (requestCode == GALLERY_REQUEST) {
                galleryPhoto.setPhotoUri(data.getData());
                Timber.d("Gallery Path: %s", galleryPhoto.getPath());
                main.observationPath = Uri.fromFile(new File(galleryPhoto.getPath()));
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setGallery();
                } else {
                    Toast.makeText(main, "Gallery Access Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case CAMERA_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setGallery();
                } else {
                    Toast.makeText(main, "Camera Access Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}