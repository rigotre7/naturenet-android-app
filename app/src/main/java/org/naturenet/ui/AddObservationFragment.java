package org.naturenet.ui;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.naturenet.R;
import org.naturenet.data.model.Data;
import org.naturenet.data.model.Project;

import java.util.Map;

public class AddObservationFragment extends Fragment {
    static String GEO = "geo";
    static String ACTIVITIES = "activities";
    static String ACTIVITY = "activity";
    static String SITE = "site";
    static String ID = "id";
    TextView send, project;
    EditText description;
    ImageView image;
    ImageButton back;
    Button choose;
    ListView mProjectsListView;
    TextView noProjects;
    LinearLayout add_observation_ll;
    AddObservationActivity add;
    DatabaseReference fbRef;
    Project selectedProject;
    public AddObservationFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_observation, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        add = ((AddObservationActivity) getActivity());
        back = (ImageButton) add.findViewById(R.id.toolbar_back);
        send = (TextView) add.findViewById(R.id.toolbar_send);
        project = (TextView) add.findViewById(R.id.add_observation_tv_project);
        image = (ImageView) add.findViewById(R.id.add_observation_iv);
        description = (EditText) add.findViewById(R.id.add_observation_et_description);
        choose = (Button) add.findViewById(R.id.add_observation_b_project);
        mProjectsListView = (ListView) add.findViewById(R.id.projects_list);
        add_observation_ll = (LinearLayout) add.findViewById(R.id.add_observation_ll);
        noProjects = (TextView) add.findViewById(R.id.projecs_tv);
        mProjectsListView.setAdapter(new ProjectAdapter(add, add.mProjects));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add.onBackPressed();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (add.signedUser != null) {
                    fbRef = FirebaseDatabase.getInstance().getReference();

                    Data data = new Data();
                    data.setText(description.getText().toString());
                    add.newObservation.setData(data);
                    add.newObservation.setActivity(selectedProject.id);
                    add.newObservation.setSite(add.signedUser.affiliation);
                    add.goBackToMainActivity();
                } else {
                    Toast.makeText(add, "Please login to add an observation.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mProjectsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                project.setText(add.mProjects.get(position).name);
                selectedProject = add.mProjects.get(position);
                add_observation_ll.setVisibility(View.VISIBLE);
                mProjectsListView.setVisibility(View.GONE);
            }
        });
        Picasso.with(AddObservationFragment.this.getActivity()).load(add.observationPath)
                .placeholder(R.drawable.no_image).error(R.drawable.no_image).fit().into(image);
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_observation_ll.setVisibility(View.GONE);
                if (add.signedUser != null) {
                    mProjectsListView.setVisibility(View.VISIBLE);
                    noProjects.setVisibility(View.GONE);
                } else {
                    mProjectsListView.setVisibility(View.GONE);
                    noProjects.setVisibility(View.VISIBLE);
                }
            }
        });
        if (add.defaultProject != null) {
            selectedProject = add.defaultProject;
            project.setText(selectedProject.name+" (Default)");
        } else {
            project.setText("Free Observation (Default)");
        }
        add_observation_ll.setVisibility(View.VISIBLE);
        mProjectsListView.setVisibility(View.GONE);
        noProjects.setVisibility(View.GONE);
    }
}