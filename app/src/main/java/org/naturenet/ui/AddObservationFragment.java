package org.naturenet.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.naturenet.R;
import org.naturenet.data.model.PhotoCaptionContent;
import org.naturenet.data.model.Project;

import timber.log.Timber;

public class AddObservationFragment extends Fragment {

    public static final String FRAGMENT_TAG = "add_observation_fragment";
    private static final String DEFAULT_PROJECT_ID = "-ACES_a38";

    TextView send, project;
    EditText description, whereIsIt;
    ImageView image;
    Button choose;
    ListView mProjectsListView;
    TextView noProjects;
    LinearLayout add_observation_ll;
    View projectsLayout;
    AddObservationActivity add;
    DatabaseReference fbRef;
    String selectedProjectId;
    ProjectAdapter mProjectAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_observation, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        add = ((AddObservationActivity) getActivity());

        Query query = FirebaseDatabase.getInstance().getReference(Project.NODE_NAME).orderByChild("latest_contribution");
        mProjectAdapter = new ProjectAdapter(getActivity(), query);
        mProjectsListView.setAdapter(mProjectAdapter);
        mProjectsListView.setEmptyView(noProjects);

        send = (TextView) getActivity().findViewById(R.id.toolbar_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (add.signed_user!=null) {
                    send.setVisibility(View.GONE);
                    fbRef = FirebaseDatabase.getInstance().getReference();
                    PhotoCaptionContent data = new PhotoCaptionContent();
                    data.text = description.getText().toString();
                    String where = whereIsIt.getText().toString().trim();

                    if (!where.isEmpty()) {
                        add.newObservation.where = where;
                    }

                    add.newObservation.data = data;
                    add.newObservation.projectId = selectedProjectId;

                    add.submitObservation();
                } else {
                    Toast.makeText(getActivity(), "Please sign in to contribute to NatureNet", Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(loginIntent, 2);
                }
            }
        });

        mProjectsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Project p = (Project) view.getTag();
                project.setText(p.name);
                selectedProjectId = p.id;
                add_observation_ll.setVisibility(View.VISIBLE);
                projectsLayout.setVisibility(View.GONE);
            }
        });

        Picasso.with(AddObservationFragment.this.getActivity())
                .load(add.observationPath)
                .placeholder(R.drawable.default_image)
                .error(R.drawable.no_image)
                .fit()
                .centerInside()
                .into(image);

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_observation_ll.setVisibility(View.GONE);
                projectsLayout.setVisibility(View.VISIBLE);
            }
        });

        selectedProjectId = DEFAULT_PROJECT_ID;
        FirebaseDatabase.getInstance().getReference(Project.NODE_NAME).child(DEFAULT_PROJECT_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Project p = dataSnapshot.getValue(Project.class);
                project.setText(String.format(getString(R.string.add_observation_default_project), p.name));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.e(databaseError.toException(), "Could not read default project");
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        project = (TextView) view.findViewById(R.id.add_observation_tv_project);
        image = (ImageView) view.findViewById(R.id.add_observation_iv);
        description = (EditText) view.findViewById(R.id.add_observation_et_description);
        whereIsIt = (EditText) view.findViewById(R.id.add_observation_et_where);
        choose = (Button) view.findViewById(R.id.add_observation_b_project);
        mProjectsListView = (ListView) view.findViewById(R.id.projects_list);
        add_observation_ll = (LinearLayout) view.findViewById(R.id.add_observation_ll);
        noProjects = (TextView) view.findViewById(R.id.projecs_tv);
        projectsLayout = view.findViewById(R.id.projects_layout);
    }
}