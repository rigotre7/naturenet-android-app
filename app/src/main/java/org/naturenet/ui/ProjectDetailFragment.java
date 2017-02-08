package org.naturenet.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import org.naturenet.R;
import org.naturenet.data.model.Observation;
import org.naturenet.data.model.Project;

import timber.log.Timber;

public class ProjectDetailFragment extends Fragment {

    static String COMPLETED = "Completed";
    private static final String ARG_PROJECT = "ARG_PROJECT";

    TextView name, status, description, no_recent;
    ImageView icon, iv_status;
    ProjectActivity p;
    GridView gridView;
    Project mProject;

    public static ProjectDetailFragment newInstance(Project p) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_PROJECT, p);
        ProjectDetailFragment frag = new ProjectDetailFragment();
        frag.setArguments(args);
        frag.setRetainInstance(true);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project_detail, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() == null || getArguments().getParcelable(ARG_PROJECT) == null) {
            Timber.e(new IllegalArgumentException(), "Tried to load ProjectDetailFragment without a Project argument");
            Toast.makeText(getActivity(), "No project to display", Toast.LENGTH_SHORT).show();
            return;
        }
        mProject = getArguments().getParcelable(ARG_PROJECT);

        p = (ProjectActivity) getActivity();
        name = (TextView) p.findViewById(R.id.project_tv_name);
        status = (TextView) p.findViewById(R.id.project_tv_status);
        description = (TextView) p.findViewById(R.id.project_tv_description);
        no_recent = (TextView) p.findViewById(R.id.project_tv_no_recent_contributions);
        icon = (ImageView) p.findViewById(R.id.project_iv_icon);
        iv_status = (ImageView) p.findViewById(R.id.project_iv_status);
        name.setText(mProject.name);

        if (mProject.status != null) {
            status.setText(mProject.status);

            if (mProject.status.equals(COMPLETED)) {
                iv_status.setVisibility(View.VISIBLE);
            } else {
                iv_status.setVisibility(View.GONE);
            }
        } else {
            iv_status.setVisibility(View.GONE);
        }

        if (mProject.description != null) { description.setText(mProject.description); }
        if (mProject.iconUrl != null) { Picasso.with(p).load(Strings.emptyToNull(mProject.iconUrl)).fit().into(icon); }

        gridView = (GridView) p.findViewById(R.id.observation_gallery);
        Query query = FirebaseDatabase.getInstance().getReference(Observation.NODE_NAME)
                .orderByChild("activity").equalTo(mProject.id).limitToLast(20);
        ObservationAdapter adapter = new ObservationAdapter(p, query);
        gridView.setAdapter(adapter);
        gridView.setEmptyView(no_recent);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                p.selectedObservation = (Observation) view.getTag();
                p.goToSelectedObservation();
            }
        });
    }
}