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
import android.media.ExifInterface;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.naturenet.R;
import org.naturenet.data.model.Observation;
import org.naturenet.data.model.Project;
import org.naturenet.data.model.Users;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    final static int REQUEST_CODE_JOIN = 1;
    final static int REQUEST_CODE_LOGIN = 2;
    final static int REQUEST_CODE_ADD_OBSERVATION = 3;
    final static int REQUEST_CODE_PROJECT_ACTIVITY = 4;
    final static int REQUEST_CODE_OBSERVATION_ACTIVITY = 5;
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
    String[] affiliation_ids, affiliation_names;
    List<String> ids, names;
    DatabaseReference fbRef;
    Users signed_user;
    String signed_user_email, signed_user_password, observationPath;
    byte[] observationBitmap;
    Observation newObservation;
    DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView navigationView;
    View header;
    Button sign_in, join;
    TextView toolbar_title, display_name, affiliation, licenses;
    ImageView nav_iv;
    MenuItem logout;
    ProgressDialog pd;
    public static FragmentManager fragmentManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_title = (TextView) findViewById(R.id.app_bar_main_tv);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
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
                        .setView(getLayoutInflater().inflate(R.layout.license_information, null))
                        .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {}
                        })
                        .setCancelable(false)
                        .show();
            }
        });
        logout = navigationView.getMenu().findItem(R.id.nav_logout);
        logout.setVisible(false);
        this.invalidateOptionsMenu();
        nav_iv.setImageDrawable(getResources().getDrawable(R.drawable.default_avatar));
        nav_iv.setImageBitmap(GetBitmapClippedCircle(((BitmapDrawable) nav_iv.getDrawable()).getBitmap()));
        display_name.setText(EMPTY);
        affiliation.setText(EMPTY);
        sign_in.setVisibility(View.VISIBLE);
        join.setVisibility(View.VISIBLE);
        display_name.setVisibility(View.GONE);
        affiliation.setVisibility(View.GONE);
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
                goToJoinActivity();
            }
        });
        goToLaunchFragment();
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
        fbRef = FirebaseDatabase.getInstance().getReference();
        int id = item.getItemId();
        switch(id) {
            case R.id.nav_explore:
                pd.setMessage(LOADING_OBSERVATIONS);
                pd.show();
                goToExploreFragment();
                drawer.closeDrawer(GravityCompat.START);
                pd.dismiss();
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
        toolbar_title.setText(R.string.explore_title);
        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, new ExploreFragment(), FRAGMENT_TAG_EXPLORE).
                addToBackStack(null).
                commit();
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
        nav_iv.setImageDrawable(getResources().getDrawable(R.drawable.default_avatar));
        nav_iv.setImageBitmap(GetBitmapClippedCircle(((BitmapDrawable) nav_iv.getDrawable()).getBitmap()));
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
        fbRef = FirebaseDatabase.getInstance().getReference();
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
        addObservation.putExtra(OBSERVATION_BITMAP, observationBitmap);
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
        while (cursor.moveToNext()) {
            if (listOfAllImages.size() < 8)
                listOfAllImages.add(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)));
        }
        return listOfAllImages;
    }
    public void uploadObservation() {
        pd.setMessage(SUBMITTING);
        pd.setCancelable(false);
        pd.show();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("photos").child(observationPath);
        UploadTask uploadTask = storageRef.putBytes(observationBitmap);
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
                                    fbRef.child(newObservation.NODE_NAME).child(id).setValue(newObservation);
                                    pd.dismiss();
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
    public Bitmap decodeURI(String filePath){
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
                    if (data.getExtras().getString(JOIN).equals(GUEST)) {
                        drawer.openDrawer(GravityCompat.START);
                    } else if (data.getExtras().getString(JOIN).equals(LAUNCH)) {
                        goToLaunchFragment();
                    } else if (data.getExtras().getString(JOIN).equals(LOGIN)) {
                        signed_user = (Users) data.getSerializableExtra(NEW_USER);
                        signed_user_email = data.getStringExtra(EMAIL);
                        signed_user_password = data.getStringExtra(PASSWORD);
                        logout.setVisible(true);
                        this.supportInvalidateOptionsMenu();
                        if (signed_user.getAvatar() != null) {
                            Picasso.with(this).load(signed_user.getAvatar()).fit().into(nav_iv, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    nav_iv.setImageBitmap(GetBitmapClippedCircle(((BitmapDrawable) nav_iv.getDrawable()).getBitmap()));
                                }
                                @Override
                                public void onError() {
                                }
                            });
                        } else {
                            nav_iv.setImageDrawable(getResources().getDrawable(R.drawable.default_avatar));
                            nav_iv.setImageBitmap(GetBitmapClippedCircle(((BitmapDrawable) nav_iv.getDrawable()).getBitmap()));
                        }
                        display_name.setText(signed_user.getDisplay_name());
                        affiliation.setText(signed_user.getAffiliation());
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
                        logout.setVisible(true);
                        this.supportInvalidateOptionsMenu();
                        if (signed_user.getAvatar() != null) {
                            Picasso.with(this).load(signed_user.getAvatar()).fit().into(nav_iv, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    nav_iv.setImageBitmap(GetBitmapClippedCircle(((BitmapDrawable) nav_iv.getDrawable()).getBitmap()));
                                }
                                @Override
                                public void onError() {
                                }
                            });
                        } else {
                            nav_iv.setImageDrawable(getResources().getDrawable(R.drawable.default_avatar));
                            nav_iv.setImageBitmap(GetBitmapClippedCircle(((BitmapDrawable) nav_iv.getDrawable()).getBitmap()));
                        }
                        display_name.setText(signed_user.getDisplay_name());
                        affiliation.setText(signed_user.getAffiliation());
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
                    goToExploreFragment();
                }
                break;
            }
        }
    }
    public static Bitmap GetBitmapClippedCircle(Bitmap bitmap) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Path path = new Path();
        path.addCircle((float) (width/2), (float) (height/2), (float) Math.min(width, (height/2)), Path.Direction.CCW);
        final Canvas canvas = new Canvas(outputBitmap);
        canvas.clipPath(path);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return outputBitmap;
    }
    private boolean haveNetworkConnection() {
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