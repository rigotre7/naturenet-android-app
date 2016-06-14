package org.naturenet.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.android.gms.maps.MapFragment;

import org.naturenet.BuildConfig;
import org.naturenet.R;
import org.naturenet.data.model.Users;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
//    static String FIREBASE_ENDPOINT = BuildConfig.FIREBASE_ROOT_URL;
    static String FIREBASE_ENDPOINT = "https://naturenet-staging.firebaseio.com/";
//    static String FIREBASE_ENDPOINT = "https://naturenet.firebaseio.com/";
    static String FRAGMENT_TAG_LAUNCH = "launch_fragment";
    static String FRAGMENT_TAG_EXPLORE = "explore_fragment";
    static String FRAGMENT_TAG_PROJECTS = "projects_fragment";
    static String FRAGMENT_TAG_DESIGNIDEAS = "designideas_fragment";
    static String FRAGMENT_TAG_COMMUNITIES = "communities_fragment";
    TextView toolbar_title;
    DrawerLayout drawer;
    String key_consent, key_join;
    Button sign_in, join;
    TextView display_name, affiliation;
    MenuItem logout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar_title = (TextView) findViewById(R.id.main_tB);
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
        sign_in = (Button) header.findViewById(R.id.navigation_b_sign_in);
        join = (Button) header.findViewById(R.id.navigation_b_join);
        display_name = (TextView) header.findViewById(R.id.navigation_tv_display_name);
        affiliation = (TextView) header.findViewById(R.id.navigation_tv_affiliation);
        display_name.setText("");
        affiliation.setText("");
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
            case R.id.nav_ideas:
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
//    public void showGallery() {
//        transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.main_content, ObservationGalleryFragment.newInstance());
//        transaction.addToBackStack(null);
//        transaction.commit();
//    }
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
                replace(R.id.fragment_container, new MapFragment(), FRAGMENT_TAG_EXPLORE).
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
        toolbar_title.setText(R.string.designIdeas_designIdeas_title);
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
                    key_consent = data.getStringExtra("key_consent");
                    if(key_consent.equals("guest")) drawer.openDrawer(GravityCompat.START);
                }
                break;
            }
            case(2): {
                if(resultCode == Activity.RESULT_OK) {
                    key_join = data.getStringExtra("key_join");
                    if(key_join.equals("join")) goToConsentActivity();
                    else {
                        Users user = (Users) data.getExtras().getSerializable("key_signin");
                        logout.setVisible(true);
                        this.supportInvalidateOptionsMenu();
                        display_name.setText(user.getDisplay_name());
                        affiliation.setText(user.getAffiliation());
                        sign_in.setVisibility(View.GONE);
                        join.setVisibility(View.GONE);
                        display_name.setVisibility(View.VISIBLE);
                        affiliation.setVisibility(View.VISIBLE);
                    }
                }
                break;
            }
        }
    }
    public void logout() {
        logout.setVisible(false);
        this.invalidateOptionsMenu();
        display_name.setText("");
        affiliation.setText("");
        sign_in.setVisibility(View.VISIBLE);
        join.setVisibility(View.VISIBLE);
        display_name.setVisibility(View.GONE);
        affiliation.setVisibility(View.GONE);
    }
    public void closeCurrent() {
        getFragmentManager().popBackStack();
    }
}