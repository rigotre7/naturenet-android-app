package org.naturenet.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import org.naturenet.data.model.Data;
import org.naturenet.data.model.Observation;
import org.naturenet.data.model.ObserverInfo;
import org.naturenet.data.model.Project;
import org.naturenet.data.model.Users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectActivity extends AppCompatActivity {
    static String FRAGMENT_TAG_PROJECT_DETAIL = "project_detail_fragment";
    static String FRAGMENT_TAG_SELECTED_OBSERVATION = "selected_observation_fragment";
    static String SIGNED_USER = "signed_user";
    static String EMPTY = "";
    static int NUM_OF_OBSERVATIONS = 4;
    static String SITES = "sites";
    static String ID = "id";
    static String USERS = "users";
    static String DISPLAY_NAME = "display_name";
    static String AFFILIATION = "affiliation";
    static String AVATAR = "avatar";
    static String CREATED_AT = "created_at";
    static String UPDATED_AT = "updated_at";
    static String NAME = "name";
    static String LOADING = "Loading...";
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
    static String COMMENT = "comment";
    static String COMMENTER = "commenter";
    static String PARENT = "parent";
    static String CONTEXT = "context";
    static String LIKES = "likes";
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
        toolbar_title.setText(project.getName());
        if (haveNetworkConnection()) {
            pd.setMessage(LOADING);
            pd.show();
            if (observations == null) {
                observations = Lists.newArrayList();
                observers = Lists.newArrayList();
                mFirebase = FirebaseDatabase.getInstance().getReference();
                mFirebase.child(Observation.NODE_NAME).orderByChild(UPDATED_AT).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        int count = 0;
                        for(DataSnapshot child : snapshot.getChildren()) {
                            count = count+1;
                            Map<String, Object> map = (Map<String, Object>) child.getValue();
                            if (map.get(ACTIVITY) != null) {
                                if (map.get(ACTIVITY).toString().equals(project.getId()) && (observations.size() < NUM_OF_OBSERVATIONS)) {
                                    String id = map.get(ID).toString();
                                    Long created_at = (Long) map.get(CREATED_AT);
                                    Long updated_at = (Long) map.get(UPDATED_AT);
                                    String observerId = map.get(OBSERVER).toString();
                                    String activity = map.get(ACTIVITY).toString();
                                    String site = map.get(SITE).toString();
                                    Data data = new Data();
                                    Map<String, Object> d = (Map<String, Object>) map.get(DATA);
                                    if (d.get(IMAGE) != null) {
                                        data.setImage(d.get(IMAGE).toString());
                                    }
                                    if (d.get(TEXT) != null)
                                        data.setText(d.get(TEXT).toString());
                                    String g = null;
                                    if (map.get(G) != null)
                                        g = map.get(G).toString();
                                    Map<String, Double> l = new HashMap<String, Double>();
                                    if (map.get(L) != null) {
                                        ArrayList<Double> lMap = (ArrayList<Double>) map.get(L);
                                        l.put(LAT, lMap.get(0));
                                        l.put(LON, lMap.get(1));
                                    }
                                    Map<String, Boolean> comments = new HashMap<String, Boolean>();
                                    if (map.get(COMMENTS) != null) {
                                        Map<String, Object> c = (Map<String, Object>) map.get(COMMENTS);
                                        for (String key: c.keySet())
                                            comments.put(key, c.get(key).toString().equals(TRUE));
                                    }
                                    Map<String, Boolean> likes = new HashMap<String, Boolean>();
                                    if (map.get(LIKES) != null) {
                                        Map<String, Object> li = (Map<String, Object>) map.get(LIKES);
                                        for (String key: li.keySet())
                                            likes.put(key, li.get(key).toString().equals(TRUE));
                                    }
                                    final Observation observation = new Observation(id, created_at, updated_at, observerId, activity, site, data, g, l, comments, likes);
                                    observations.add(observation);
                                }
                            }
                            if (count == snapshot.getChildrenCount()) {
                                int myCount = 0;
                                for (int j=0; j<observations.size(); j++) {
                                    myCount = myCount+1;
                                    String observerId = observations.get(j).getObserver();
                                    boolean contains = false;
                                    for (int i=0; i<observers.size(); i++) {
                                        contains = observers.get(i).getObserverId().equals(observerId);
                                        if (contains) {
                                            if (myCount == observations.size()) {
                                                pd.dismiss();
                                                getFragmentManager().
                                                        beginTransaction().
                                                        replace(R.id.fragment_container, new ProjectDetailFragment(), FRAGMENT_TAG_PROJECT_DETAIL).
                                                        addToBackStack(null).
                                                        commit();
                                            }
                                            break;
                                        }
                                    }
                                    if (!contains) {
                                        final ObserverInfo observer = new ObserverInfo();
                                        observer.setObserverId(observerId);
                                        DatabaseReference f = FirebaseDatabase.getInstance().getReference();
                                        final int finalMyCount = myCount;
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
                                                        observers.add(observer);
                                                        if (finalMyCount == observations.size()) {
                                                            pd.dismiss();
                                                            getFragmentManager().
                                                                    beginTransaction().
                                                                    replace(R.id.fragment_container, new ProjectDetailFragment(), FRAGMENT_TAG_PROJECT_DETAIL).
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
                getFragmentManager().
                        beginTransaction().
                        replace(R.id.fragment_container, new ProjectDetailFragment(), FRAGMENT_TAG_PROJECT_DETAIL).
                        addToBackStack(null).
                        commit();
            }
        } else {
            Toast.makeText(ProjectActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }
    public void goToSelectedObservationFragment() {
        toolbar_title.setVisibility(View.GONE);
        project_back.setText(leftArrows+" "+project.getName());
        String[] commentsList = selectedObservation.getComments().keySet().toArray(new String[selectedObservation.getComments().keySet().size()]);
        String[] likes = selectedObservation.getLikes().keySet().toArray(new String[selectedObservation.getLikes().keySet().size()]);
        comments = null;
        like = null;
        if (commentsList != null) {
            comments = Lists.newArrayList();
            for (String commentId: commentsList)
                addComment(commentId);
        }
        if (signed_user != null) {
            like = false;
            if (likes != null) {
                for (String id: likes) {
                    if (signed_user.getId().equals(id))
                        like = true;
                    break;
                }
            }
        }
        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, new SelectedObservationFragment(), FRAGMENT_TAG_SELECTED_OBSERVATION).
                addToBackStack(null).
                commit();
    }
    private void addComment(String s) {
        final Comment c = new Comment();
        mFirebase = FirebaseDatabase.getInstance().getReference();
        mFirebase.child(COMMENTS).child(s).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                if (map.get(ID) != null) {
                    c.setId(map.get(ID).toString());
                }
                if (map.get(COMMENT) != null) {
                    c.setComment(map.get(COMMENT).toString());
                }
                if (map.get(COMMENTER) != null) {
                    c.setCommenter(map.get(COMMENTER).toString());
                }
                if (map.get(PARENT) != null) {
                    c.setParent(map.get(PARENT).toString());
                }
                if (map.get(CONTEXT) != null) {
                    c.setContext(map.get(CONTEXT).toString());
                }
                if (map.get(CREATED_AT) != null) {
                    c.setCreated_at(map.get(CREATED_AT));
                }
                if (map.get(UPDATED_AT) != null) {
                    c.setUpdated_at(map.get(UPDATED_AT));
                }
                comments.add(c);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    public void goBackToProjectDetailFragment() {
        selectedObservation = null;
        selectedObserverInfo = null;
        project_back.setText(leftArrows+" PROJECTS");
        if (project.getName() != null)
            toolbar_title.setText(project.getName());
        toolbar_title.setVisibility(View.VISIBLE);
        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, new ProjectDetailFragment(), FRAGMENT_TAG_PROJECT_DETAIL).
                addToBackStack(null).
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
}