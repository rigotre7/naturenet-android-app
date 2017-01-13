package org.naturenet.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.naturenet.R;
import org.naturenet.data.ObserverInfo;
import org.naturenet.data.model.Comment;
import org.naturenet.data.model.Observation;
import org.naturenet.data.model.Users;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class ObservationActivity extends AppCompatActivity {

    static String FRAGMENT_TAG_OBSERVATION_GALLERY = "observation_gallery_fragment";
    static String FRAGMENT_TAG_OBSERVATION = "observation_fragment";
    static String TITLE = "EXPLORE";
    static String SIGNED_USER = "signed_user";
    static String OBSERVERS = "observers";
    static String OBSERVATION = "observation";
    static String OBSERVATIONS = "observations";
    static String EMPTY = "";

    Toolbar toolbar;
    Observation selectedObservation;
    ObserverInfo selectedObserverInfo;
    TextView explore_tv_back, toolbar_title;
    GridView gridView;
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
        gridView = (GridView) findViewById(R.id.observation_gallery);
        signed_user = (Users) getIntent().getSerializableExtra(SIGNED_USER);
        observations = (ArrayList<Observation>) getIntent().getSerializableExtra(OBSERVATIONS);
        observers = (ArrayList<ObserverInfo>) getIntent().getSerializableExtra(OBSERVERS);
        setSupportActionBar(toolbar);
        toolbar.setTitle(EMPTY);
        selectedObservation = null;
        selectedObserverInfo = null;
        comments = null;

        explore_tv_back.setOnClickListener(v -> goBackToExploreFragment());

        goToObservationGalleryFragment();

        if (getIntent().getSerializableExtra(OBSERVATION) != null) {
            selectedObservation = (Observation) getIntent().getSerializableExtra(OBSERVATION);
            for (ObserverInfo observer : observers) {
                if (observer.getObserverId().equals(selectedObservation.userId)) {
                    selectedObserverInfo = observer;
                    break;
                }
            }
            goToSelectedObservationFragment();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        selectedObservation = null;
        selectedObserverInfo = null;
        comments = null;
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

        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, new ObservationGalleryFragment(), FRAGMENT_TAG_OBSERVATION_GALLERY).
                commit();
    }

    public void goToSelectedObservationFragment() {
        toolbar_title.setVisibility(View.GONE);
        comments = null;
        like = null;

        if (selectedObservation.comments != null) { getCommentsFor(selectedObservation.id); }

        if (signed_user != null) {
            like = (selectedObservation.likes != null) && selectedObservation.likes.keySet().contains(signed_user.id);
        }

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ObservationFragment(), FRAGMENT_TAG_OBSERVATION)
                .addToBackStack(FRAGMENT_TAG_OBSERVATION).commit();
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
                Toast.makeText(ObservationActivity.this, "Unable to load comments for this observation.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}