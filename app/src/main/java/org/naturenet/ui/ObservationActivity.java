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
import org.naturenet.data.model.Comment;
import org.naturenet.data.model.Observation;
import org.naturenet.data.model.Users;

import java.util.List;

import timber.log.Timber;

public class ObservationActivity extends AppCompatActivity {

    static String FRAGMENT_TAG_OBSERVATION = "observation_fragment";
    static String SIGNED_USER = "signed_user";
    static String OBSERVATION = "observation";
    static String OBSERVATIONS = "observations";
    static String EMPTY = "";

    Toolbar toolbar;
    Observation selectedObservation;
    TextView explore_tv_back, toolbar_title;
    GridView gridView;
    DatabaseReference mFirebase;
    List<Observation> observations;
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
        signed_user = getIntent().getParcelableExtra(SIGNED_USER);
        observations = getIntent().getParcelableArrayListExtra(OBSERVATIONS);
        setSupportActionBar(toolbar);
        toolbar.setTitle(EMPTY);
        comments = null;

        explore_tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObservationActivity.this.goBackToExploreFragment();
            }
        });

        selectedObservation = getIntent().getParcelableExtra(OBSERVATION);
        goToSelectedObservationFragment();
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    public void goBackToExploreFragment() {
        observations = null;
        selectedObservation = null;
        comments = null;
        gridView = null;

        Intent resultIntent = new Intent(this, MainActivity.class);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
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
                .add(R.id.fragment_container, ObservationFragment.newInstance(selectedObservation.id), FRAGMENT_TAG_OBSERVATION)
                .commit();
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