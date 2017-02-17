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
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.squareup.picasso.Picasso;

import org.naturenet.R;
import org.naturenet.data.model.Observation;
import org.naturenet.data.model.Site;
import org.naturenet.data.model.Users;
import org.naturenet.util.NatureNetUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class ExploreFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String FRAGMENT_TAG = "explore_fragment";
    private static final String ARG_SITE = "arg_site";
    static final private int CAMERA_REQUEST = 1;
    static final private int GALLERY_REQUEST = 2;

    ImageButton add_observation, add_design_idea;
    Button camera, gallery, design_ideas, design_challenges;
    TextView toolbar_title, preview_observer_user_name, preview_observer_affiliation, preview_observation_text, preview_likes_count, preview_comments_count, select;
    LinearLayout dialog_preview, dialog_add_observation, dialog_add_design_idea;
    FrameLayout floating_buttons;
    GridView gridview;
    ImageView add_observation_cancel, add_design_idea_cancel, preview_cancel, preview_observation_image, preview_observer_avatar, gallery_item;
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

    private Query mQuery = FirebaseDatabase.getInstance()
            .getReference(Observation.NODE_NAME).orderByChild("updated_at").limitToLast(20);

    private final ChildEventListener mObservationListener = new ChildEventListener() {

        private Map<String, Marker> mMapMarkers = new HashMap<>();

        private void addMarker(String key, Observation obs) {
            if (googleMap != null) {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_observation);
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(obs.location.get(0), obs.location.get(1)))
                        .icon(icon));
                marker.setTag(obs);
                mMapMarkers.put(key, marker);
            }
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String key) {
            Observation obs = dataSnapshot.getValue(Observation.class);
            addMarker(key, obs);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String key) {
            Observation obs = dataSnapshot.getValue(Observation.class);
            mMapMarkers.remove(key);
            addMarker(key, obs);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Observation obs = dataSnapshot.getValue(Observation.class);
            String key = obs.id;
            if (mMapMarkers.containsKey(key)) {
                mMapMarkers.get(key).remove();
            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String key) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Timber.w(databaseError.toException(), "Observation listener canceled");
        }
    };

    public static ExploreFragment newInstance(Site site) {
        ExploreFragment frag = new ExploreFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SITE, site);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Site home = getArguments().getParcelable(ARG_SITE);
        if(home != null) {
            latValue = home.location.get(0);
            longValue = home.location.get(1);
        } else {
            latValue = 0;
            longValue = 0;
        }

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
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
                        ExploreFragment.this.zoomToUser();
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

                mQuery.addChildEventListener(mObservationListener);

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if (!ExploreFragment.this.getString(R.string.explore_my_location).equals(marker.getTitle())) {
                            previewSelectedObservation = (Observation)marker.getTag();
                            showPreview();
                        } else {
                            hidePreview();
                        }

                        return false;
                    }
                });
            }
        });

        return v;
    }

    private void showPreview() {

        Picasso.with(getActivity()).load(Strings.emptyToNull(previewSelectedObservation.data.image))
                .fit().centerCrop().into(preview_observation_image);

        preview_observation_text.setText(previewSelectedObservation.data.text);
        String likes = (previewSelectedObservation.likes == null) ? "0" : String.valueOf(previewSelectedObservation.likes.size());
        preview_likes_count.setText(likes);
        String comments = (previewSelectedObservation.comments == null) ? "0" : String.valueOf(previewSelectedObservation.comments.size());
        preview_comments_count.setText(comments);

        FirebaseDatabase.getInstance().getReference(Users.NODE_NAME).child(previewSelectedObservation.userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final Users user = dataSnapshot.getValue(Users.class);
                        NatureNetUtils.showUserAvatar(getActivity(), preview_observer_avatar, user.avatar);
                        if (user.displayName != null) {
                            preview_observer_user_name.setText(user.displayName);
                        } else {
                            preview_observer_user_name.setText(R.string.unknown_user);
                        }

                        FirebaseDatabase.getInstance().getReference(Site.NODE_NAME).child(user.affiliation)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Site site = dataSnapshot.getValue(Site.class);
                                        if (site.name != null) {
                                            preview_observer_affiliation.setText(site.name);
                                        } else {
                                            preview_observer_affiliation.setText(null);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Timber.w(databaseError.toException(), "Unable to read data for site %s", user.affiliation);
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Timber.w(databaseError.toException(), "Unable to read data for user %s", previewSelectedObservation.userId);
                    }
                });

        floating_buttons.setVisibility(View.GONE);
        dialog_preview.setVisibility(View.VISIBLE);
    }

    private void hidePreview() {
        if (dialog_preview.getVisibility() == View.VISIBLE) {
            floating_buttons.setVisibility(View.VISIBLE);
            dialog_preview.setVisibility(View.GONE);
        }
        preview_observer_avatar.setImageDrawable(null);
        preview_observer_user_name.setText(null);
        preview_observer_affiliation.setText(null);
        preview_observation_image.setImageDrawable(null);
        preview_comments_count.setText(null);
        preview_likes_count.setText(null);
        preview_observation_text.setText(null);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location) {
        if (!googleMap.isMyLocationEnabled()) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
            }

            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                    .title(getString(R.string.explore_my_location)));
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
        if (mGoogleApiClient.isConnected()) { requestLocationUpdates(); }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        dialog_preview = null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        floating_buttons = (FrameLayout) view.findViewById(R.id.fl_floating_buttons);
        add_observation = (ImageButton) view.findViewById(R.id.floating_buttons_ib_add_observation);
        add_design_idea = (ImageButton) view.findViewById(R.id.floating_buttons_ib_add_design_idea);
        dialog_preview = (LinearLayout) view.findViewById(R.id.ll_dialog_preview);
        preview_cancel = (ImageView) view.findViewById(R.id.preview_cancel);
        preview_observation_image = (ImageView) view.findViewById(R.id.preview_observation_iv);
        preview_observer_avatar = (ImageView) view.findViewById(R.id.preview_observer_avatar);
        preview_observer_user_name = (TextView) view.findViewById(R.id.preview_observer_user_name);
        preview_observer_affiliation = (TextView) view.findViewById(R.id.preview_observer_affiliation);
        preview_observation_text = (TextView) view.findViewById(R.id.preview_observation_text);
        preview_likes_count = (TextView) view.findViewById(R.id.preview_likes_count);
        preview_comments_count = (TextView) view.findViewById(R.id.preview_comments_count);
        dialog_add_observation = (LinearLayout) view.findViewById(R.id.ll_dialog_add_observation);
        add_observation_cancel = (ImageView) view.findViewById(R.id.dialog_add_observation_iv_cancel);
        camera = (Button) view.findViewById(R.id.dialog_add_observation_b_camera);
        gallery = (Button) view.findViewById(R.id.dialog_add_observation_b_gallery);
        select = (TextView) view.findViewById(R.id.dialog_add_observation_tv_select);
        gridview = (GridView) view.findViewById(R.id.dialog_add_observation_gv);
        gallery_item = (ImageView) view.findViewById(R.id.gallery_iv);
        dialog_add_design_idea = (LinearLayout) view.findViewById(R.id.ll_dialog_add_design_idea);
        add_design_idea_cancel = (ImageView) view.findViewById(R.id.dialog_add_design_idea_iv_cancel);
        design_ideas = (Button) view.findViewById(R.id.dialog_add_design_idea_b_design_ideas);
        design_challenges = (Button) view.findViewById(R.id.dialog_add_design_idea_b_design_challenges);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        main = ((MainActivity) this.getActivity());
        toolbar_title = (TextView) main.findViewById(R.id.app_bar_main_tv);
        toolbar_title.setText(R.string.explore_title);

        cameraPhoto = new CameraPhoto(getActivity());
        galleryPhoto = new GalleryPhoto(getActivity());

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
                dialog_preview.setVisibility(View.GONE);
            }
        });

        add_observation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY_REQUEST);
                } else {
                    ExploreFragment.this.setGallery();
                    select.setVisibility(View.GONE);
                    floating_buttons.setVisibility(View.GONE);
                    dialog_add_observation.setVisibility(View.VISIBLE);
                }
            }
        });

        add_design_idea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floating_buttons.setVisibility(View.GONE);
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
                dialog_add_observation.setVisibility(View.GONE);
            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.observationPath = selectedImage;
                main.newObservation = new Observation();
                main.newObservation.location = Lists.newArrayList(latValue, longValue);
                ExploreFragment.this.setGallery();
                main.goToAddObservationActivity();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExploreFragment.this.setGallery();
                select.setVisibility(View.GONE);
                try {
                    ExploreFragment.this.startActivityForResult(cameraPhoto.takePhotoIntent(), CAMERA_REQUEST);
                } catch (IOException e) {
                    Toast.makeText(main, "Something Wrong while taking photo", Toast.LENGTH_SHORT).show();
                }
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExploreFragment.this.setGallery();
                select.setVisibility(View.GONE);
                ExploreFragment.this.startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST);
            }
        });

        add_design_idea_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floating_buttons.setVisibility(View.VISIBLE);
                dialog_add_design_idea.setVisibility(View.GONE);
            }
        });

        floating_buttons.setVisibility(View.VISIBLE);
        dialog_add_observation.setVisibility(View.GONE);
        dialog_add_design_idea.setVisibility(View.GONE);
        dialog_preview.setVisibility(View.GONE);
    }

    public void setGallery() {
        recentImageGallery = main.getRecentImagesUris();

        if (recentImageGallery.size() != 0) {
            gridview.setAdapter(new ImageGalleryAdapter(main, recentImageGallery));
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    ImageView iv = (ImageView) v.findViewById(R.id.gallery_iv);

                    if (selectedImage == null) {
                        selectedImage = recentImageGallery.get(position);
                        iv.setBackground(ContextCompat.getDrawable(ExploreFragment.this.getActivity(), R.drawable.border_selected_image));
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
                        iv.setBackground(ContextCompat.getDrawable(ExploreFragment.this.getActivity(), R.drawable.border_selected_image));
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
            main.newObservation.location = Lists.newArrayList(latValue, longValue);
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