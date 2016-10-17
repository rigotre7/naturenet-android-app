package org.naturenet.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import org.naturenet.util.CroppedCircleTransformation;
import org.naturenet.R;
import org.naturenet.data.model.Comment;
import org.naturenet.data.model.Observation;
import org.naturenet.data.ObserverInfo;
import org.naturenet.data.PreviewInfo;
import org.naturenet.data.model.Project;
import org.naturenet.data.model.Site;
import org.naturenet.data.model.Users;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FirebaseAuth.AuthStateListener {
    final static int REQUEST_CODE_JOIN = 1;
    final static int REQUEST_CODE_LOGIN = 2;
    final static int REQUEST_CODE_ADD_OBSERVATION = 3;
    final static int REQUEST_CODE_PROJECT_ACTIVITY = 4;
    final static int REQUEST_CODE_OBSERVATION_ACTIVITY = 5;
    final static int NUM_OF_OBSERVATIONS = 20;
    final static int MAX_IMAGE_DIMENSION = 1920;
    static String FRAGMENT_TAG_LAUNCH = "launch_fragment";
    static String FRAGMENT_TAG_EXPLORE = "explore_fragment";
    static String FRAGMENT_TAG_PROJECTS = "projects_fragment";
    static String FRAGMENT_TAG_DESIGNIDEAS = "designideas_fragment";
    static String FRAGMENT_TAG_COMMUNITIES = "communities_fragment";
    static String LOGIN = "login";
    static String GUEST = "guest";
    static String LAUNCH = "launch";
    static String JOIN = "join";
    static String IDS = "ids";
    static String NAMES = "names";
    static String NEW_USER = "new_user";
    static String SIGNED_USER = "signed_user";
    static String LATEST_CONTRIBUTION = "latest_contribution";
    static String UPDATED_AT = "updated_at";
    static String NAME = "name";
    static String OBSERVATION = "observation";
    static String OBSERVATION_PATH = "observation_path";
    static String PROJECT = "project";
    static String EMPTY = "";
    static String SUBMITTING = "Submitting...";
    static String LOADING_OBSERVATIONS = "Loading Observations...";
    static String LOADING_DESIGN_IDEAS = "Loading Design Ideas...";
    static String LOADING_COMMUNITIES = "Loading Communities...";
    static String SIGNING_OUT = "Signing Out...";
    static String OBSERVERS = "observers";
    static String OBSERVATIONS = "observations";
    Observation selectedObservation, previewSelectedObservation;
    ObserverInfo selectedObserverInfo;
    List<Observation> observations;
    List<ObserverInfo> observers;
    List<Comment> comments;
    String[] affiliation_ids, affiliation_names;
    List<String> ids, names;
    DatabaseReference mFirebase;
    Users signed_user;
    Site user_home_site;
    Observation newObservation;
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
    private FirebaseUser mFirebaseUser;
    public static FragmentManager fragmentManager;
    Map<Observation, PreviewInfo> previews = new HashMap<Observation, PreviewInfo>();
    private Transformation mAvatarTransform = new CroppedCircleTransformation();

    @Override
    protected void onSaveInstanceState(Bundle outState) {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
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
        toolbar.setTitle(EMPTY);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        licenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setView(getLayoutInflater().inflate(R.layout.about, null))
                        .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {}
                        })
                        .setCancelable(false)
                        .show();
            }
        });
        this.invalidateOptionsMenu();
        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (haveNetworkConnection()) {
                    goToLoginActivity();
                } else {
                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (haveNetworkConnection()) {
                    goToJoinActivity();
                } else {
                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mFirebase = FirebaseDatabase.getInstance().getReference();
        mFirebase.child(Site.NODE_NAME).keepSynced(true);
        mFirebase.child(Project.NODE_NAME).keepSynced(true);

        updateUINoUser();
        observations = null;
        observers = null;
        selectedObservation = null;
        selectedObserverInfo = null;
        comments = null;
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new android.app.AlertDialog.Builder(this)
                .setMessage("Your GPS seems to be disabled, do you want to enable it?")
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
                })
                .create().show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

    @Override
    public void onBackPressed() {
//        if(getFragmentManager().findFragmentByTag(FRAGMENT_TAG_LAUNCH).isVisible()) {
//
//        } else if(getFragmentManager().findFragmentByTag(FRAGMENT_TAG_EXPLORE).isVisible()) {
//
//        } else if(getFragmentManager().findFragmentByTag(FRAGMENT_TAG_PROJECTS).isVisible()) {
//
//        } else if(getFragmentManager().findFragmentByTag(FRAGMENT_TAG_DESIGNIDEAS).isVisible()) {
//
//        } else if(getFragmentManager().findFragmentByTag(FRAGMENT_TAG_COMMUNITIES).isVisible()) {
//
//        } else
//            super.onBackPressed();
//        if(getFragmentManager().getBackStackEntryCount() > 0)
//            getFragmentManager().popBackStack();
//        else
//            super.onBackPressed();
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (haveNetworkConnection()) {
            int id = item.getItemId();
            switch(id) {
                case R.id.nav_explore:
                    goToExploreFragment();
                    drawer.closeDrawer(GravityCompat.START);
                    break;
                case R.id.nav_projects:
                    goToProjectsFragment();
                    drawer.closeDrawer(GravityCompat.START);
                    break;
                case R.id.nav_design_ideas:
                    pd.setMessage(LOADING_DESIGN_IDEAS);
                    pd.setCancelable(false);
                    pd.show();
                    goToDesignIdeasFragment();
                    drawer.closeDrawer(GravityCompat.START);
                    pd.dismiss();
                    break;
                case R.id.nav_communities:
                    pd.setMessage(LOADING_COMMUNITIES);
                    pd.setCancelable(false);
                    pd.show();
                    goToCommunitiesFragment();
                    drawer.closeDrawer(GravityCompat.START);
                    pd.dismiss();
                    break;
                case R.id.nav_logout:
                    pd.setMessage(SIGNING_OUT);
                    pd.setCancelable(false);
                    pd.show();
                    FirebaseAuth.getInstance().signOut();
                    pd.dismiss();
                    break;
            }
        } else {
            Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
    public void goToLaunchFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LaunchFragment(), FRAGMENT_TAG_LAUNCH)
                .addToBackStack(FRAGMENT_TAG_LAUNCH)
                .commit();
    }
    public void goToExploreFragment() {
        if (haveNetworkConnection()) {
            pd.setMessage(LOADING_OBSERVATIONS);
            pd.setCancelable(false);
            pd.show();
            if (observations == null) {
                observations = Lists.newArrayList();
                observers = Lists.newArrayList();
                mFirebase = FirebaseDatabase.getInstance().getReference();
                mFirebase.child(Observation.NODE_NAME).orderByChild(UPDATED_AT).limitToLast(NUM_OF_OBSERVATIONS).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for(DataSnapshot child : snapshot.getChildren()) {
                            final Observation observation = child.getValue(Observation.class);
                            observations.add(observation);
                            final PreviewInfo preview = new PreviewInfo();
                            preview.observationImageUrl = observation.data.image;
                            if (observation.data.text != null) {
                                preview.observationText = observation.data.text;
                            } else {
                                preview.observationText = "No Description";
                            }
                            if (observation.comments != null) {
                                preview.commentsCount = Integer.toString(observation.comments.size());
                            } else {
                                preview.commentsCount = "0";
                            }
                            if (observation.likes != null) {
                                preview.likesCount = String.valueOf(HashMultiset.create(observation.likes.values()).count(new Boolean(true)));
                            } else {
                                preview.likesCount = "0";
                            }
                            boolean contains = false;
                            for (int i=0; i<observers.size(); i++) {
                                contains = observers.get(i).getObserverId().equals(observation.userId);
                                if (contains) {
                                    preview.observerAvatarUrl = observers.get(i).getObserverAvatar();
                                    preview.observerName = observers.get(i).getObserverName();
                                    preview.affiliation = observers.get(i).getObserverAffiliation();
                                    break;
                                }
                            }
                            if (!contains) {
                                final ObserverInfo observer = new ObserverInfo();
                                observer.setObserverId(observation.userId);
                                DatabaseReference f = FirebaseDatabase.getInstance().getReference();
                                f.child(Users.NODE_NAME).child(observation.userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        Users user = snapshot.getValue(Users.class);
                                        observer.setObserverName(user.displayName);
                                        observer.setObserverAvatar(user.avatar);
                                        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
                                        fb.child(Site.NODE_NAME).child(user.affiliation).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot snapshot) {
                                                Site site =  snapshot.getValue(Site.class);
                                                observer.setObserverAffiliation(site.name);
                                                preview.observerAvatarUrl = observer.getObserverAvatar();
                                                preview.observerName = observer.getObserverName();
                                                preview.affiliation = observer.getObserverAffiliation();
                                                observers.add(observer);
                                                previews.put(observation, preview);
                                                if (observations.size() >= NUM_OF_OBSERVATIONS) {
                                                    pd.dismiss();
                                                    getFragmentManager().
                                                            beginTransaction().
                                                            replace(R.id.fragment_container, new ExploreFragment(), FRAGMENT_TAG_EXPLORE).
                                                            addToBackStack(FRAGMENT_TAG_EXPLORE).
                                                            commit();
                                                }
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {}
                                        });
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {}
                                });
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        pd.dismiss();
                        Toast.makeText(MainActivity.this, "Could not get observations: "+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                pd.dismiss();
                getFragmentManager().
                        beginTransaction().
                        replace(R.id.fragment_container, new ExploreFragment(), FRAGMENT_TAG_EXPLORE).
                        addToBackStack(FRAGMENT_TAG_EXPLORE).
                        commit();
            }
        } else {
            Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }
    public void goToProjectsFragment() {
        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, new ProjectsFragment(), FRAGMENT_TAG_PROJECTS).
                addToBackStack(FRAGMENT_TAG_PROJECTS).
                commit();
    }
    public void goToDesignIdeasFragment() {
        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, new IdeasFragment(), FRAGMENT_TAG_DESIGNIDEAS).
                addToBackStack(FRAGMENT_TAG_DESIGNIDEAS).
                commit();
    }
    public void goToCommunitiesFragment() {
        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, new CommunitiesFragment(), FRAGMENT_TAG_COMMUNITIES).
                addToBackStack(FRAGMENT_TAG_COMMUNITIES).
                commit();
    }
    public void logout() {
        Toast.makeText(this, "You have been logged out.", Toast.LENGTH_SHORT).show();
        signed_user = null;
        user_home_site = null;
        logout.setVisible(false);
        this.invalidateOptionsMenu();
        Picasso.with(this).load(R.drawable.default_avatar)
                .transform(mAvatarTransform).fit().into(nav_iv);
        display_name.setText(EMPTY);
        affiliation.setText(EMPTY);
        sign_in.setVisibility(View.VISIBLE);
        join.setVisibility(View.VISIBLE);
        display_name.setVisibility(View.GONE);
        affiliation.setVisibility(View.GONE);
    }

    public void goToJoinActivity() {
        ids = new ArrayList<String>();
        names = new ArrayList<String>();
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
                    join.putExtra(IDS, affiliation_ids);
                    join.putExtra(NAMES, affiliation_names);
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
        addObservation.putExtra(OBSERVATION_PATH, observationPath);
        addObservation.putExtra(OBSERVATION, newObservation);
        addObservation.putExtra(SIGNED_USER, signed_user);
        startActivityForResult(addObservation, REQUEST_CODE_ADD_OBSERVATION);
        overridePendingTransition(R.anim.slide_up, R.anim.stay);
    }
    public void goToProjectActivity(Project p) {
        Intent project = new Intent(this, ProjectActivity.class);
        project.putExtra(PROJECT, p);
        project.putExtra(SIGNED_USER, signed_user);
        startActivityForResult(project, REQUEST_CODE_PROJECT_ACTIVITY);
        overridePendingTransition(R.anim.slide_up, R.anim.stay);
    }
    public void goToObservationActivity() {
        Intent observation = new Intent(this, ObservationActivity.class);
        observation.putExtra(SIGNED_USER, signed_user);
        observation.putExtra(OBSERVATIONS, (Serializable) observations);
        observation.putExtra(OBSERVERS, (Serializable) observers);
        observation.putExtra(OBSERVATION, previewSelectedObservation);
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
                    listOfAllImages.add(Uri.fromFile(new File(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)))));
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

    private void uploadObservation() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            pd.setMessage(SUBMITTING);
            pd.setCancelable(false);
            pd.show();

            Timber.d("Preparing image for upload");
            Toast.makeText(this, "Your observation is being submitted.", Toast.LENGTH_SHORT).show();

            AsyncTask<Uri, Void, Map> uploader = new AsyncTask<Uri, Void, Map>() {
                @Override
                protected Map doInBackground(Uri... uris) {
                    final Uri observationPath = uris[0];
                    final Map<String, String> config = Maps.newHashMap();
                    config.put("cloud_name", "university-of-colorado");
                    Cloudinary cloudinary = new Cloudinary(config);

                    try {
                        return cloudinary.uploader().unsignedUpload(new File(observationPath.getPath()), "android-preset", ObjectUtils.emptyMap());
                    } catch (IOException ex) {
                        Timber.w(ex, "Failed to upload image to Cloudinary");
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Map results) {
                    super.onPostExecute(results);
                    pd.dismiss();
                    if (results == null) {
                        uploadImageWithFirebase();
                    } else {
                        continueWithCloudinaryUpload(results);
                    }
                }
            };

            uploader.execute(observationPath);
        }
    }

    private void continueWithCloudinaryUpload(Map results) {
        Timber.i("Image uploaded to Cloudinary as %s", results.get("public_id"));
        writeObservationToFirebase((String)results.get("secure_url"));
    }

    private void uploadImageWithFirebase() {
        Timber.i("Attempting upload to Firebase");
        Picasso.with(this).load(observationPath).resize(1920, 1920).centerInside().onlyScaleDown().into(new Target() {

            final StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("photos").child(UUID.randomUUID().toString());

            @Override
            public int hashCode() {
                return observationPath.hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                return observationPath.equals(obj);
            }

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Timber.d("Bitmap loaded; uploading data stream to Firebase");
                final ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                byte[] data = stream.toByteArray();
                continueWithFirebaseUpload(storageRef.putBytes(data));
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Timber.e("Could not load bitmap; uploading original file to Firebase");
                continueWithFirebaseUpload(storageRef.putFile(observationPath));
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Timber.d("Loading raw bitmap data from %s", observationPath.getPath());
            }
        });
    }

    private void continueWithFirebaseUpload(UploadTask uploadTask) {
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                writeObservationToFirebase(taskSnapshot.getDownloadUrl().toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception ex) {
                Timber.w(ex, "Image upload task failed: %s", ex.getMessage());
                Toast.makeText(MainActivity.this, "Your photo could not be uploaded.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void writeObservationToFirebase(String imageUrl) {
        final String id = mFirebase.child(OBSERVATIONS).push().getKey();
        newObservation.id = id;
        newObservation.data.image = imageUrl;
        mFirebase.child(Observation.NODE_NAME).child(id).setValue(newObservation, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Timber.w(databaseError.toException(), "Failed to write observation to database: %s", databaseError.getMessage());
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.dialog_add_observation_error), Toast.LENGTH_SHORT).show();
                } else {
                    mFirebase.child(Users.NODE_NAME).child(signed_user.id).child(LATEST_CONTRIBUTION).setValue(ServerValue.TIMESTAMP);
                    mFirebase.child(Project.NODE_NAME).child(newObservation.projectId).child(LATEST_CONTRIBUTION).setValue(ServerValue.TIMESTAMP);
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.dialog_add_observation_success), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (REQUEST_CODE_JOIN): {
                if (resultCode == Activity.RESULT_OK) {
                    if (GUEST.equals(data.getExtras().getString(JOIN))) {
                        drawer.openDrawer(GravityCompat.START);
                    } else if (LAUNCH.equals(data.getExtras().getString(JOIN))) {
                        goToLaunchFragment();
                    } else if (LOGIN.equals(data.getExtras().getString(JOIN))) {
                        signed_user = (Users) data.getSerializableExtra(NEW_USER);
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
                    if (data.getStringExtra(LOGIN).equals(JOIN)) {
                        goToJoinActivity();
                    } else if (data.getStringExtra(LOGIN).equals(GUEST)) {
                        drawer.openDrawer(GravityCompat.START);
                    }
                }
                break;
            }
            case(REQUEST_CODE_ADD_OBSERVATION): {
                if(resultCode == Activity.RESULT_OK) {
                    newObservation = (Observation) data.getSerializableExtra(OBSERVATION);
                    newObservation.userId = signed_user.id;
                    uploadObservation();
                    goToExploreFragment();
                }
                break;
            }
            case(REQUEST_CODE_PROJECT_ACTIVITY): {
                if(resultCode == Activity.RESULT_OK) {
                    goToProjectsFragment();
                }
                break;
            }
            case(REQUEST_CODE_OBSERVATION_ACTIVITY): {
                if(resultCode == Activity.RESULT_OK) {
                    previewSelectedObservation = null;
                    goToExploreFragment();
                }
                break;
            }
        }
    }
    public void getSignedUser() {
        mFirebase.child(Users.NODE_NAME).child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                signed_user = snapshot.getValue(Users.class);
                mFirebase.child(Site.NODE_NAME).child(signed_user.affiliation).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        user_home_site = dataSnapshot.getValue(Site.class);
                        updateUIUser(signed_user);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(MainActivity.this, getString(R.string.login_error_message_firebase_read), Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(MainActivity.this, String.format("Welcome, %s!", signed_user.displayName), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.login_error_message_firebase_read) + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateUINoUser() {
        Picasso.with(this).load(R.drawable.default_avatar)
                .transform(mAvatarTransform).fit().into(nav_iv);
        logout.setVisible(false);
        display_name.setText(EMPTY);
        affiliation.setText(EMPTY);
        sign_in.setVisibility(View.VISIBLE);
        join.setVisibility(View.VISIBLE);
        display_name.setVisibility(View.GONE);
        affiliation.setVisibility(View.GONE);
    }

    public void updateUIUser(final Users user) {
        Picasso.with(MainActivity.this).load(Strings.emptyToNull(user.avatar))
                .placeholder(R.drawable.default_avatar)
                .transform(mAvatarTransform).fit().into(nav_iv);
        logout.setVisible(true);
        display_name.setText(user.displayName);
        affiliation.setText(user_home_site.name);
        sign_in.setVisibility(View.GONE);
        join.setVisibility(View.GONE);
        display_name.setVisibility(View.VISIBLE);
        affiliation.setVisibility(View.VISIBLE);
        goToExploreFragment();
        drawer.openDrawer(GravityCompat.START);
    }

    public boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        mFirebaseUser = firebaseAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            mFirebase.child(Users.NODE_NAME).child(mFirebaseUser.getUid()).keepSynced(true);
            getSignedUser();
        } else {
            if (signed_user != null) {
                mFirebase.child(Users.NODE_NAME).child(signed_user.id).keepSynced(false);
                logout();
            }
            updateUINoUser();
            goToLaunchFragment();
        }
    }
}