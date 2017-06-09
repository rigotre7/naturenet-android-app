package org.naturenet.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.naturenet.R;
import org.naturenet.data.model.Project;
import org.naturenet.util.NatureNetUtils;

import java.util.ArrayList;



public class ProjectsFragment extends Fragment {

    public static final String FRAGMENT_TAG = "projects_fragment";

    ArrayList<Project> projectsList;
    DatabaseReference dbRef;
    FloatingActionButton addProjectButton;
    MainActivity main;
    private ListView mProjectsListView = null;
    private String search;
    private EditText searchBox;
    private ProjectAdapter  mAdapter, mAdapterSearch;
    private ArrayList<Project> searchResults;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_projects, container, false);
        main = ((MainActivity) getActivity());
        TextView toolbar_title = (TextView) main.findViewById(R.id.app_bar_main_tv);
        toolbar_title.setText(R.string.projects_title);

        searchBox = (EditText) root.findViewById(R.id.searchProjectText);
        mProjectsListView = (ListView) root.findViewById(R.id.projects_list);
        addProjectButton = (FloatingActionButton) root.findViewById(R.id.fabAddProject);

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.clear();
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dbRef = FirebaseDatabase.getInstance().getReference();
        projectsList = new ArrayList<>();

        dbRef.child(Project.NODE_NAME).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get all the projects from the snapshot
                Project project;
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    project = data.getValue(Project.class);
                    projectsList.add(project);
                }

                setProjects(projectsList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        searchResults = new ArrayList<>();

        mProjectsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                main.goToProjectActivity(mAdapter.getItem(position));
            }
        });

        addProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mProjectsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == SCROLL_STATE_FLING) {
                    Picasso.with(getActivity()).pauseTag(NatureNetUtils.PICASSO_TAGS.PICASSO_TAG_PROJECT_LIST);
                } else {
                    Picasso.with(getActivity()).resumeTag(NatureNetUtils.PICASSO_TAGS.PICASSO_TAG_PROJECT_LIST);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {}
        });

        /*
            Listens to any change in the search bar.
         */
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //make the search query lowercase
                search = editable.toString().toLowerCase();

                //make sure the search bar isn't empty
                if(search.length() > 0){
                    //clear the arraylist of results
                    searchResults.clear();

                    //iterate over all the Projects to see if we find any matches
                    for(Project project: projectsList){
                        if(project.name.toLowerCase().contains(search))
                            searchResults.add(project);
                    }

                    mAdapterSearch = new ProjectAdapter(main, R.layout.project_list_item, searchResults);
                    mProjectsListView.setAdapter(mAdapterSearch);
                }else{
                    //when no text is available, reuse original adapter
                    mProjectsListView.setAdapter(mAdapter);
                }

            }
        });

    }

    /*
        This method will be called when we've retrieved all the Projects.
     */
    public void setProjects(ArrayList<Project> p) {
        mAdapter = new ProjectAdapter(main, R.layout.project_list_item, p);
        mProjectsListView.setAdapter(mAdapter);
    }
}
