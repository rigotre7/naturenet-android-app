package org.naturenet.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.naturenet.R;
import org.naturenet.data.model.Comment;
import org.naturenet.data.model.Observation;
import org.naturenet.data.ObserverInfo;
import org.naturenet.data.model.Project;
import org.naturenet.data.model.Site;
import org.naturenet.data.model.Users;

import java.util.List;

import timber.log.Timber;

public class ProjectActivity extends AppCompatActivity {
    static String FRAGMENT_TAG_PROJECT_DETAIL = "project_detail_fragment";
    static String FRAGMENT_TAG_SELECTED_OBSERVATION = "selected_observation_fragment";
    static String SIGNED_USER = "signed_user";
    static String EMPTY = "";
    static int NUM_OF_OBSERVATIONS = 10;
    static String NAME = "name";
    static String LOADING = "Loading...";
    static String ACTIVITY = "activity";
    String leftArrows;
    Project project;
    List<Observation> observations;
    List<ObserverInfo> observers;
    List<Comment> comments;
    Boolean like;
    DatabaseReference mFirebase;
    Toolbar toolbar;
    ProgressDialog pd;
    Observation selectedObservation;
    ObserverInfo selectedObserverInfo;
    Users signed_user;
    TextView project_back, toolbar_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        project_back = (TextView) findViewById(R.id.project_back);
        toolbar_title = (TextView) findViewById(R.id.app_bar_project_tv);
        signed_user = (Users) getIntent().getSerializableExtra(SIGNED_USER);
        project = (Project) getIntent().getSerializableExtra("project");
        setSupportActionBar(toolbar);
        leftArrows = getResources().getString(R.string.project_back_left_arrows);
        toolbar.setTitle(EMPTY);
        toolbar_title.setVisibility(View.VISIBLE);
        project_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedObservation != null)
                    goBackToProjectDetailFragment();
                else
                    goBackToProjectsFragment();
            }
        });
        selectedObservation = null;
        selectedObserverInfo = null;
        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        goToProjectDetailFragment();
    }
    public void goBackToProjectsFragment() {
        project = null;
        selectedObservation = null;
        observations = null;
        observers = null;
        Intent resultIntent = new Intent(this, MainActivity.class);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }
    public void goToProjectDetailFragment() {
        toolbar_title.setText(project.name);
        if (haveNetworkConnection()) {
            pd.setMessage(LOADING);
            pd.show();
            if (observations == null) {
                observations = Lists.newArrayList();
                observers = Lists.newArrayList();
                mFirebase = FirebaseDatabase.getInstance().getReference();
                mFirebase.child(Observation.NODE_NAME).orderByChild(ACTIVITY).equalTo(project.id).limitToLast(NUM_OF_OBSERVATIONS).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        int count = 0;
                        for(DataSnapshot child : snapshot.getChildren()) {
                            count++;
                            final Observation observation = child.getValue(Observation.class);
                            observations.add(observation);
                            if (count == snapshot.getChildrenCount()) {
                                int myCount = 0;
                                for (Observation obs : observations) {
                                    myCount++;
                                    String observerId = obs.userId;
                                    boolean contains = false;
                                    for (ObserverInfo info : observers) {
                                        contains = info.getObserverId().equals(observerId);
                                        if (contains) {
                                            if (myCount == observations.size()) {
                                                pd.dismiss();
                                                showDetailFragment();
                                            }
                                            break;
                                        }
                                    }
                                    if (!contains) {
                                        final ObserverInfo observer = new ObserverInfo();
                                        observer.setObserverId(observerId);
                                        DatabaseReference f = FirebaseDatabase.getInstance().getReference();
                                        final int finalMyCount = myCount;
                                        f.child(Users.NODE_NAME).child(observerId).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot snapshot) {
                                                Users user = snapshot.getValue(Users.class);
                                                observer.setObserverName(user.displayName);
                                                observer.setObserverAvatar(user.avatar);
                                                DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
                                                fb.child(Site.NODE_NAME).child(user.affiliation).child(NAME).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot snapshot) {
                                                        String siteName = (String)snapshot.getValue();
                                                        observer.setObserverAffiliation(siteName);
                                                        observers.add(observer);
                                                        if (finalMyCount == observations.size()) {
                                                            pd.dismiss();
                                                            getFragmentManager().beginTransaction()
                                                                    .replace(R.id.fragment_container, new ProjectDetailFragment(), FRAGMENT_TAG_PROJECT_DETAIL)
                                                                    .commit();
                                                        }
                                                    }
                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                        pd.dismiss();
                                                        Toast.makeText(ProjectActivity.this, "Could not get observations: "+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();}
                                                });
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                pd.dismiss();
                                                Toast.makeText(ProjectActivity.this, "Could not get observations: "+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();}
                                        });
                                    }
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        pd.dismiss();
                        Toast.makeText(ProjectActivity.this, "Could not get observations: "+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                pd.dismiss();
                showDetailFragment();
            }
        } else {
            Toast.makeText(ProjectActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }
    public void goToSelectedObservationFragment() {
        toolbar_title.setVisibility(View.GONE);
        project_back.setText(leftArrows+" "+project.name);
        comments = null;
        like = null;
        if (selectedObservation.comments != null) {
            getCommentsFor(selectedObservation.id);
        }
        if (signed_user != null) {
            like = (selectedObservation.likes != null) && selectedObservation.likes.keySet().contains(signed_user.id);
        }
        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, new SelectedObservationFragment(), FRAGMENT_TAG_SELECTED_OBSERVATION).
                addToBackStack(null).
                commit();
    }
    private void getCommentsFor(final String parent) {
        comments = Lists.newArrayList();
        mFirebase = FirebaseDatabase.getInstance().getReference();
        mFirebase.child(Comment.NODE_NAME).orderByChild("parent").equalTo(parent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    comments.add(child.getValue(Comment.class));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.w("Could not load comments for record %s, query canceled: %s", parent, databaseError.getDetails());
                Toast.makeText(ProjectActivity.this, "Unable to load comments for this observation.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void goBackToProjectDetailFragment() {
        selectedObservation = null;
        selectedObserverInfo = null;
        project_back.setText(leftArrows+" PROJECTS");
        if (project.name != null)
            toolbar_title.setText(project.name);
        toolbar_title.setVisibility(View.VISIBLE);
        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, new ProjectDetailFragment(), FRAGMENT_TAG_PROJECT_DETAIL).
                commit();
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
    private void showDetailFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ProjectDetailFragment(), FRAGMENT_TAG_PROJECT_DETAIL)
                .addToBackStack(null).commit();
    }
}