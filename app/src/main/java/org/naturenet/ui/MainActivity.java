package org.naturenet.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.maps.MapFragment;

import org.naturenet.BuildConfig;
import org.naturenet.R;
import org.naturenet.data.model.Project;
import org.naturenet.data.model.Users;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    static String FIREBASE_ENDPOINT = BuildConfig.FIREBASE_ROOT_URL;
    static String FRAGMENT_TAG_LAUNCH = "launch_fragment";
    static String FRAGMENT_TAG_EXPLORE = "explore_fragment";
    static String FRAGMENT_TAG_PROJECTS = "projects_fragment";
    static String FRAGMENT_TAG_DESIGNIDEAS = "designideas_fragment";
    static String FRAGMENT_TAG_COMMUNITIES = "communities_fragment";
    static String KEY_CONSENT = "key_consent";
    static String KEY_SIGNIN = "key_signin";
    static String KEY_JOIN = "key_join";
    static String GUEST = "guest";
    static String JOIN = "join";
    static String EMPTY = "";
    DrawerLayout drawer;
    String key_consent, key_join;
    String[] signed_user;
    Button sign_in, join;
    TextView toolbar_title, display_name, affiliation;
    MenuItem logout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(EMPTY);
        setSupportActionBar(toolbar);
        toolbar_title = (TextView) findViewById(R.id.app_bar_main_tv);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        logout = navigationView.getMenu().findItem(R.id.nav_logout);
        logout.setVisible(false);
        this.invalidateOptionsMenu();
        View header = navigationView.getHeaderView(0);
        sign_in = (Button) header.findViewById(R.id.nav_b_sign_in);
        join = (Button) header.findViewById(R.id.nav_b_join);
        display_name = (TextView) header.findViewById(R.id.nav_tv_display_name);
        affiliation = (TextView) header.findViewById(R.id.nav_tv_affiliation);
        display_name.setText(EMPTY);
        affiliation.setText(EMPTY);
        sign_in.setVisibility(View.VISIBLE);
        join.setVisibility(View.VISIBLE);
        display_name.setVisibility(View.GONE);
        affiliation.setVisibility(View.GONE);
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginActivity();
            }
        });
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToConsentActivity();
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
        Firebase fbRef = new Firebase(FIREBASE_ENDPOINT);
        int id = item.getItemId();
        switch(id) {
            case R.id.nav_explore:
                goToExploreFragment();
                break;
            case R.id.nav_projects:
                goToProjectsFragment();
                break;
            case R.id.nav_design_ideas:
                goToDesignIdeasFragment();
                break;
            case R.id.nav_communities:
                goToCommunitiesFragment();
                break;
            case R.id.nav_logout:
                fbRef.unauth();
                logout();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void goToLoginActivity() {
        startActivityForResult(new Intent(this, LoginActivity.class), 2);
    }
    public void showGallery() {
//        transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.main_content, ObservationGalleryFragment.newInstance());
//        transaction.addToBackStack(null);
//        transaction.commit();
    }
    public void goToLaunchFragment() {
        toolbar_title.setText(R.string.launch_title);
        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, new LaunchFragment(), FRAGMENT_TAG_LAUNCH).
                addToBackStack(null).
                commit();
    }
    public void goToConsentActivity() {
        startActivityForResult(new Intent(this, ConsentActivity.class), 1);
        overridePendingTransition(R.anim.slide_up, R.anim.stay);
    }
    public void goToExploreFragment() {
        toolbar_title.setText(R.string.explore_title);
        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, new ExploreFragment(), FRAGMENT_TAG_EXPLORE).
                addToBackStack(null).
                commit();
    }
    public void goToAddObservationActivity(String img) {
        Intent i = new Intent(this, AddObservationActivity.class);
        i.putExtra("image", img);
        startActivityForResult(i, 3);
        overridePendingTransition(R.anim.slide_up, R.anim.stay);
    }
    public void goToProjectsFragment() {
        toolbar_title.setText(R.string.projects_title);
        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, new ProjectsFragment(), FRAGMENT_TAG_PROJECTS).
                addToBackStack(null).
                commit();
    }
    public void goToProjectActivity(Project p) {
        Intent i = new Intent(this, ProjectActivity.class);
        i.putExtra("project", p);
        startActivity(i);
        overridePendingTransition(R.anim.slide_up, R.anim.stay);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case(1): {
                if(resultCode == Activity.RESULT_OK) {
                    key_consent = data.getStringExtra(KEY_CONSENT);
                    if(key_consent.equals(GUEST)) drawer.openDrawer(GravityCompat.START);
                }
                break;
            }
            case(2): {
                if(resultCode == Activity.RESULT_OK) {
                    key_join = data.getStringExtra(KEY_JOIN);
                    if(key_join.equals(JOIN)) goToConsentActivity();
                    else {
                        signed_user = data.getStringArrayExtra(KEY_SIGNIN);
                        logout.setVisible(true);
                        this.supportInvalidateOptionsMenu();
                        display_name.setText(signed_user[1]);
                        affiliation.setText(signed_user[2]);
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
            case(3): {
                if(resultCode == Activity.RESULT_OK) {

                }
                break;
            }
        }
    }
    public void logout() {
        if (signed_user==null) {
            signed_user = new String[3];
        }
        signed_user[0] = EMPTY;
        signed_user[1] = EMPTY;
        signed_user[2] = EMPTY;
        logout.setVisible(false);
        this.invalidateOptionsMenu();
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
}