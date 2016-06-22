package org.naturenet.ui;

import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.common.collect.Lists;

import org.naturenet.BuildConfig;
import org.naturenet.R;
import org.naturenet.data.model.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ProjectsFragment extends Fragment {
    MainActivity main;
    private Logger mLogger = LoggerFactory.getLogger(ProjectsFragment.class);
    private ListView mProjectsListView = null;
    private List<Project> mProjects = Lists.newArrayList();
    private Firebase mFirebase = new Firebase(BuildConfig.FIREBASE_ROOT_URL);
    public ProjectsFragment() {}
    public static ProjectsFragment newInstance() {
        ProjectsFragment fragment = new ProjectsFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_projects, container, false);
        main = ((MainActivity) getActivity());
        mProjectsListView = (ListView) root.findViewById(R.id.projects_list);
        readProjects();
        return root;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mProjectsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                main.goToProjectActivity(mProjects.get(position));
            }
        });
    }
    private void readProjects() {
        mLogger.info("Getting projects");
        mFirebase.child(Project.NODE_NAME).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                mLogger.info("Got projects, count: {}", snapshot.getChildrenCount());
                for(DataSnapshot child : snapshot.getChildren())
                    mProjects.add(child.getValue(Project.class));
                if (mProjects.size() != 0)
                    mProjectsListView.setAdapter(new ProjectAdapter(main, mProjects));
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                mLogger.error("Failed to read Projects: {}", firebaseError.getMessage());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Toast.makeText(ProjectsFragment.this.getContext(), "Could not get projects", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}