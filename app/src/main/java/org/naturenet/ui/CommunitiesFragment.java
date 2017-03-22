package org.naturenet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.naturenet.R;
import org.naturenet.data.model.Users;


public class CommunitiesFragment extends Fragment {

    public static final String FRAGMENT_TAG = "communities_fragment";
    public static final String USER_EXTRA = "user";

    private DatabaseReference mFirebase = FirebaseDatabase.getInstance().getReference();
    private ListView mCommunitiesListView = null;
    private FirebaseListAdapter mAdapter;
    Users user;

    MainActivity main;
    TextView toolbar_title, peopleCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_communities, container, false);
        main = (MainActivity) getActivity();
        toolbar_title = (TextView) main.findViewById(R.id.app_bar_main_tv);
        toolbar_title.setText(R.string.communities_title);
        peopleCount = (TextView) root.findViewById(R.id.people_count_tv_communities);

        mCommunitiesListView = (ListView) root.findViewById(R.id.communities_list);

        return root;


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Query query = mFirebase.child(Users.NODE_NAME).orderByChild("latest_contribution");
        mAdapter = new UsersAdapter(main, query);
        mCommunitiesListView.setAdapter(mAdapter);

        mCommunitiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                user = (Users) mAdapter.getItem(i); //get the clicked user
                Intent userIntent = new Intent(getActivity(), UsersDetailActivity.class);
                userIntent.putExtra(USER_EXTRA, user);
                startActivity(userIntent);

            }
        });

        mFirebase.child(Users.NODE_NAME).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long i = dataSnapshot.getChildrenCount();
                peopleCount.setText("(" + String.valueOf(i) + ")");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }

}