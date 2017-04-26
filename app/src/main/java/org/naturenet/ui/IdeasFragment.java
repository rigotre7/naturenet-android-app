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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.naturenet.R;
import org.naturenet.data.model.Idea;

public class IdeasFragment extends Fragment {

    public static final String FRAGMENT_TAG = "designideas_fragment";
    public static final String IDEA_EXTRA = "idea";

    MainActivity main;
    TextView toolbar_title;
    Idea idea;

    private DatabaseReference mFirebase = FirebaseDatabase.getInstance().getReference();
    private ListView ideas_list = null;
    private FirebaseListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ideas, container, false);

        main = ((MainActivity)  this.getActivity());
        toolbar_title = (TextView) main.findViewById(R.id.app_bar_main_tv);
        toolbar_title.setText(R.string.design_ideas_title_design_ideas);

        ideas_list = (ListView) root.findViewById(R.id.design_ideas_lv);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Query query = mFirebase.child(Idea.NODE_NAME).limitToLast(20);
        mAdapter = new IdeasAdapter(main, query);
        ideas_list.setAdapter(mAdapter);

        ideas_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                idea = (Idea) mAdapter.getItem(i);  //get clicked idea
                Intent ideaDetailIntent = new Intent(main, IdeaDetailsActivity.class);
                ideaDetailIntent.putExtra(IDEA_EXTRA, idea);
                startActivity(ideaDetailIntent);
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }

}