package org.naturenet.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.kosalgeek.android.photoutil.ImageLoader;

import org.naturenet.R;
import org.naturenet.data.model.Observation;
import org.naturenet.data.model.Project;
import org.naturenet.data.model.Site;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class ProjectsFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    final private static int CAMERA_REQUEST = 1;
    final private static int GALLERY_REQUEST = 2;
    final private static int GV_IMAGE_WIDTH = 240;
    final private static int GV_IMAGE_HEIGHT = 240;
    static String ID = "id";
    static String ICON_URL = "icon_url";
    static String DESCRIPTION = "description";
    static String NAME = "name";
    static String STATUS = "status";
    static String LATEST_CONTRIBUTION = "latest_contribution";
    static String CREATED_AT = "created_at";
    static String UPDATED_AT = "updated_at";
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
    List<String> galleryLatestList;
    String[] galleryLatest;
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
            latValue = home.l.get(0);
            longValue = home.l.get(1);
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
        final LocationManager manager = (LocationManager) main.getSystemService( Context.LOCATION_SERVICE );
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(main);
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
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
                if (galleryLatest != null)
                    for (int i = 0; i < galleryLatest.length; i++)
                        if (galleryLatest[i].equals(selectedImage))
                            gridview.getChildAt(i).findViewById(R.id.gallery_iv).setBackgroundResource(0);
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
                        selectedImage = Uri.fromFile(new File(galleryLatest[position]));
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
                        selectedImage = Uri.fromFile(new File(galleryLatest[position]));
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
//                main.observationPath = fileUri.getPath();
//                Toast.makeText(main, "Image saved to:\n" + data.getData().toString(), Toast.LENGTH_LONG).show();
//                main.observationPath = data.getData().toString();
                Timber.d("Camera Path: "+cameraPhoto.getPhotoPath());
                main.observationPath = data.getData();
                cameraPhoto.addToGallery();
            } else if (requestCode == GALLERY_REQUEST) {
                galleryPhoto.setPhotoUri(data.getData());
                Timber.d("Gallery Path: "+galleryPhoto.getPath());
                main.observationPath = data.getData();
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
                    Map<String, Object> map = (Map<String, Object>) child.getValue();
                    String id = null;
                    String icon_url = null;
                    String description = null;
                    String name = null;
                    String status = null;
                    Long latest_contribution = null;
                    Long created_at = null;
                    Long updated_at = null;
                    if (map.get(ID) != null)
                        id = map.get(ID).toString();
                    if (map.get(ICON_URL) != null)
                        icon_url = map.get(ICON_URL).toString();
                    if (map.get(DESCRIPTION) != null)
                        description = map.get(DESCRIPTION).toString();
                    if (map.get(NAME) != null)
                        name = map.get(NAME).toString();
                    if (map.get(STATUS) != null)
                        status = map.get(STATUS).toString();
                    if (map.get(LATEST_CONTRIBUTION) != null)
                        latest_contribution = (Long) map.get(LATEST_CONTRIBUTION);
                    if (map.get(CREATED_AT) != null)
                        created_at = (Long) map.get(CREATED_AT);
                    if (map.get(UPDATED_AT) != null)
                        updated_at = (Long) map.get(UPDATED_AT);
                    Project project = new Project(id, icon_url, description, name, status, latest_contribution, created_at, updated_at);
                    mProjects.add(project);
                }
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