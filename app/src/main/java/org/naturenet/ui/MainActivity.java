package org.naturenet.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.firebase.auth.AuthResult;
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
import com.squareup.picasso.Transformation;

import org.naturenet.CroppedCircleTransformation;
import org.naturenet.R;
import org.naturenet.data.model.Comment;
import org.naturenet.data.model.Data;
import org.naturenet.data.model.Observation;
import org.naturenet.data.model.ObserverInfo;
import org.naturenet.data.model.PreviewInfo;
import org.naturenet.data.model.Project;
import org.naturenet.data.model.Users;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    final static int REQUEST_CODE_JOIN = 1;
    final static int REQUEST_CODE_LOGIN = 2;
    final static int REQUEST_CODE_ADD_OBSERVATION = 3;
    final static int REQUEST_CODE_PROJECT_ACTIVITY = 4;
    final static int REQUEST_CODE_OBSERVATION_ACTIVITY = 5;
    static int NUM_OF_OBSERVATIONS = 8;
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
    static String SITES = "sites";
    static String NEW_USER = "new_user";
    static String SIGNED_USER = "signed_user";
    static String ID = "id";
    static String USERS = "users";
    static String DISPLAY_NAME = "display_name";
    static String AFFILIATION = "affiliation";
    static String AVATAR = "avatar";
    static String BIO = "bio";
    static String LATEST_CONTRIBUTION = "latest_contribution";
    static String CREATED_AT = "created_at";
    static String UPDATED_AT = "updated_at";
    static String NAME = "name";
    static String OBSERVATION = "observation";
    static String OBSERVATION_PATH = "observation_path";
    static String OBSERVATION_BITMAP = "observation_bitmap";
    static String PROJECT = "project";
    static String EMAIL = "email";
    static String PASSWORD = "password";
    static String EMPTY = "";
    static String SUBMITTING = "Submitting...";
    static String LOADING_OBSERVATIONS = "Loading Observations...";
    static String LOADING_DESIGN_IDEAS = "Loading Design Ideas...";
    static String LOADING_COMMUNITIES = "Loading Communities...";
    static String SIGNING_OUT = "Signing Out...";
    static String OBSERVERS = "observers";
    static String OBSERVATIONS = "observations";
    static String OBSERVER = "observer";
    static String ACTIVITY = "activity";
    static String SITE = "site";
    static String DATA = "data";
    static String IMAGE = "image";
    static String TEXT = "text";
    static String G = "g";
    static String L = "l";
    static String LAT = "0";
    static String LON = "1";
    static String TRUE = "true";
    static String COMMENTS = "comments";
    static String LIKES = "likes";
    Observation selectedObservation, previewSelectedObservation;
    ObserverInfo selectedObserverInfo;
    List<Observation> observations;
    List<ObserverInfo> observers;
    List<Comment> comments;
    String[] affiliation_ids, affiliation_names;
    List<String> ids, names;
    DatabaseReference fbRef, mFirebase;
    Users signed_user;
    String signed_user_email, signed_user_password;
    Observation newObservation;
    Uri observationPath;
    DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView navigationView;
    View header;
    Button sign_in, join;
    TextView toolbar_title, display_name, affiliation, licenses;
    ImageView nav_iv;
    MenuItem logout;
    ProgressDialog pd;
    private FirebaseAuth mFirebaseAuth;
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
        toolbar_title = (TextView) findViewById(R.id.app_bar_main_tv);
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
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        licenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setView(getLayoutInflater().inflate(R.layout.license_information, null))
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
                goToLoginActivity();
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
        fbRef = FirebaseDatabase.getInstance().getReference();
        updateUINoUser();
        if (mFirebaseUser != null) {
            getSignedUser();
        } else {
            goToLaunchFragment();
        }
        observations = null;
        observers = null;
        selectedObservation = null;
        selectedObserverInfo = null;
        comments = null;
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
        if(getFragmentManager().getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();
        else
            super.onBackPressed();
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
                    logout();
                    pd.dismiss();
                    break;
            }
        } else {
            Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
    public void goToLaunchFragment() {
        toolbar_title.setText(R.string.launch_title);
        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, new LaunchFragment(), FRAGMENT_TAG_LAUNCH).
                addToBackStack(null).
                commit();
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
                            Map<String, Object> map = (Map<String, Object>) child.getValue();
                            final PreviewInfo preview = new PreviewInfo();
                            String id = map.get(ID).toString();
                            Long created_at = (Long) map.get(CREATED_AT);
                            Long updated_at = (Long) map.get(UPDATED_AT);
                            String observerId = map.get(OBSERVER).toString();
                            String activity = map.get(ACTIVITY).toString();
                            String site = map.get(SITE).toString();
                            Data data = new Data();
                            Map<String, Object> d = (Map<String, Object>) map.get(DATA);
                            data.setImage(d.get(IMAGE).toString());
                            preview.observationImageUrl = d.get(IMAGE).toString();
                            if (d.get(TEXT) != null) {
                                data.setText(d.get(TEXT).toString());
                                preview.observationText = d.get(TEXT).toString();
                            } else
                                preview.observationText = "No Description";
                            String g = null;
                            if (map.get(G) != null)
                                g = map.get(G).toString();
                            Map<String, Double> l = new HashMap<String, Double>();
                            if (map.get(L) != null) {
                                ArrayList<Number> lMap = (ArrayList<Number>) map.get(L);
                                l.put(LAT, lMap.get(0).doubleValue());
                                l.put(LON, lMap.get(1).doubleValue());
                            }
                            Map<String, Boolean> comments = new HashMap<String, Boolean>();
                            if (map.get(COMMENTS) != null) {
                                Map<String, Object> c = (Map<String, Object>) map.get(COMMENTS);
                                preview.commentsCount = Integer.toString(c.keySet().size());
                                for (String key: c.keySet())
                                    comments.put(key, c.get(key).toString().equals(TRUE));
                            } else
                                preview.commentsCount = "0";
                            Map<String, Boolean> likes = new HashMap<String, Boolean>();
                            if (map.get(LIKES) != null) {
                                Map<String, Object> li = (Map<String, Object>) map.get(LIKES);
                                preview.likesCount = Integer.toString(li.keySet().size());
                                for (String key: li.keySet())
                                    likes.put(key, li.get(key).toString().equals(TRUE));
                            } else
                                preview.likesCount = "0";
                            final Observation observation = new Observation(id, created_at, updated_at, observerId, activity, site, data, g, l, comments, likes);
                            observations.add(observation);
                            boolean contains = false;
                            for (int i=0; i<observers.size(); i++) {
                                contains = observers.get(i).getObserverId().equals(observerId);
                                if (contains) {
                                    preview.observerAvatarUrl = observers.get(i).getObserverAvatar();
                                    preview.observerName = observers.get(i).getObserverName();
                                    preview.affiliation = observers.get(i).getObserverAffiliation();
                                    break;
                                }
                            }
                            if (!contains) {
                                final ObserverInfo observer = new ObserverInfo();
                                observer.setObserverId(observerId);
                                DatabaseReference f = FirebaseDatabase.getInstance().getReference();
                                f.child(USERS).child(observerId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        Map<String, String> map = (Map<String, String>) snapshot.getValue();
                                        observer.setObserverName(map.get(DISPLAY_NAME));
                                        observer.setObserverAvatar(map.get(AVATAR));
                                        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
                                        fb.child(SITES).child(map.get(AFFILIATION)).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot snapshot) {
                                                Map<String, String> map = (Map<String, String>) snapshot.getValue();
                                                observer.setObserverAffiliation(map.get(NAME));
                                                preview.observerAvatarUrl = observer.getObserverAvatar();
                                                preview.observerName = observer.getObserverName();
                                                preview.affiliation = observer.getObserverAffiliation();
                                                observers.add(observer);
                                                previews.put(observation, preview);
                                                if (observations.size() == 8) {
                                                    pd.dismiss();
                                                    toolbar_title.setText(R.string.explore_title);
                                                    getFragmentManager().
                                                            beginTransaction().
                                                            replace(R.id.fragment_container, new ExploreFragment(), FRAGMENT_TAG_EXPLORE).
                                                            addToBackStack(null).
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
                toolbar_title.setText(R.string.explore_title);
                getFragmentManager().
                        beginTransaction().
                        replace(R.id.fragment_container, new ExploreFragment(), FRAGMENT_TAG_EXPLORE).
                        addToBackStack(null).
                        commit();
            }
        } else {
            Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }
    public void goToProjectsFragment() {
        toolbar_title.setText(R.string.projects_title);
        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, new ProjectsFragment(), FRAGMENT_TAG_PROJECTS).
                addToBackStack(null).
                commit();
    }
    public void goToDesignIdeasFragment() {
        toolbar_title.setText(R.string.design_ideas_title_design_ideas);
        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, new IdeasFragment(), FRAGMENT_TAG_DESIGNIDEAS).
                addToBackStack(null).
                commit();
    }
    public void goToCommunitiesFragment() {
        toolbar_title.setText(R.string.communities_title);
        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, new CommunitiesFragment(), FRAGMENT_TAG_COMMUNITIES).
                addToBackStack(null).
                commit();
    }
    public void logout() {
        signed_user = null;
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
    public void closeCurrent() {
        getFragmentManager().popBackStack();
    }
    public void goToJoinActivity() {
        ids = new ArrayList<String>();
        names = new ArrayList<String>();
        fbRef.child(SITES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Map<String, String> map = (Map<String, String>) postSnapshot.getValue();
                    ids.add(map.get(ID));
                    names.add(map.get(NAME));
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
        addObservation.putExtra(EMAIL, signed_user_email);
        addObservation.putExtra(PASSWORD, signed_user_password);
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
    public List<String> getAllShownImagesPath() {
        Uri uri;
        Cursor cursor;
        List<String> listOfAllImages = new ArrayList<String>();
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[] { MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DATE_TAKEN };
        cursor = this.getContentResolver().query(uri, projection, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
        cursor.moveToFirst();
        do {
            listOfAllImages.add(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)));
        } while (cursor.moveToNext() && listOfAllImages.size() < 8);
        cursor.close();
        return listOfAllImages;
    }
    public void uploadObservation() {
        pd.setMessage(SUBMITTING);
        pd.setCancelable(false);
        pd.show();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("photos").child(UUID.randomUUID().toString());
        UploadTask uploadTask = storageRef.putFile(observationPath);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                pd.dismiss();
                Toast.makeText(MainActivity.this, "Failed to upload image to Firebase", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signInWithEmailAndPassword(signed_user_email, signed_user_password)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    final String id = fbRef.push().getKey();
                                    newObservation.setId(id);
                                    newObservation.getData().setImage(downloadUrl.toString());
                                    fbRef.child(newObservation.NODE_NAME).child(id).setValue(newObservation, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            pd.dismiss();
                                            if(databaseError != null) {
                                                Toast.makeText(MainActivity.this, getResources().getString(R.string.dialog_add_observation_error), Toast.LENGTH_SHORT).show();
                                            }
                                            fbRef.child(USERS).child(signed_user.getId()).child(LATEST_CONTRIBUTION).setValue(ServerValue.TIMESTAMP);
                                            fbRef.child(Project.NODE_NAME).child(newObservation.getActivity()).child(LATEST_CONTRIBUTION).setValue(ServerValue.TIMESTAMP);
                                        }
                                    });
                                    Toast.makeText(MainActivity.this, getResources().getString(R.string.dialog_add_observation_success), Toast.LENGTH_SHORT).show();
                                } else {
                                    pd.dismiss();
                                    Toast.makeText(MainActivity.this, getResources().getString(R.string.login_error_message_firebase_login) + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
    public Bitmap decodeURI(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        // Only scale if we need to
        // (16384 buffer for img processing)
        Boolean scaleByHeight = Math.abs(options.outHeight - 100) >= Math.abs(options.outWidth - 100);
        if (options.outHeight * options.outWidth * 2 >= 16384) {
            // Load, scaling to smallest power of 2 that'll get it <= desired dimensions
            double sampleSize = scaleByHeight ? options.outHeight / 100 : options.outWidth / 100;
            options.inSampleSize = (int) Math.pow(2d, Math.floor(Math.log(sampleSize)/Math.log(2d)));
        }
        // Do the actual decoding
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[512];
        Bitmap output = BitmapFactory.decodeFile(filePath, options);
        return output;
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
                        signed_user_email = data.getStringExtra(EMAIL);
                        signed_user_password = data.getStringExtra(PASSWORD);
                        logout.setVisible(true);
                        this.supportInvalidateOptionsMenu();

                        if (signed_user.getAvatar() != null) {
                            Picasso.with(this).load(Strings.emptyToNull(signed_user.getAvatar()))
                                    .placeholder(R.drawable.default_avatar)
                                    .transform(mAvatarTransform).fit().into(nav_iv);
                        }

                        display_name.setText(signed_user.getDisplay_name());
                        fbRef.child(SITES).child(signed_user.getAffiliation()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                affiliation.setText(snapshot.getValue().toString());
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("MainActivity", "Could not get user's affiliation");
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
                    } else if (data.getStringExtra(LOGIN).equals(LOGIN)) {
                        signed_user = (Users) data.getSerializableExtra(SIGNED_USER);
                        signed_user_email = data.getStringExtra(EMAIL);
                        signed_user_password = data.getStringExtra(PASSWORD);
                        updateUINoUser();
                        updateUIUser(signed_user);
                    }
                }
                break;
            }
            case(REQUEST_CODE_ADD_OBSERVATION): {
                if(resultCode == Activity.RESULT_OK) {
                    newObservation = (Observation) data.getSerializableExtra(OBSERVATION);
                    newObservation.setObserver(signed_user.getId());
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
        fbRef.child(USERS).child(mFirebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                String id = map.get(ID).toString();
                String displayName = map.get(DISPLAY_NAME).toString();
                String affiliationName = map.get(AFFILIATION).toString();
                String avatar = map.get(AVATAR).toString();
                String bio = null;
                if (map.get(BIO) != null)
                    bio = map.get(BIO).toString();
                Long latest_contribution = null;
                if (map.get(LATEST_CONTRIBUTION) != null)
                    latest_contribution = (Long) map.get(LATEST_CONTRIBUTION);
                Object created_at = map.get(CREATED_AT);
                Object updated_at = map.get(UPDATED_AT);
                signed_user = new Users(id, displayName, affiliationName, avatar, bio, latest_contribution, created_at, updated_at);
                Picasso.with(MainActivity.this).load(Strings.emptyToNull(signed_user.getAvatar()))
                        .placeholder(R.drawable.default_avatar).fit().into(nav_iv, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        updateUIUser(signed_user);
                    }
                    @Override
                    public void onError() {
                        nav_iv.setImageDrawable(getResources().getDrawable(R.drawable.default_avatar));
                        updateUIUser(signed_user);
                    }
                });
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
        Picasso.with(MainActivity.this).load(Strings.emptyToNull(user.getAvatar()))
                .placeholder(R.drawable.default_avatar)
                .transform(mAvatarTransform).fit().into(nav_iv);
        logout.setVisible(true);
        display_name.setText(user.getDisplay_name());
        affiliation.setText(user.getAffiliation());
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
}