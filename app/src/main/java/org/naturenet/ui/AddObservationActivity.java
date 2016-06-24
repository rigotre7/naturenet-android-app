package org.naturenet.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.common.collect.Lists;

import org.naturenet.R;
import org.naturenet.data.model.Project;

import java.util.List;

public class AddObservationActivity extends AppCompatActivity {
    static String FRAGMENT_TAG_ADD_OBSERVATION = "add_observation_fragment";
    static String OBSERVATION = "observation";
    static String EMPTY = "";
    String imgDecodableString;
    TextView toolbar_title;
    List<Project> mProjects = Lists.newArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_observation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(EMPTY);
        setSupportActionBar(toolbar);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        imgDecodableString = getIntent().getExtras().getString(OBSERVATION);
        goToAddObservationFragment();
    }
    public void goToAddObservationFragment() {
        toolbar_title.setText(R.string.add_observation_title);
        Firebase.setAndroidContext(this);
        Firebase fbRef = new Firebase(MainActivity.FIREBASE_ENDPOINT);
        fbRef.child(Project.NODE_NAME).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren())
                    mProjects.add(child.getValue(Project.class));
                if (mProjects.size() != 0) {
                    getFragmentManager().
                            beginTransaction().
                            replace(R.id.fragment_container, new AddObservationFragment(), FRAGMENT_TAG_ADD_OBSERVATION).
                            addToBackStack(null).
                            commit();
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(AddObservationActivity.this, "Could not get projects", Toast.LENGTH_SHORT).show();
            }
        });
    }
}