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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import org.naturenet.R;
import org.naturenet.data.model.Project;
import org.naturenet.util.NatureNetUtils;

import timber.log.Timber;

public class ProjectsFragment extends Fragment {

    public static final String FRAGMENT_TAG = "projects_fragment";
    static String LATEST_CONTRIBUTION = "latest_contribution";

    MainActivity main;
    private ListView mProjectsListView = null;
    private String search;
    private EditText searchText;
    private Query projectQuery;
    private DatabaseReference mFirebase = FirebaseDatabase.getInstance().getReference();
    private FirebaseListAdapter mAdapterOrig, mAdapter;

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
        if(mAdapter==null)
            mAdapterOrig.cleanup();
        else {
            mAdapter.cleanup();
            mAdapterOrig.cleanup();
        }
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Timber.d("Getting projects");
        Query query = mFirebase.child(Project.NODE_NAME).orderByChild(LATEST_CONTRIBUTION);
        mAdapterOrig = new ProjectAdapter(main, query);
        mProjectsListView.setAdapter(mAdapterOrig);

        mProjectsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                main.goToProjectActivity((Project)view.getTag());
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

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                search = editable.toString();

                if(search.length() > 0){
                    search = capitalizeString(search);
                    projectQuery = mFirebase.child(Project.NODE_NAME).orderByChild("name").startAt(search).endAt(search+"\uf8ff");
                    mAdapter = new ProjectAdapter(main, projectQuery);
                    mProjectsListView.setAdapter(mAdapter);
                }else{
                    //when no text is available, reuse original adapter
                    mProjectsListView.setAdapter(mAdapterOrig);
                }

            }
        });

    }

    public String capitalizeString(String text){
        //split the given string into an array
        String [] arr = text.split(" ");

        StringBuilder sb = new StringBuilder();

        //iterate over each word in the given text
        for(int j = 0; j<arr.length; j++){
            //capitalize the first letter, append it to sb, append the rest of the word making sure it's lowercase,
            //append a space at the end
            sb.append(Character.toUpperCase(arr[j].charAt(0))).append(arr[j].substring(1).toLowerCase()).append(" ");
        }

        //return the string and remove and leading and trailing spaces
        return sb.toString().trim();

    }
}