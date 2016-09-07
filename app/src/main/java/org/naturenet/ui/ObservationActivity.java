package org.naturenet.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.naturenet.R;
import org.naturenet.data.model.Comment;
import org.naturenet.data.model.Observation;
import org.naturenet.data.model.ObserverInfo;
import org.naturenet.data.model.Users;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ObservationActivity extends AppCompatActivity {
    static String FRAGMENT_TAG_OBSERVATION_GALLERY = "observation_gallery_fragment";
    static String FRAGMENT_TAG_OBSERVATION = "observation_fragment";
    static String TITLE = "EXPLORE";
    static String SIGNED_USER = "signed_user";
    static String OBSERVERS = "observers";
    static String OBSERVATION = "observation";
    static String OBSERVATIONS = "observations";
    static String ID = "id";
    static String CREATED_AT = "created_at";
    static String UPDATED_AT = "updated_at";
    static String COMMENTS = "comments";
    static String COMMENT = "comment";
    static String COMMENTER = "commenter";
    static String PARENT = "parent";
    static String CONTEXT = "context";
    static String EMPTY = "";
    Toolbar toolbar;
    Observation selectedObservation;
    ObserverInfo selectedObserverInfo;
    TextView explore_tv_back, toolbar_title;
    ImageButton back;
    GridView gridView;
    DatabaseReference mFirebase;
    List<Observation> observations;
    List<ObserverInfo> observers;
    List<Comment> comments;
    Users signed_user;
    Boolean like;
    @Override
    protected void onSaveInstanceState(Bundle outState) {}
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
        observations = (ArrayList<Observation>) getIntent().getSerializableExtra(OBSERVATIONS);
        observers = (ArrayList<ObserverInfo>) getIntent().getSerializableExtra(OBSERVERS);
        setSupportActionBar(toolbar);
        toolbar.setTitle(EMPTY);
        selectedObservation = null;
        selectedObserverInfo = null;
        comments = null;
        if (getIntent().getSerializableExtra(OBSERVATION) != null) {
            selectedObservation = (Observation) getIntent().getSerializableExtra(OBSERVATION);
            for (int i=0; i<observers.size(); i++) {
                if (observers.get(i).getObserverId().equals(selectedObservation.getObserver())) {
                    selectedObserverInfo = observers.get(i);
                    break;
                }
            }
            goToSelectedObservationFragment();
        } else
            goToObservationGalleryFragment();
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
        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, new ObservationGalleryFragment(), FRAGMENT_TAG_OBSERVATION_GALLERY).
                addToBackStack(null).
                commit();
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
                    if (signed_user.id.equals(id))
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
    public void goBackToObservationGalleryFragment() {
        selectedObservation = null;
        selectedObserverInfo = null;
        comments = null;
        goToObservationGalleryFragment();
    }
}