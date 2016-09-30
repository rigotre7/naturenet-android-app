package org.naturenet.ui;

import android.Manifest;
import android.app.Fragment;
import android.app.ProgressDialog;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.common.collect.Lists;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;

import org.naturenet.R;
import org.naturenet.data.model.Observation;
import org.naturenet.data.model.Project;
import org.naturenet.data.model.Site;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class ProjectsFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    final private static int CAMERA_REQUEST = 1;
    final private static int GALLERY_REQUEST = 2;
    static String LATEST_CONTRIBUTION = "latest_contribution";
    static String LOADING_PROJECTS = "Loading Projects...";
    static String LATITUDE = "0";
    static String LONGITUDE = "1";
    MainActivity main;
    ProgressDialog pd;
    private ListView mProjectsListView = null;
    private List<Project> mProjects = Lists.newArrayList();
    private DatabaseReference mFirebase = FirebaseDatabase.getInstance().getReference();
    ImageButton add_observation, add_design_idea;
    Button camera, gallery, design_ideas, design_challenges;
    TextView select, add_observation_cancel, add_design_idea_cancel;
    LinearLayout dialog_add_observation, dialog_add_design_idea;
    FrameLayout floating_buttons;
    GridView gridview;
    ImageView gallery_item;
    List<Uri> recentImageGallery;
    Uri selectedImage;
    double latValue, longValue;
    GoogleApiClient mGoogleApiClient;
    LocationRequest locationRequest;
    CameraPhoto cameraPhoto;
    GalleryPhoto galleryPhoto;
    public ProjectsFragment() {}
    public static ProjectsFragment newInstance() {
        ProjectsFragment fragment = new ProjectsFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_projects, container, false);
        main = ((MainActivity) getActivity());
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
            locationRequest.setInterval(10*1000);
            locationRequest.setFastestInterval(1*1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        }
        mProjectsListView = (ListView) root.findViewById(R.id.projects_list);
        pd = new ProgressDialog(main);
        readProjects();
        return root;
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
    public void onConnectionSuspended(int i) {}
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
    @Override
    public void onLocationChanged(Location location) {
        latValue = location.getLatitude();
        longValue = location.getLongitude();
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
        if (mGoogleApiClient.isConnected())
            requestLocationUpdates();
        super.onResume();
    }
    @Override
    public void onPause() {
        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        super.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
        add_observation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(main, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(main, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY_REQUEST);
                else
                    setGallery();
                select.setVisibility(View.GONE);
                floating_buttons.setVisibility(View.GONE);
                dialog_add_observation.setVisibility(View.VISIBLE);
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
                setGallery();
                main.goToAddObservationActivity();
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGallery();
                select.setVisibility(View.GONE);
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                fileUri = getOutputMediaFileUri(1);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//                startActivityForResult(intent, CAMERA_REQUEST);
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
                dialog_add_design_idea.setVisibility(View.GONE);
            }
        });
        design_ideas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });
        design_challenges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });
        floating_buttons.setVisibility(View.VISIBLE);
        dialog_add_observation.setVisibility(View.GONE);
        dialog_add_design_idea.setVisibility(View.GONE);
        mProjectsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                main.goToProjectActivity(mProjects.get(position));
            }
        });
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
            main.newObservation.location = Lists.newArrayList(latValue, longValue);
            setGallery();
            main.goToAddObservationActivity();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    setGallery();
                else
                    Toast.makeText(main, "Gallery Access Permission Denied", Toast.LENGTH_SHORT).show();
                break;
            case CAMERA_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    setGallery();
                else
                    Toast.makeText(main, "Camera Access Permission Denied", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
    private void readProjects() {
        Timber.d("Getting projects");
        pd.setMessage(LOADING_PROJECTS);
        pd.setCancelable(false);
        pd.show();
        mFirebase.child(Project.NODE_NAME).orderByChild(LATEST_CONTRIBUTION).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Timber.d("Got projects, count: %d", snapshot.getChildrenCount());
                for(DataSnapshot child : snapshot.getChildren()) {
                    Project project = child.getValue(Project.class);
                    mProjects.add(project);
                }
                // Timestamps sort in ascending order
                Collections.reverse(mProjects);
                if (mProjects.size() != 0) {
                    mProjectsListView.setAdapter(new ProjectAdapter(main, mProjects));
                }
                pd.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.e("Failed to read Projects: %s", databaseError.getMessage());
                pd.dismiss();
                Toast.makeText(main, "Could not get projects: "+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}