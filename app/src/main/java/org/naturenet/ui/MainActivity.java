package org.naturenet.ui;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
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
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.naturenet.NatureNetApplication;
import org.naturenet.R;
import org.naturenet.UploadService;
import org.naturenet.data.model.Observation;
import org.naturenet.data.model.Project;
import org.naturenet.data.model.Site;
import org.naturenet.data.model.Users;
import org.naturenet.util.CroppedCircleTransformation;
import org.naturenet.util.NatureNetUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final static int REQUEST_CODE_JOIN = 1;
    private final static int REQUEST_CODE_LOGIN = 2;
    private final static int REQUEST_CODE_ADD_OBSERVATION = 3;
    private final static int REQUEST_CODE_PROJECT_ACTIVITY = 4;
    private final static int REQUEST_CODE_OBSERVATION_ACTIVITY = 5;

    static String NAME = "name";

    String[] affiliation_ids, affiliation_names;
    Observation newObservation, previewSelectedObservation;
    List<String> ids, names;
    DatabaseReference mFirebase;
    Users signed_user;
    Site user_home_site;
    Uri observationPath;
    DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView navigationView;
    View header;
    Button sign_in, join;
    TextView display_name, affiliation, licenses;
    ImageView nav_iv;
    MenuItem logout;
    ProgressDialog pd;
    private Transformation mAvatarTransform = new CroppedCircleTransformation();
    private Disposable mUserAuthSubscription;

    /* Common submission items */
    static final private int REQUEST_CODE_CAMERA = 6;
    static final private int REQUEST_CODE_GALLERY = 7;
    static final private int REQUEST_CODE_CHECK_LOCATION_SETTINGS = 8;
    CameraPhoto cameraPhoto;
    GalleryPhoto galleryPhoto;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    ImageButton add_observation, add_design_idea;
    Button camera, gallery, design_ideas, design_challenges;
    TextView select;
    LinearLayout dialog_add_observation, dialog_add_design_idea;
    FrameLayout floating_buttons;
    GridView gridview;
    ImageView add_observation_cancel, add_design_idea_cancel, gallery_item;
    List<Uri> recentImageGallery;
    Uri selectedImage;
    double latValue, longValue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        logout = navigationView.getMenu().findItem(R.id.nav_logout);
        header = navigationView.getHeaderView(0);
        sign_in = (Button) header.findViewById(R.id.nav_b_sign_in);
        join = (Button) header.findViewById(R.id.nav_b_join);
        nav_iv = (ImageView) header.findViewById(R.id.nav_iv);
        display_name = (TextView) header.findViewById(R.id.nav_tv_display_name);
        affiliation = (TextView) header.findViewById(R.id.nav_tv_affiliation);
        licenses = (TextView) navigationView.findViewById(R.id.licenses);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        licenses.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(v.getContext())
                            .setView(View.inflate(MainActivity.this, R.layout.about, null))
                            .setNegativeButton("Dismiss", null)
                            .setCancelable(false)
                            .show();
                }
            }
        );

        this.invalidateOptionsMenu();
        pd = new ProgressDialog(this);
        pd.setCancelable(false);

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((NatureNetApplication)getApplication()).isConnected()) {
                    MainActivity.this.goToLoginActivity();
                } else {
                    Toast.makeText(MainActivity.this, R.string.no_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((NatureNetApplication)getApplication()).isConnected()) {
                    MainActivity.this.goToJoinActivity();
                } else {
                    Toast.makeText(MainActivity.this, R.string.no_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mFirebase = FirebaseDatabase.getInstance().getReference();
        showNoUser();

        getFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new LaunchFragment())
                .commit();

        mUserAuthSubscription = ((NatureNetApplication)getApplication()).getCurrentUserObservable().subscribe(new Consumer<Optional<Users>>() {
            @Override
            public void accept(Optional<Users> user) throws Exception {
                if (user.isPresent()) {
                    if (signed_user == null) {
                        onUserSignIn(user.get());
                    }

                    if (getFragmentManager().getBackStackEntryCount() == 0) {
                        getFragmentManager()
                                .beginTransaction()
                                .add(R.id.fragment_container, ExploreFragment.newInstance(user_home_site))
                                .commit();
                    }
                } else {
                    if (signed_user != null) {
                        onUserSignOut();
                    }
                    showNoUser();
                }
            }
        });

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                if (status.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                    try {
                        status.startResolutionForResult(MainActivity.this, REQUEST_CODE_CHECK_LOCATION_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                        Timber.w(e, "Unable to resolve location settings");
                    }
                } else if (status.getStatusCode() == LocationSettingsStatusCodes.SUCCESS) {
                    requestLocationUpdates();
                }
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (lastLocation != null) {
                latValue = lastLocation.getLatitude();
                longValue = lastLocation.getLongitude();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_CHECK_LOCATION_SETTINGS);
        }

        latValue = 0.0;
        longValue = 0.0;

        floating_buttons = (FrameLayout) findViewById(R.id.fl_floating_buttons);
        add_observation = (ImageButton) findViewById(R.id.floating_buttons_ib_add_observation);
        add_design_idea = (ImageButton) findViewById(R.id.floating_buttons_ib_add_design_idea);
        dialog_add_observation = (LinearLayout) findViewById(R.id.ll_dialog_add_observation);
        add_observation_cancel = (ImageView) findViewById(R.id.dialog_add_observation_iv_cancel);
        camera = (Button) findViewById(R.id.dialog_add_observation_b_camera);
        gallery = (Button) findViewById(R.id.dialog_add_observation_b_gallery);
        select = (TextView) findViewById(R.id.dialog_add_observation_tv_select);
        gridview = (GridView) findViewById(R.id.dialog_add_observation_gv);
        gallery_item = (ImageView) findViewById(R.id.gallery_iv);
        dialog_add_design_idea = (LinearLayout) findViewById(R.id.ll_dialog_add_design_idea);
        add_design_idea_cancel = (ImageView) findViewById(R.id.dialog_add_design_idea_iv_cancel);
        design_ideas = (Button) findViewById(R.id.dialog_add_design_idea_b_design_ideas);
        design_challenges = (Button) findViewById(R.id.dialog_add_design_idea_b_design_challenges);
        cameraPhoto = new CameraPhoto(this);
        galleryPhoto = new GalleryPhoto(this);

        add_observation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY);
                } else {
                    setGallery();
                }

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
                observationPath = selectedImage;
                newObservation = new Observation();
                newObservation.location = Lists.newArrayList(latValue, longValue);
                setGallery();
                goToAddObservationActivity();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGallery();
                select.setVisibility(View.GONE);

                try {
                    startActivityForResult(cameraPhoto.takePhotoIntent(), REQUEST_CODE_CAMERA);
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, "Something Wrong while taking photo", Toast.LENGTH_SHORT).show();
                }
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGallery();
                select.setVisibility(View.GONE);
                startActivityForResult(galleryPhoto.openGalleryIntent(), REQUEST_CODE_GALLERY);
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
    }

    public void setGallery() {
        recentImageGallery = getRecentImagesUris();

        if (recentImageGallery.size() != 0) {
            gridview.setAdapter(new ImageGalleryAdapter(this, recentImageGallery));

            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    ImageView iv = (ImageView) v.findViewById(R.id.gallery_iv);

                    if (selectedImage == null) {
                        selectedImage = recentImageGallery.get(position);
                        iv.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.border_selected_image));
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
                        iv.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.border_selected_image));
                        select.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_GALLERY:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    setGallery();
                else
                    Toast.makeText(this, "Gallery Access Permission Denied", Toast.LENGTH_SHORT).show();
                break;
            case REQUEST_CODE_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    setGallery();
                else
                    Toast.makeText(this, "Camera Access Permission Denied", Toast.LENGTH_SHORT).show();
                break;
            case REQUEST_CODE_CHECK_LOCATION_SETTINGS:
                if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocationUpdates();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
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
        if (mGoogleApiClient.isConnected()) {
            requestLocationUpdates();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mUserAuthSubscription.dispose();
        super.onDestroy();
    }

    private void clearBackStack() {
        FragmentManager manager = getFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(getFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.nav_explore:
                goToExploreFragment();
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_gallery:
                goToGalleryFragment();
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_projects:
                goToProjectsFragment();
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_design_ideas:
                goToDesignIdeasFragment();
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_communities:
                goToCommunitiesFragment();
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                break;
        }
        return true;
    }

    public void showLaunchFragment() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new LaunchFragment())
                .commit();
    }

    public void goToGalleryFragment() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new ObservationGalleryFragment())
                .addToBackStack(ObservationGalleryFragment.FRAGMENT_TAG)
                .commit();
    }

    public void goToExploreFragment() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ExploreFragment.newInstance(user_home_site))
                .addToBackStack(ExploreFragment.FRAGMENT_TAG)
                .commit();
    }

    public void goToProjectsFragment() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new ProjectsFragment())
                .addToBackStack(ProjectsFragment.FRAGMENT_TAG)
                .commit();
    }

    public void goToDesignIdeasFragment() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new IdeasFragment())
                .addToBackStack(IdeasFragment.FRAGMENT_TAG)
                .commit();
    }

    public void goToCommunitiesFragment() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new CommunitiesFragment())
                .addToBackStack(CommunitiesFragment.FRAGMENT_TAG)
                .commit();
    }

    public void goToJoinActivity() {
        ids = new ArrayList<>();
        names = new ArrayList<>();
        mFirebase.child(Site.NODE_NAME).orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Site site = postSnapshot.getValue(Site.class);
                    ids.add(site.id);
                    names.add(site.name);
                }
                if (ids.size() != 0 && names.size() != 0) {
                    affiliation_ids = ids.toArray(new String[ids.size()]);
                    affiliation_names = names.toArray(new String[names.size()]);
                    Intent join = new Intent(getApplicationContext(), JoinActivity.class);
                    join.putExtra(JoinActivity.EXTRA_SITE_IDS, affiliation_ids);
                    join.putExtra(JoinActivity.EXTRA_SITE_NAMES, affiliation_names);
                    startActivityForResult(join, REQUEST_CODE_JOIN);
                    overridePendingTransition(R.anim.slide_up, R.anim.stay);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.join_error_message_firebase_read) + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void goToLoginActivity() {
        Intent login = new Intent(this, LoginActivity.class);
        startActivityForResult(login, REQUEST_CODE_LOGIN);
    }

    public void goToAddObservationActivity() {
        Intent addObservation = new Intent(this, AddObservationActivity.class);
        addObservation.putExtra(AddObservationActivity.EXTRA_IMAGE_PATH, observationPath);
        addObservation.putExtra(AddObservationActivity.EXTRA_OBSERVATION, newObservation);
        addObservation.putExtra(AddObservationActivity.EXTRA_USER, signed_user);
        startActivityForResult(addObservation, REQUEST_CODE_ADD_OBSERVATION);
        overridePendingTransition(R.anim.slide_up, R.anim.stay);
    }

    public void goToProjectActivity(Project p) {
        Intent project = new Intent(this, ProjectActivity.class);
        project.putExtra(ProjectActivity.EXTRA_PROJECT, p);
        startActivityForResult(project, REQUEST_CODE_PROJECT_ACTIVITY);
        overridePendingTransition(R.anim.slide_up, R.anim.stay);
    }

    public void goToObservationActivity() {
        Intent observation = new Intent(this, ObservationActivity.class);
        observation.putExtra(ObservationActivity.EXTRA_USER, signed_user);
        if (previewSelectedObservation != null) {
            observation.putExtra(ObservationActivity.EXTRA_OBSERVATION, previewSelectedObservation);
        }
        startActivityForResult(observation, REQUEST_CODE_OBSERVATION_ACTIVITY);
        overridePendingTransition(R.anim.slide_up, R.anim.stay);
    }

    public List<Uri> getRecentImagesUris() {
        Uri uri;
        Cursor cursor;
        List<Uri> listOfAllImages = Lists.newArrayList();
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[] { MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DATE_TAKEN };
        cursor = this.getContentResolver().query(uri, projection, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
        if (cursor != null) {
            cursor.moveToFirst();
            try {
                do {
                    listOfAllImages.add(FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider",
                            new File(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)))));
                } while (cursor.moveToNext() && listOfAllImages.size() < 8);
            } catch (CursorIndexOutOfBoundsException ex) {
                Timber.e(ex, "Could not read data from MediaStore, image gallery may be empty");
            } finally {
                cursor.close();
            }
        }else {
            Timber.e("Could not get MediaStore content!");
        }
        return listOfAllImages;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (REQUEST_CODE_JOIN): {
                if (resultCode == Activity.RESULT_OK) {
                    if (JoinActivity.EXTRA_LAUNCH.equals(data.getExtras().getString(JoinActivity.EXTRA_JOIN))) {
                        showLaunchFragment();
                    } else if (JoinActivity.EXTRA_LOGIN.equals(data.getExtras().getString(JoinActivity.EXTRA_JOIN))) {
                        signed_user = data.getParcelableExtra(JoinActivity.EXTRA_NEW_USER);
                        logout.setVisible(true);
                        this.supportInvalidateOptionsMenu();

                        if (signed_user.avatar != null) {
                            Picasso.with(this).load(Strings.emptyToNull(signed_user.avatar))
                                    .placeholder(R.drawable.default_avatar)
                                    .transform(mAvatarTransform).fit().into(nav_iv);
                        }

                        display_name.setText(signed_user.displayName);
                        mFirebase.child(Site.NODE_NAME).child(signed_user.affiliation).child(NAME).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                affiliation.setText((String)snapshot.getValue());
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Timber.w("Could not get user's affiliation");
                            }
                        });
                        sign_in.setVisibility(View.GONE);
                        join.setVisibility(View.GONE);
                        display_name.setVisibility(View.VISIBLE);
                        affiliation.setVisibility(View.VISIBLE);
                        goToExploreFragment();
                        drawer.openDrawer(GravityCompat.START);
                    }
                }
                break;
            }
            case(REQUEST_CODE_LOGIN): {
                if (resultCode == Activity.RESULT_OK) {
                    if (data.getStringExtra(LoginActivity.EXTRA_LOGIN).equals(LoginActivity.EXTRA_JOIN)) {
                        goToJoinActivity();
                    } else {
                        drawer.openDrawer(GravityCompat.START);
                    }
                }
                break;
            }
            case(REQUEST_CODE_ADD_OBSERVATION): {
                if(resultCode == Activity.RESULT_OK) {
                    newObservation = data.getParcelableExtra(AddObservationActivity.EXTRA_OBSERVATION);
                    newObservation.userId = signed_user.id;
                    Intent uploadIntent = new Intent(MainActivity.this, UploadService.class);
                    uploadIntent.putExtra(UploadService.EXTRA_OBSERVATION, newObservation);
                    uploadIntent.putExtra(UploadService.EXTRA_URI_PATH, observationPath);
                    startService(uploadIntent);
                    goToExploreFragment();
                }
                break;
            }
            case REQUEST_CODE_CAMERA: {
                if (resultCode == MainActivity.RESULT_OK) {
                    Timber.d("Camera Path: %s", cameraPhoto.getPhotoPath());
                    observationPath = Uri.fromFile(new File(cameraPhoto.getPhotoPath()));
                    cameraPhoto.addToGallery();
                    newObservation = new Observation();
                    newObservation.location = Lists.newArrayList(latValue, longValue);
                    setGallery();
                    goToAddObservationActivity();
                }
                break;
            }
            case REQUEST_CODE_GALLERY: {
                if (resultCode == MainActivity.RESULT_OK) {
                    galleryPhoto.setPhotoUri(data.getData());
                    Timber.d("Gallery Path: %s", galleryPhoto.getPath());
                    observationPath = Uri.fromFile(new File(galleryPhoto.getPath()));
                    newObservation = new Observation();
                    newObservation.location = Lists.newArrayList(latValue, longValue);
                    setGallery();
                    goToAddObservationActivity();
                }
                break;
            }
        }
    }
    public void onUserSignIn(@NonNull Users user) {
        signed_user = user;
        mFirebase.child(Site.NODE_NAME).child(signed_user.affiliation).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user_home_site = dataSnapshot.getValue(Site.class);
                if(user_home_site != null && !mGoogleApiClient.isConnected()) {
                    latValue = user_home_site.location.get(0);
                    longValue = user_home_site.location.get(1);
                }
                showUserInfo(signed_user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, getString(R.string.login_error_message_firebase_read), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onUserSignOut() {
        if (signed_user != null) {
            Toast.makeText(this, "You have been logged out.", Toast.LENGTH_SHORT).show();
            mFirebase.child(Users.NODE_NAME).child(signed_user.id).keepSynced(false);
            signed_user = null;
        }
        user_home_site = null;
        this.invalidateOptionsMenu();
        clearBackStack();
        showLaunchFragment();
    }

    public void showNoUser() {
        NatureNetUtils.showUserAvatar(this, nav_iv, R.drawable.default_avatar);
        logout.setVisible(false);
        display_name.setText(null);
        affiliation.setText(null);
        display_name.setVisibility(View.GONE);
        affiliation.setVisibility(View.GONE);
        sign_in.setVisibility(View.VISIBLE);
        join.setVisibility(View.VISIBLE);
    }

    public void showUserInfo(final Users user) {
        NatureNetUtils.showUserAvatar(this, nav_iv, user.avatar);
        logout.setVisible(true);
        display_name.setText(user.displayName);
        affiliation.setText(user_home_site.name);
        display_name.setVisibility(View.VISIBLE);
        affiliation.setVisibility(View.VISIBLE);
        sign_in.setVisibility(View.GONE);
        join.setVisibility(View.GONE);
        drawer.openDrawer(GravityCompat.START);
    }
}