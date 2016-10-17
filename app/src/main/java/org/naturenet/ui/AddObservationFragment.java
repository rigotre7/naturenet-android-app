package org.naturenet.ui;

import android.app.Fragment;
import android.os.Bundle;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.naturenet.R;
import org.naturenet.data.model.PhotoCaptionContent;
import org.naturenet.data.model.Project;

public class AddObservationFragment extends Fragment {
    TextView toolbar_title, send, project;
    EditText description, whereIsIt;
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
        toolbar_title = (TextView) add.findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.add_observation_title);
        back = (ImageButton) add.findViewById(R.id.toolbar_back);
        send = (TextView) add.findViewById(R.id.toolbar_send);
        project = (TextView) add.findViewById(R.id.add_observation_tv_project);
        image = (ImageView) add.findViewById(R.id.add_observation_iv);
        description = (EditText) add.findViewById(R.id.add_observation_et_description);
        whereIsIt = (EditText) add.findViewById(R.id.add_observation_et_where);
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

                    PhotoCaptionContent data = new PhotoCaptionContent();
                    data.text = description.getText().toString();
                    String where = whereIsIt.getText().toString().trim();
                    if(!where.isEmpty()) {
                        add.newObservation.where = where;
                    }
                    add.newObservation.data = data;
                    add.newObservation.projectId = selectedProject.id;
                    add.newObservation.siteId = add.signedUser.affiliation;
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
                .placeholder(R.drawable.no_image).error(R.drawable.no_image).fit().centerInside().into(image);
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