package org.naturenet.ui;

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
import org.naturenet.data.model.Project;

import java.util.List;
import java.util.Map;

public class AddObservationActivity extends AppCompatActivity {
    static String FRAGMENT_TAG_ADD_OBSERVATION = "add_observation_fragment";
    static String OBSERVATION = "observation";
    static String EMPTY = "";
    DatabaseReference fbRef;
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
        fbRef = FirebaseDatabase.getInstance().getReference();
        fbRef.child(Project.NODE_NAME).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    if (map.get("id") != null)
                        id = map.get("id").toString();
                    if (map.get("icon_url") != null)
                        icon_url = map.get("icon_url").toString();
                    if (map.get("description") != null)
                        description = map.get("description").toString();
                    if (map.get("name") != null)
                        name = map.get("name").toString();
                    if (map.get("status") != null)
                        status = map.get("status").toString();
                    if (map.get("latest_contribution") != null)
                        latest_contribution = (Long) map.get("latest_contribution");
                    if (map.get("created_at") != null)
                        created_at = map.get("created_at");
                    if (map.get("updated_at") != null)
                        updated_at = map.get("updated_at");
                    Project project = new Project(id, icon_url, description, name, status, latest_contribution, created_at, updated_at);
                    mProjects.add(project);
                }
                if (mProjects.size() != 0) {
                    getFragmentManager().
                            beginTransaction().
                            replace(R.id.fragment_container, new AddObservationFragment(), FRAGMENT_TAG_ADD_OBSERVATION).
                            addToBackStack(null).
                            commit();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AddObservationActivity.this, "Could not get projects: "+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}