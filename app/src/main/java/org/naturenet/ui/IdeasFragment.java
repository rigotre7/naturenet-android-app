package org.naturenet.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.naturenet.R;
import org.naturenet.data.model.Idea;
import org.naturenet.data.model.Users;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IdeasFragment extends Fragment {

    public static final String FRAGMENT_TAG = "designideas_fragment";
    public static final String IDEA_EXTRA = "idea";

    MainActivity main;
    TextView toolbar_title;
    Idea idea;
    FloatingActionButton addIdeaButton;
    private EditText searchBar;
    private TextWatcher textWatcher;
    private String search;
    private boolean isDataLoaded;

    private DatabaseReference mFirebase = FirebaseDatabase.getInstance().getReference();
    private ListView ideas_list = null;
    private IdeasAdapter mAdapter, mAdapterSearch;
    private ArrayList<Idea> ideas, searchResults;
    private List<Users> users;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ideas, container, false);

        main = ((MainActivity)  this.getActivity());
        toolbar_title = (TextView) main.findViewById(R.id.app_bar_main_tv);
        toolbar_title.setText(R.string.design_ideas_title_design_ideas);
        searchBar = (EditText) root.findViewById(R.id.ideasSearchBar);

        ideas_list = (ListView) root.findViewById(R.id.design_ideas_lv);
        addIdeaButton = (FloatingActionButton) root.findViewById(R.id.fabAddIdea);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(!isConnectedToInternet())
            Toast.makeText(main, R.string.no_network, Toast.LENGTH_SHORT).show();

        isDataLoaded = false;

        //initialize
        ideas = new ArrayList<>();
        searchResults = new ArrayList<>();
        //retrieve users from Singleton instance
        users = FetchData.getInstance().getUsers();

        mFirebase.child(Idea.NODE_NAME).orderByChild("created_at").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot idea : dataSnapshot.getChildren()){
                    ideas.add(idea.getValue(Idea.class));
                }

                Collections.reverse(ideas);
                setIdeas();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        addIdeaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                main.goToAddDesignIdeaActivity();
            }
        });

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                search = editable.toString().toLowerCase();

                //make sure there is actually ideas loaded
                if(isDataLoaded){
                    if(search.length()>0){
                        searchResults.clear();

                        //iterate over the ideas to see if we match anything
                        for(int i = 0; i<ideas.size(); i++){
                            Idea idea = ideas.get(i);
                            //check to see if the search query matches any content OR usernames
                            if(idea.content.toLowerCase().contains(search) || getUserName(idea.submitter).contains(search))
                                searchResults.add(ideas.get(i));
                        }
                    }else
                        ideas_list.setAdapter(mAdapter);
                }

                setSearchResults();

            }
        };

        searchBar.addTextChangedListener(textWatcher);

    }

    /**
     * This function queries our sorted list of users (sorted by id) and returns the corresponding display name.
     * This is done by implementing binary search.
     * @param id - the id of the user whose display name we want.
     * @return - String - the display name of the user whose id we provided.
     */
    private String getUserName(String id){

        //set left index
        int low = 0;

        //set right index
        int high = users.size() -1;

        //set middle index
        int mid = 0;

        String name = null;

        while(low <= high) {
            //id is in users[low..high] or not present
            mid = low + (high - low) / 2;

            if (users.get(mid).id.compareTo(id) > 0)
                high = mid - 1;
            else if(users.get(mid).id.compareTo(id) < 0)
                low = mid + 1;
            else {
                name = users.get(mid).displayName;
                break;
            }

        }

        return name;
    }

    /**
     * This function is called when our query for all the ideas has completed. It sets the adpater and ListView.
     */
    private void setIdeas(){
        mAdapter = new IdeasAdapter(main, R.layout.design_ideas_row_layout, ideas.subList(0, 25));
        ideas_list.setAdapter(mAdapter);

        ideas_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                idea = mAdapter.getItem(i);  //get clicked idea
                Intent ideaDetailIntent = new Intent(main, IdeaDetailsActivity.class);
                ideaDetailIntent.putExtra(IDEA_EXTRA, idea);
                startActivity(ideaDetailIntent);
            }
        });

        isDataLoaded = true;
    }

    /**
     * This function is called after a user has entered a search query. It simply creates a new adapter for the search results and sets the adapter on the ListView.
     */
    private void setSearchResults(){
        mAdapterSearch = new IdeasAdapter(main, R.layout.design_ideas_row_layout, searchResults);
        ideas_list.setAdapter(mAdapterSearch);

        ideas_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                idea = mAdapterSearch.getItem(i);
                Intent ideaDetailIntent = new Intent(main, IdeaDetailsActivity.class);
                ideaDetailIntent.putExtra(IDEA_EXTRA, idea);
                startActivity(ideaDetailIntent);
            }
        });
    }

    /*
        This method detects if we are connected to the Internet.
     */
    private boolean isConnectedToInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) main.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}