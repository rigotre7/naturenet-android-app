package org.naturenet.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
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

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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
        updateUINoUser();

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new android.app.AlertDialog.Builder(this)
                .setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .create().show();
        }

        goToExploreFragment();
    }

    @Override
    public void onStart() {
        super.onStart();

        ((NatureNetApplication)getApplication()).getCurrentUserObservable().subscribe(new Consumer<Optional<Users>>() {
            @Override
            public void accept(Optional<Users> user) throws Exception {
                if (user.isPresent()) {
                    if (signed_user == null) {
                        MainActivity.this.onUserSignIn(user.get());
                    }
                } else {
                    if (signed_user != null) {
                        mFirebase.child(Users.NODE_NAME).child(signed_user.id).keepSynced(false);
                        MainActivity.this.onUserSignOut();
                    }
                    MainActivity.this.updateUINoUser();
                    MainActivity.this.showLaunchFragment();
                }
            }
        });
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
        if(getFragmentManager().getBackStackEntryCount() == 0) {
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

    public void onUserSignOut() {
        Toast.makeText(this, "You have been logged out.", Toast.LENGTH_SHORT).show();
        signed_user = null;
        user_home_site = null;
        logout.setVisible(false);
        this.invalidateOptionsMenu();
        Picasso.with(this).load(R.drawable.default_avatar)
                .transform(mAvatarTransform).fit().into(nav_iv);
        display_name.setText(null);
        affiliation.setText(null);
        sign_in.setVisibility(View.VISIBLE);
        join.setVisibility(View.VISIBLE);
        display_name.setVisibility(View.GONE);
        affiliation.setVisibility(View.GONE);
        clearBackStack();
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
        }
    }
    public void onUserSignIn(@NonNull Users user) {
        signed_user = user;
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
    }

    public void updateUINoUser() {
        Picasso.with(this).load(R.drawable.default_avatar)
                .transform(mAvatarTransform).fit().into(nav_iv);
        logout.setVisible(false);
        display_name.setText(null);
        affiliation.setText(null);
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
        drawer.openDrawer(GravityCompat.START);
    }
}