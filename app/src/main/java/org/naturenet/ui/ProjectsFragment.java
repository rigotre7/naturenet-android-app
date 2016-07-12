package org.naturenet.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.naturenet.R;
import org.naturenet.data.model.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class ProjectsFragment extends Fragment {
    static String ID = "id";
    static String ICON_URL = "icon_url";
    static String DESCRIPTION = "description";
    static String NAME = "name";
    static String STATUS = "status";
    static String LATEST_CONTRIBUTION = "latest_contribution";
    static String CREATED_AT = "created_at";
    static String UPDATED_AT = "updated_at";
    static String LOADING_PROJECTS = "Loading Projects...";
    MainActivity main;
    ProgressDialog pd;
    private Logger mLogger = LoggerFactory.getLogger(ProjectsFragment.class);
    private ListView mProjectsListView = null;
    private List<Project> mProjects = Lists.newArrayList();
    private DatabaseReference mFirebase = FirebaseDatabase.getInstance().getReference();
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
        pd = new ProgressDialog(main);
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
        pd.setMessage(LOADING_PROJECTS);
        pd.setCancelable(false);
        pd.show();
        mFirebase.child(Project.NODE_NAME).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                mLogger.info("Got projects, count: {}", snapshot.getChildrenCount());
                for(DataSnapshot child : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) child.getValue();
                    String id = null;
                    String icon_url = null;
                    String description = null;
                    String name = null;
                    String status = null;
                    Long latest_contribution = null;
                    Long created_at = null;
                    Long updated_at = null;
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
                        created_at = (Long) map.get(CREATED_AT);
                    if (map.get(UPDATED_AT) != null)
                        updated_at = (Long) map.get(UPDATED_AT);
                    Project project = new Project(id, icon_url, description, name, status, latest_contribution, created_at, updated_at);
                    mProjects.add(project);
                }
                if (mProjects.size() != 0) {
                    mProjectsListView.setAdapter(new ProjectAdapter(main, mProjects));
                }
                pd.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                mLogger.error("Failed to read Projects: {}", databaseError.getMessage());
                pd.dismiss();
                Toast.makeText(main, "Could not get projects: "+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}