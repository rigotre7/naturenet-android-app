package org.naturenet.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import org.naturenet.DataServices;
import org.naturenet.R;
import org.naturenet.data.model.Project;
import org.naturenet.util.NatureNetUtils;

import java.util.ArrayList;

import timber.log.Timber;

public class ProjectsFragment extends Fragment {

    public static final String FRAGMENT_TAG = "projects_fragment";

    MainActivity main;
    private ListView mProjectsListView = null;
    private String search;
    private EditText searchText;
    private ProjectAdapter  mAdapter, mAdapterSearch;
    private ArrayList<Project> searchResults;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_projects, container, false);
        main = ((MainActivity) getActivity());
        TextView toolbar_title = (TextView) main.findViewById(R.id.app_bar_main_tv);
        toolbar_title.setText(R.string.projects_title);

        searchText = (EditText) root.findViewById(R.id.searchProjectText);
        mProjectsListView = (ListView) root.findViewById(R.id.projects_list);

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

        //create Adapter
        mAdapter = new ProjectAdapter(main, R.layout.project_list_item, DataServices.getInstance().getProjectsList());
        mProjectsListView.setAdapter(mAdapter);
        searchResults = new ArrayList<>();

        mProjectsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                main.goToProjectActivity(mAdapter.getItem(position));
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
        searchText.addTextChangedListener(new TextWatcher() {
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
                    for(Project project: DataServices.getInstance().getProjectsList()){
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

}