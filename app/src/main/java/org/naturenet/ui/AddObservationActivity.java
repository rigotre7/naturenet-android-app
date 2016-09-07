package org.naturenet.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.naturenet.R;
import org.naturenet.data.model.Observation;
import org.naturenet.data.model.Project;
import org.naturenet.data.model.Users;

import java.util.List;
import java.util.Map;

public class AddObservationActivity extends AppCompatActivity {
    static String FRAGMENT_TAG_ADD_OBSERVATION = "add_observation_fragment";
    static String OBSERVATION = "observation";
    static String OBSERVATION_PATH = "observation_path";
    static String OBSERVATION_BITMAP = "observation_bitmap";
    static String SIGNED_USER = "signed_user";
    static String EMAIL = "email";
    static String PASSWORD = "password";
    static String ID = "id";
    static String ICON_URL = "icon_url";
    static String DESCRIPTION = "description";
    static String NAME = "name";
    static String STATUS = "status";
    static String LATEST_CONTRIBUTION = "latest_contribution";
    static String CREATED_AT = "created_at";
    static String UPDATED_AT = "updated_at";
    static String SITES = "sites";
    static String LOADING = "Loading...";
    static String DEFAULT_PROJECT_ID = "-ACES_a38";
    static String EMPTY = "";
    DatabaseReference fbRef;
    ProgressDialog pd;
    String signed_user_email, signed_user_password;
    Uri observationPath;
    Observation newObservation;
    Project defaultProject;
    Users signedUser;
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
        newObservation = (Observation) getIntent().getSerializableExtra(OBSERVATION);
        observationPath = getIntent().getParcelableExtra(OBSERVATION_PATH);
        signedUser = (Users) getIntent().getSerializableExtra(SIGNED_USER);
        signed_user_email = getIntent().getStringExtra(EMAIL);
        signed_user_password = getIntent().getStringExtra(PASSWORD);
        pd = new ProgressDialog(this);
        defaultProject = null;
        goToAddObservationFragment();
    }
    public void goToAddObservationFragment() {
        toolbar_title.setText(R.string.add_observation_title);
        if (signedUser != null) {
            fbRef = FirebaseDatabase.getInstance().getReference();
            pd.setMessage(LOADING);
            pd.setCancelable(false);
            pd.show();
            fbRef.child(Project.NODE_NAME).orderByChild(LATEST_CONTRIBUTION).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) child.getValue();
                        String id = null;
                        String icon_url = null;
                        String description = null;
                        String name = null;
                        String status = null;
                        Long latest_contribution = null;
                        Object created_at = null;
                        Object updated_at = null;
                        if (map.get(ID) != null)
                            id = map.get(ID).toString();
                        if (map.get(ICON_URL) != null)
                            icon_url = map.get(ICON_URL).toString();
                        if (map.get(DESCRIPTION) != null)
                            description = map.get(DESCRIPTION).toString();
                        if (map.get(NAME) != null)
                            name = map.get(NAME).toString();
                        if (map.get(STATUS) != null)
                            status = map.get(STATUS).toString();
                        if (map.get(LATEST_CONTRIBUTION) != null)
                            latest_contribution = (Long) map.get(LATEST_CONTRIBUTION);
                        if (map.get(CREATED_AT) != null)
                            created_at = map.get(CREATED_AT);
                        if (map.get(UPDATED_AT) != null)
                            updated_at = map.get(UPDATED_AT);
                        Project project = new Project(id, icon_url, description, name, status, latest_contribution, created_at, updated_at);
                        if (project.getId().equals(DEFAULT_PROJECT_ID)) {
                            defaultProject = project;
                        }
                        // TODO: decide on site-project submission rule
                        //Map<String, Object> sites = (Map<String, Object>) map.get(SITES);
                        //if (sites.containsKey(signedUser.affiliation)) {
                            mProjects.add(project);
                        //}
                    }
                    if (mProjects.size() != 0) {
                        getFragmentManager().
                                beginTransaction().
                                replace(R.id.fragment_container, new AddObservationFragment(), FRAGMENT_TAG_ADD_OBSERVATION).
                                addToBackStack(null).
                                commit();
                    }
                    pd.dismiss();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    pd.dismiss();
                    Toast.makeText(AddObservationActivity.this, "Could not get projects: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            getFragmentManager().
                    beginTransaction().
                    replace(R.id.fragment_container, new AddObservationFragment(), FRAGMENT_TAG_ADD_OBSERVATION).
                    addToBackStack(null).
                    commit();
        }
    }
    public void goBackToMainActivity() {
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra(OBSERVATION, newObservation);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}