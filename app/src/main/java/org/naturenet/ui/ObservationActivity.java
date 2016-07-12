package org.naturenet.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
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
import org.naturenet.data.model.Users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObservationActivity extends AppCompatActivity {
    static String FRAGMENT_TAG_OBSERVATION_GALLERY = "observation_gallery_fragment";
    static String FRAGMENT_TAG_OBSERVATION = "observation_fragment";
    static int NUM_OF_OBSERVATIONS = 4;
    static String TITLE = "EXPLORE";
    static String SIGNED_USER = "signed_user";
    static String ID = "id";
    static String CREATED_AT = "created_at";
    static String UPDATED_AT = "updated_at";
    static String OBSERVER = "observer";
    static String ACTIVITY_LOCATION = "activity_location";
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
    static String LOADING_OBSERVATIONS = "Loading Observations...";
    static String USERS = "users";
    static String DISPLAY_NAME = "display_name";
    static String AFFILIATION = "affiliation";
    static String AVATAR = "avatar";
    static String SITES = "sites";
    static String NAME = "name";
    static String COMMENT = "comment";
    static String COMMENTER = "commenter";
    static String PARENT = "parent";
    static String STATUS = "status";
    static String EMPTY = "";
    Toolbar toolbar;
    Observation selectedObservation;
    ObserverInfo selectedObserverInfo;
    TextView explore_tv_back, toolbar_title;
    ImageButton back;
    GridView gridView;
    ProgressDialog pd;
    DatabaseReference mFirebase;
    List<Observation> observations;
    List<ObserverInfo> observers;
    List<Comment> comments;
    Users signed_user;
    Boolean like;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_title = (TextView) findViewById(R.id.app_bar_explore_tv);
        explore_tv_back = (TextView) findViewById(R.id.explore_tv_back);
        back = (ImageButton) findViewById(R.id.explore_b_back);
        gridView = (GridView) findViewById(R.id.observation_gallery);
        signed_user = (Users) getIntent().getSerializableExtra(SIGNED_USER);
        setSupportActionBar(toolbar);
        toolbar.setTitle(EMPTY);
        observations = null;
        observers = null;
        selectedObservation = null;
        selectedObserverInfo = null;
        comments = null;
        explore_tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToObservationGalleryFragment();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToExploreFragment();
            }
        });
        goToObservationGalleryFragment();
    }
    public void goBackToExploreFragment() {
        observations = null;
        observers = null;
        selectedObservation = null;
        selectedObserverInfo = null;
        comments = null;
        gridView = null;
        Intent resultIntent = new Intent(this, MainActivity.class);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }
    public void goToObservationGalleryFragment() {
        toolbar_title.setVisibility(View.VISIBLE);
        toolbar_title.setText(TITLE);
        back.setVisibility(View.VISIBLE);
        explore_tv_back.setVisibility(View.GONE);
        pd = new ProgressDialog(this);
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
                        String id = null;
                        Long created_at = null;
                        Long updated_at = null;
                        String observer = null;
                        String activity_location = null;
                        Data data = null;
                        String g = null;
                        Map<String, Double> l = new HashMap<String, Double>();
                        Map<String, Boolean> comments = new HashMap<String, Boolean>();
                        Map<String, Boolean> likes = new HashMap<String, Boolean>();
                        if (map.get(ID) != null)
                            id = map.get(ID).toString();
                        if (map.get(CREATED_AT) != null)
                            created_at = (Long) map.get(CREATED_AT);
                        if (map.get(UPDATED_AT) != null)
                            updated_at = (Long) map.get(UPDATED_AT);
                        if (map.get(OBSERVER) != null) {
                            observer = map.get(OBSERVER).toString();
                            addObserver(observer);
                        }
                        if (map.get(ACTIVITY_LOCATION) != null)
                            activity_location = map.get(ACTIVITY_LOCATION).toString();
                        if (map.get(DATA) != null) {
                            data = new Data();
                            Map<String, Object> d = (Map<String, Object>) map.get(DATA);
                            if (d.get(IMAGE) != null)
                                data.setImage(d.get(IMAGE).toString());
                            if (d.get(TEXT) != null)
                                data.setText(d.get(TEXT).toString());
                        }
                        if (map.get(G) != null)
                            g = map.get(G).toString();
                        if (map.get(L) != null) {
                            ArrayList<Double> lMap = (ArrayList<Double>) map.get(L);
                            if (lMap != null) {
                                l.put(LAT, lMap.get(0));
                                l.put(LON, lMap.get(1));
                            }
                        }
                        if (map.get(COMMENTS) != null) {
                            Map<String, Object> c = (Map<String, Object>) map.get(COMMENTS);
                            for (String key: c.keySet())
                                comments.put(key, c.get(key).toString().equals(TRUE));
                        }
                        if (map.get(LIKES) != null) {
                            Map<String, Object> li = (Map<String, Object>) map.get(LIKES);
                            for (String key: li.keySet())
                                likes.put(key, li.get(key).toString().equals(TRUE));
                        }
                        Observation observation = new Observation(id, created_at, updated_at, observer, activity_location, data, g, l, comments, likes);
                        observations.add(observation);
                    }
                    if (observations.size() != 0) {
                        pd.dismiss();
                        getFragmentManager().
                                beginTransaction().
                                replace(R.id.fragment_container, new ObservationGalleryFragment(), FRAGMENT_TAG_OBSERVATION_GALLERY).
                                addToBackStack(null).
                                commit();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    pd.dismiss();
                    Toast.makeText(ObservationActivity.this, "Could not get observations: "+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            pd.dismiss();
            getFragmentManager().
                    beginTransaction().
                    replace(R.id.fragment_container, new ObservationGalleryFragment(), FRAGMENT_TAG_OBSERVATION_GALLERY).
                    addToBackStack(null).
                    commit();
        }
    }
    public void addObserver (final String observerId) {
        boolean contains = false;
        for (int i=0; i<observers.size(); i++) {
            contains = observers.get(i).getObserverId().equals(observerId);
            if (contains)
                break;
        }
        if (!contains) {
            final ObserverInfo observer = new ObserverInfo();
            observer.setObserverId(observerId);
            DatabaseReference fbRef = FirebaseDatabase.getInstance().getReference();
            mFirebase.child(USERS).child(observerId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Map<String, String> map = (Map<String, String>) snapshot.getValue();
                    observer.setObserverName(map.get(DISPLAY_NAME));
                    observer.setObserverAvatar(map.get(AVATAR));
                    DatabaseReference fbRef2 = FirebaseDatabase.getInstance().getReference();
                    mFirebase.child(SITES).child(map.get(AFFILIATION)).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            Map<String, String> map = (Map<String, String>) snapshot.getValue();
                            observer.setObserverAffiliation(map.get(NAME));
                            observers.add(observer);
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
    public void goToSelectedObservationFragment() {
        toolbar_title.setVisibility(View.GONE);
        back.setVisibility(View.GONE);
        explore_tv_back.setVisibility(View.VISIBLE);
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
                replace(R.id.fragment_container, new ObservationFragment(), FRAGMENT_TAG_OBSERVATION).
                addToBackStack(null).
                commit();
    }
    private void addComment(String s) {
        final Comment c = new Comment();
        DatabaseReference fbRef = FirebaseDatabase.getInstance().getReference();
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
                if (map.get(STATUS) != null) {
                    c.setStatus(map.get(STATUS).toString());
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
    public void goBackToObservationGalleryFragment() {
        selectedObservation = null;
        selectedObserverInfo = null;
        comments = null;
        goToObservationGalleryFragment();
    }
}